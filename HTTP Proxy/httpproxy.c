#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h> 
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>

#define MAX_CLIENTS	20
#define BUFLEN 4096
#define MAX_LEN 100
#define HTTP_PORT 80

void error(char *msg)
{
    perror(msg);
    exit(1);
}


void parse_command(char* command, char* dname, int* port) {
	char* command_copy = strdup(command);
	char* command_aux = strdup(command);
	char* token;
	char aux[MAX_LEN];
	char* token_aux;
	*port = 0;

	token_aux = strtok(command_aux, "\n");
	token_aux = strtok(NULL, "\n");

	// cazul in care host-ul este speficiat explicit
	if(token_aux != NULL && strstr(token_aux, "Host:")) {
		token_aux = strtok(token = strdup(token_aux), ":\n");
		token_aux = strtok(NULL, ":\n");
		sscanf(token_aux, "%s", dname);
		free(command_copy);
		free(command_aux);
		free(token);
		return;
	}


	token = strtok(command_copy, " \n");
	token = strtok(NULL, " ");
	
	sscanf(token, "http://%99[^/\n]", aux);
	
	if(strstr(aux, ":")) { // daca se specifica port-ul in url, se extrage
		token = strtok(aux, ":\n");
		sscanf(token, "%s", dname);
		token = strtok(NULL, ":\n");
		*port = atoi(token);
	} else { // altfel ne intereseaza doar numele
		sscanf(aux, "%s", dname);
	}

	free(command_copy);
	free(command_aux);
}


int main(int argc, char *argv[])
{
    int sockfd, proxy_sock, port, newsockfd, portno, clilen;
    char buffer[BUFLEN];
    char dname[MAX_LEN];

    struct sockaddr_in serv_addr, cli_addr, proxy_addr;
    struct hostent* he;
    
    int n, i;
    int yes = 1;
    
    fd_set read_fds;	//multimea de citire folosita in select()
    fd_set tmp_fds;		//multime folosita temporar 
    int fdmax;			//valoare maxima file descriptor din multimea read_fds

    if (argc < 2) {
        fprintf(stderr,"Usage : %s <port>\n", argv[0]);
        exit(1);
    }

    //golim multimea de descriptori de citire (read_fds) si multimea tmp_fds 
    FD_ZERO(&read_fds);
    FD_ZERO(&tmp_fds);
     
    proxy_sock = socket(AF_INET, SOCK_STREAM, 0);
    if (proxy_sock < 0) 
    	error("ERROR opening socket");
     
    portno = atoi(argv[1]);

    memset((char *) &proxy_addr, 0, sizeof(proxy_addr));
    proxy_addr.sin_family = AF_INET;
    proxy_addr.sin_addr.s_addr = INADDR_ANY;	// foloseste adresa IP a masinii
    proxy_addr.sin_port = htons(portno);
     
    // optiune pentru reutilizarea adresei socketului tpc
    setsockopt(proxy_sock, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(yes));

    if(bind(proxy_sock, (struct sockaddr *) &proxy_addr, sizeof(struct sockaddr)) < 0) 
   		error("ERROR on binding");
     
    listen(proxy_sock, MAX_CLIENTS);

    //adaugam noul file descriptor (socketul pe care se asculta conexiuni) in multimea read_fds
    FD_SET(proxy_sock, &read_fds);
    fdmax = proxy_sock;

    // main loop
	while(1) {
		tmp_fds = read_fds; 
		if(select(fdmax + 1, &tmp_fds, NULL, NULL, NULL) == -1) 
			error("ERROR in select");
	
		for(i = 0; i <= fdmax; i++) {
			if(FD_ISSET(i, &tmp_fds)) {
			
				if(i == proxy_sock) {
					// a venit ceva pe socketul inactiv(cel cu listen) = o noua conexiune
					// actiunea serverului: accept()
					clilen = sizeof(cli_addr);
					if((newsockfd = accept(proxy_sock, (struct sockaddr *)&cli_addr, (socklen_t *)&clilen)) == -1) {
						error("ERROR in accept");
					} 
					else {
						//adaug noul socket intors de accept() la multimea descriptorilor de citire
						FD_SET(newsockfd, &read_fds);
						if (newsockfd > fdmax) { 
							fdmax = newsockfd;
						}
					}
					printf("Noua conexiune de la %s, port %d, socket_client %d\n ", inet_ntoa(cli_addr.sin_addr), ntohs(cli_addr.sin_port), newsockfd);
				}
					
				else {
					// am primit date pe unul din socketii cu care vorbesc cu clientii
					//actiunea serverului: recv()
					memset(buffer, 0, BUFLEN);
					if((n = recv(i, buffer, sizeof(buffer), 0)) <= 0) {
						if(n == 0) {
							//conexiunea s-a inchis
							printf("selectserver: socket %d hung up\n", i);
						} else {
							error("ERROR in recv");
						}
						close(i); 
						FD_CLR(i, &read_fds); // scoatem din multimea de citire socketul pe care 
					} 
					
					else { //recv intoarce >0

						//se parseaza comanda (se extrage numele si port-ul)
						parse_command(buffer, dname, &port);

						he = gethostbyname(dname);
						
						if(he != NULL) {
							if((sockfd = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
								error("Eroare creare socket");
							}

							// daca port-ul nu este existent in url
							// se seteaza port-ul pe 80
							if(port == 0) {
								port = HTTP_PORT;
							}

							// se stabileste conexiunea
							memset(&serv_addr, 0, sizeof(serv_addr));
							serv_addr.sin_family = AF_INET;
							serv_addr.sin_port = htons(port);
							serv_addr.sin_addr = *((struct in_addr*)he->h_addr);

							if(connect(sockfd, (struct sockaddr*) &serv_addr, sizeof(serv_addr)) < 0) {
								error("Eroare conectare");
							}

							// se trimite comanda la server 
							if(send(sockfd, buffer, BUFLEN - 1, 0) < 0) {
								error("Eroare in send");
							}

							memset(buffer, 0, BUFLEN);
							// cat timp se primeste pe socket, se trimite inapoi la client (browser)
							while((n = recv(sockfd, buffer, BUFLEN, 0)) > 0) {
								write(i, buffer, n);
							}

							// se intrerupe conexiunea cu serverul
							close(sockfd);
							FD_CLR(i, &read_fds);
							close(i);
						}
					}
				} 
			}
		}
    }

    close(sockfd);
   
    return 0; 
}


