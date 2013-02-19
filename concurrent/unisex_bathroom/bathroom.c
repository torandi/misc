#include <pthread.h>
#include <semaphore.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

const int male = 0;
const int female = 1;

static const unsigned int work_time_avg[2] = {15, 15};
static const unsigned int work_time_var[2] = {5, 5};

static const unsigned int bathroom_time_avg[2] = {5, 10}; /* Females spend more time in the bathroom... */
static const unsigned int bathroom_time_var[2] = {3, 4};

sem_t lock, work_counter[2];
sem_t entry[2];

int in_bathroom[2] = {0, 0};
int in_queue[2] = {0, 0};
int working[2] = {0, 0};

sem_t change_lock;
int change = 1;

void * simulate(void * data);
void render(int cleanup);

int main(const int argc, const char ** argv) {
	if(argc != 2) {
		fprintf(stderr, "Usage ./bathroom num_people_of_each_sex\n");
		exit(-1);
	}

	srand(time(NULL));

	int num_people = atoi(argv[1]);
	pthread_t threads[num_people*2];

	/* Initialize semaphores */
	sem_init(&lock, 0, 1);
	sem_init(&(entry[0]), 0, 0);
	sem_init(&(entry[1]), 0, 0);
	sem_init(&(work_counter[0]), 0, 1);
	sem_init(&(work_counter[1]), 0, 1);
	sem_init(&change_lock, 0, 1);

	for(int i=0; i<num_people; ++i) {
		pthread_create(threads + i*2 + 0, NULL, simulate, (void*)&male);
		pthread_create(threads + i*2 + 1, NULL, simulate, (void*)&female);
	}

	render(0);
	while(1) {
		render(1);
		sleep(1);
	}

	return 0;
}

void work(int sex) {
	unsigned int time = work_time_avg[sex] + ( rand() % ( work_time_var[sex] * 2) - work_time_var[sex] );
	sem_wait(work_counter + sex);
	++working[sex];
	sem_post(work_counter + sex);
	sleep(time);
	sem_wait(work_counter + sex);
	--working[sex];
	sem_post(work_counter + sex);
}

void do_bathroom(int sex) {
	unsigned int time = bathroom_time_avg[sex] + ( rand() % ( bathroom_time_var[sex] * 2) - bathroom_time_var[sex] );
	sleep(time);
}

void aquire_bathroom(int sex) {
	int other = sex == male ? female : male;

	sem_wait(&lock);

	/* Don't enter the bathroom if there is someone of the other sex in there
	 * or if there is someone of the other sex in the queue (this prevents infinite waiting)
	 */
	if(in_bathroom[other] > 0 || in_queue[other] > 0) {
		++in_queue[sex];
		sem_post(&lock);
		sem_wait(entry + sex);
		--in_queue[sex];
	}

	++in_bathroom[sex];

	if(in_queue[sex] > 0) {
		/* Let next person of our sex into the bathroom */
		sem_post(entry + sex);
	} else {
		sem_post(&lock);
	}
}

void release_bathroom(int sex) {
	int other = sex == male ? female : male;

	sem_wait(&lock);
	--in_bathroom[sex];
	if(in_bathroom[sex] == 0 && in_queue[other] > 0) {
		sem_post(entry + other); /* Pass the lock sem to the waiting person (of the other sex) */
	} else if(in_bathroom[sex] == 0 && in_queue[sex] > 0) {
		sem_post(entry + sex); /* Pass the lock sem to the waiting person (of the same sex) */
	} else {
		sem_post(&lock); /* There are still people in the bathroom, just release the lock */
	}
}

void * simulate(void * data) {
	int sex = *((int*) data);

	const char * sex_str = (sex == 0) ? "male" : "female";

	while(1) {
		work(sex);

		sem_wait(&change_lock);
		change = 1;
		sem_post(&change_lock);

		printf("%s goes to the bathroom\n", sex_str);

		aquire_bathroom(sex);
		
		printf("%s enters the bathroom\n", sex_str);

		sem_wait(&change_lock);
		change = 1;
		sem_post(&change_lock);

		do_bathroom(sex);

		release_bathroom(sex);

		sem_wait(&change_lock);
		change = 1;
		sem_post(&change_lock);

		printf("%s goes back to work\n", sex_str);
	}

	return NULL;
}

void render(int cleanup) {
	if(change) {
		printf("\nIn Bathroom:\n");
		printf("Males    %d\n", in_bathroom[male]);
		printf("Females  %d\n", in_bathroom[female]);
		printf("--------------------\n");
		printf("In Queue:\n");
		printf("Males    %d\n", in_queue[male]);
		printf("Females  %d\n", in_queue[female]);
		printf("--------------------\n");
		printf("Working:\n");
		printf("Males    %d\n", working[male]);
		printf("Females  %d\n", working[female]);
		printf("\n");
		sem_wait(&change_lock);
		change = 0;
		sem_post(&change_lock);
	}
}
