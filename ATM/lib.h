#ifndef LIB
#define LIB

typedef struct {
	char nume[13];
	char prenume[13];
	char nr_card[7];
	char pin[5];
	char parola[17];
	double sold;
	int logat; 		// 1 - logat, 0 - delogat
	int blocat; 	// 1 - blocat, 0 - deblocat
	int incercari; 	// de la 0 la 3
} user;

typedef struct { 	// pereche (socket, user)
	int sock;
	user* user;
	int crt;
	int len;
} pair;

void addPair(pair* pairs, user* u, int sock);
void removePair(pair* pairs, int sock);
double getSold(pair* pairs, int sock);
int getMoney(pair* pairs, int sock, int money);
int getUserIndex(user* users, char* nr_card, int no_users);
void putMoney(pair* pairs, int sock, int money);
int findCard(user* users, char* nr_card, int no_users);
#endif