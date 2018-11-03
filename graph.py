
G2 = {
    'S': (
        'A',
        'B'
    ),
    'A': (
        'aB',
        'bS',
        'b'
    ),
    'B': (
        'AB',
        'Ba'
    ),
    'C': (
        'AS',
        'b'
    )
}
"""
G2 = {
    'PROGRAM': {
        (['beginprog'], ['progname'],)
    },
    ''
}
"""
G = {
    'S': ('a', 'A'),
    'A': ('AB',),
    'B': ('b',)
}

G = {
    'S': (['alpha'], ['OPERATOR']),
    'OPERATOR': (['OPERATOR', 'BOOLEAN'], ),
    'BOOLEAN': (['false', 'true'], )
}
G = {
    'S': [['beginprog', 'progname', 'endline', 'VARIABLES', 'CODE', 'endprog']],
    'VARIABLES': [['variables', 'VARLIST', 'endline'], ['epsilone']],
    'VARLIST': [['varname', 'VARLIST'], ['varname']],
    'CODE': [['INSTRUCTION', 'endline', 'CODE'], ['epsilone']],
    'INSTRUCTION': [['ASSIGN'], ['IF'], ['WHILE'], ['FOR'], ['PRINT'], ['READ']],
    'ASSIGN': [['varname', 'equal', 'EXPRARITH']],
    'EXPRARITH': [['varname'], ['number'], ['open_par', 'EXPRARITH', 'close_par'], ['minus', 'EXPRARITH'], ['EXPRARITH', 'OP', 'EXPRARITH']],
    'OP': [['plus'], ['minus'], ['times'], ['divide']],
    'IF': [
        ['if', 'open_par', 'COND', 'close_par', 'then', 'endline', 'CODE', 'endif'],
        ['if', 'open_par', 'COND', 'close_par', 'then', 'endline', 'CODE', 'else', 'endline', 'CODE', 'endif'],
    ],
    'COND': [['COND', 'BINOP', 'COND'], ['not', 'SIMPLECOND'], ['SIMPLECOND']],
    'SIMPLECOND': [['EXPRARITH', 'COMP', 'EXPRARITH']],
    'BINOP': [['and'], ['or']],
    'COMP': [['eq'], ['ge'], ['gt'], ['le'], ['lt'], ['ne']],
    'WHILE': [['while', 'COND', 'do', 'endline', 'CODE', 'endwhile']],
    'FOR': [['for', 'varname', 'equal', 'EXPRARITH', 'to', 'EXPRARITH', 'do', 'CODE', 'endfor']],
    'PRINT': [['print', 'open_par', 'EXPLIST', 'close_par']],
    'READ': [['read', 'open_par', 'VARLIST', 'close_par']],
    'EXPLIST': [['EXPRARITH', 'comma', 'EXPLIST'], ['EXPRARITH']]
}


def remove_inaccessible(g: dict):
    visited = set()
    stack = ['S']
    while stack:
        k = stack.pop()
        visited.add(k)
        #

        for rules in g[k]:
            access = filter(str.isupper, rules)
            access = filter(lambda x: x not in visited, access)
            access = set(access)
            stack.extend(access)

    print("\nvisited", visited)
    remove_vars = g.keys() - visited
    print("\nUseless vars rules are", remove_vars)
    return {
        k: tuple(filter(lambda x: not (set(x) & remove_vars), v)) for k, v in g.items()
        if k not in remove_vars
    }


def remove_inaccessible_str(g: dict):
    visited = set()
    stack = ['S']
    while stack:
        k = stack.pop()
        visited.add(k)
        #

        for rules in g[k]:
            access = filter(str.isupper, ''.join(rules))
            access = filter(lambda x: x not in visited, access)
            access = set(access)
            stack.extend(access)
    print("visited", visited)
    remove_vars = g.keys() - visited
    print("Useless vars rules are", remove_vars)
    return {
        k: tuple(filter(lambda x: not (set(x) & remove_vars), v)) for k, v in g.items()
        if k not in remove_vars
    }


def remove_unproductive(g):
    old_vars = g.keys()
    old_g = g.copy()
    g = g.copy()
    # build terminals
    T = []
    for right_set in g.values():
        for right in right_set:
            T.extend(filter(str.islower, right))
    print("Terminaux", T)
    # start
    v = set(T)
    v.add('S')
    changed = True
    while changed:
        changed = []
        for var, right_set in g.items():
            for r in map(set, right_set):
                if r <= v:
                    changed.append(var)
                    break
        for var in changed:
            del g[var]
            v.add(var)
    vars = v - set(T)
    remove_vars = old_vars - vars
    for v in remove_vars:
        del old_g[v]
    return {
        k: tuple(filter(lambda x: not (set(x) & remove_vars), v)) for k, v in old_g.items()
        if k not in remove_vars
    }


def clean(g):
    from pprint import pprint
    print("Original: ")
    pprint(g)
    g = remove_unproductive(g)
    print("Productive only")
    pprint(g)
    g = remove_inaccessible(g)
    print("Accessible only")
    pprint(g)

clean(G)
print("\n" * 4)
# clean(G2)