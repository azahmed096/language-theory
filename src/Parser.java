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
    
     */
    public Parser(Iterator<Symbol> symbols) {
        this.symbols = symbols;
    }

    /**
     * Should be called when a rule is applied
     * 
     * @param number number of the production
    
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
    
     */
    public ParseTree beginParse(Config config) throws IOException {
        rules = new ArrayList<Integer>();
        lookAhead = symbols.next();
        ParseTree res = varProgram();

        List display = rules;
        // default separator is space
        String sep = " ";

        if (config.isVerbose()) {
            display = new ArrayList();
            for (int rule: rules){
                display.add(rule + " " + Grammar.rule(rule));
            }
            sep = "\n";
        }

        if (config.hasTexFile()) {
            OutputStreamWriter stream = config.getTexStream();
            stream.write(res.toLaTeX());
            stream.close();
        }

        System.out.println(Java8util.Stringjoin(sep, display));

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
            
             */
            ParseTree res = new ParseTree(lookAhead);
            //
            lookAhead = this.symbols.next();
            return res;
        }
        unexpectedTokenHelper(unit);
        return null;
    }

    /** Method for the variable <Program>
     * 
     */
    private ParseTree varProgram() {
        rule(1);
        return new ParseTree("<Program>", Arrays.asList(match(LexicalUnit.BEGINPROG), match(LexicalUnit.PROGNAME),
                match(LexicalUnit.ENDLINE), varVariables(), varCode(), match(LexicalUnit.ENDPROG)));
    }

    /** Method for the variable <Variables>
     * 
     */
    private ParseTree varVariables() {
        if (lookAhead.getType() == LexicalUnit.VARIABLES) {
            rule(2);
            return new ParseTree("<Variables>",
                    Arrays.asList(match(LexicalUnit.VARIABLES), varVarList(), match(LexicalUnit.ENDLINE)));

        }
        rule(3);
        return getEpsilon("<Variables>");
    }

    private ParseTree getEpsilon(String from) {
        return new ParseTree(from, Arrays.asList(EPS));
    }

    /** Method for the variable <VarList>
     * 
     */
    private ParseTree varVarList() {
        rule(4);
        return new ParseTree("<VarList>", Arrays.asList(match(LexicalUnit.VARNAME), varVarListPrim()));
    }

    private static ParseTree EPS = new ParseTree(new Symbol(LexicalUnit.EPSILON));

    /** Method for the variable <VarListPrim>
     * 
     */
    private ParseTree varVarListPrim() {

        if (lookAhead.getType() == LexicalUnit.COMMA) {
            rule(5);
            return new ParseTree("<VarListPrim>",
                    Arrays.asList(match(LexicalUnit.COMMA), match(LexicalUnit.VARNAME), varVarListPrim()));
        } else {
            rule(6);
            return getEpsilon("<VarListPrim>");
        }
    }

    /** Method for the variable <Code>
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
            return new ParseTree("<Code>", Arrays.asList(varInstruction(), match(LexicalUnit.ENDLINE), varCode()));
        }
        rule(8);
        return getEpsilon("<Code>");
    }

    /** Method for the variable <Instruction>
     * 
     */
    private ParseTree varInstruction() {
        switch (lookAhead.getType()) {
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
            unexpectedTokenHelper(LexicalUnit.VARNAME, LexicalUnit.IF, LexicalUnit.WHILE, LexicalUnit.FOR,
                    LexicalUnit.PRINT, LexicalUnit.READ);
        }
        return null;
    }

    /** Method for the variable <Assign>
     * 
     */
    private ParseTree varAssign() {
        rule(15);
        return new ParseTree("<Assign>",
                Arrays.asList(match(LexicalUnit.VARNAME), match(LexicalUnit.ASSIGN), varExprArith()));
    }

    /** Method for the variable <ExprArith>
     * 
     */
    private ParseTree varExprArith() {
        rule(16);
        ArrayList sons = new ArrayList();
        sons.add(varTerm());
        sons.add(varExprArithPrim());
        return new ParseTree("<ExprArith>", sons);
    }

    /** Method for the variable <ExprArithPrim>
     * 
     */
    private ParseTree varExprArithPrim() {
        LexicalUnit type = lookAhead.getType();
        ArrayList sons = new ArrayList();
        switch (type) {
        case PLUS:
            rule(17);
            break;
        case MINUS:
            rule(18);
            break;
        default:
            rule(19);
            return getEpsilon("<ExprArithPrim>");

        }
        sons.add(match(type));
        sons.add(varTerm());
        sons.add(varExprArithPrim());
        return new ParseTree("<ExprArithPrim>", sons);
    }

    /** Method for the variable <Term>
     * 
     */
    private ParseTree varTerm() {
        ArrayList sons = new ArrayList();
        rule(20);
        sons.add(varAtom());

        sons.add(varTermPrim());

        return new ParseTree("<Term>", sons);
    }

    /** Method for the variable <TermPrim>
     * 
     */
    private ParseTree varTermPrim() {
        LexicalUnit type = lookAhead.getType();
        ArrayList<ParseTree> sons = new ArrayList();
        switch (type) {
        case TIMES:
            rule(21);
            break;
        case DIVIDE:
            rule(22);
            break;
        default:
            rule(23);
            return getEpsilon("<TermPrim>");
        }
        sons.add(match(type));
        sons.add(varAtom());
        sons.add(varTermPrim());
        return new ParseTree("<TermPrim>", sons);
    }

    /** Method for the variable <Atom>
     * 
     */
    private ParseTree varAtom() {
        LexicalUnit type = lookAhead.getType();
        List<ParseTree> sons = null;
        switch (type) {
        case NUMBER:
            rule(24);
            sons = Arrays.asList(match(type));
            break;
        case VARNAME:
            rule(25);
            sons = Arrays.asList(match(type));
            break;
        case LPAREN:
            rule(26);
            sons = Arrays.asList(match(LexicalUnit.LPAREN), varExprArith(), match(LexicalUnit.RPAREN));
            break;
        case MINUS:
            rule(27);
            sons = Arrays.asList(match(LexicalUnit.MINUS), varAtom());
            break;
        default:
            unexpectedTokenHelper(LexicalUnit.NUMBER, LexicalUnit.VARNAME, LexicalUnit.LPAREN, LexicalUnit.MINUS);
        }
        return new ParseTree("<Atom>", sons);
    }

    /** Method for the variable <If>
     * 
     */
    private ParseTree varIf() {
        rule(28);
        return new ParseTree("<If>", Arrays.asList(match(LexicalUnit.IF), match(LexicalUnit.LPAREN), varCond(),
                match(LexicalUnit.RPAREN), match(LexicalUnit.THEN), match(LexicalUnit.ENDLINE), varCode(), varIfSeq()));
    }

    /** Method for the variable <IfSeq>
     * 
     */
    private ParseTree varIfSeq() {
        switch (lookAhead.getType()) {
        case ENDIF:
            rule(29);
            return new ParseTree("<IfSeq>", Arrays.asList(match(LexicalUnit.ENDIF)));
        case ELSE:
            rule(30);
            return new ParseTree("<IfSeq>", Arrays.asList(match(LexicalUnit.ELSE), match(LexicalUnit.ENDLINE),
                    varCode(), match(LexicalUnit.ENDIF)));
        default:
            unexpectedTokenHelper(LexicalUnit.ENDIF, LexicalUnit.ELSE);
            return null;
        }
    }

    /** Method for the variable <Cond>
     * 
     */
    private ParseTree varCond() {
        rule(31);
        return new ParseTree("<Cond>", Arrays.asList(varAndCond(), varCondPrim()));
    }

    /** Method for the variable <CondPrim>
     * 
     */
    private ParseTree varCondPrim() {

        if (lookAhead.getType() == LexicalUnit.OR) {
            rule(32);
            return new ParseTree("<CondPrim>", Arrays.asList(match(LexicalUnit.OR), varAndCond(), varCondPrim()));
        }
        rule(33);
        return getEpsilon("<CondPrim>");
    }

    /** Method for the variable <AndCond>
     * 
     */
    private ParseTree varAndCond() {
        rule(34);
        return new ParseTree("<AndCond>", Arrays.asList(varSimpleCond(), varAndCondPrim()));
    }

    /** Method for the variable <AndCondPrim>
     * 
     */
    private ParseTree varAndCondPrim() {

        if (lookAhead.getType() == LexicalUnit.AND) {
            rule(35);
            return new ParseTree("<CondPrim>",
                    Arrays.asList(match(LexicalUnit.AND), varSimpleCond(), varAndCondPrim()));
        } // espilon
        rule(36);
        return getEpsilon("<AndCondPrim>");
    }

    /** Method for the variable <SimpleCond>
     * 
     */
    private ParseTree varSimpleCond() {
        if (lookAhead.getType() != LexicalUnit.NOT) {
            rule(37);
            return new ParseTree("<SimpleCond>", Arrays.asList(varExprArith(), varComp(), varExprArith()));
        } else {
            rule(38);
            return new ParseTree("<SimpleCond>", Arrays.asList(match(LexicalUnit.NOT), varSimpleCond()));
        }
    }

    /** Method for the variable <Comp>
     * 
     */
    private ParseTree varComp() {
        switch (lookAhead.getType()) {
        case EQ:
            rule(39);
            return new ParseTree("<Comp>", Arrays.asList(match(LexicalUnit.EQ)));
        case LEQ:
            rule(40);
            return new ParseTree("<Comp>", Arrays.asList(match(LexicalUnit.LEQ)));
        case LT:
            rule(41);
            return new ParseTree("<Comp>", Arrays.asList(match(LexicalUnit.LT)));
        case GEQ:
            rule(42);
            return new ParseTree("<Comp>", Arrays.asList(match(LexicalUnit.GEQ)));
        case GT:
            rule(43);
            return new ParseTree("<Comp>", Arrays.asList(match(LexicalUnit.GT)));
        case NEQ:
            rule(44);
            return new ParseTree("<Comp>", Arrays.asList(match(LexicalUnit.NEQ)));
        default:
            unexpectedTokenHelper(LexicalUnit.EQ, LexicalUnit.LEQ, LexicalUnit.LT, LexicalUnit.GEQ, LexicalUnit.GT,
                    LexicalUnit.NEQ);
            return null; // never accessed
        }
    }

    /** Method for the variable <While>
     * 
     */
    private ParseTree varWhile() {
        rule(45);
        return new ParseTree("<While>",
                Arrays.asList(match(LexicalUnit.WHILE), match(LexicalUnit.LPAREN), varCond(), match(LexicalUnit.RPAREN),
                        match(LexicalUnit.DO), match(LexicalUnit.ENDLINE), varCode(), match(LexicalUnit.ENDWHILE)));
    }

    /** Method for the variable <For>
     * 
     */
    private ParseTree varFor() {
        rule(46);
        return new ParseTree("<For>",
                Arrays.asList(match(LexicalUnit.FOR), match(LexicalUnit.VARNAME), match(LexicalUnit.ASSIGN),
                        varExprArith(), match(LexicalUnit.TO), varExprArith(), match(LexicalUnit.DO),
                        match(LexicalUnit.ENDLINE), varCode(), match(LexicalUnit.ENDFOR)));
    }

    /** Method for the variable <Print>
     * 
     */
    private ParseTree varPrint() {
        rule(47);
        return new ParseTree("<Print>", Arrays.asList(match(LexicalUnit.PRINT), match(LexicalUnit.LPAREN), varExpList(),
                match(LexicalUnit.RPAREN)));
    }

    /** Method for the variable <Read>
     * 
     */
    private ParseTree varRead() {
        rule(48);
        return new ParseTree("<Read>", Arrays.asList(match(LexicalUnit.READ), match(LexicalUnit.LPAREN), varVarList(),
                match(LexicalUnit.RPAREN)));
    }

    /** Method for the variable <ExpList>
     * 
     */
    private ParseTree varExpList() {
        rule(49);
        return new ParseTree("<ExpList>", Arrays.asList(varExprArith(), varExpListPrim()));
    }

    /** Method for the variable <ExpListPrim>
     * 
     */
    private ParseTree varExpListPrim() {

        if (lookAhead.getType() == LexicalUnit.COMMA) {
            rule(50);
            return new ParseTree("<ExpListPrim>",
                    Arrays.asList(match(LexicalUnit.COMMA), varExprArith(), varExpListPrim()));
        }
        rule(51);
        return getEpsilon("<ExpListPrim>");
    }

}