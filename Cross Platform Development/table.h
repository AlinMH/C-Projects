#ifndef HASH_H
#define HASH_H

#include <stdio.h>
#include <stdlib.h>
#include "list.h"

#define HALVE 1
#define DOUBLE 2

struct hash {
	List** buckets;
	unsigned int size;	
};

typedef struct hash Hashtable;

Hashtable *create_table (unsigned int size);
int destroy_table (Hashtable** table);
int add_to_table (Hashtable* table, char* word);
int remove_from_table (Hashtable* table, char* word);
int print_bucket (Hashtable* table, unsigned int index, char* output_file);
int print_table (Hashtable* table, char* output_file);
int find_in_table (Hashtable* table, char* word, char* output_file);
int table_clear (Hashtable* table);
int table_resize (Hashtable* table, int mode);

void execute_command (Hashtable* table, char* line);
#endif