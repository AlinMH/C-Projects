from collections import namedtuple
from copy import deepcopy

Factor = namedtuple("Factor", ["vars", "values"])
IdentityFactor = Factor(vars=[], values={(): 1})


def print_factor(phi, indent="\t"):
    line = " | ".join(phi.vars + ["ϕ(" + ",".join(phi.vars) + ")"])
    sep = "".join(["+" if c == "|" else "-" for c in list(line)])
    print(indent + sep)
    print(indent + line)
    print(indent + sep)
    for values, p in phi.values.items():
        print(indent + " | ".join([str(v) for v in values] + [str(p)]))
    print(indent + sep)


def multiply(f1, f2):
    # sanity check
    assert isinstance(f1, Factor) and isinstance(f2, Factor)

    all_variables = []
    common_vars = []
    for var in f1.vars:
        all_variables.append(var)
    for var in f2.vars:
        if var not in all_variables:
            all_variables.append(var)
        else:
            common_vars.append(var)
    values = {}
    for line1 in f1.values:
        for line2 in f2.values:
            if len(common_vars) == 0:
                line = line1 + line2
                val = f1.values[line1] * f2.values[line2]
                values[line] = val
            else:
                common_match = True
                for var in common_vars:
                    index1 = f1.vars.index(var)
                    index2 = f2.vars.index(var)
                    if line1[index1] != line2[index2]:
                        common_match = False
                        break

                if common_match:
                    line_result = []
                    for var in f1.vars:
                        line_result.append(line1[f1.vars.index(var)])
                    for var in f2.vars:
                        if var not in common_vars:
                            line_result.append(line2[f2.vars.index(var)])
                    values[tuple(line_result)] = f1.values[line1] * f2.values[line2]

    result = Factor(vars=all_variables, values=values)
    return result


def reduce(factors):
    if len(factors) == 1:
        return factors[0]

    multiplied_factor = factors[0]
    for factor in factors[1:]:
        multiplied_factor = multiply(multiplied_factor, factor)

    return multiplied_factor


def condition_factors(factor, Z):
    copy_factor = deepcopy(factor)
    for var in Z:
        for fact in copy_factor:
            if var not in fact.vars:
                continue
            index = fact.vars.index(var)
            deletable = []
            for v in fact.values:
                if v[index] != Z[var]:
                    deletable.append(v)
            for v in deletable:
                del fact.values[v]
    return copy_factor


def sum_out(var, factor):
    # sanity check
    assert isinstance(factor, Factor) and var in factor.vars
    values = {}
    delete_idx = factor.vars.index(var)

    vars = deepcopy(factor.vars)
    vars.pop(delete_idx)

    for key, value in factor.values.items():
        key_list = list(key)
        key_list.pop(delete_idx)
        key_tuple = tuple(key_list)
        if key_tuple not in values:
            values[key_tuple] = value
        else:
            values[key_tuple] += value

    return Factor(vars, values)


def divide(phi1, phi2):
    # sanity check
    assert isinstance(phi1, Factor) and isinstance(phi2, Factor)

    vars1 = deepcopy(phi1.vars)
    vars2 = deepcopy(phi2.vars)

    common_vars = list(filter(lambda x: x in vars1, vars2))
    all_vars = deepcopy(vars1)
    for v in vars2:
        if v not in all_vars:
            all_vars.append(v)

    values = {}
    for (vals1, func1) in phi1.values.items():
        for (vals2, func2) in phi2.values.items():
            matching_values = True

            for var in common_vars:
                if vals1[vars1.index(var)] != vals2[vars2.index(var)]:
                    matching_values = False

            if matching_values:
                res = ()
                for var in all_vars:
                    if var in vars1:
                        res += (vals1[vars1.index(var)],)
                    elif var in vars2:
                        res += (vals2[vars2.index(var)],)

                values[res] = 1.0 * func1 / func2

    return Factor(all_vars, values)
