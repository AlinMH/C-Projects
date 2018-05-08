#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include "table.h"
#include "list.h"
#include "utils.h"
#include "hash.h"

Hashtable *create_table (unsigned int size)
{
	Hashtable *table;
	int i;

	DIE (size < 1, "Invalid size");

	table = malloc (sizeof(Hashtable));
	DIE (table == NULL, "Eroare malloc");

	table->size = size;

	table->buckets = malloc (sizeof(List*) * size);
	DIE (table->buckets == NULL, "Eroare malloc");

	for (i = 0; i < size; i++)
		table->buckets[i] = NULL;

	return table;
}

int destroy_table (Hashtable** table)
{
	table_clear (*table);
	free ((*table)->buckets);
	free (*table);

	return 1;
}

int add_to_table (Hashtable* table, char* word)
{
	List* cell, *p;
	char* aux;
	unsigned int index;

	if (word == NULL)
		return -1;

	index = hash (word, table->size);
	//printf ("idx : %d\n", index);

	if (table->buckets[index] == NULL) {
		aux = strdup (word);
		cell = malloc (sizeof(List));
		DIE (cell == NULL, "Eroare malloc");
		cell->info = aux;
		cell->next = NULL;

		table->buckets[index] = cell;
	} else {
		p = table->buckets[index];
		while (p != NULL) {
			if (!strcmp (word, p->info)) {
				/* Cuvantul deja exista */
				return 2;
			}

			if (p->next == NULL) {
				aux = strdup (word);
				cell = malloc (sizeof(List));
				DIE (cell == NULL, "Eroare malloc");
				cell->info = aux;
				cell->next = NULL;
				p->next = cell;
			}
			p = p->next;
		}
	}

	return 1;
}

int remove_from_table (Hashtable* table, char* word)
{
	unsigned int index;
	List *p, *cel_aux;

	if (word == NULL)
		return -1;

	index = hash (word, table->size);
	//printf ("\t -- idx : %d, size : %d -- \n", index, table->size);

	/* Cuvantul nu exista */
	if(table->buckets[index] == NULL)
		return 2;
	
	p = table->buckets[index];

	cel_aux = NULL;

	while (p != NULL) {
		if (!strcmp (word, p->info)) {
			free (p->info);
			if (cel_aux != NULL) {
				cel_aux->next = p->next;
				free (p);

			} else {
				/* Primul element */
				table->buckets[index] = p->next;
				free (p);
			}
			return 1;
		}
		cel_aux = p;
		p = p->next;
	}

	/* Cuvantul nu a fost gasit */
	return 2;
}

int print_bucket (Hashtable* table, unsigned int index, char* output_file)
{
	FILE *f;
	List *p;
	int ret;

	if (table->buckets[index] == NULL)
		return 1;

	if (output_file) {
		f = fopen(output_file, "a");
		DIE (f == NULL, "Eroare deschidere fisier");
	}

	p = table->buckets[index];
	
	for (; p != NULL; p = p->next) {
		if(output_file)
			fprintf(f, "%s ", p->info);
		else
			printf("%s ", p->info);
	}
	if (output_file) {
		fprintf(f, "\n");

		ret = fclose(f);
		DIE (ret < 0, "Eroare inchidere fisier");
	} else
		printf("\n");

	return 1;
}

int print_table (Hashtable* table, char* output_file)
{
	FILE *f;
	List *p;
	int i, ret;

	if (output_file) {
		f = fopen(output_file, "a");
		DIE (f == NULL, "Eroare deschidere fisier");
	}

	for (i = 0; i < table->size; i++) {
		p = table->buckets[i];

		if(p == NULL)
			continue;

		for (; p != NULL; p = p->next) {
			if (output_file) {
				fprintf (f, "%s ", p->info);
			} else {
				printf ("%s ", p->info);
			}
		}

		if (output_file)
			fprintf (f, "\n");
		else 
			printf ("\n");
	}

	if (output_file) {
		ret = fclose (f);
		DIE (ret < 0, "Eroare inchidere fisier");
	}

	return 1;
}

int find_in_table (Hashtable* table, char* word, char* output_file)
{
	FILE *f;
	List *p;
	unsigned int index;
	int ret;

	if (word == NULL)
		return -1;

	index = hash (word, table->size);

	if (output_file) {
		f = fopen (output_file, "a");
		DIE (f == NULL, "Eroare deschidere fisier");
	}

	if (table->buckets[index] == NULL) {
		if (output_file) {
			fprintf (f, "False\n");

			ret = fclose (f);
			DIE (ret < 0, "Eroare inchidere fisier");
		} else
			printf ("False\n");

		return 2;
	}

	p = table->buckets[index];

	while (p != NULL) {
		if (!strcmp (word, p->info)) {
			if (output_file) {
				fprintf (f, "True\n");

				ret = fclose (f);
				DIE (ret < 0, "Eroare inchidere fisier");	
			} else
				printf ("True\n");
			return 1;
		}
		p = p->next;
	}
	if (output_file) {
		fprintf (f, "False\n");

		ret = fclose (f);
		DIE (ret < 0, "Eroare inchidere fisier");
	} else
		printf ("False\n");

	return 2;
}

int table_clear (Hashtable* table)
{
	List *p, *cell_aux;
	int i;

	for (i = 0; i < table->size; i++) {
		p = table->buckets[i];

		if(p == NULL)
			continue;

		while (p != NULL) {
			cell_aux = p;
			p = p->next;
			cell_aux->next = NULL;
			free (cell_aux->info);
			free (cell_aux);
			cell_aux = NULL;
		}

		table->buckets[i] = NULL;
	}

	return 1;
}

int table_resize (Hashtable* table, int mode)
{
	unsigned int size;
	unsigned int i, old_size;
	List **buckets;
	List **old_buckets;
	List *p, *cell_aux;

	old_size = table->size;

	if (mode == DOUBLE)
		size = 2 * table->size;
	else
		size = table->size / 2;

	old_buckets = table->buckets;
	table->size = size;

	buckets = malloc (sizeof(List*) * size);
	DIE (buckets == NULL, "Eroare malloc");

	for (i = 0; i < size; i++) {
		buckets[i] = NULL;
	}

	table->buckets = buckets;

	for (i = 0; i < old_size; i++) {
		p = old_buckets[i];

		if (p == NULL)
			continue;

		for (; p != NULL; p = p->next)
			add_to_table (table, p->info);
	}

	for (i = 0; i < old_size; i++) {
		p = old_buckets[i];

		if(p == NULL) {
			continue;
		}

		while (p != NULL) {
			cell_aux = p;
			p = p->next;
			cell_aux->next = NULL;
			free (cell_aux->info);
			free (cell_aux);
			cell_aux = NULL;
		}
	}

	free (old_buckets);
	return 1;
}

void execute_command (Hashtable* table, char* line)
{
	char* tok, *aux_tok;
	int ret, i;
	tok = strtok (line, " \n");
	
	if (tok == NULL)
		return;

	if (!strcmp (tok, "add")) {
		tok = strtok (NULL, " \n");
		ret = add_to_table (table, tok);
		DIE (ret < 0, "Eroare in add");

	} else if (!strcmp (tok, "remove")) {
		tok = strtok (NULL, " \n");
		ret = remove_from_table (table, tok);
		DIE (ret < 0, "Eroare in remove");
	
	} else if (!strcmp (tok, "find")) {
		tok = strtok (NULL, " \n");
		aux_tok = strtok (NULL, " \n");
		ret = find_in_table (table, tok, aux_tok);
		DIE (ret < 0, "Eroare in find");
	
	} else if (!strcmp (tok, "clear")) {
		ret = table_clear (table);
		DIE (ret < 0, "Eroare in clear");
	
	} else if (!strcmp (tok, "print_bucket")) {
		tok = strtok (NULL, " \n");
		
		/* Testare input */
		for (i = 0; i < strlen (tok); i++) {
			DIE (!isdigit(tok[i]), "Lungimea nu este numar");
		}

		aux_tok = strtok (NULL, " \n");
		ret = print_bucket (table, atoi(tok), aux_tok);
		DIE (ret < 0, "Eroare in print_bucket");
	
	} else if (!strcmp (tok, "print")) {
		tok = strtok (NULL, " \n");
		ret = print_table (table, tok);
		DIE (ret < 0, "Eroare in print");
	
	} else if (!strcmp (tok, "resize")) {
		tok = strtok (NULL, " \n");
		DIE (tok == NULL, "Eroare resize");

		if ((ret = strcmp (tok, "double")) == 0)
			table_resize (table, DOUBLE);
		else if ((ret = strcmp (tok, "halve")) == 0)
			table_resize (table, HALVE);
	} else {
		perror ("Comanda invalida");
		exit (EXIT_FAILURE);
	}
}
