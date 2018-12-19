import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Generate a list of LLVM instructions
 * from a Program ParseTree
 */
public class CodeGenerator {
    private ArrayList<String> instructions = new ArrayList<String>();
    private List<String> variables;
    ContextManager contextManager;

    /**
     * @param tree Program tree
     * @return list of LLVM instructions
     */
    public List<String> generateProgram(ParseTree tree) {
        makeBuiltins();
        List<ParseTree> childs = tree.getChildren();
        if (childs.size() == 4) {
            emitMain(null);
        } else if (childs.size() == 5) {
            ParseTree beforeLast = childs.get(3);
            if (beforeLast.getRule() == 2) {
                variables = new VarListExtract(beforeLast).getAsStrings();
                emitMain(null);
            } else {
                assert beforeLast.getRule() == 7;
                emitMain(beforeLast);
            }
        } else {
            assert childs.size() == 6;
            ParseTree vars = tree.getChildren().get(3);
            variables = new VarListExtract(vars).getAsStrings();
            ParseTree code = tree.getChildren().get(4);
            emitMain(code);
        }
        return instructions;
    }

    /**
     * Build the scope context manager
     */
    private void buildContext() {
        contextManager = new ContextManager(instructions);
        if (variables != null){
            for (String name: variables){
                contextManager.declareVariable(name);
                // default initialization
                contextManager.assign(name, "0");
            }
        }
    }

    /**
     * Emit the main function with its body
     * @param tree <code> node
     */
    private void emitMain(ParseTree tree) {
        instructions.add("define i32 @main(){");
        buildContext();
        if (tree != null) {
            emitCode(tree);
        }
        instructions.add("ret i32 0\n}");
    }

    /**
     * Declare read and print functions
     */
    private void makeBuiltins() {
        instructions.addAll(BuiltIns.list);
    }

    /**
     * Emit llvm code corresponding to the tree
     * @param tree <code> tree
     */
    private void emitCode(ParseTree tree) {
        List<ParseTree> childs = tree.getChildren();
        emitInstruction(childs.get(0));
        if (childs.size() > 2) {
            emitCode(childs.get(2));
        }
    }

    /**
     * Emit an instruction
     * @param tree <instruction> tree
     */
    private void emitInstruction(ParseTree tree) {
        ParseTree child = tree.getChildren().get(0);
        switch (tree.getRule()) {
            case 9:
                emitAssign(child);
                break;
            case 10:
                emitIF(child);
                break;
            case 11:
                emitWhile(child);
                break;
            case 12:
                emitFor(child);
                break;
            case 13:
                emitPrint(child);
                break;
            case 14:
                emitRead(child);
                break;
        }
    }

    /**
     * emit assignation
     * @param tree <assign> tree
     */
    private void emitAssign(ParseTree tree) {
        ParseTree expr = tree.getChildren().get(2);
        String val = new Expression(new LCRSTree(expr), instructions, contextManager).getValue();
        String dest = tree.getChildren().get(0).getLabel().getValue().toString();
        contextManager.assign(dest, val);
        // store(contextManager.getVariable(dest), val);
    }

    /**
     * Emit a label
     */
    private void emitLabel(String label) {
        instructions.add("br label %" + label);
        instructions.add(label + ":");
    }

    /**
     * Emit conditional jump
     * @param condition bit condition
     * @param trueLabel label to jump if condition is true
     * @param falseLabel label to jump if condition is false
     */
    private void emitJump(String condition, String trueLabel, String falseLabel) {
        contextManager.newVar();
        instructions.add("br i1 " + condition + ", label %" + trueLabel + ", label %" + falseLabel);
    }

    /**
     * Emit a jump
     * @param label to jump to
     * 
     * The difference between this and emitLabel is the need
     * of incrementing the register counter
     */
    private void emitJump(String label) {
        contextManager.newVar();
        instructions.add("br label %" + label);
    }
    private int x = 0;

    /**
     * @return a unique suffixe to add to labels
     */
    private String getId() {
        // Can't depend on tree
        // the symbol of tree has lost information
        // of columns/line number
        String id = String.valueOf(x++);
        return id;
    }

    /**
     * for:
     *  if (x) {code_a} else {code_b}
     * gives:
     *  if x jump truelabel else falselabel
     *  truelabel: 
     *   code_a
     *   jump endlabel
     *  falselabel:
     *   code_b // if no else bloc is there, this line will not be there
     *  endlabel:
     */
    private void emitIF(ParseTree tree) {
        ParseTree condTree = tree.getChildren().get(2);
        String cond = new Expression(new LCRSTree(condTree), instructions, contextManager).getValue();
        String id = getId();
        // System.out.println("id:" + tree.getLabel());
        String trueLabel = "true" + id;
        String falseLabel = "false" + id;
        String endLabel = "end" + id;
        emitJump(cond, trueLabel, falseLabel);
        emitLabel(trueLabel);
        ParseTree codeTree = tree.getChildren().get(6);
        emitCode(codeTree);
        emitJump(endLabel);
        emitLabel(falseLabel);
        ParseTree ifSeq = tree.getChildren().get(7);
        if (ifSeq.getRule() == 30) {
            ParseTree codeTreeElse = ifSeq.getChildren().get(2);
            emitCode(codeTreeElse);
        }
        emitLabel(endLabel);
    }

    /**
     * for:
     *  while (x) {code_a}
     * gives:
     *  beginLabel:
     *   if x jump insideLabel else outsideLabel
     *  insideLabel:
     *   code_a
     *   jump beginLabel
     *  outsideLabel:
     */
    private void emitWhile(ParseTree tree) {
        String id = getId();
        String beginLabel = "begin" + id;
        String insideLabel = "inside" + id;
        String outsideLabel = "outside" + id;

        ParseTree condTree = tree.getChildren().get(2);
        ParseTree codeTree = tree.getChildren().get(6);

        emitLabel(beginLabel);
        String cond = new Expression(new LCRSTree(condTree), instructions, contextManager).getValue();
        emitJump(cond, insideLabel, outsideLabel);
        emitLabel(insideLabel);
        emitCode(codeTree);
        emitJump(beginLabel);
        emitLabel(outsideLabel);
    }

    private static final String INCREMENTER = "$increment";
    /**
     * for:
     *  FOR a := expr1 TO expr2 DO  {code_A} ENDFOR
     * gives:
     *   src = expr1
     *   targ = expr2
     *   if src < targ jump positive else negative
     *  positve:
     *   inc = 1
     *   jump beginLabel
     *  negative:
     *   inc = -1
     *  beginLabel
     *   targ += inc
     *  compareLabel
     *   if src == targ jump outsideLabel else insideLabel
     *  insideLabel:
     *   code_a
     *   src += inc
     *   jump beginLabel
     *  outsideLabel:
     */
    private void emitFor(ParseTree tree) {
        ParseTree src = tree.getChildren().get(3);
        ParseTree targ = tree.getChildren().get(5);
        ParseTree code = tree.getChildren().get(8);
        String id = getId();
        String varFor = tree.getChildren().get(1).getLabel().getValue().toString();

        // LABELS
        String positiveLabel = "positive" + id;
        String negativeLabel = "negative" + id;
        String beginLabel = "begin" + id;
        String compareLabel = "compare" + id;
        String insideLabel = "inside" + id;
        String outsideLabel = "outside" + id;

        // since varFor iterator is not initiialized
        // we cant use it so we are not yet in the new context
        String from = new Expression(new LCRSTree(src), instructions, contextManager).getValue();
        String innerBound = new Expression(new LCRSTree(targ), instructions, contextManager).getValue();
        String ascending = Expression.LessThan(from, innerBound, instructions, contextManager).getValue();

        // Entering context
        contextManager.EnterContext();
        contextManager.declareVariable(varFor);
        contextManager.declareVariable(INCREMENTER);

        contextManager.assign(varFor, from);


        emitJump(ascending, positiveLabel, negativeLabel);
        emitLabel(positiveLabel);
        contextManager.assign(INCREMENTER, "1");
        emitJump(beginLabel);
        emitLabel(negativeLabel);
        contextManager.assign(INCREMENTER, "-1");
        emitLabel(beginLabel);
        String outerBound = Expression.sum(innerBound, INCREMENTER, instructions, contextManager).getValue();
        emitLabel(compareLabel);
        String cond = Expression.eq(varFor, outerBound, instructions, contextManager).getValue();
        emitJump(cond, outsideLabel, insideLabel);
        emitLabel(insideLabel);
        emitCode(code);
        String temp = Expression.sum(varFor, INCREMENTER, instructions, contextManager).getValue();
        contextManager.assign(varFor, temp);
        emitJump(compareLabel);
        emitLabel(outsideLabel);
        contextManager.exitContext();
    }

    /**
     * Emit read function calls
     * @param tree <read> tree
     */
    private void emitRead(ParseTree tree) {
        List<String> variables = new VarListExtract(tree.getChildren().get(2)).getAsStrings();

        for (String variable: variables) {
            String temp = contextManager.newVar();
            instructions.add(temp + " = call i32 @readInt()");
            contextManager.assign(variable, temp);
        }
    }

    /**
     * Emit print function calls
     * @param tree <print> tree
     */
    private void emitPrint(ParseTree tree) {
        ParseTree explist = tree.getChildren().get(2);
        List<ParseTree> expressions = new VarListExtract(explist).getNodes();

        for (ParseTree exp: expressions) {
            String temp = new Expression(new LCRSTree(exp), instructions, contextManager).getValue();
            instructions.add("call void @println(i32 " + temp + ")");
        }
    }
}

/**
 * Contais declarations of print and read
 * 
 * taken from practical sessions corrections
 */
class BuiltIns {
    static List<String> list;
    static {
        list = Arrays.asList("@.strR = private unnamed_addr constant [3 x i8] c\"%d\\00\", align 1",
        "define i32 @readInt() {",
        "  %x = alloca i32, align 4",
        "  %1 = call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.strR, i32 0, i32 0), i32* %x)",
        "  %2 = load i32, i32* %x, align 4",
        "  ret i32 %2",
        "}",
        "declare i32 @__isoc99_scanf(i8*, ...)",
        "@.strP = private unnamed_addr constant [4 x i8] c\"%d\\0A\\00\", align 1",
        "define void @println(i32 %x) {",
        "  %1 = alloca i32, align 4",
        "  store i32 %x, i32* %1, align 4",
        "  %2 = load i32, i32* %1, align 4",
        "  %3 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.strP, i32 0, i32 0), i32 %2)",
        "  ret void",
        "}",
        "declare i32 @printf(i8*, ...)");
    }
}