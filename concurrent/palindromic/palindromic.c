#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>

int main(const int argc, const char ** argv) {
	if(argc != 2) {
		fprintf(stderr, "Usage ./palindromic num_threads\n");
		exit(-1);
	}
	int w = atoi(argv[1]);
	printf("Num workers: %d\n", w);
}
