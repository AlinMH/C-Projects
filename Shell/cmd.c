/**
 * Operating Systems 2013-2018 - Assignment 2
 *
 * Mihaila Alin-Florin, 334CB
 *
 */
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/wait.h>

#include <fcntl.h>
#include <unistd.h>

#include "cmd.h"
#include "utils.h"

#define READ		0
#define WRITE		1

/**
 *Functie care salveaza file descriptorii
 */
static void save_fds(int *stdin_copy, int *stdout_copy, int *stderr_copy)
{
	int ret;

	*stdin_copy = dup(STDIN_FILENO);
	DIE(*stdin_copy < 0, "Error dup");

	*stdout_copy = dup(STDOUT_FILENO);
	DIE(*stdout_copy < 0, "Error dup");

	*stderr_copy = dup(STDERR_FILENO);
	DIE(*stderr_copy < 0, "Error dup");

	ret = close(STDIN_FILENO);
	DIE(ret < 0, "Error close");

	ret = close(STDOUT_FILENO);
	DIE(ret < 0, "Error close");

	ret = close(STDERR_FILENO);
	DIE(ret < 0, "Error close");
}

/**
 * Restaureaza file descriptorii
 */
static void restore_fds(int *stdin_copy, int *stdout_copy, int *stderr_copy)
{
	int ret;

	ret = dup2(*stdin_copy, STDIN_FILENO);
	DIE(ret < 0, "Error dup2");

	ret = dup2(*stdout_copy, STDOUT_FILENO);
	DIE(ret < 0, "Error dup2");

	ret = dup2(*stderr_copy, STDERR_FILENO);
	DIE(ret < 0, "Error dup2");

	ret = close(*stdin_copy);
	DIE(ret < 0, "Error close");

	ret = close(*stdout_copy);
	DIE(ret < 0, "Error close");

	ret = close(*stderr_copy);
	DIE(ret < 0, "Error close");
}

/**
 * Redirecteaza stdin/stdout/stderr in fisierul dat ca parametru
 */
static void do_redirect(int filedes, const char *filename, int mode)
{
	int rc;
	int fd;

	if (filedes == STDIN_FILENO) {
		fd = open(filename, O_RDONLY, 0644);
	} else {
		if (mode == IO_REGULAR)
			fd = open(filename
				, O_WRONLY | O_CREAT | O_TRUNC, 0644);
		else
			fd = open(filename
				, O_WRONLY | O_CREAT | O_APPEND, 0644);
	}

	DIE(fd < 0, "Error open");

	rc = dup2(fd, filedes);
	DIE(rc < 0, "Error dup2");

	rc = close(fd);
	DIE(rc < 0, "Error close");
}

/**
 * Redirectarea in functie de parametrii de redirectare al comenzii.
 */
static void redirect_fds(simple_command_t *s)
{
	int ret;

	/* daca se cere redirectarea la stderr */
	if (s->err) {
		char *err_file = get_word(s->err);

		do_redirect(STDERR_FILENO, err_file, s->io_flags);
		free(err_file);
	}

	/* daca se cere redirectarea la stdin */
	if (s->in) {
		char *in_file = get_word(s->in);

		do_redirect(STDIN_FILENO, in_file, s->io_flags);
		free(in_file);
	}

	/* daca se cere redirectarea la stdout */
	if (s->out) {
		char *out_file = get_word(s->out);

		do_redirect(STDOUT_FILENO, out_file, s->io_flags);
		/* daca se cere si la stderr */
		if (s->err) {
			char *err_file = get_word(s->err);

			if (!strcmp(out_file, err_file)) {
				free(err_file);
				ret = dup2(STDOUT_FILENO, STDERR_FILENO);
				DIE(ret < 0, "Error dup2");
			} else {
				do_redirect(STDERR_FILENO
					, err_file, IO_OUT_APPEND);
				free(err_file);
			}
		}
		free(out_file);
	}
}

/**
 * Internal change-directory command.
 */
static bool shell_cd(word_t *dir)
{
	int ret = 0;
	char *path;

	/* Se extrage path-ul */
	path = get_word(dir);

	if (path != NULL)
		/* Daca path-ul nu este vid atunci se schimba directorul */
		ret = chdir(path);
	free(path);

	return ret;
}

/**
 * Functie de eliberare a argumentelor unei comenzi.
 */
static void free_argv(char **argv, int size)
{
	int i;

	for (i = 0; i < size; i++)
		free(argv[i]);

	free(argv);
}

/**
 * Internal exit/quit command.
 */
static int shell_exit(void)
{
	return SHELL_EXIT;
}

/**
 * Parse a simple command (internal, environment variable assignment,
 * external command).
 */
static int parse_simple(simple_command_t *s, int level, command_t *father)
{

	int size, ret, pid, wait_r, status;
	int stdin_copy, stdout_copy, stderr_copy;
	char **argv = get_argv(s, &size);
	char *verb = get_word(s->verb);

	/* Sanity checks */
	DIE(s == NULL, "Error: null command");
	DIE(father != s->up, "Error: Bad synthax tree");

	/* Daca am exit sau quit, returnez exit status-ul shell-ului */
	if (!strcmp(verb, "exit") || !(strcmp(verb, "quit"))) {
		free(verb);
		free_argv(argv, size);
		return shell_exit();
	/* Daca am cd, salvez descriptorii, redirectez,
	 * apelez cd si restaurez descriptorii
	 */
	} else if (!strcmp(verb, "cd")) {
		save_fds(&stdin_copy, &stdout_copy, &stderr_copy);
		redirect_fds(s);

		ret = shell_cd(s->params);
		restore_fds(&stdin_copy, &stdout_copy, &stderr_copy);

		free(verb);
		free_argv(argv, size);

		return ret;
	/* Daca next part nu e null, atunci inseamna ca am
	 * o comanda de a seta variabila de mediu
	 */
	} else if (s->verb->next_part != NULL) {
		const char *name = s->verb->string;
		const char *eq = s->verb->next_part->string;

		if (!strcmp(eq, "=")) {
			/* Folosesc get_word pentru a expanda valoarea,
			 * in caz ca e nevoie.
			 */
			char* value = get_word(s->verb->next_part->next_part);

			/* Se seteaza valoarea */
			ret = setenv(name, value, 1);
			free(verb);
			free(value);
			free_argv(argv, size);
			return ret;
		}
	}

	/* Se creaza procesul copil */
	pid = fork();

	switch (pid) {
	case -1:
		DIE(pid < 1, "fork");

	case 0:
		/* Se redirecteaza descriptorii in functie de comanda */
		redirect_fds(s);

		/* Se executa comanda in copil */
		execvp(verb, (char * const *) argv);
		/* Daca exec-ul da fail, atunci se va afisa
		 * mesajul de eroare si se va elibera memoria
		 */
		fprintf(stderr, "Execution failed for '%s'\n", verb);
		free(verb);
		free_argv(argv, size);
		exit(EXIT_FAILURE);

	default:
		/* Se asteapta procesul copil */
		wait_r = waitpid(pid, &status, 0);
		DIE(wait_r < 0, "Error waitpid");
		free(verb);
		free_argv(argv, size);

		if (WIFEXITED(status))
			return WEXITSTATUS(status);
		else
			return EXIT_FAILURE;
	}
}

/**
 * Process two commands in parallel, by creating two children.
 */
static bool do_in_parallel(command_t *cmd1, command_t *cmd2, int level,
		command_t *father)
{
	int pid1, pid2, ret, wait_r;
	int status1, status2;

	/* Se creaza primul copil */
	pid1 = fork();

	switch (pid1) {
	case -1:
		DIE(pid1 < 0, "Error fork");
	case 0:
		/* Procesul copil1 va efectual comanda 1 */
		ret = parse_command(cmd1, level + 1, father);
		exit(ret);

	default:
		break;
	}

	/* Se creaza al doilea copil */
	pid2 = fork();

	switch (pid2) {
	case -1:
		DIE(pid2 < 0, "Error fork");
	case 0:
		/* Procesul copil2 va efectual comanda 2 */
		ret = parse_command(cmd2, level + 1, father);
		exit(ret);

	default:
		break;
	}

	/* Se asteapta ambii copii */
	wait_r = waitpid(pid1, &status1, 0);
	DIE(wait_r < 0, "Error waitpid");

	wait_r = waitpid(pid2, &status2, 0);
	DIE(wait_r < 0, "Error waitpid");

	if (WIFEXITED(status1) && WIFEXITED(status2))
		return WEXITSTATUS(status1) & WEXITSTATUS(status2);
	else
		return EXIT_FAILURE;
}

/**
 * Run commands by creating an anonymous pipe (cmd1 | cmd2)
 */
static bool do_on_pipe(command_t *cmd1, command_t *cmd2, int level,
		command_t *father)
{
	int fd[2], pid1, pid2, ret, wait_r;
	int status1, status2;

	/* Se creaza pipe-ul */
	ret = pipe(fd);
	DIE(ret < 0, "Error pipe");

	/* Se creaza primul copil */
	pid1 = fork();

	switch (pid1) {
	case -1:
		DIE(pid1 < 0, "Error fork");
	case 0:
		/* Se inchide capul de citire */
		ret = close(fd[0]);
		DIE(ret < 0, "Error close");

		/* Se redirecteaza output-ul */
		ret = dup2(fd[1], STDOUT_FILENO);
		DIE(ret < 0, "Error dup2");

		ret = close(fd[1]);
		DIE(ret < 0, "Error close");

		/* Primul copil va efectua prima comanda */
		ret = parse_command(cmd1, level + 1, father);
		exit(ret);
	default:
		break;
	}

	/* Se creaza al doilea copil */
	pid2 = fork();

	switch (pid2) {
	case -1:
		DIE(pid2 < 0, "Error fork");
	case 0:
		/* Se inchide capul de scriere */
		ret = close(fd[1]);
		DIE(ret < 0, "Error close");

		/* Se redirecteaza inputul */
		ret = dup2(fd[0], STDIN_FILENO);
		DIE(ret < 0, "Error dup2");

		ret = close(fd[0]);
		DIE(ret < 0, "Error close");

		/* Al doilea copil va efectua a doua comanda */
		ret = parse_command(cmd2, level + 1, father);
		exit(ret);

	default:
		break;
	}

	/* Se inchid capetele pipe-ului */
	ret = close(fd[1]);
	DIE(ret < 0, "Error close");

	ret = close(fd[0]);
	DIE(ret < 0, "Error close");

	/* Se asteapta ambii copii */
	wait_r = waitpid(pid1, &status1, 0);
	DIE(wait_r < 0, "Error waitpid");

	wait_r = waitpid(pid2, &status2, 0);
	DIE(wait_r < 0, "Error waitpid");

	if (WIFEXITED(status2))
		return WEXITSTATUS(status2);
	else
		return EXIT_FAILURE;
}


/**
 * Parse and execute a command.
 */
int parse_command(command_t *c, int level, command_t *father)
{
	int ret;

	/* Sanity checks */
	DIE(c == NULL, "Error: null command");
	DIE(father != c->up, "Error: Bad synthax tree");

	/* Comanda simpla */
	if (c->op == OP_NONE) {
		ret = parse_simple(c->scmd, level + 1, c);
		return ret;
	}

	switch (c->op) {
	/* Comenzi secventiale: cmd1 ; cmd2 */
	case OP_SEQUENTIAL:
		parse_command(c->cmd1, level + 1, c);
		ret = parse_command(c->cmd2, level + 1, c);

		break;
	/* Comemzi paralele: cmd1 & cmd2 */
	case OP_PARALLEL:
		ret = do_in_parallel(c->cmd1, c->cmd2, level + 1, c);
		break;

	/* Comenzi conditionale non-zero: cmd1 || cmd2 */
	case OP_CONDITIONAL_NZERO:
		ret = parse_command(c->cmd1, level + 1, c);

		if (ret != 0)
			ret = parse_command(c->cmd2, level + 1, c);

		break;

	/* Comenzi conditionale zero: cmd1 && cmd2 */
	case OP_CONDITIONAL_ZERO:
		ret = parse_command(c->cmd1, level + 1, c);

		if (ret == 0)
			ret = parse_command(c->cmd2, level + 1, c);

		break;

	/* Comenzi care comunica printr-un pipe: cmd1 | cmd2 */
	case OP_PIPE:
		ret = do_on_pipe(c->cmd1, c->cmd2, level + 1, c);
		break;

	default:
		return SHELL_EXIT;
	}

	return ret;
}
