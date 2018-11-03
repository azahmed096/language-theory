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