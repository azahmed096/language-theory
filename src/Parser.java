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

    public ParseTree beginParse(){
        lookAhead = symbols.next();
        ParseTree res = varExprArith();// varProgram();
        //ParseTree.print(res, 0);
        System.out.println(res.toLaTeX());
        if (this.lookAhead.getType() != LexicalUnit.EOS){
            // consumed entirly?
            throw new RuntimeException();
        }
        return res;
    }

    private ParseTree match(LexicalUnit unit) {
        if (lookAhead.getType() == unit){
           // System.out.println("Lexical unit matched " + unit + "" + lookAhead);
/*            System.out.println("Stack");
            StackTraceElement[] e = Thread.currentThread().getStackTrace();
            for (int i = 0; i < e.length - 3; ++i){
                System.out.println("\t\t\t"+ e[i]);
            }
            System.out.println("End of stack");*/
            ParseTree res = new ParseTree(lookAhead);
            //
            lookAhead = this.symbols.next();
            return res;
        }
        else {
            throw new RuntimeException("have" + lookAhead.getType() + " excpect " + unit);
        }
    }

    private void varProgram() {
      /*  return ParseTree(
            "<Program>",
            Arrays.asList(
            match(LexicalUnit.BEGINPROG),
            match(LexicalUnit.PROGNAME),
            match(LexicalUnit.ENDLINE),
            varVariables(),
            varCode(),
            match(LexicalUnit.ENDPROG)
            )
        );*/
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

    private ParseTree varExprArith() {
        ArrayList sons = new ArrayList();
        sons.add(varTerm());
        ParseTree prim = varExprArithPrim();
        if (prim != null){
            sons.add(prim);
        }
        return new ParseTree(
            "<ExprArith>",
            sons
        );
    }

    private ParseTree varExprArithPrim() {
        LexicalUnit type = lookAhead.getType();
        switch (type){
            case MINUS:
            case PLUS:
                ArrayList sons = new ArrayList();
                sons.add(match(type));
                sons.add(varTerm());
                ParseTree prim = varExprArithPrim();
                if (prim != null){
                    sons.add(prim);
                }
                return new ParseTree(
                    "<ExprArithPrim>",
                    sons
                );
        }
        return null;
    }

    private ParseTree varTerm() {
        ArrayList sons = new ArrayList();
        sons.add(varAtom());
        ParseTree prim = varTermPrim();
        if (prim != null){
            sons.add(prim);
        }
        return new ParseTree(
            "<Term>",
            sons
        );
    }

    private ParseTree varTermPrim() {
        LexicalUnit type = lookAhead.getType();
        switch (type){
            case DIVIDE:
            case TIMES:
                ArrayList sons = new ArrayList();
                sons.add(match(type));
                sons.add(varAtom());
                ParseTree prim = varTermPrim();
                if (prim != null){
                    sons.add(prim);
                }
                return new ParseTree(
                    "<TermPrim>",
                    sons
                );
        }
        return null;
    }

    private ParseTree varAtom() {
        LexicalUnit type = lookAhead.getType();
        List<ParseTree> sons = null;
        switch (type){
            case VARNAME:
            case NUMBER:
                sons = Arrays.asList(match(type));
                break;
            case LPAREN:
                sons = Arrays.asList(
                    match(LexicalUnit.LPAREN),
                    varExprArith(),
                    match(LexicalUnit.RPAREN)
                );
                break;
            case MINUS:
                sons = Arrays.asList(
                    match(LexicalUnit.MINUS),
                    varAtom()
                );
                break;
            default:
                throw new RuntimeException("kfdksl");
        }
        return new ParseTree("<Atom>", sons);
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