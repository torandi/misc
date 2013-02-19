#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <string.h>
#include <math.h>
#include <omp.h>

#define WORDLIST_LOCATION "words"

#define MIN(X,Y) ((X) < (Y) ? (X) : (Y))

typedef struct {
	char* start;
	unsigned char length;
	unsigned char palindromic;
} word_t;

typedef struct {
	unsigned int start;
	unsigned int end;
} thread_data_t;

char * word_data;
unsigned int num_words;
word_t * wordlist;

void load_wordlist();
void free_wordlist();
void * worker(void * data);
int is_palindromic(word_t * w);

int main(const int argc, const char ** argv) {
	if(argc != 2) {
		fprintf(stderr, "Usage ./palindromic num_threads\n");
		exit(-1);
	}
	int num_workers = atoi(argv[1]);

	load_wordlist();

	double time = omp_get_wtime();

	size_t num_palindromic = 0;

	omp_set_num_threads(num_workers);

	word_t * w = NULL;

#pragma omp parallel private(w)
	{
#pragma omp for reduction(+: num_palindromic) schedule(guided, 50) 
		for(unsigned int i = 0; i < num_words; ++i) {
			w = wordlist + i;
			num_palindromic += is_palindromic(w);
		}
	}

	time = omp_get_wtime() - time;

	FILE * f = fopen("results", "w");

	for(unsigned int i=0; i<num_words; ++i) {
		if(wordlist[i].palindromic) {
			fprintf(f,"%s\n", wordlist[i].start);
		}
	}

	fclose(f);

	printf("%zd palindromes found\n", num_palindromic);

	printf("Runtime: %fs\n", time);

	free_wordlist();

	return 0;
}

void load_wordlist() {
	FILE * f = fopen(WORDLIST_LOCATION, "r");

	fseek(f, 0, SEEK_END);
	long size = ftell(f);
	word_data = (char*)malloc(size);

	fseek(f, 0, SEEK_SET);
	fread((void*) word_data, size, 1, f);

	fclose(f);

	num_words = 0;

	/* Count words and replace \n with \0 
	 * Also convert all words to lowercase
	 */
	for(int i=0; i<size; ++i) {
		if(word_data[i] == '\n') {
			++num_words;
			word_data[i] = 0;
		} else {
			word_data[i] = tolower(word_data[i]);
		}
	}

	/* Fill word-list */
	wordlist = (word_t*) malloc(num_words * sizeof(word_t));

	char * word = word_data;
	int word_index = 0;

	for(char* cur = word_data; cur < (word_data + size); ++cur) {
		if(*cur == '\0') {
			/* Create and push new word */
			word_t w;
			w.start = word;
			w.length = (unsigned char) (cur - word);
			w.palindromic = 0;
			wordlist[word_index++] = w;
			word = cur + 1;
		}
	}
}

void free_wordlist() {
	free((void*)word_data);
	free((void*)wordlist);
}

word_t * binary_search(char * find) {
	int start = 0;
	int end = num_words - 1;

	while(start < end) {
		int pos = start + (end - start) / 2;
		int cmp = strcmp(find, wordlist[pos].start);
		if(cmp == 0) {
			return wordlist + pos;
		} else if(cmp < 0) {
			end = pos - 1;
		} else {
			start = pos + 1;
		}
	}
	if(strcmp(find, wordlist[start].start) == 0) {
		return wordlist + start;
	}
	return NULL;
}

/*
 * Tests if the given word is a palindrom
 *
 * @returns the number of palindromes found 
 */
int is_palindromic(word_t * w) {
	char rev[w->length + 1];

	for(int i=0;i<w->length; ++i) {
		rev[i] = w->start[w->length - (i + 1)];
	}
	rev[w->length] = 0;

	if(w->length == 1 || strcmp(w->start, rev) == 0) {
		w->palindromic = 1;
		return 1;
	}

	word_t * match = binary_search(rev);

	if(match != NULL) {
		w->palindromic = 1;
		return 1;
	} else {
		return 0;
	}
}
