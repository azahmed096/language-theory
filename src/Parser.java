import java.util.Iterator;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
    private Symbol lookAhead;
    private Iterator<Symbol> symbols;
    private List<Integer> rules;

    /**
     * 
     * @param symbols a stream of token, the last token should be of type EOS
     * 
     */
    public Parser(Iterator<Symbol> symbols) {
        this.symbols = symbols;
    }

    /**
     * Should be called when a rule is applied
     * 
     * @param number number of the production
     * 
     */
    private void rule(int number) {
        rules.add(number);
    }

    /**
     * Print rules numbers seperated by space except if the config is in verbose
     * mode, it separate on lines and print the rule string. Write latex file if
     * config specify it
     * 
     * @param config
     * @return a ParseTree derivated from symbols iterator.
     * @throws IOException
     * 
     */
    public ParseTree beginParse(Config config) throws IOException {
        rules = new ArrayList<Integer>();
        lookAhead = symbols.next();
        ParseTree res = varProgram();
        // new NodeVisitor().Visit(res);

        List display = rules;
        // default separator is space
        String sep = " ";

        if (config.isVerbose()) {
            display = new ArrayList();
            for (int rule : rules) {
                display.add(rule + " " + Grammar.rule(rule));
            }
            sep = "\n";
        }

        if (config.hasTexFile()) {
            OutputStreamWriter stream = config.getTexStream();
            stream.write(res.toLaTeX());
            stream.close();
        }

        // System.out.println(Java8util.Stringjoin(sep, display));

        if (this.lookAhead.getType() != LexicalUnit.EOS) {
            // consumed entirly?
            throw new RuntimeException();
        }
        return res;
    }

    private void unexpectedTokenHelper(LexicalUnit... units) {
        String ex_string;

        if (units.length > 1) {
            // initially we were using List.stream() but it is not available for java 1.6
            String[] verboses = new String[units.length];
            for (int i = 0; i < verboses.length; ++i) {
                verboses[i] = units[i].getVerbose();
            }
            ex_string = "one of :\n\t\t" + Java8util.Stringjoin("\n\t\t", Arrays.asList(verboses));
        } else {
            ex_string = units[0].getVerbose();
        }

        throw new UnexpectedTokenException(
                "\n\t" + "at line " + lookAhead.getLine() + " column " + lookAhead.getColumn() + "\n\texpected "
                        + ex_string + "\n\tbut found \"" + lookAhead.getValue() + "\"");
    }

    /**
     * Try to match the given lexical unit with the next token
     * 
     * @exception UnexpectedTokenException if the token does not correspond with
     *                                     input
     * @param unit expected token type
     * @return ParseTree with single terminal child
     * 
     */
    private ParseTree match(LexicalUnit unit) {
        if (lookAhead.getType() == unit) {
            // System.out.println("Match: "+unit);
            // System.out.println("Lexical unit matched " + unit + "" + lookAhead);
            /*
             * System.out.println("Stack"); StackTraceElement[] e =
             * Thread.currentThread().getStackTrace(); for (int i = 0; i < e.length - 3;
             * ++i){ System.out.println("\t\t\t"+ e[i]); }
             * System.out.println("End of stack");
             * 
             */
            ParseTree res = new ParseTree(lookAhead);
            //
            lookAhead = this.symbols.next();
            return res;
        }
        unexpectedTokenHelper(unit);
        return null;
    }

    /**
     * Method for the variable <Program>
     * 
     */
    private ParseTree varProgram() {
        rule(1);
        List<ParseTree> childs = new ArrayList<>(Arrays.asList(match(LexicalUnit.BEGINPROG), match(LexicalUnit.PROGNAME),
                match(LexicalUnit.ENDLINE)));
        ParseTree variables = varVariables();
        if (variables != null) {
            childs.add(variables);
        }
        ParseTree code = varCode();
        if (code != null) {
            childs.add(code);
        }
        childs.add(match(LexicalUnit.ENDPROG));
        return new ParseTree(1, childs);
    }

    /**
     * Method for the variable <Variables>
     * 
     */
    private ParseTree varVariables() {
        if (lookAhead.getType() == LexicalUnit.VARIABLES) {
            rule(2);
            return new ParseTree(2,
                    Arrays.asList(match(LexicalUnit.VARIABLES), varVarList(), match(LexicalUnit.ENDLINE)));

        }
        rule(3);
        return getEpsilon(3);
    }

    private ParseTree getEpsilon(int s){
        return null;
    }

    private ParseTree getEpsilon(String from) {
        return null;
        // return new ParseTree(from, Arrays.asList(EPS));
    }

    /**
     * Method for the variable <VarList>
     * 
     */
    private ParseTree varVarList() {
        rule(4);
        List<ParseTree> childs = new ArrayList<>(Arrays.asList(match(LexicalUnit.VARNAME)));
        ParseTree prim = varVarListPrim();
        if (prim != null) {
            childs.add(prim);
        }
        return new ParseTree(4, childs);
    }

    private static ParseTree EPS = new ParseTree(new Symbol(LexicalUnit.EPSILON));

    /**
     * Method for the variable <VarListPrim>
     * 
     */
    private ParseTree varVarListPrim() {

        if (lookAhead.getType() == LexicalUnit.COMMA) {
            rule(5);
            List<ParseTree> childs = new ArrayList<>(Arrays.asList(match(LexicalUnit.COMMA), match(LexicalUnit.VARNAME)));
            ParseTree prim = varVarListPrim();
            if (prim != null) {
                childs.add(prim);
            }
            return new ParseTree(5, childs);
        } else {
            rule(6);
            return getEpsilon("<VarListPrim>");
        }
    }

    /**
     * Method for the variable <Code>
     * 
     */
    private ParseTree varCode() {
        switch (lookAhead.getType()) {
        case VARNAME:
        case IF:
        case WHILE:
        case FOR:
        case PRINT:
        case READ:
            rule(7);
            List<ParseTree> childs = new ArrayList<>(Arrays.asList(varInstruction(), match(LexicalUnit.ENDLINE)));
            ParseTree code = varCode();
            if (code != null) {
                childs.add(code);
            }
            return new ParseTree(7, childs);
        }
        rule(8);
        return getEpsilon("<Code>");
    }

    /**
     * Method for the variable <Instruction>
     * 
     */
    private ParseTree varInstruction() {
        switch (lookAhead.getType()) {
        case VARNAME:
            rule(9);
            return new ParseTree(9, Arrays.asList(varAssign()));
        case IF:
            rule(10);
            return new ParseTree(10, Arrays.asList(varIf()));
        case WHILE:
            rule(11);
            return new ParseTree(11, Arrays.asList(varWhile()));
        case FOR:
            rule(12);
            return new ParseTree(12, Arrays.asList(varFor()));
        case PRINT:
            rule(13);
            return new ParseTree(13, Arrays.asList(varPrint()));
        case READ:
            rule(14);
            return new ParseTree(14, Arrays.asList(varRead()));
        default:
            unexpectedTokenHelper(LexicalUnit.VARNAME, LexicalUnit.IF, LexicalUnit.WHILE, LexicalUnit.FOR,
                    LexicalUnit.PRINT, LexicalUnit.READ);
        }
        return null;
    }

    /**
     * Method for the variable <Assign>
     * 
     */
    private ParseTree varAssign() {
        rule(15);
        return new ParseTree(15,
                Arrays.asList(match(LexicalUnit.VARNAME), match(LexicalUnit.ASSIGN), varExprArith()));
    }

    /**
     * Method for the variable <ExprArith>
     * 
     */
    private ParseTree varExprArith() {
        rule(16);
        ArrayList sons = new ArrayList();
        sons.add(varTerm());
        ParseTree prim = varExprArithPrim();
        if (prim != null) sons.add(prim);
        return new ParseTree(16, sons);
    }

    /**
     * Method for the variable <ExprArithPrim>
     * 
     */
    private ParseTree varExprArithPrim() {
        LexicalUnit type = lookAhead.getType();
        ArrayList sons = new ArrayList();
        int rule = 0;
        switch (type) {
        case PLUS:
            rule = 17;
            break;
        case MINUS:
            rule = 18;
            break;
        default:
            rule(19);
            return getEpsilon("<ExprArithPrim>");

        }
        rule(rule);
        sons.add(match(type));
        sons.add(varTerm());
        ParseTree prim = varExprArithPrim();
        if (prim != null) sons.add(prim);
        return new ParseTree(rule, sons);
    }

    /**
     * Method for the variable <Term>
     * 
     */
    private ParseTree varTerm() {
        ArrayList sons = new ArrayList();
        rule(20);
        sons.add(varAtom());
        ParseTree prim = varTermPrim();
        if (prim != null) sons.add(prim);

        return new ParseTree(20, sons);
    }

    /**
     * Method for the variable <TermPrim>
     * 
     */
    private ParseTree varTermPrim() {
        LexicalUnit type = lookAhead.getType();
        ArrayList<ParseTree> sons = new ArrayList();
        int rule = 0;
        switch (type) {
        case TIMES:
            rule = 21;
            break;
        case DIVIDE:
            rule = 22;
            break;
        default:
            rule = 23;
            return getEpsilon("<TermPrim>");
        }
        rule(rule);
        sons.add(match(type));
        sons.add(varAtom());
        ParseTree prim = varTermPrim();
        if (prim != null) sons.add(prim);
        return new ParseTree(rule, sons);
    }

    /**
     * Method for the variable <Atom>
     * 
     */
    private ParseTree varAtom() {
        LexicalUnit type = lookAhead.getType();
        List<ParseTree> sons = null;
        int rule = 0;
        switch (type) {
        case NUMBER:
            rule = 24;
            sons = Arrays.asList(match(type));
            break;
        case VARNAME:
            rule = 25;
            sons = Arrays.asList(match(type));
            break;
        case LPAREN:
            rule = 26;
            sons = Arrays.asList(
                match(LexicalUnit.LPAREN),
                varExprArith(),
                match(LexicalUnit.RPAREN)
            );
            break;
        case MINUS:
            rule = 27;
            sons = Arrays.asList(match(LexicalUnit.MINUS), varAtom());
            break;
        default:
            unexpectedTokenHelper(LexicalUnit.NUMBER, LexicalUnit.VARNAME, LexicalUnit.LPAREN, LexicalUnit.MINUS);
        }
        rule(rule);
        return new ParseTree(rule, sons);
    }

    /**
     * Method for the variable <If>
     * 
     */
    private ParseTree varIf() {
        rule(28);
        List<ParseTree> childs = new ArrayList<>(Arrays.asList(match(LexicalUnit.IF), match(LexicalUnit.LPAREN), varCond(),
                match(LexicalUnit.RPAREN), match(LexicalUnit.THEN), match(LexicalUnit.ENDLINE)));
        ParseTree code = varCode();
        if (code != null) {
            childs.add(code);
        }
        childs.add(varIfSeq());
        return new ParseTree(28, childs);
    }

    /**
     * Method for the variable <IfSeq>
     * 
     */
    private ParseTree varIfSeq() {
        switch (lookAhead.getType()) {
        case ENDIF:
            rule(29);
            return new ParseTree(29, Arrays.asList(match(LexicalUnit.ENDIF)));
        case ELSE:
            rule(30);
            List<ParseTree> childs = new ArrayList<>(Arrays.asList(match(LexicalUnit.ELSE), match(LexicalUnit.ENDLINE)));
            ParseTree code = varCode();
            if (code != null) {
                childs.add(code);
            }
            childs.add(match(LexicalUnit.ENDIF));
            return new ParseTree(30, childs);
        default:
            unexpectedTokenHelper(LexicalUnit.ENDIF, LexicalUnit.ELSE);
            return null;
        }
    }

    /**
     * Method for the variable <Cond>
     * 
     */
    private ParseTree varCond() {
        rule(31);
        List<ParseTree> childs = new ArrayList<>();
        childs.add(varAndCond());
        ParseTree prim = varCondPrim();
        if (prim != null) childs.add(prim);
        return new ParseTree(31, childs);
    }

    /**
     * Method for the variable <CondPrim>
     * 
     */
    private ParseTree varCondPrim() {

        if (lookAhead.getType() == LexicalUnit.OR) {
            rule(32);
            List<ParseTree> childs = new ArrayList<>(Arrays.asList(match(LexicalUnit.OR), varAndCond()));
            ParseTree prim = varCondPrim();
            if (prim != null) childs.add(prim);
            return new ParseTree(32, childs);
        }
        rule(33);
        return getEpsilon(33);
    }

    /**
     * Method for the variable <AndCond>
     * 
     */
    private ParseTree varAndCond() {
        rule(34);
        List<ParseTree> childs = new ArrayList<>();
        childs.add(varSimpleCond());
        ParseTree prim = varAndCondPrim();
        if (prim != null) childs.add(prim);
        return new ParseTree(34, childs);
    }

    /**
     * Method for the variable <AndCondPrim>
     * 
     */
    private ParseTree varAndCondPrim() {

        if (lookAhead.getType() == LexicalUnit.AND) {
            rule(35);
            List<ParseTree> childs = new ArrayList<>();
            childs.add(match(LexicalUnit.AND));
            childs.add(varSimpleCond());
            ParseTree prim = varAndCondPrim();
            if (prim != null) childs.add(prim);
            return new ParseTree(35, childs);
        } // espilon
        rule(36);
        return getEpsilon("<AndCondPrim>");
    }

    /**
     * Method for the variable <SimpleCond>
     * 
     */
    private ParseTree varSimpleCond() {
        if (lookAhead.getType() != LexicalUnit.NOT) {
            rule(37);
            return new ParseTree(37, Arrays.asList(varExprArith(), varComp(), varExprArith()));
        } else {
            rule(38);
            return new ParseTree(38, Arrays.asList(match(LexicalUnit.NOT), varSimpleCond()));
        }
    }

    /**
     * Method for the variable <Comp>
     * 
     */
    private ParseTree varComp() {
        switch (lookAhead.getType()) {
        case EQ:
            rule(39);
            return new ParseTree(39, Arrays.asList(match(LexicalUnit.EQ)));
        case LEQ:
            rule(40);
            return new ParseTree(40, Arrays.asList(match(LexicalUnit.LEQ)));
        case LT:
            rule(41);
            return new ParseTree(41, Arrays.asList(match(LexicalUnit.LT)));
        case GEQ:
            rule(42);
            return new ParseTree(42, Arrays.asList(match(LexicalUnit.GEQ)));
        case GT:
            rule(43);
            return new ParseTree(43, Arrays.asList(match(LexicalUnit.GT)));
        case NEQ:
            rule(44);
            return new ParseTree(44, Arrays.asList(match(LexicalUnit.NEQ)));
        default:
            unexpectedTokenHelper(LexicalUnit.EQ, LexicalUnit.LEQ, LexicalUnit.LT, LexicalUnit.GEQ, LexicalUnit.GT,
                    LexicalUnit.NEQ);
            return null; // never accessed
        }
    }

    /**
     * Method for the variable <While>
     * 
     */
    private ParseTree varWhile() {
        rule(45);
        List<ParseTree> childs = new ArrayList<>(Arrays.asList(match(LexicalUnit.WHILE), match(LexicalUnit.LPAREN), varCond(),
                match(LexicalUnit.RPAREN), match(LexicalUnit.DO), match(LexicalUnit.ENDLINE)));
        ParseTree code = varCode();
        if (code != null) {
            childs.add(code);
        }
        childs.add(match(LexicalUnit.ENDWHILE));
        return new ParseTree(45, childs);
    }

    /**
     * Method for the variable <For>
     * 
     */
    private ParseTree varFor() {
        rule(46);
        List<ParseTree> childs = new ArrayList<>(Arrays.asList(match(LexicalUnit.FOR), match(LexicalUnit.VARNAME),
                match(LexicalUnit.ASSIGN), varExprArith(), match(LexicalUnit.TO), varExprArith(), match(LexicalUnit.DO),
                match(LexicalUnit.ENDLINE)));
        ParseTree code = varCode();
        if (code != null) {
            childs.add(code);
        }
        childs.add(match(LexicalUnit.ENDFOR));
        return new ParseTree(46, childs);
    }

    /**
     * Method for the variable <Print>
     * 
     */
    private ParseTree varPrint() {
        rule(47);
        return new ParseTree(47, Arrays.asList(match(LexicalUnit.PRINT), match(LexicalUnit.LPAREN), varExpList(),
                match(LexicalUnit.RPAREN)));
    }

    /**
     * Method for the variable <Read>
     * 
     */
    private ParseTree varRead() {
        rule(48);
        return new ParseTree(48, Arrays.asList(match(LexicalUnit.READ), match(LexicalUnit.LPAREN), varVarList(),
                match(LexicalUnit.RPAREN)));
    }

    /**
     * Method for the variable <ExpList>
     * 
     */
    private ParseTree varExpList() {
        rule(49);
        List<ParseTree> childs = new ArrayList<>();
        childs.add(varExprArith());
        ParseTree prim = varExpListPrim();
        if (prim != null) childs.add(prim);
        return new ParseTree(49, childs);
    }

    /**
     * Method for the variable <ExpListPrim>
     * 
     */
    private ParseTree varExpListPrim() {

        if (lookAhead.getType() == LexicalUnit.COMMA) {
            rule(50);
            List<ParseTree> childs = new ArrayList<>();
            childs.add(match(LexicalUnit.COMMA));
            childs.add(varExprArith());
            ParseTree prim = varExpListPrim();
            if (prim != null) childs.add(prim);
            return new ParseTree(50, childs);
        }
        rule(51);
        return getEpsilon("<ExpListPrim>");
    }

}