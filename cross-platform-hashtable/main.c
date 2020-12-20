#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include "list.h"
#include "utils.h"
#include "table.h"

#define BUFSIZE 20000

int main(int argc, char const *argv[])
{
	Hashtable* table;
	FILE* f;
	char buffer[BUFSIZE];
	int i, ret;

	DIE (argc < 2, "Prea putini parametrii");

	for (i = 0; i < strlen (argv[1]); i++) {
		DIE (!isdigit(argv[1][i]), "Lungimea nu este numar");
	}

	table = create_table (atoi(argv[1]));

	if (argc == 2) {
		/* Se citeste de la tastatura */
		while (fgets (buffer, BUFSIZE, stdin) != NULL) {
			execute_command (table, buffer);
		}
	} else {
		for (i = 2; i < argc; i++) {
			f = fopen (argv[i], "r");
			DIE (f == NULL, "Eroare deschidere fisier");

			while (fgets (buffer, BUFSIZE, f) != NULL) {
				execute_command (table, buffer);
			}

			ret = fclose (f);
			DIE (ret < 0, "Eroare inchidere fisier");
		}
	}

	destroy_table(&table);
	return 0;
}