#ifndef LIST_H
#define LIST_H

#include <stdio.h>
#include <stdlib.h>

struct cel {
	struct cel* next;
	char* info;	
};

typedef struct cel List;
#endif