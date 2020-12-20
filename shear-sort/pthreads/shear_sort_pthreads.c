#include "utils.h"

int main(int argc, char** argv) {
	if (argc != 2) {
		printf ("Use: %s <size>\n", argv[0]);
		return 0;
	}
	
	size = atoi (argv[1]);
	int i;
	
	matrix = calloc (size, sizeof (int *));
	for(i = 0; i < size; i++) {
		matrix[i] = calloc (size, sizeof (int));
	}

	init_random_matrix();
	int num_cores = sysconf(_SC_NPROCESSORS_ONLN);	
	printf("Number of cores = %d\n", num_cores);
	sort_matrix(num_cores);

	int check = check_sorted();
	if (check == 1) {
		printf ("Is sorted!\n");
	} else {
		printf ("It is not sorted!\n");
	}

	print_matrix();
	free_memory();
	return 0;
}
