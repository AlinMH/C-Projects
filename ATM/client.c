#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h> 
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include "lib.h"

#define BUFLEN 256
#define FNAMELEN 20

void error(char *msg)
{
    perror(msg);
    exit(0);
}

int main(int argc, char *argv[])
{
    int sockfd, udpsock, n, i;
    int logged = 0; // flag pentru sesiune deja deschisa
    struct sockaddr_in serv_addr;
    char* token;
    char* token_aux;
    char* current_card; // cardul retinut de la ultimul login
    char* aux;
    char aux_buffer[BUFLEN];
    int addr_len = sizeof(serv_addr);
    char client_file[FNAMELEN];
    char buffer[BUFLEN];
    int fdmax;
    FILE* fp;

    fd_set read_fds; // multimea de citire folosita in select()
    fd_set tmp_fds;  // multime folosita temporar 

    if (argc < 3) {
       fprintf(stderr,"Usage %s <IP_server> <port_server>\n", argv[0]);
       exit(0);
    }

    sprintf(client_file, "client-%d.log", getpid());  
    fp = fopen(client_file, "w");

    FD_ZERO(&read_fds);
    FD_ZERO(&tmp_fds);
    
    // socketul tcp al clientului
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0) 
        error("ERROR opening socket");
    
    //socketul udp al clientului
    udpsock = socket(AF_INET, SOCK_DGRAM, 0);
    if(udpsock < 0)
        error("ERROR opening socket");
    
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(atoi(argv[2]));
    inet_aton(argv[1], &serv_addr.sin_addr);
    
    //se introduc socketii udp, tcp (si 0 pentru stdin) in multimea de descriptori
    FD_SET(sockfd, &read_fds);
    FD_SET(udpsock, &read_fds);
    FD_SET(0, &read_fds);
    fdmax = udpsock;

    if (connect(sockfd, (struct sockaddr*) &serv_addr, sizeof(serv_addr)) < 0) 
        error("ERROR connecting");    

    while(1) {
        tmp_fds = read_fds; 
        if (select(fdmax + 1, &tmp_fds, NULL, NULL, NULL) == -1) 
            error("ERROR in select");

        for(i = 0; i <= fdmax; i++) {
            if(FD_ISSET(i, &tmp_fds)) {
                if (i == 0) {
                    //citesc de la tastatura
                    memset(buffer, 0 , BUFLEN);
                    fgets(buffer, BUFLEN - 1, stdin);
                    fprintf(fp, "%s", buffer);
                    token = strtok(strdup(buffer), " \n");
                    if(!strcmp(token, "login")) { // la login se pastreaza ultimul card
                        aux = strdup(buffer);
                        token_aux = strtok(aux, " \n");
                        token_aux = strtok(NULL, " \n");
                        current_card = strdup(token_aux);
                    }

                    if(!strcmp(token, "login") && logged == 1) { // daca in sesiunea curenta sunt deja logat, trimit de la client eroare coresp.
                        printf("-2 : Sesiune deja deschisa\n");
                        fprintf(fp, "%s", "-2 : Sesiune deja deschisa\n");
                        continue;
                    } else if(!strcmp(token, "unlock")) { // daca primesc de la tastatura unlock, trimit pe udp unlock <nr_card>
                        memset(buffer, 0, BUFLEN);
                        sprintf(buffer, "unlock %s", current_card);
                        if(sendto(udpsock, buffer, BUFLEN, 0, (struct sockaddr*)&serv_addr, addr_len) == -1) {
                            error("err sendto");
                        }
                        continue;
                    } else if(!strcmp(token, "logout")) { // daca primesc logout, verific daca sunt logat deja
                        if(logged == 0) {
                            printf("-1 : Clientul nu este autentificat\n");
                            fprintf(fp, "%s", "-1 : Clientul nu este autentificat\n");
                            continue;
                        } else { // daca s-a efectuat cu succes, se reseteaza flagul logged
                            logged = 0;
                        }
                    } else if((!strcmp(token, "listsold") || !strcmp(token, "getmoney") || !strcmp(token, "putmoney")) && logged == 0) {
                        // daca trimit o comanda specifica userului si nu sunt logat, trimit eroarea coresp.
                        printf("-1 : Clientul nu este autentificat\n");
                        fprintf(fp, "%s", "-1 : Clientul nu este autentificat\n");
                        continue;                        
                    } else if(!strcmp(token, "quit")) { // se trimite mesajul de quit, si se inchid socketele
                        n = send(sockfd, buffer, strlen(buffer), 0);
                        if (n < 0) 
                            error("ERROR writing to socket");
                        close(sockfd);
                        close(udpsock);
                        return 0;
                    }
                    
                    n = send(sockfd, buffer, strlen(buffer), 0);
                    if (n < 0) 
                        error("ERROR writing to socket");
                } else if(i == sockfd) {
                    n = recv(sockfd, buffer, sizeof(buffer), 0);
                    if(n <= 0) { // daca nu mai trimite nimic serverul sau am eroare in recv, inchid socketele si fisierul
                        error("err recv");
                        close(sockfd);
                        close(udpsock);
                        FD_ZERO(&read_fds);
                        fclose(fp);
                        return 0;
                    } else if (n > 0) { // daca primesc mesajul de inchidere al serverului, se inchide la fel.
                        if(strstr(buffer, "Bye") != NULL) {
                            close(sockfd);
                            close(udpsock);
                            FD_ZERO(&read_fds);
                            fclose(fp);
                            return 0;
                        } else {
                            printf("%s", buffer);
                            fprintf(fp, "%s", buffer);
                        }
                    }
                    if(strstr(buffer, "Welcome") != NULL) { // daca primesc mesaj de welcome, inseamna ca in sesiunea asta sunt logat
                        logged = 1;
                    }

                } else if(i == udpsock) { 
                    // primesc pe socketul udp
                    memset(buffer, 0, BUFLEN);
                    if(recvfrom(udpsock, buffer, BUFLEN, 0, (struct sockaddr*)&serv_addr, (socklen_t*)&addr_len) == -1) {
                        error("recvfrom");
                    }
                    printf("%s", buffer);
                    fprintf(fp, "%s", buffer);
                    if(strstr(buffer, "Trimite") != NULL) { // daca trebuie trimisa parola, o citim de la tastatura
                        memset(buffer, 0, BUFLEN);
                        fgets(buffer, BUFLEN - 1, stdin);
                        fprintf(fp, "%s", buffer);
                        buffer[strlen(buffer) - 1] = 0; // sterg \n de la sfarsit
                        sprintf(aux_buffer, "%s %s", current_card, buffer); // mesajul o sa fie de forma <nr_card> <parola>
                        if(sendto(udpsock, aux_buffer, BUFLEN, 0, (struct sockaddr*)&serv_addr, addr_len) == -1) {
                            error("err sendto");
                        }
                    }
                }
            }
        }   
    }
    return 0;
}


