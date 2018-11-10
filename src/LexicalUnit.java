public enum LexicalUnit{
    BEGINPROG,
    PROGNAME("a program name"),
    ENDLINE,
    ENDPROG,
    VARIABLES,
    COMMA(","),
    VARNAME,
    ASSIGN(":="),
    NUMBER("a number"),
    LPAREN("("),
    RPAREN(")"),
    MINUS("-"),
    PLUS("+"),
    TIMES("*"),
    DIVIDE("/"),
    IF,
    THEN,
    ENDIF,
    ELSE,
    NOT,
    AND,
    OR,
    EQ("="),
    GEQ(">="),
    GT(">"),
    LEQ("<="),
    LT("<"),
    NEQ("<>"),
    WHILE,
    DO,
    ENDWHILE,
    FOR,
    TO,
    ENDFOR,
    PRINT,
    READ,
    EPSILON,
    EOS;
    private String verbose;
    LexicalUnit(){
        verbose = toString();
    }

    LexicalUnit(String verbose){
        this.verbose = verbose;
    }

    public String getVerbose(){
        return verbose;
    }
}
