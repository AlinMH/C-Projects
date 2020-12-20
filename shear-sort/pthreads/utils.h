#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <unistd.h>
#include <pthread.h>
#include <time.h>

int** matrix;
int size;
int num_lines_per_proc;

void init_random_matrix();
int compare_asc(const void* a, const void* b);
int compare_dsc(const void* a, const void* b);
void* sort_lines(void* start_line);
void* sort_columns(void* arg);
void sort_matrix(int num_cores);
void print_matrix();
int check_sorted();
void free_memory();