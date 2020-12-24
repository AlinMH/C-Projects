from copy import deepcopy
from graph import Graph


def create_graph(variables, parents):
    G = Graph()

    for var in variables:
        G.add_node(var)

    for var, ps in parents.items():
        for parent in ps:
            G.add_edge(parent, var)

    return G


def create_undirected_graph(G):
    U = deepcopy(G)

    for parent, child in G.get_edges():
        U.add_edge(child, parent)
    return U


def moralize_graph(U, parents):
    H = deepcopy(U)

    parents_values = parents.values()
    for parent_v in parents_values:
        for parent1, parent2 in [(p1, p2) for p1 in parent_v for p2 in parent_v if p1 != p2]:
            H.add_edge(parent1, parent2)
    return H


def triangualate_graph(H):
    def remove_connected():
        while True:
            remove = False
            for node in H_copy.get_nodes():
                neighbours = node.get_neighbours_names()
                nodes_connected = True

                edges = H_copy.get_edges()
                for node1 in neighbours:
                    for node2 in neighbours:
                        if node1 != node2:
                            if (node1, node2) not in edges:
                                nodes_connected = False
                                break
                    if not nodes_connected:
                        break

                if nodes_connected:
                    remove = True
                    H_copy.remove_node(node.get_var_name())

            if not remove:
                break

    H_copy = deepcopy(H)
    H_star = deepcopy(H)

    while True:
        remove_connected()

        costs_dict = {}
        for node in H_copy.get_nodes():
            neighbours = node.get_neighbours_names()

            cost = 0
            edges = H_copy.get_edges()
            for node1 in neighbours:
                for node2 in neighbours:
                    if node1 != node2:
                        if (node1, node2) not in edges:
                            cost += 1
            costs_dict[node.get_var_name()] = cost

        if not costs_dict:
            break

        node_name_to_be_deleted = min(costs_dict, key=costs_dict.get)
        node_to_be_deleted = H_copy.get_node(node_name_to_be_deleted)

        neighbours = node_to_be_deleted.get_neighbours_names()

        edges = H_copy.get_edges()
        for node1 in neighbours:
            for node2 in neighbours:
                if node1 != node2:
                    if (node1, node2) not in edges:
                        H_copy.add_edge(node1, node2)
                        H_star.add_edge(node1, node2)

        H_copy.remove_node(node_name_to_be_deleted)

    return H_star


def bron_kerbosch(G, R, P, X, max_cliques):
    if not P and not X:
        max_cliques.append(tuple(R))

    for node_name in P[:]:
        nv = [name for name in G.get_node(node_name).get_neighbours_names()]
        nv = set(nv)

        bron_kerbosch(G, R + [node_name], list(set(P).intersection(nv)), list(set(X).intersection(nv)), max_cliques)
        P.remove(node_name)
        X.append(node_name)


def create_graph_of_cliques(maximal_cliques):
    C = []

    for c1 in maximal_cliques:
        for c2 in maximal_cliques:
            if c1 != c2:
                n_intersetions = len(set(c1).intersection(set(c2)))

                if n_intersetions > 0:
                    C.append((c1, c2, n_intersetions))

    C.sort(key=lambda c: c[2], reverse=True)
    return C


def kruskal(C, maximal_cliques):
    def find_set(sets, node):
        for i in range(len(sets)):
            for node_set in sets[i]:
                intersect = list(set(node_set).intersection(set(node)))

                if len(intersect) == len(node):
                    return i

    def union(sets, index1, index2):
        sets[index1] += sets[index2]
        sets.pop(index2)

    T = Graph()
    for clique in maximal_cliques:
        T.add_node(clique)

    sets = [[clique] for clique in maximal_cliques]
    added_edges = 0

    cliques_size = len(maximal_cliques)
    for c1, c2, _ in C:
        if added_edges == cliques_size:
            break

        i1 = find_set(sets, c1)
        i2 = find_set(sets, c2)

        if i1 != i2:
            T.add_edge(c1, c2)
            T.add_edge(c2, c1)

            union(sets, i1, i2)
            added_edges += 1

    return T


def create_directed_graph(T):
    d_graph = Graph()
    visited = {}
    for node_name in T.get_var_names():
        d_graph.add_node(node_name)
        visited[node_name] = False

    root = T.get_var_names()[-1]
    stack = [root]

    while stack != []:
        node_name = stack.pop()
        if not visited[node_name]:
            visited[node_name] = True
            for neighbour_name in T.get_node(node_name).get_neighbours_names():
                is_neighbour = node_name in d_graph.get_node(neighbour_name).get_neighbours_names()
                if not is_neighbour:
                    d_graph.add_edge(node_name, neighbour_name)
                    d_graph.get_node(neighbour_name).parent = node_name
                stack.append(neighbour_name)
    return d_graph
