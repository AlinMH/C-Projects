from itertools import product
from copy import deepcopy

from factor_operations import multiply, reduce, condition_factors, sum_out, divide, Factor, IdentityFactor
from graph_utils import create_directed_graph


class FactorWrapper:
    def __init__(self, variables, parents, probabilities, T):
        self.__variables = variables
        self.__parents = parents
        self.__probabilities = probabilities
        self.__factors = None
        self.__T = T

        self.__init_factors()
        self.__assign_factors()
        self.__reduce_factors()
        self.__d_graph = create_directed_graph(self.__T)

    def __init_factors(self):
        self.__factors = []
        for v in self.__variables:
            _parents = self.__parents[v]
            if not _parents:
                factor = Factor(vars=[v],
                                values={(1,): self.__probabilities[v][0], (0,): 1 - self.__probabilities[v][0]})
            else:
                values = {}
                parent_values = list(product([0, 1], repeat=len(_parents)))
                i = 0

                for e in parent_values:
                    values[(1,) + e] = self.__probabilities[v][i]
                    values[(0,) + e] = 1 - self.__probabilities[v][i]
                    i += 1

                factor = Factor(vars=[v] + _parents, values=values)
            self.__factors.append(factor)

    def __assign_factors(self):
        def included():
            return all(var in list(clique_tuple) for var in factor.vars)

        self.__factors_assignment = {}
        assigned = {}
        for clique_tuple in self.__T.get_var_names():
            phis = []
            for factor in self.__factors:
                if not tuple(factor.vars) in assigned and included():
                    phis.append(factor)
                    assigned[tuple(factor.vars)] = True
            self.__factors_assignment[clique_tuple] = phis

    def __reduce_factors(self):
        for clique, factors in self.__factors_assignment.items():
            if len(factors) == 0:
                self.__factors_assignment[clique] = IdentityFactor
            else:
                self.__factors_assignment[clique] = reduce(factors)

    def query(self, req_obs, req_var):
        # Reducerea factorilor din fiecare nod care corespund obs Z = z (req_obs)
        def calculate_phi0():
            phi_0 = deepcopy(self.__factors_assignment)
            vertices = list(self.__factors_assignment.keys())
            result = condition_factors(list(self.__factors_assignment.values()), req_obs)
            for i in range(len(vertices)):
                phi_0[vertices[i]] = result[i]

            return phi_0

        # Sunt propagate mesaje de la frunze spre radacina
        # Fiecare nod isi actualizeaza factorul
        def calculate_phi1():
            phi_1 = deepcopy(phi_0)
            leaves = list(filter(lambda x: len(self.__d_graph.get_node(x).get_neighbours_names()) == 0,
                                 self.__d_graph.get_var_names()))
            recv = {}
            sent = {}

            # Initializarea listelor de mesaje trimise/primite
            for node_name in self.__d_graph.get_var_names():
                recv[node_name] = []
                sent[node_name] = []

            # Stiva de noduri care sunt gata sa trimita mesaje parintilor
            stack = leaves
            while stack != []:
                node_name = stack.pop()
                parent_name = self.__d_graph.get_node(node_name).parent
                if not parent_name:
                    break  # root

                parent_node = self.__d_graph.get_node(parent_name)
                others = [x for x in phi_1[node_name].vars if
                          (x not in list(set(list(node_name)) & set(list(parent_name))))]
                msg = deepcopy(phi_1[node_name])
                for other in others:
                    msg = sum_out(other, msg)
                sent[node_name].append(msg)
                recv[parent_name].append(msg)

                # Se verifica daca parintele este gata sa primeasca mesajul
                if len(recv[parent_name]) == len(parent_node.get_neighbours_names()):
                    phi_1[parent_name] = reduce([phi_1[parent_name]] + recv[parent_name])
                    stack.append(parent_name)

            return phi_1, deepcopy(sent)

        # Propagarea inversa a mesajelor
        # Cand un nod a primit mesaj de la parinte isi actualizeaza factorul si trimite mesaj copiilor
        def calculate_phi2():
            phi_2 = deepcopy(phi_1)
            root = list(filter(lambda x: self.__d_graph.get_node(x).parent == None,
                               self.__d_graph.get_var_names()))
            received = {}
            for node_names in self.__d_graph.get_var_names():
                received[node_names] = []

            stack = root
            while stack != []:
                id = stack.pop()
                neighbour_names = self.__d_graph.get_node(id).get_neighbours_names()
                if not neighbour_names:
                    continue

                for node_name in neighbour_names:
                    others = [x for x in phi_2[id].vars if (x not in list(set(list(id)) & set(list(node_name))))]
                    msg = deepcopy(phi_2[id])
                    msg = divide(msg, sent[node_name][0])
                    for other in others:
                        msg = sum_out(other, msg)
                    received[node_name].append(msg)

                # Se verifica daca copilul este gata sa primeasca mesajul
                for node_name in neighbour_names:
                    if len(received[node_name]):
                        phi_2[node_name] = multiply(phi_2[node_name], received[node_name][0])
                        stack.append(node_name)

            return phi_2

        # Calcularea probabilitatilor marginale (prin operatia sum_out)
        def inference():
            req_vars = [x for x in req_var.keys()]

            chosen_phi = None
            for _, phi in phi_2.items():
                if set(req_vars).issubset(set(list(phi.vars))):
                    chosen_phi = phi
                    break

            if not chosen_phi:
                return None

            final_phi = deepcopy(chosen_phi)
            for var in chosen_phi.vars:
                if var not in req_vars:
                    final_phi = sum_out(var, final_phi)
            return final_phi

        def compute_final_probability(last_phi):
            _sum = sum(last_phi.values.values())
            last_phi = Factor(vars=last_phi.vars, values={k: v / _sum for (k, v) in last_phi.values.items()})

            good_line = ()
            for var in last_phi.vars:
                good_line += (req_var[var],)
            for value, result in last_phi.values.items():
                if value == good_line:
                    return result
            return -1

        phi_0 = calculate_phi0()
        phi_1, sent = calculate_phi1()
        phi_2 = calculate_phi2()
        last_phi = inference()
        if last_phi:
            result = compute_final_probability(last_phi)
            return result
        return None
