import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
    private Symbol lookAhead;
    private Iterator<Symbol> symbols;

    public Parser(Iterator<Symbol> symbols){
        this.symbols = symbols;
    }

    public void beginParse(){
        lookAhead = symbols.next();
        ParseTree tree = new ParseTree(lookAhead);
        varProgram();
        if (this.lookAhead.getType() != LexicalUnit.EOS){
            // consumed entirly?
            throw new RuntimeException();
        }
        tree.toLaTeX();
    }

    private ParseTree match(LexicalUnit unit) {
        if (lookAhead.getType() == unit){
            System.out.println("Lexical unit matched " + unit + "" + lookAhead);
/*            System.out.println("Stack");
            StackTraceElement[] e = Thread.currentThread().getStackTrace();
            for (int i = 0; i < e.length - 3; ++i){
                System.out.println("\t\t\t"+ e[i]);
            }
            System.out.println("End of stack");*/
            return new ParseTree(unit);
            lookAhead = this.symbols.next();
        }

        throw new RuntimeException("have" + lookAhead.getType() + " excpect " + unit);

    }

    private ParseTree varProgram() {
        return new ParseTree(
            "<Program>",
            Arrays.asList(
                match(LexicalUnit.BEGINPROG),
                match(LexicalUnit.PROGNAME),
                match(LexicalUnit.ENDLINE),
                varVariables(),
                varCode(),
                match(LexicalUnit.ENDPROG)
            )
        );
    }

    private ParseTree varVariables() {
        ParseTree res = null;
        if (lookAhead.getType() == LexicalUnit.VARIABLES){
            res = new ParseTree(
                "<Variables>",
                Arrays.asList(
                    match(LexicalUnit.VARIABLES),
                    varVarList(),
                    match(LexicalUnit.ENDLINE)
                )
            );
            
        } // epsilon

        return res;
    }

    private ParseTree varVarList() {
        return new ParseTree("<VarList>",
            Arrays.asList(match(LexicalUnit.VARNAME),
            varVarListPrim())
        );
    }

    private ParseTree varVarListPrim() {
        ParseTree res = null;
        if (lookAhead.getType() == LexicalUnit.COMMA){
            res = new ParseTree(
                "<VarListPrim>",
                Arrays.asList(
                    match(LexicalUnit.COMMA),
                    match(LexicalUnit.VARNAME),
                    varVarListPrim()
                )
            );
        } // epsilon
        return res;
    }

    private ParseTree varCode() {
        switch (lookAhead.getType()){
            case VARNAME:
            case IF:
            case WHILE:
            case FOR:
            case PRINT:
            case READ:
                return new ParseTree("<Code>", Arrays.asList(
                    varInstruction(),
                    match(LexicalUnit.ENDLINE),
                    varCode()
                ));
        }
        return null;
    }

    private ParseTree varInstruction() {
        switch (lookAhead.getType()){
            case VARNAME:
                new ParseTree("<Instruction>", Arrays.asList(varAssign()));
                break;
            case IF:
                new ParseTree("<Instruction>", Arrays.asList(varIf()));
                break;
            case WHILE:
                new ParseTree("<Instruction>", Arrays.asList(varWhile()));
                break;
            case FOR:
                new ParseTree("<Instruction>", Arrays.asList(varFor()));
                break;
            case PRINT:
                new ParseTree("<Instruction>", Arrays.asList(varPrint()));
                break;
            case READ:
                new ParseTree("<Instruction>", Arrays.asList(varRead()));
                break;
            default:
                throw new RuntimeException("...");
        }
        return null;
    }

    private ParseTree varAssign() {
        return new ParseTree(
            "<Assign>",
            Arrays.asList(
                match(LexicalUnit.VARNAME),
                match(LexicalUnit.ASSIGN),
                varExprArith()
            )
        );
    }

    private ParseTree varExprArith() {
        return new ParseTree(
            "<ExprArith>",
            Arrays.asList(
                varTerm(),
                varExprArithPrim()
            )
        )
        ;
    }

    private ParseTree varExprArithPrim() {
        ParseTree res = null;
        switch (lookAhead.getType()){
            case PLUS:
                res = new ParseTree("ExprArithPrim", Arrays.asList(
                    match(LexicalUnit.PLUS),
                    varTerm(),
                    varExprArithPrim()
                ));
                break;
            case MINUS:
                res = new ParseTree("ExprArithPrim", Arrays.asList(
                    match(LexicalUnit.MINUS),
                    varTerm(),
                    varExprArithPrim()
                ));
            // epsilon
        }
        return res;
    }

    private ParseTree varTerm() {
        return new ParseTree("<Term>", Arrays.asList(varAtom(), varTermPrim()));
    }

    private ParseTree varTermPrim() {
        ParseTree res = null;
        switch(lookAhead.getType()){
            case TIMES:
                res = new ParseTree("<TermPrim>", Arrays.asList(
                    match(LexicalUnit.TIMES),
                    varAtom(),
                    varTermPrim()
                ));
                break;
            case DIVIDE:
                res = new ParseTree("<TermPrim>", Arrays.asList(
                    match(LexicalUnit.DIVIDE),
                    varAtom(),
                    varTermPrim()
                ));
            // epsilon
        }
        return res;
    }

    private ParseTree varAtom() {
        switch (lookAhead.getType()){
            case NUMBER:
                return new ParseTree("<Atom>", Arrays.asList(match(LexicalUnit.NUMBER)));
            case VARNAME:
                return new ParseTree("<Atom>", Arrays.asList(match(LexicalUnit.VARNAME)));
            case LPAREN:
                return new ParseTree("<Atom>", Arrays.asList(
                    match(LexicalUnit.LPAREN),
                    varExprArith(),
                    match(LexicalUnit.RPAREN)
                    ));
            case MINUS:
                return new ParseTree("<Atom>", Arrays.asList(
                    match(LexicalUnit.MINUS),
                    varAtom()
                    ));
            default:
                throw new RuntimeException("...");
        }
    }

    private ParseTree varIf() {
        return new ParseTree("<If>", Arrays.asList(match(LexicalUnit.IF),
        match(LexicalUnit.LPAREN),
        varCond(),
        match(LexicalUnit.RPAREN),
        match(LexicalUnit.THEN),
        match(LexicalUnit.ENDLINE),
        varCode(),
        varIfSeq()));
    }

    private ParseTree varIfSeq() {
        switch (lookAhead.getType()){
            case ELSE:
                return new ParseTree("<IfSeq>", Arrays.asList(
                    match(LexicalUnit.ELSE),
                    match(LexicalUnit.ENDLINE),
                    varCode()
                ))
                ;
            case ENDIF:
                return new ParseTree("<IfSeq>", Arrays.asList(
                    
                    match(LexicalUnit.ENDIF)
                ))
                ;
            default:
                throw new RuntimeException("...");
        }
    }

    private ParseTree varCond() {
        return new ParseTree("<Cond>", Arrays.asList(varAndCond(),
        varAndCondPrim()));
    }

    private ParseTree varCondPrim() {
        ParseTree res = null;
        if (lookAhead.getType() == LexicalUnit.OR){
            res = new ParseTree("<CondPrim>", Arrays.asList(
                match(LexicalUnit.OR),
                varAndCond(),
                varCondPrim()
            ));
        } // espilon
        return res;
    }

    private ParseTree varAndCond() {
        return new ParseTree("<AndCond>", Arrays.asList(varSimpleCond(),
        varAndCondPrim()));
    }

    private ParseTree varAndCondPrim() {
        ParseTree res = null;
        if (lookAhead.getType() == LexicalUnit.AND){
            res = new ParseTree("<CondPrim>", Arrays.asList(
                match(LexicalUnit.AND),
                varSimpleCond(),
                varAndCondPrim()
            ));
        } // espilon
        return res;
    }

    private ParseTree varSimpleCond() {
        if (lookAhead.getType() == LexicalUnit.NOT){
            return new ParseTree("<SimpleCond>", Arrays.asList(

                match(LexicalUnit.NOT),
                varSimpleCond()
            ))
        }
        else {
            return new ParseTree("<SimpleCond>", Arrays.asList(

                varExprArith(),
                varComp(),
                varExprArith()
            ))
            ;
        }
    }

    private ParseTree varComp() {
        switch (lookAhead.getType()){
            case EQ:
                new ParseTree("<Comp>", Arrays.asList(match(LexicalUnit.EQ)));
            case LEQ:
                new ParseTree("<Comp>", Arrays.asList(match(LexicalUnit.LEQ)));
            case LT:
                new ParseTree("<Comp>", Arrays.asList(match(LexicalUnit.LT)));
            case GT:
                new ParseTree("<Comp>", Arrays.asList(match(LexicalUnit.GT)));
            case GEQ:
                new ParseTree("<Comp>", Arrays.asList(match(LexicalUnit.GEQ)));
            case NEQ:
                new ParseTree("<Comp>", Arrays.asList(match(LexicalUnit.NEQ)));
            default:
                throw new RuntimeException("...");

        }
    }

    private ParseTree varWhile() {
        return ParseTree("<While>", Arrays.asList(

            match(LexicalUnit.WHILE),
            match(LexicalUnit.LPAREN),
            varCond(),
            match(LexicalUnit.RPAREN),
            match(LexicalUnit.DO),
            match(LexicalUnit.ENDLINE),
            varCode(),
            match(LexicalUnit.ENDWHILE)
        ));
    }

    private ParseTree varFor() {
        return ParseTree("<For>", Arrays.asList(

            match(LexicalUnit.FOR),
            match(LexicalUnit.VARNAME),
            match(LexicalUnit.ASSIGN),
            varExprArith(),
            match(LexicalUnit.TO),
            varExprArith(),
            match(LexicalUnit.DO),
            match(LexicalUnit.ENDLINE),
            varCode(),
            match(LexicalUnit.ENDFOR)
        ));
    }

    private ParseTree varPrint() {
        return ParseTree("<Print>", Arrays.asList(

            match(LexicalUnit.PRINT),
            match(LexicalUnit.LPAREN),
            varExpList(),
            match(LexicalUnit.RPAREN)
        ));
    }

    private ParseTree varRead() {
        return ParseTree("<Read>", Arrays.asList(

            match(LexicalUnit.READ),
            match(LexicalUnit.LPAREN),
            varVarList(),
            match(LexicalUnit.RPAREN)
        ));
    }

    private ParseTree varExpList() {
        return ParseTree("<ExpList>", Arrays.asList(

            varExprArith(),
            varExpListPrim()
        ));
    }

    private ParseTree varExpListPrim() {
        ParseTree res = null;
        if (lookAhead.getType() == LexicalUnit.COMMA){
            res = new ParseTree("<ExpListPrim>", Arrays.asList(

                match(LexicalUnit.COMMA),
                varExprArith(),
                varExpListPrim()
            ));
        } // espilon
        return res;
    }

}