#include "lib.h"
#include <arpa/inet.h>
#include <poll.h>
#include <netinet/in.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <unistd.h>
#include <string.h>


// adauga un user cu parola intr-un vector de perechi
void addPair(pair* pairs, user* u, int sock) {
	int i;
	for(i = 0; i <= pairs->crt; i++) {
		if(pairs[i].sock == sock) { // daca are acelasi socket doar se updateaza userul
			pairs[i].user = u;
			return;
		}
	}
	pairs[pairs->crt].sock = sock;
	pairs[pairs->crt].user = u;
	pairs->crt++; 
}

// delogheaza un user dintr-un vector de perechi
void removePair(pair* pairs, int sock) {
	int i;
	for(i = 0; i <= pairs->crt; i++) {
		if(pairs[i].sock == sock) {
			pairs[i].user->logat = 0;
			break;
		}
	}
}

// returneaza sold-ul din vectorul de perechi corespunzator socketului dat ca parametru
double getSold(pair* pairs, int sock) {
	int i;
	for(i = 0; i <= pairs->crt; i++) {
		if(pairs[i].sock == sock) {
			return pairs[i].user->sold;
		}
	}
	return 0;
}

// 0 - success
// -1 - fonduri insuficiente
int getMoney(pair* pairs, int sock, int money) {
	int i;
	for(i = 0; i <= pairs->crt; i++) {
		if(pairs[i].sock == sock) {
			double sold = pairs[i].user->sold;
			if(sold - money < 0)
				return -1;
			pairs[i].user->sold -= money;
			return 0;
		}
	}
	return -1;
}

// adauga suma in contul de pe socketul dat
void putMoney(pair* pairs, int sock, int money) {
	int i;
	for(i = 0; i <= pairs->crt; i++) {
		if(pairs[i].sock == sock) {
			pairs[i].user->sold += money;
			return;
		}
	}
}

//-1 card inexistent
// 0 esuare
// 1 gasit
int findCard(user* users, char* nr_card, int no_users) {
	int i;
	int ok = 0;

	for(i = 0; i < no_users; i++) {
		if(!strcmp(users[i].nr_card, nr_card)) {
			ok = 1;
			if(users[i].blocat == 0)
				return 0;
			break;
		}
	}
	if(!ok)
		return -1;
	return 1;
}

// returneaza indexul userului cu nr. de card dat
int getUserIndex(user* users, char* nr_card, int no_users) {
	int i;
	for (i = 0; i < no_users; i++) {
		if(!strcmp(users[i].nr_card, nr_card)) {
			return i;
		}
	}
	return -1;

}