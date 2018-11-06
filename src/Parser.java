import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
    private Symbol lookAhead;
    private Iterator<Symbol> symbols;
    private List<String> rules;

    public Parser(Iterator<Symbol> symbols){
        this.symbols = symbols;
    }

    private void rule(int number){
        rules.add(Integer.toString(number));
    }

    public ParseTree beginParse(){
        rules = new ArrayList<>();
       // System.out.println(EPS.isEpsilon() + "epslion?");
        lookAhead = symbols.next();
        ParseTree res = varProgram();// varProgram();
        //ParseTree.print(res, 0);
        System.out.println(String.join(" ", rules));
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

        throw new RuntimeException("have" + lookAhead.getType() + " excpect " + unit);

    }

    private ParseTree varProgram() {
        rule(1);
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
        if (lookAhead.getType() == LexicalUnit.VARIABLES){
            rule(2);
            return new ParseTree(
                "<Variables>",
                Arrays.asList(
                    match(LexicalUnit.VARIABLES),
                    varVarList(),
                    match(LexicalUnit.ENDLINE)
                )
            );
            
        } 
        rule(3);
        return getEpsilon("<Variables>");
    }

    private ParseTree getEpsilon(String from){
        List<ParseTree> sons = new ArrayList<>();
        sons.add(EPS);
        return new ParseTree(from, sons);
    }

    private ParseTree varVarList() {
        rule(4);
        return new ParseTree("<VarList>",
            Arrays.asList(match(LexicalUnit.VARNAME),
            varVarListPrim())
        );
    }

    private static ParseTree EPS = new ParseTree(new Symbol(LexicalUnit.EPSILON));

    private ParseTree varVarListPrim() {
        
        if (lookAhead.getType() == LexicalUnit.COMMA){
            rule(5);
            return new ParseTree(
                "<VarListPrim>",
                Arrays.asList(
                    match(LexicalUnit.COMMA),
                    match(LexicalUnit.VARNAME),
                    varVarListPrim()
                )
            );
        } else {
            rule(6);
            return getEpsilon("<VarListPrim>");
        }
    }

    private ParseTree varCode() {
        switch (lookAhead.getType()){
            case VARNAME:
            case IF:
            case WHILE:
            case FOR:
            case PRINT:
            case READ:
                rule(7);
                return new ParseTree("<Code>", Arrays.asList(
                    varInstruction(),
                    match(LexicalUnit.ENDLINE),
                    varCode()
                ));
        }
        rule(8);
        return getEpsilon("<Code>");
    }

    private ParseTree varInstruction() {
        switch (lookAhead.getType()){
            case VARNAME:
                rule(9);
                return new ParseTree("<Instruction>", Arrays.asList(varAssign()));
            case IF:
                rule(10);
                return new ParseTree("<Instruction>", Arrays.asList(varIf()));
            case WHILE:
                rule(11);
                return new ParseTree("<Instruction>", Arrays.asList(varWhile()));
            case FOR:
                rule(12);
                return new ParseTree("<Instruction>", Arrays.asList(varFor()));
            case PRINT:
                rule(13);
                return new ParseTree("<Instruction>", Arrays.asList(varPrint()));
            case READ:
                rule(14);
                return new ParseTree("<Instruction>", Arrays.asList(varRead()));
            default:
                throw new RuntimeException("...");
        }
        // return null;
    }

    private ParseTree varAssign() {
        rule(15);
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
        rule(16);
        ArrayList sons = new ArrayList();
        sons.add(varTerm());
        sons.add(varExprArithPrim());
        return new ParseTree(
            "<ExprArith>",
            sons
        );
    }

    private ParseTree varExprArithPrim() {
        LexicalUnit type = lookAhead.getType();
        ArrayList sons = new ArrayList();
        switch (type){
            case MINUS:
                rule(18);
                sons.add(match(type));
                sons.add(varTerm());
                sons.add(varExprArithPrim());
                break;
            case PLUS:
                rule(17);
                sons.add(match(type));
                sons.add(varTerm());
                sons.add(varExprArithPrim());
                break;
            default:
                rule(19);
                return getEpsilon("<ExprArithPrim>");
                
        }
        return new ParseTree(
                    "<ExprArithPrim>",
                    sons
        );
    }

    private ParseTree varTerm() {
        ArrayList sons = new ArrayList();
        rule(20);
        sons.add(varAtom());
     
            sons.add(varTermPrim());
        
        return new ParseTree(
            "<Term>",
            sons
        );
    }

    private ParseTree varTermPrim() {
        LexicalUnit type = lookAhead.getType();
        ArrayList<ParseTree> sons = new ArrayList();
        switch (type){
            case DIVIDE:
                rule(22);
                break;
            case TIMES:
                rule(21);
                break;
            default:
                rule(23);
                return getEpsilon("<TermPrim>");
        }
        sons.add(match(type));
        sons.add(varAtom());
        sons.add(varTermPrim());
        return new ParseTree(
            "<TermPrim>",
            sons
        );
    }

    private ParseTree varAtom() {
        LexicalUnit type = lookAhead.getType();
        List<ParseTree> sons = null;
        switch (type){
            case VARNAME:
                rule(25);
                sons = Arrays.asList(match(type));
                break;
            case NUMBER:
                rule(24);
                sons = Arrays.asList(match(type));
                break;
            case LPAREN:
                rule(26);
                sons = Arrays.asList(
                    match(LexicalUnit.LPAREN),
                    varExprArith(),
                    match(LexicalUnit.RPAREN)
                );
                break;
            case MINUS:
                rule(27);
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

    private ParseTree varIf() {
        rule(28);
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
                rule(30);
                return new ParseTree("<IfSeq>", Arrays.asList(
                    match(LexicalUnit.ELSE),
                    match(LexicalUnit.ENDLINE),
                    varCode()
                ))
                ;
            case ENDIF:
                rule(29);
                return new ParseTree("<IfSeq>", Arrays.asList(
                    
                    match(LexicalUnit.ENDIF)
                ))
                ;
            default:
                throw new RuntimeException("...");
        }
    }

    private ParseTree varCond() {
        rule(31);
        return new ParseTree("<Cond>", Arrays.asList(varAndCond(),
        varAndCondPrim()));
    }

    private ParseTree varCondPrim() {
        
        if (lookAhead.getType() == LexicalUnit.OR){
            rule(32);
            return new ParseTree("<CondPrim>", Arrays.asList(
                match(LexicalUnit.OR),
                varAndCond(),
                varCondPrim()
            ));
        } 
        rule(33);
        return getEpsilon("<CondPrim>");
    }

    private ParseTree varAndCond() {
        rule(34);
        return new ParseTree("<AndCond>", Arrays.asList(varSimpleCond(),
        varAndCondPrim()));
    }

    private ParseTree varAndCondPrim() {
        
        if (lookAhead.getType() == LexicalUnit.AND){
            rule(35);
            return new ParseTree("<CondPrim>", Arrays.asList(
                match(LexicalUnit.AND),
                varSimpleCond(),
                varAndCondPrim()
            ));
        } // espilon
        rule(36);
        return getEpsilon("<AndCondPrim>");
    }

    private ParseTree varSimpleCond() {
        if (lookAhead.getType() == LexicalUnit.NOT){
            rule(38);
            return new ParseTree("<SimpleCond>", Arrays.asList(

                match(LexicalUnit.NOT),
                varSimpleCond()
            ));
        }
        else {
            rule(37);
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
                rule(39);
                new ParseTree("<Comp>", Arrays.asList(match(LexicalUnit.EQ)));
            case LEQ:
                rule(40);
                new ParseTree("<Comp>", Arrays.asList(match(LexicalUnit.LEQ)));
            case LT:
                rule(41);
                new ParseTree("<Comp>", Arrays.asList(match(LexicalUnit.LT)));
            case GT:
                rule(43);
                new ParseTree("<Comp>", Arrays.asList(match(LexicalUnit.GT)));
            case GEQ:
                rule(42);
                new ParseTree("<Comp>", Arrays.asList(match(LexicalUnit.GEQ)));
            case NEQ:
                rule(44);
                new ParseTree("<Comp>", Arrays.asList(match(LexicalUnit.NEQ)));
            default:
                throw new RuntimeException("...");

        }
    }

    private ParseTree varWhile() {
            rule(45);
        return new ParseTree("<While>", Arrays.asList(

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
        rule(46);
        return new ParseTree("<For>", Arrays.asList(

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
        rule(47);
        return new ParseTree("<Print>", Arrays.asList(

            match(LexicalUnit.PRINT),
            match(LexicalUnit.LPAREN),
            varExpList(),
            match(LexicalUnit.RPAREN)
        ));
    }

    private ParseTree varRead() {
        rule(48);
        return new ParseTree("<Read>", Arrays.asList(

            match(LexicalUnit.READ),
            match(LexicalUnit.LPAREN),
            varVarList(),
            match(LexicalUnit.RPAREN)
        ));
    }

    private ParseTree varExpList() {
        rule(49);
        return new ParseTree("<ExpList>", Arrays.asList(

            varExprArith(),
            varExpListPrim()
        ));
    }

    private ParseTree varExpListPrim() {
    
        if (lookAhead.getType() == LexicalUnit.COMMA){
            rule(50);
            return new ParseTree("<ExpListPrim>", Arrays.asList(

                match(LexicalUnit.COMMA),
                varExprArith(),
                varExpListPrim()
            ));
        }
        rule(51);
        return getEpsilon("<ExpListPrim>");
    }

}