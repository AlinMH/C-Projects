#include "utils.h"

int main(int argc, char** argv) {
	if (argc != 2){
		printf ("Use: %s <size>", argv[0]);
		return 0;
	}
	
	MPI_Init(NULL, NULL);
	int world_rank;
	MPI_Comm_rank(MPI_COMM_WORLD, &world_rank);
	int world_size;
	MPI_Comm_size(MPI_COMM_WORLD, &world_size);
  
	int size = atoi(argv[1]);
	
	int num_lines_per_proc = size / world_size;
	int remaining_lines = size % world_size;
	int* matrix;

	matrix = calloc(size * size, sizeof (int));
	int* local_matrix = calloc(num_lines_per_proc * size, sizeof(int));
	
	int num_elements = num_lines_per_proc * size;
	MPI_Scatter (matrix, num_elements, MPI_INT, 
				local_matrix, num_elements, MPI_INT, 
				0, MPI_COMM_WORLD);
				
	init_random_matrix(local_matrix, num_lines_per_proc, size);
	
	MPI_Gather (local_matrix, num_elements, MPI_INT, 
				matrix, num_elements, MPI_INT, 
				0, MPI_COMM_WORLD);

	if (world_rank == 0 && remaining_lines > 0) {	
		init_random_matrix(matrix, remaining_lines, size);
	}
	
	MPI_Barrier(MPI_COMM_WORLD);

	int k;
	for(k = 0; k <= ceil(log2(size)); k++){
		MPI_Scatter (matrix, num_elements, MPI_INT, 
					local_matrix, num_elements, MPI_INT, 
					0, MPI_COMM_WORLD);
		
		sort_lines(local_matrix, num_lines_per_proc, size);
		
		MPI_Gather(local_matrix, num_elements, MPI_INT, 
					matrix, num_elements, MPI_INT, 
					0, MPI_COMM_WORLD);
					
		if (world_rank == 0 && remaining_lines > 0){	
			sort_lines_start_finish(matrix, remaining_lines, size);
		}
		MPI_Barrier(MPI_COMM_WORLD);
		sort_columns(matrix, size);
	}
	
	MPI_Barrier(MPI_COMM_WORLD);
	MPI_Finalize();

	if (world_rank == 0) {	
		int check = check_sorted(matrix, num_lines_per_proc, size);
		if (check == 1){
			printf ("Is sorted!\n");
		} else {
			printf ("It is not sorted!\n");
		}
	}

	if (world_rank == 0){
		print_matrix(matrix, size, size);
	}
		
	return 0;
}
