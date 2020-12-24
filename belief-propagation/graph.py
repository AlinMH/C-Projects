class Node:
    def __init__(self, var_name):
        self.__var_name = var_name
        self.__neighbours_dict = {}
        self.__parent = None

    def add_neighbour(self, var_name, cost=0):
        self.__neighbours_dict[var_name] = cost

    def remove_neighbour(self, var_name):
        self.__neighbours_dict.pop(var_name, None)

    def get_neighbours_names(self):
        return self.__neighbours_dict.keys()

    def get_var_name(self):
        return self.__var_name

    def get_cost(self, var_name):
        return self.__neighbours_dict[var_name]

    def remove_parent_if_exists(self, parent):
        if self.__parent == parent:
            self.__parent = None

    @property
    def parent(self):
        return self.__parent

    @parent.setter
    def parent(self, parent):
        self.__parent = parent


class Graph:
    def __init__(self):
        self.__nodes = {}

    def add_node(self, var_name):
        n = Node(var_name)
        self.__nodes[var_name] = n

    def remove_node(self, var_name):
        for neighbour_name in self.__nodes[var_name].get_neighbours_names():
            neighbour_node = self.get_node(neighbour_name)

            neighbour_node.remove_neighbour(var_name)
            neighbour_node.remove_parent_if_exists(var_name)
        self.__nodes.pop(var_name)

    def get_node(self, var_name):
        return self.__nodes[var_name]

    def get_nodes(self):
        return list(self.__nodes.values())

    def get_var_names(self):
        return list(self.__nodes.keys())

    def add_edge(self, src, dst, cost=0):
        self.__nodes[src].add_neighbour(dst, cost)

    def get_edges(self):
        return [(parent, child) for parent in self.__nodes.keys() for child in
                self.__nodes[parent].get_neighbours_names()]

    def print_graph(self):
        for n in self.__nodes.values():
            print(str(n.get_var_name()) + " neighbours:" + str(list(n.get_neighbours_names())) + " parent:", n.parent)
