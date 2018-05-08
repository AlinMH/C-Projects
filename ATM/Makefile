all: build

build: client server

server: server.o lib.o
	gcc -g server.o lib.o -o server

client: client.o lib.o
	gcc -g client.o lib.o -o client
	
.c.o:
	gcc -Wall -g -c $?
	
clean:
	rm -f *.o server client	
