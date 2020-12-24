num_threads = [2, 4, 8, 16];
time_serial = 6.1;
time_omp = [3.746, 2.823, 2.509, 2.44];
time_pthreads = [5.821416, 4.114692, 3.851149, 3.740918];
time_mpi = [3.541766, 3.040054, 2.817501, 2.925460];

figure;
plot(num_threads, time_omp, '-', num_threads, time_pthreads, '-', num_threads, time_mpi, '-');
hold on;
title('Time comparison')
xlabel('Number of threads');
ylabel('Time');
legend('OMP', 'Pthreads', 'MPI')


%% OMP
figure;
plot (num_threads, (1 ./ time_omp) * time_serial);
hold on;
title('OMP Speedup')
xlabel('Number of threads')
ylabel('Speedup')

figure;
plot (num_threads, ((1 ./ time_omp) * time_serial) ./ num_threads);
hold on;
title('OMP Efficiency')
xlabel('Number of threads')
ylabel('Efficiency')

%% PTHREADS
figure;
plot (num_threads, (1 ./ time_pthreads) * time_serial);
hold on;
title('Pthreads Speedup')
xlabel('Number of threads')
ylabel('Speedup')

figure;
plot (num_threads, ((1 ./ time_pthreads) * time_serial) ./ num_threads);
hold on;
title('Pthreads Efficiency')
xlabel('Number of threads')
ylabel('Efficiency')

%% MPI
figure;
plot (num_threads, (1 ./ time_mpi) * time_serial);
hold on;
title('MPI Speedup')
xlabel('Number of processes')
ylabel('Speedup')

figure;
plot (num_threads, ((1 ./ time_mpi) * time_serial) ./ num_threads);
hold on;
title('MPI Efficiency')
xlabel('Number of processes')
ylabel('Efficiency')