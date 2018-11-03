import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private Symbol lookAhead;
    private Iterator<Symbol> symbols;

    public Parser(Iterator<Symbol> symbols){
        this.symbols = symbols;
    }

    public void beginParse(){
        lookAhead = symbols.next();
        varProgram();
        if (this.lookAhead.getType() != LexicalUnit.EOS){
            // consumed entirly?
            throw new RuntimeException();
        }
    }

    private void match(LexicalUnit unit) {
        if (lookAhead.getType() == unit){
            System.out.println("Lexical unit matched " + unit + "" + lookAhead);
/*            System.out.println("Stack");
            StackTraceElement[] e = Thread.currentThread().getStackTrace();
            for (int i = 0; i < e.length - 3; ++i){
                System.out.println("\t\t\t"+ e[i]);
            }
            System.out.println("End of stack");*/
            lookAhead = this.symbols.next();
        }
        else {
            throw new RuntimeException("have" + lookAhead.getType() + " excpect " + unit);
        }
    }

    private void varProgram() {
        match(LexicalUnit.BEGINPROG);
        match(LexicalUnit.PROGNAME);
        match(LexicalUnit.ENDLINE);
        varVariables();
        varCode();
        match(LexicalUnit.ENDPROG);
    }

    private void varVariables() {
        if (lookAhead.getType() == LexicalUnit.VARIABLES){
            match(LexicalUnit.VARIABLES);
            varVarList();
            match(LexicalUnit.ENDLINE);
        } // epsilon
    }

    private void varVarList() {
        match(LexicalUnit.VARNAME);
        varVarListPrim();
    }

    private void varVarListPrim() {
        if (lookAhead.getType() == LexicalUnit.COMMA){
            match(LexicalUnit.COMMA);
            match(LexicalUnit.VARNAME);
            varVarListPrim();
        } // epsilon
    }

    private void varCode() {
        switch (lookAhead.getType()){
            case VARNAME:
            case IF:
            case WHILE:
            case FOR:
            case PRINT:
            case READ:
                varInstruction();
                match(LexicalUnit.ENDLINE);
                varCode();
                break;
            default:
                ; // epsilon
        }

        
    }

    private void varInstruction() {
        switch (lookAhead.getType()){
            case VARNAME:
                varAssign();
                break;
            case IF:
                varIf();
                break;
            case WHILE:
                varWhile();
                break;
            case FOR:
                varFor();
                break;
            case PRINT:
                varPrint();
                break;
            case READ:
                varRead();
                break;
            default:
                /* error*/
                break;
        }
    }

    private void varAssign() {
        match(LexicalUnit.VARNAME);
        match(LexicalUnit.ASSIGN);
        varExprArith();
    }

    private void varExprArith() {
        varTerm();
        varExprArithPrim();
    }

    private void varExprArithPrim() {
        switch (lookAhead.getType()){
            case PLUS:
                match(LexicalUnit.PLUS);
                varTerm();
                varExprArithPrim();
                break;
            case MINUS:
                match(LexicalUnit.MINUS);
                varTerm();
                varExpListPrim();
            // epsilon
        }
    }

    private void varTerm() {
        varAtom();
        varTermPrim();
    }

    private void varTermPrim() {
        switch(lookAhead.getType()){
            case TIMES:
                match(LexicalUnit.TIMES);
                varAtom();
                varTermPrim();
                break;
            case DIVIDE:
                match(LexicalUnit.DIVIDE);
                varAtom();
                varTermPrim();
            // epsilon
        }
    }

    private void varAtom() {
        switch (lookAhead.getType()){
            case NUMBER:
                match(LexicalUnit.NUMBER);
                break;
            case VARNAME:
                match(LexicalUnit.VARNAME);
                break;
            case LPAREN:
                match(LexicalUnit.LPAREN);
                varExprArith();
                match(LexicalUnit.RPAREN);
                break;
            case MINUS:
                match(LexicalUnit.MINUS);
                varAtom();
                break;
            default:
                /*  error */
                break;
        }
    }

    private void varIf() {
        match(LexicalUnit.IF);
        match(LexicalUnit.LPAREN);
        varCond();
        match(LexicalUnit.RPAREN);
        match(LexicalUnit.THEN);
        match(LexicalUnit.ENDLINE);
        varCode();
        varIfSeq();
    }

    private void varIfSeq() {
        switch (lookAhead.getType()){
            case ELSE:
                match(LexicalUnit.ELSE);
                match(LexicalUnit.ENDLINE);
                varCode();
            case ENDIF:
                match(LexicalUnit.ENDIF);
                break;
            default:
                /* error */;
        }
    }

    private void varCond() {
        varAndCond();
        varAndCondPrim();
    }

    private void varCondPrim() {
        if (lookAhead.getType() == LexicalUnit.OR){
            match(LexicalUnit.OR);
            varAndCond();
            varCondPrim();
        } // espilon
    }

    private void varAndCond() {
        varSimpleCond();
        varAndCondPrim();
    }

    private void varAndCondPrim() {
        if (lookAhead.getType() == LexicalUnit.AND){
            match(LexicalUnit.AND);
            varSimpleCond();
            varAndCondPrim();
        } // espilon
    }

    private void varSimpleCond() {
        if (lookAhead.getType() == LexicalUnit.NOT){
            match(LexicalUnit.NOT);
            varSimpleCond();
        }
        else {
            varExprArith();
            varComp();
            varExprArith();
        }
    }

    private void varComp() {
        switch (lookAhead.getType()){
            case EQ:
                match(LexicalUnit.EQ);
                break;
            case LEQ:
                match(LexicalUnit.LEQ);
                break;
            case LT:
                match(LexicalUnit.LT);
                break;
            case GT:
                match(LexicalUnit.GT);
                break;
            case GEQ:
                match(LexicalUnit.GEQ);
                break;
            case NEQ:
                match(LexicalUnit.NEQ);
                break;
            default:
                // error
                break;
        }
    }

    private void varWhile() {
        match(LexicalUnit.WHILE);
        match(LexicalUnit.LPAREN);
        varCond();
        match(LexicalUnit.RPAREN);
        match(LexicalUnit.DO);
        match(LexicalUnit.ENDLINE);
        varCode();
        match(LexicalUnit.ENDWHILE);
    }

    private void varFor() {
        match(LexicalUnit.FOR);
        match(LexicalUnit.VARNAME);
        match(LexicalUnit.ASSIGN);
        varExprArith();
        match(LexicalUnit.TO);
        varExprArith();
        match(LexicalUnit.DO);
        match(LexicalUnit.ENDLINE);
        varCode();
        match(LexicalUnit.ENDFOR);
    }

    private void varPrint() {
        match(LexicalUnit.PRINT);
        match(LexicalUnit.LPAREN);
        varExpList();
        match(LexicalUnit.RPAREN);
    }

    private void varRead() {
        match(LexicalUnit.READ);
        match(LexicalUnit.LPAREN);
        varVarList();
        match(LexicalUnit.RPAREN);
    }

    private void varExpList() {
        varExprArith();
        varExpListPrim();
    }

    private void varExpListPrim() {
        if (lookAhead.getType() == LexicalUnit.COMMA){
            match(LexicalUnit.COMMA);
            varExprArith();
            varExpListPrim();
        } // espilon
    }

}