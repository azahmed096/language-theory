import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CodeGenerator {
    private ArrayList<String> instructions = new ArrayList<>();
    private Registers registers = new Registers();

    public void increment() {
        this.x++;
    }

    public List<String> generateProgram(ParseTree tree, SymbolTable table) {
        makeBuiltins();
        List<ParseTree> childs = tree.getChildren();
        if (childs.size() == 4) {
            makeMain(null, null);
        } else if (childs.size() == 5) {
            ParseTree beforeLast = childs.get(3);
            if (beforeLast.getRule() == 2) {
                makeMain(null, beforeLast);
            } else {
                assert beforeLast.getRule() == 7;
                makeMain(beforeLast, null);
            }
        } else {
            assert childs.size() == 6;
            ParseTree vars = tree.getChildren().get(3);
            ParseTree code = tree.getChildren().get(4);
            makeMain(code, vars);
        }
        return instructions;
    }

    private void addVariableDeclaration(String name) {
        // instructions.add("@" + name + " = global i32 0");
        instructions.add("%" + name + " = alloca i32");
    }

    private void variables(ParseTree tree) {
        ParseTree varlist = tree.getChildren().get(1);
        addVariableDeclaration(varlist.getChildren().get(0).getLabel().getValue().toString());
        ParseTree varlistPrim = varlist.getChildren().get(1);

        while (varlistPrim.getChildren().size() == 3) {
            addVariableDeclaration(varlistPrim.getChildren().get(1).getLabel().getValue().toString());
            varlistPrim = varlistPrim.getChildren().get(2);
        }
        if (varlistPrim.getChildren().size() == 2) {
            addVariableDeclaration(varlistPrim.getChildren().get(1).getLabel().getValue().toString());
        }
    }

    private void makeMain(ParseTree tree, ParseTree vars) {
        instructions.add("define i32 @main(){");
        if (vars != null) {
            variables(vars);
        }
        if (tree != null) {
            code(tree);
        }
        instructions.add("ret i32 0\n}");
    }

    private void makeBuiltins() {
        instructions.addAll(BuiltIns.list);
    }

    private void code(ParseTree tree) {
        List<ParseTree> childs = tree.getChildren();
        instruction(childs.get(0));
        if (childs.size() > 2) {
            code(childs.get(2));
        }
    }

    private void instruction(ParseTree tree) {
        ParseTree child = tree.getChildren().get(0);
        switch (tree.getRule()) {
            case 9:
                assign(child);
                break;
            case 10:
                if_(child);
                break;
            case 11:
                while_(child);
                break;
            case 12:
                for_(child);
                break;
            case 13:
                print(child);
                break;
            case 14:
                read(child);
                break;
        }
    }

    private void assign(ParseTree tree) {
        ParseTree expr = tree.getChildren().get(2);
        String val = new Expression(new LCRSTree(expr), instructions, registers).getValue();
        String dest = tree.getChildren().get(0).getLabel().getValue().toString();
        store(dest, val);
    }

    private void label(String label) {
        instructions.add("br label %" + label);
        instructions.add(label + ":");
    }

    private void jump(String condition, String trueLabel, String falseLabel) {
        registers.increment();
        instructions.add("br i1 " + condition + ", label %" + trueLabel + ", label %" + falseLabel);
    }

    private void jump(String label) {
        registers.increment();
        instructions.add("br label %" + label);
    }
    private int x = 0;
    private String getId(ParseTree tree) {
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
    private void if_(ParseTree tree) {
        ParseTree condTree = tree.getChildren().get(2);
        String cond = new Expression(new LCRSTree(condTree), instructions, registers).getValue();
        String id = getId(tree);
        // System.out.println("id:" + tree.getLabel());
        String trueLabel = "true" + id;
        String falseLabel = "false" + id;
        String endLabel = "end" + id;
        jump(cond, trueLabel, falseLabel);
        label(trueLabel);
        ParseTree codeTree = tree.getChildren().get(6);
        code(codeTree);
        jump(endLabel);
        label(falseLabel);
        ParseTree ifSeq = tree.getChildren().get(7);
        if (ifSeq.getRule() == 30) {
            ParseTree codeTreeElse = ifSeq.getChildren().get(2);
            code(codeTreeElse);
        }
        label(endLabel);
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
    private void while_(ParseTree tree) {
        String id = getId(tree);
        String beginLabel = "begin" + id;
        String insideLabel = "inside" + id;
        String outsideLabel = "outside" + id;

        ParseTree condTree = tree.getChildren().get(2);
        ParseTree codeTree = tree.getChildren().get(6);

        label(beginLabel);
        String cond = new Expression(new LCRSTree(condTree), instructions, registers).getValue();
        jump(cond, insideLabel, outsideLabel);
        label(insideLabel);
        code(codeTree);
        jump(beginLabel);
        label(outsideLabel);
    }

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
     *   if src == targ jump outsideLabel else insideLabel
     *  insideLabel:
     *   code_a
     *   src += inc
     *   jump beginLabel
     *  outsideLabel:
     */
    private void for_(ParseTree tree) {
        ParseTree src = tree.getChildren().get(3);
        ParseTree targ = tree.getChildren().get(5);
        ParseTree code = tree.getChildren().get(8);
        String varFor = tree.getChildren().get(1).getLabel().getValue().toString();
        String id = getId(tree);
        String positiveLabel = "positive" + id;
        String negativeLabel = "negative" + id;
        String beginLabel = "begin" + id;
        String compareLabel = "compare" + id;
        String insideLabel = "inside" + id;
        String outsideLabel = "outside" + id;

        String from = new Expression(new LCRSTree(src), instructions, registers).getValue();
        String to = new Expression(new LCRSTree(targ), instructions, registers).getValue();
        String ascending = Expression.LessThan(from, to, instructions, registers).getValue();
        String varIncrement = "incrementer_" + id; // registers.getNewRegister();
        instructions.add("%" + varIncrement + " = alloca i32");
        instructions.add("%" + varFor + " = alloca i32");
        store(varFor, from);

        jump(ascending, positiveLabel, negativeLabel);
        label(positiveLabel);
        store(varIncrement, "1");
        jump(beginLabel);
        label(negativeLabel);
        store(varIncrement, "-1");
        label(beginLabel);
        label(compareLabel);
        String cond = Expression.eq(varFor, to, instructions, registers).getValue();
        jump(cond, outsideLabel, insideLabel);
        label(insideLabel);
        code(code);
        String tempinc = registers.getNewRegister();
        instructions.add(tempinc + " = load i32, i32* %" + varIncrement);
        String temp = Expression.sum(varFor, tempinc, instructions, registers).getValue();
        store(varFor, temp);
        jump(compareLabel);
        label(outsideLabel);
    }

    private void store(String variableName, String source){
        instructions.add("store i32 "+source+", i32* %"+variableName);
    }

    private void read(ParseTree tree) {
        // TODO multiple params READ
        String temp = registers.getNewRegister();
        String variable = tree.getChildren().get(2).getChildren().get(0).getLabel().getValue().toString();
        instructions.add(temp + " = call i32 @readInt()");
        store(variable, temp);
    }

    private void print(ParseTree tree) {
        ParseTree explist = tree.getChildren().get(2).getChildren().get(0);
        String temp = new Expression(new LCRSTree(explist), instructions, registers).getValue();
        instructions.add("call void @println(i32 " + temp + ")");
    }
}

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