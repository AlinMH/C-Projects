#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h> 
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include "lib.h"

#define MAX_CLIENTS	20
#define BUFLEN 256

void error(char *msg)
{
    perror(msg);
    exit(1);
}

int main(int argc, char *argv[])
{
   	int sockfd, udpsock, newsockfd, portno, clilen;
    char buffer[BUFLEN];
    char* token; // auxiliar pentru strtok
    char* nr_card;
    char* parola;
    user* users; // vectorul de useri
    struct sockaddr_in serv_addr, cli_addr; // adresa serverului si a clientului
    int n, i, j, k;
    int no_users; // numarul de useri din fisier
   	int idx_usr;
   	int findRes;
   	int sold;
   	int money;
	pair* pairs; // perechi de socketi cu useri (adica ce user e logat pe socket) 
	FILE* fp;
    int fdmax;	 // valoare maxima file descriptor din multimea read_fds
	
	fd_set read_fds; // multimea de citire folosita in select()
    fd_set tmp_fds;	// multime folosita temporar

    if (argc < 3) {
        fprintf(stderr, "Usage : %s <port_server> <user_data_files>\n", argv[0]);
        exit(1);
    }

    fp = fopen(argv[2], "r");
    fscanf(fp, "%d\n", &no_users);

    // initializari structuri de date
    users = malloc(no_users * sizeof(user)); 
    pairs = malloc(no_users * sizeof(pair));

    pairs->len = no_users;
    pairs->crt = 0;

    for(i = 0; i < no_users; i++) {
     	fscanf(fp,"%s %s %s %s %s %lf\n", users[i].nume, users[i].prenume, 
     			users[i].nr_card, users[i].pin, users[i].parola, &users[i].sold);
     	users[i].blocat = 0;
     	users[i].logat = 0;
     	users[i].incercari = 0;
    }
    fclose(fp);

    FD_ZERO(&read_fds);
    FD_ZERO(&tmp_fds);
    
    //socketul tcp al serverului 
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0) 
   		error("ERROR opening socket");
   	//socketul udp al serverului
	udpsock = socket(AF_INET, SOCK_DGRAM, 0);
   	if(udpsock < 0)
   		error("ERROR opening socket");

    portno = atoi(argv[1]);
    memset((char *) &serv_addr, 0, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    serv_addr.sin_port = htons(portno);
     
    int yes = 1;
    // optiune pentru reutilizarea adresei socketului tpc
    setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(yes));
    if (bind(sockfd, (struct sockaddr *) &serv_addr, sizeof(struct sockaddr)) < 0) 
     	error("ERROR on binding");

    yes = 1;
    // si pentru socketul udp la fel
    setsockopt(udpsock, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(yes));
    if(bind(udpsock, (struct sockaddr *) &serv_addr, sizeof(struct sockaddr)) < 0)
    	error("ERROR on binding");

    listen(sockfd, MAX_CLIENTS);

    //se introduc socketii udp, tcp (si 0 pentru stdin) in multimea de descriptori
    FD_SET(sockfd, &read_fds);
    FD_SET(udpsock, &read_fds);
    FD_SET(0, &read_fds);
    fdmax = sockfd;

    // main loop
	while (1) {
		tmp_fds = read_fds; 
		if (select(fdmax + 1, &tmp_fds, NULL, NULL, NULL) == -1) 
			error("ERROR in select");
	
		for(i = 0; i <= fdmax; i++) {
			if (FD_ISSET(i, &tmp_fds)) {
				// daca se citeste de la tastatura din server (doar quit se poate citi de aici)
				if(i == 0) {
					memset(buffer, 0, BUFLEN);
                    fgets(buffer, BUFLEN - 1, stdin);
                   	if(!strcmp(buffer, "quit\n")) { // daca se da comanda de inchidere al serverului
                   		for(k = 1; k <= fdmax; k++) { // se trece prin toti clientii si se trimite mesajul de inchidere al serverului
                            if(FD_ISSET(k, &read_fds) && k != sockfd && k != udpsock) {
                   				sprintf(buffer, "ATM> Bye!\n");
                   				send(k, buffer, BUFLEN, 0);
                   				close(k); // se inchide socketul
                   			}
                   		}
                   		FD_ZERO(&read_fds);
                   		close(sockfd);
                   		close(udpsock);
                   		return 0;
                   	}

				} else if (i == sockfd) {
					// a venit ceva pe socketul inactiv(cel cu listen) = o noua conexiune
					// actiunea serverului: accept()
					clilen = sizeof(cli_addr);
					if ((newsockfd = accept(sockfd, (struct sockaddr *)&cli_addr, (socklen_t*)&clilen)) == -1) {
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
				} else if(i == udpsock) { // daca se primeste de pe socketul udp
					clilen = sizeof(cli_addr);
					if(recvfrom(udpsock, buffer, BUFLEN, 0, (struct sockaddr*)&cli_addr, (socklen_t*)&clilen) == -1) {
						error("err recvfrom");
					}
					printf ("Am primit de la clientul de pe socketul %d (UDP), mesajul: %s\n", i, buffer);	

					token = strtok(buffer, " \n");
					 if (!strcmp(token, "unlock")) { // daca se primeste comanda unlock <nr_card>
						token = strtok(NULL, " \n");

						// se verifica daca s-a gasit userul si daca este blocat sau nu, si se trimite la client
						// mesajul corespunzator
						if((findRes = findCard(users, strdup(token), no_users)) == 1) {
							memset(buffer, 0, BUFLEN);
							sprintf(buffer, "UNLOCK> Trimite parola secreta\n");
						} else if(findRes == -1) {
							memset(buffer, 0, BUFLEN);
							sprintf(buffer, "UNLOCK> -4 : Numar card inexistent\n");
						} else if(findRes == 0) {
							memset(buffer, 0, BUFLEN);
							sprintf(buffer, "UNLOCK> -6 : Operatie esuata\n");
						}

						if(sendto(udpsock, buffer, BUFLEN, 0, (struct sockaddr*)&cli_addr, clilen) == -1) {
							error("err sendto");
						}
					} else { // daca se trimite <nr_card> <parola>
						nr_card = strdup(token);
						token = strtok(NULL, " \n");
						parola = strdup(token);

						// se cauta indexul userului (in cazul asta stim sigur ca exista)
						idx_usr = getUserIndex(users, nr_card, no_users);

						// se verifica daca parola este corecta
						// sau daca nu cumva a fost deblocat in alta sesiune (caz tratat, desi nu este prezentat in tema)
						if(!strcmp(parola, users[idx_usr].parola)) {
							if(users[idx_usr].blocat == 0) { // cardul nu este blocat
								memset(buffer, 0, BUFLEN);
								sprintf(buffer, "UNLOCK> -6 : Operatie esuata\n");
							}else { // cardul era blocat, si-l deblocam
								memset(buffer, 0, BUFLEN);
								sprintf(buffer, "UNLOCK> Client deblocat\n");
								users[idx_usr].blocat = 0;
								users[idx_usr].incercari = 0;
							}
						} else { // daca parola nu este corecta
							memset(buffer, 0, BUFLEN);
							sprintf(buffer, "UNLOCK> -7 : Deblocare esuata\n");							
						}
						
					    if(sendto(udpsock, buffer, BUFLEN, 0, (struct sockaddr*)&cli_addr, clilen) == -1) {
                        	error("err sendto");
                       	}

					}
				} else {
					memset(buffer, 0, BUFLEN);
					if ((n = recv(i, buffer, sizeof(buffer), 0)) <= 0) {
						if (n == 0) {
							//conexiunea s-a inchis
							printf("Server: socket %d hung up\n", i);
						} else {
							error("ERROR in recv");
						}
						close(i); 
						FD_CLR(i, &read_fds); 
					} else { //recv intoarce >0
						printf ("Am primit de la clientul de pe socketul %d (TCP), mesajul: %s", i, buffer);
						int ok1 = 0;
						int ok2 = 0;

						char* tok = strtok(strdup(buffer), " \n");
						if(!strcmp(tok, "login")) { // comanda login
							tok = strtok(NULL, " \n");
							for(j = 0; j < no_users; j++) {
								if(!strcmp(users[j].nr_card, tok)) {
									ok1 = 1;
									if(users[j].blocat == 1) { // daca este blocat, se trimite mesajul corespunzator
										ok2 = 1;
										sprintf(buffer, "ATM> -5 : Card blocat\n");
										send(i, buffer, strlen(buffer), 0);
										break;
									}

									if(users[j].logat == 1) { // daca este deja logat in alta sesiune
										ok2 = 1;
										sprintf(buffer, "ATM> -2 : Sesiune deja deschisa\n");
										send(i, buffer, strlen(buffer), 0);
										break;

									} else if(!strcmp(tok = strtok(NULL, " \n"), users[j].pin)) { // se verifica pin-ul
										ok2 = 1;
										users[j].logat = 1;
										users[j].incercari = 0;
										sprintf(buffer, "ATM> Welcome %s %s\n", users[j].nume, users[j].prenume);
										addPair(pairs, &users[j], i);
										send(i, buffer, strlen(buffer), 0);
										break;

									} else {
										// am luat in considerare cazul in care se blocheaza un card, ci nu sesiunea.
										if(++users[j].incercari == 3) { // daca nu este bun pin-ul, se incrementeaza contorul de incercari
											ok2 = 1;
											users[j].blocat = 1;
											sprintf(buffer, "ATM> -5 : Card blocat\n");
											send(i, buffer, strlen(buffer), 0);
										}
									}									
								}
							}

							if(!ok1 && !ok2) { // cazul in care nu exista cardul
								sprintf(buffer, "ATM> -4 : Numar card inexistent\n");
								send(i, buffer, strlen(buffer), 0);
							}

							if(ok1 && !ok2) { // caz in care pin-ul este gresit
								sprintf(buffer, "ATM> -3 : Pin gresit\n");
								send(i, buffer, strlen(buffer), 0);
							}

						} else if(!strcmp(tok, "logout")) { // comanda logout
							removePair(pairs, i); // se delogheaza userul de pe socket
							sprintf(buffer, "ATM> Deconectare de la bancomat\n");
							send(i, buffer, strlen(buffer), 0);

						} else if(!strcmp(tok, "listsold")) { // comanda listsold
							sprintf(buffer, "ATM> %.2lf\n", getSold(pairs, i));
							send(i, buffer, strlen(buffer), 0);

						} else if(!strcmp(tok, "getmoney")) { // comanda getmoney
							tok = strtok(NULL, " \n");
							sscanf(tok, "%d", &sold);
							if(sold % 10 != 0) { // daca nu este multiplu de 10, se intoarce mesajul coresp.
								sprintf(buffer, "ATM> -9 : Suma nu este multiplu de 10\n");
								send(i, buffer, strlen(buffer), 0);
							} else if(getMoney(pairs, i, sold) == 0) {
								sprintf(buffer, "ATM> Suma %d retrasa cu succes\n", sold);	
								send(i, buffer, strlen(buffer), 0);
							} else {
								sprintf(buffer, "ATM> -8 : Fonduri insuficiente\n");
								send(i, buffer, strlen(buffer), 0);
							}
						} else if(!strcmp(tok, "putmoney")) { // comanda putmoney
							tok = strtok(NULL, " \n");
							sscanf(tok, "%d", &money);
							putMoney(pairs, i, money);

							sprintf(buffer, "ATM> Suma depusa cu succes\n");
							send(i, buffer, strlen(buffer), 0);
						} else if(!strcmp(tok, "quit")) {
							removePair(pairs, i); // daca se primeste quit de la client, il deloghez
						}
					}
				} 
			}
		}
    }
    close(sockfd);
    return 0; 
}