#include "main.h"
#include <stdio.h>
#include <stdlib.h>

typedef struct cel Cell;
typedef struct coord Coord;

// aloca o celula pentru lista
Cell* alocaCell(int i, int j) {
	Cell* cell = (Cell*)malloc(sizeof(Cell));
	
	cell->prev = NULL;
	cell->next = NULL;
	cell->poz.line = i;
	cell->poz.col = j;

	return cell;
}

//adauga celula la sfarsitul listei
void addCell(struct snake* snake, Cell* el) {
	if(snake->coada == NULL && snake->cap == NULL) {
		snake->cap = el;
		snake->coada = el;
		return;
	}

	snake->coada->next = el;
	el->prev = snake->coada;
	el->next = NULL;
	snake->coada = el;
}

//adauga celula din coada in capul listei
void attachHead(struct snake* snake) {
	if(snake->cap == snake->coada)
		return;

	snake->coada->next = snake->cap;
	snake->cap->prev = snake->coada;

	snake->coada = snake->coada->prev;
	snake->coada->next = NULL;

	snake->cap = snake->cap->prev;
	snake->cap->prev = NULL;

}


//completeaza sarpele pornind din coordonatele capului
void completeSnake(struct snake* snake, int **world, int num_lines, int num_cols) {
	int crti = snake->head.line;;
	int crtj = snake->head.col;

	int auxi;
	int auxj;

	char prevDir = snake->direction;

	snake->cap = alocaCell(snake->head.line, snake->head.col);
	snake->coada = snake->cap;

	auxi = crti;
	auxj = crtj;

	while(1) {
		// daca directia precedenta este N, atunci nu se va mai cauta sus
		if(prevDir == 'N') {
			if(auxj == 0)
				auxj = num_cols - 1;
			else
				auxj--;
			//stanga
			if(world[auxi][auxj] == snake->encoding) {
				prevDir = 'E';
				Cell* cell = alocaCell(auxi, auxj);
				addCell(snake, cell);
				crti = auxi;
				crtj = auxj;
				continue;
			} else {
				auxi = crti;
				auxj = crtj;
			}

			if(auxj == num_cols - 1)
				auxj = 0;
			else
				auxj++;
			//dreapta
			if(world[auxi][auxj] == snake->encoding) {
				prevDir = 'V';
				Cell* cell = alocaCell(auxi, auxj);
				addCell(snake, cell);
				crti = auxi;
				crtj = auxj;
				continue;
			} else {
				auxi = crti;
				auxj = crtj;
			}

			if (auxi == num_lines - 1) {
				auxi = 0;
			}
			else {
				auxi++;
			}

			//jos
			if(world[auxi][auxj] == snake->encoding) {
				prevDir = 'N';
				Cell* cell = alocaCell(auxi, auxj);
				addCell(snake, cell);
				crti = auxi;
				crtj = auxj;
				continue;
			} else {
				auxi = crti;
				auxj = crtj;
			}
			break;

		//daca pozitia precedenta este S, nu se va mai cauta jos
		} else if (prevDir == 'S') {
			if(auxi == 0)
				auxi = num_lines - 1;
			else
				auxi--;
			//sus
			if(world[auxi][auxj] == snake->encoding) {
				prevDir = 'S';
				Cell* cell = alocaCell(auxi, auxj);
				addCell(snake, cell);
				crti = auxi;
				crtj = auxj;
				continue;
			} else {
				auxi = crti;
				auxj = crtj;
			}


			if(auxj == 0)
				auxj = num_cols - 1;
			else
				auxj--;
			//stanga
			if(world[auxi][auxj] == snake->encoding) {
				prevDir = 'E';
				Cell* cell = alocaCell(auxi, auxj);
				addCell(snake, cell);
				crti = auxi;
				crtj = auxj;
				continue;
			} else {
				auxi = crti;
				auxj = crtj;
			}

			if(auxj == num_cols - 1)
				auxj = 0;
			else
				auxj++;
			//dreapta
			if(world[auxi][auxj] == snake->encoding) {
				prevDir = 'V';
				Cell* cell = alocaCell(auxi, auxj);
				addCell(snake, cell);
				crti = auxi;
				crtj = auxj;
				continue;
			} else {
				auxi = crti;
				auxj = crtj;
			}
			break;

		//daca pozitia precedenta este E, nu se va mai cauta in dreapta
		} else if (prevDir == 'E') {
			if(auxi == 0)
				auxi = num_lines - 1;
			else
				auxi--;
			//sus
			if(world[auxi][auxj] == snake->encoding) {
				prevDir = 'S';
				Cell* cell = alocaCell(auxi, auxj);
				addCell(snake, cell);
				crti = auxi;
				crtj = auxj;
				continue;
			} else {
				auxi = crti;
				auxj = crtj;
			}

			if (auxi == num_lines - 1)
				auxi = 0;
			else
				auxi++;
			//jos
			if(world[auxi][auxj] == snake->encoding) {
				prevDir = 'N';
				Cell* cell = alocaCell(auxi, auxj);
				addCell(snake, cell);
				crti = auxi;
				crtj = auxj;
				continue;
			} else {
				auxi = crti;
				auxj = crtj;
			}

			if(auxj == 0)
				auxj = num_cols - 1;
			else
				auxj--;
			//stanga
			if(world[auxi][auxj] == snake->encoding) {
				prevDir = 'E';
				Cell* cell = alocaCell(auxi, auxj);
				addCell(snake, cell);
				crti = auxi;
				crtj = auxj;
				continue;
			} else {
				auxi = crti;
				auxj = crtj;
			}
			break;

		//daca pozitia precedenta este V, nu se va mai cauta in stanga
		} else if (prevDir == 'V') {
			if(auxi == 0)
				auxi = num_lines - 1;
			else
				auxi--;
			//sus
			if(world[auxi][auxj] == snake->encoding) {
				prevDir = 'S';
				Cell* cell = alocaCell(auxi, auxj);
				addCell(snake, cell);
				crti = auxi;
				crtj = auxj;
				continue;
			} else {
				auxi = crti;
				auxj = crtj;
			}

			if (auxi == num_lines - 1)
				auxi = 0;
			else
				auxi++;
			//jos
			if(world[auxi][auxj] == snake->encoding) {
				prevDir = 'N';
				Cell* cell = alocaCell(auxi, auxj);
				addCell(snake, cell);
				crti = auxi;
				crtj = auxj;
				continue;
			} else {
				auxi = crti;
				auxj = crtj;
			}

			if(auxj == num_cols - 1)
				auxj = 0;
			else
				auxj++;
			//dreapta
			if(world[auxi][auxj] == snake->encoding) {
				prevDir = 'V';
				Cell* cell = alocaCell(auxi, auxj);
				addCell(snake, cell);
				crti = auxi;
				crtj = auxj;
				continue;
			} else {
				auxi = crti;
				auxj = crtj;
			}
			//daca nu am gasit in cele 3 directii, atunci inseamna ca am dat de coada si ies din loop
			break;
		}
	}
}


int checkCollision(struct snake* snake, int** world) {
	return (world[snake->head.line][snake->head.col] != 0);
}

//calcularea noilor pozitii
void computeMoves(struct snake* snake,  int** world, int num_lines, int num_cols) {
	snake->lastTail = snake->coada->poz; //se pastreaza coada anterioara a sarpelui
	world[snake->coada->poz.line][snake->coada->poz.col] = 0; // se sterge coada, pentru ca sarpele o sa se mute

	snake->oldHead = snake->head; //se pastreaza ultimul cap al sarpelui
	attachHead(snake); //segmentul de coada se ataseaza capului


	//se calculeaza noua pozitia a capului
	if(snake->direction == 'N') {
		if(snake->head.line == 0) {
			snake->head.line = num_lines - 1;
		} else {
			snake->head.line--;
		}

	} else if(snake->direction == 'S') {
		if(snake->head.line == num_lines - 1) {
			snake->head.line = 0;
		} else {
			snake->head.line++;
		}
	} else if(snake->direction == 'V') {
		if(snake->head.col == 0) {
			snake->head.col = num_cols - 1;
		} else {
			snake->head.col--;
		}
	} else if(snake->direction == 'E') {
		if(snake->head.col == num_cols - 1) {
			snake->head.col = 0;
		} else {
			snake->head.col++;
		}
	}

	//actualizare cap
	snake->cap->poz.line = snake->head.line;
	snake->cap->poz.col = snake->head.col;
}


void run_simulation(int num_lines, int num_cols, int **world, int num_snakes,
	struct snake *snakes, int step_count, char *file_name) {
	int ok = 0;

	int i, j;

	//completarea serpilor se face paralel, deoarece este intependent de fiecare sarpe
	#pragma omp parallel for
	for(i = 0; i < num_snakes; i++)
		completeSnake(&snakes[i], world, num_lines, num_cols);

	for(i = 0; i < step_count; i++) {
		//calcularea noilor pozitii, tot paralel, din aceiasi cauza
		#pragma omp parallel for
		for(j = 0; j < num_snakes; j++) {
			computeMoves(&snakes[j], world, num_lines, num_cols);
		}

		//testarea de coliziune si mutarea se face in thread-ul master
		for(j = 0; j < num_snakes; j++) {
			if(!checkCollision(&snakes[j], world)) {
				world[snakes[j].head.line][snakes[j].head.col] = snakes[j].encoding;
			} else {
				ok = 1;
				break;
			}
		}
		//daca s-a intalnit o coliziune, trebuie refacuta harta la pasul anterior
		if(ok) {
			//pana la indicele j (indicele sarpelui care a facut coliziunea), se vor restaura capetele
			#pragma omp parallel for
			for(i = 0; i < j; i++) {
				world[snakes[i].head.line][snakes[i].head.col] = 0;
			}
			//pentru toti serpii se vor restaura pozitiile capetelor anterioare
			#pragma omp parallel for
			for(i = 0; i < num_snakes; i++) {
				snakes[i].head = snakes[i].oldHead;
				world[snakes[i].lastTail.line][snakes[i].lastTail.col] = snakes[i].encoding;
			}
			//daca s-a intalnit coliziune trebuie iesit din loop
			break;
		}

	}
}