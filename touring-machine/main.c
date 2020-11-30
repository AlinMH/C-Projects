#include <stdio.h>
#include <string.h>
#include <stdlib.h>

typedef struct {
	char* banda;
	int currentPosition;
}Tape;

typedef struct {

	int current_state;
	int next_state;
	char* read_symbol;
	char* write_symbol;
	char direction;
}Tranzitie;

typedef struct {

	int terminare; // 1 - se termina, -1 se agata, 0 default;
	int stareInitiala;
	int stareCrt;
	int nrStari;
	int nrStariF;
	int nrTran;
	int* stari;
	int* stariFinale;
	Tranzitie* tranzitii;
}TM;


Tape* initTape(char* banda) {
	Tape* r = malloc(sizeof(Tape));
	r->currentPosition = 1;
	r->banda = banda;
	return r;
}

TM* initTM(int stareInitiala, int nrStari, int nrStariF, int nrTran, int* stari, int* stariFinale) {
	int i;
	TM* t = malloc(sizeof(TM));

	t->terminare = 0;
	t->stareCrt = stareInitiala;
	t->stareInitiala = stareInitiala;
	t->nrStari = nrStari;
	t->nrStariF = nrStariF;
	t->nrTran = nrTran;
	t->tranzitii = malloc(nrTran * sizeof(Tranzitie));
	t->stari = stari;
	t->stariFinale = stariFinale;

	for(i = 0; i < nrTran; i++) {
		t->tranzitii[i].read_symbol = malloc(sizeof(char));
		t->tranzitii[i].write_symbol = malloc(sizeof(char));
	}

	return t;
}

int* convertStates(char** states, int nrStari) {

	int* rez = malloc(nrStari * sizeof(int));
	int i;

	for(i = 0; i < nrStari; i++) {
		rez[i] = atoi(++states[i]);
	}

	return rez;
}

void getTransitions(TM* turingMachine, char** tran, int nrTran) {
	int i;
	char* aux1 = malloc(100 * sizeof(char));
	char* aux2 = malloc(100);
	for(i = 0; i < nrTran; i++) {
		turingMachine->tranzitii[i].current_state = atoi(tran[i] + 1);
		sprintf(aux1, "%d", turingMachine->tranzitii[i].current_state);
		turingMachine->tranzitii[i].next_state = atoi(tran[i] + strlen(aux1)+ 1 + 4);
		strncpy(turingMachine->tranzitii[i].read_symbol, tran[i] + 1 + strlen(aux1) + 1, 1);
		sprintf(aux2, "%d", turingMachine->tranzitii[i].next_state);
		strncpy(turingMachine->tranzitii[i].write_symbol, tran[i] + 1 + strlen(aux1) + 4 + strlen(aux2) + 1, 1);
		turingMachine->tranzitii[i].direction = tran[i][strlen(aux1) + 1 + 4 + strlen(aux2) +3];
	}
}

void verificare(TM* turingMachine, Tape* tape) {

	int i, j;
	int found1 = 0;
	int found2 = 0;
	for(i = 0; i < turingMachine->nrTran; i++) {
		if(turingMachine->stareCrt == turingMachine->tranzitii[i].current_state)
			if(strncmp(tape->banda + tape->currentPosition, turingMachine->tranzitii[i].read_symbol, 1) == 0) {
				found1 = 1;
				j = i;
			}
	}
	if(found1 == 1) {
		tape->banda[tape->currentPosition] = *turingMachine->tranzitii[j].write_symbol;
		turingMachine->stareCrt = turingMachine->tranzitii[j].next_state;
		for(i = 0; i < turingMachine->nrStariF; i++) {
			if(turingMachine->stareCrt == turingMachine->stariFinale[i]) {
				found2 = 1;
			}
		}
		if(turingMachine->tranzitii[j].direction == 'R') {
			tape->currentPosition++;
		}
		if(turingMachine->tranzitii[j].direction == 'L') {
			tape->currentPosition--;
		}
	}

	if(found2 == 1) {
		turingMachine->terminare = 1;
		return;
	}

	if(found1 == 0) {
		turingMachine->terminare = -1;
		return;

	}
}

int main(int argc, char const *argv[]) {

	char* buffer = malloc(10000);
	char* p;
	char* tape = malloc(10000* sizeof(char));

	Tape* myTape = initTape(tape);

	int* rez1, *rez2;
	int i = 0;
	long int posFile;

	int nrStari;
	int nrStariF;

	FILE* fp;
	FILE* fptr;
	FILE* fout;
	fp = fopen("tm.in", "r");
	fptr = fopen("tape.in", "r");
	fout = fopen("tape.out", "w");

	fgets(myTape->banda, 10000, fptr);
	posFile = ftell(fptr);

	for(i = posFile; i < 10000 - posFile; i++) {
		myTape->banda[i] = '#';
	}

	printf("TAPE Initial:%s\n", myTape->banda);

	fgets(buffer, 10000, fp);
	p = strtok(buffer, " ");

	nrStari = atoi(p);

	char** stari = malloc(nrStari * sizeof(char*));
	for(i = 0; i < nrStari; i++) {
		stari[i] = malloc(10);
	}

	i = 0;
	while(p != NULL && i != nrStari) {
		p = strtok(NULL, " ");
		stari[i] = strdup(p);
		i++;
	}

	fgets(buffer, 10000, fp);
	p = strtok(buffer, " ");
	nrStariF = atoi(p);


	char** stariF = malloc(nrStariF * sizeof(char*));
	for(i = 0; i < nrStariF; i++) {
		stariF[i] = malloc(10);
	}

	i = 0;
	while(p != NULL && i != nrStariF) {
		p = strtok(NULL, " ");
		stariF[i] = strdup(p);
		i++;
	}

	rez1 = convertStates(stari, nrStari);
	rez2 = convertStates(stariF, nrStariF);
	
	fgets(buffer, 10000, fp);
	int stareInitiala = atoi(++buffer);

	fgets(buffer, 10000, fp);
	int nrTran = atoi(buffer);

	TM* turingMachine = initTM(stareInitiala, nrStari, nrStariF, nrTran, rez1, rez2);

	char** tran = malloc(nrTran * sizeof(char*));
	for(i = 0; i < nrTran; i++) {
		tran[i] = malloc(100);
	}
	i = 0;
	while(fgets(buffer, 10000, fp) && i != nrTran) {
		tran[i] = strdup(buffer);
		i++;
	}

	getTransitions(turingMachine, tran, nrTran);
	while(turingMachine->terminare != 1 && turingMachine->terminare != -1) {
		verificare(turingMachine, myTape);
	}

	if(turingMachine->terminare == 1)
		fprintf(fout, "%s", myTape->banda);

	if(turingMachine->terminare == -1)
		fprintf(fout, "Se agata!");
	
	fclose(fp);
	fclose(fptr);
	fclose(fout);
	return 0;
}