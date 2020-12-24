import os

from graph_utils import create_graph, create_undirected_graph, create_graph_of_cliques
from graph_utils import moralize_graph, triangualate_graph
from graph_utils import bron_kerbosch, kruskal

from factor import FactorWrapper

EPS = 0.0001
TEST_DIR = 'test_networks'


def read_file(file_path):
    variables = []
    parents = {}
    probabilities = {}
    expected_results = []

    with open(file_path) as fp:
        n, m = [int(x) for x in next(fp).split()]
        req_vars = [{} for _ in range(m)]
        req_obs = [{} for _ in range(m)]

        for _ in range(n):
            line = next(fp).split(';')
            variable = line[0].strip()
            variables.append(variable)
            parents[variable] = line[1].split()
            probabilities[variable] = [float(i) for i in line[2].split()]

        for i in range(m):
            [vars, obs] = next(fp).split('|')

            for e in vars.split():
                (lhs, rhs) = e.split('=')
                req_vars[i][lhs] = int(rhs)

            for e in obs.split():
                (lhs, rhs) = e.split('=')
                req_obs[i][lhs] = int(rhs)

        for _ in range(m):
            expected_results.append(float(next(fp)))

    return variables, parents, probabilities, req_vars, req_obs, expected_results


if __name__ == '__main__':
    for filepath in os.listdir(TEST_DIR):
        variables, parents, probabilities, req_vars, req_obs, expected_results = read_file(TEST_DIR + '/' + filepath)
        G = create_graph(variables, parents)
        U = create_undirected_graph(G)
        H = moralize_graph(U, parents)
        H_star = triangualate_graph(H)

        max_cliques = []
        bron_kerbosch(H_star, [], H_star.get_var_names(), [], max_cliques)
        C = create_graph_of_cliques(max_cliques)
        T = kruskal(C, max_cliques)
        factor_wrapper = FactorWrapper(variables, parents, probabilities, T)

        for ro, rv, er in list(zip(req_obs, req_vars, expected_results)):
            result = factor_wrapper.query(ro, rv)
            if not result:
                print('Not implemented')
                continue

            if abs(result - er) < EPS:
                print("Correct!")
            else:
                print("Wrong!")

        print("\n============================")
