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
            makeMain(null);
        } else if (childs.size() == 5) {
            ParseTree beforeLast = childs.get(3);
            if (beforeLast.getRule() == 2) {
                variables(beforeLast);
                makeMain(null);
            } else {
                assert beforeLast.getRule() == 7;
                makeMain(beforeLast);
            }
        } else {
            assert childs.size() == 6;
            ParseTree vars = tree.getChildren().get(3);
            ParseTree code = tree.getChildren().get(4);
            variables(vars);
            makeMain(code);
        }
        return instructions;
    }

    private void addVariableDeclaration(String name) {
        instructions.add("@" + name + " = global i32 0");
        // instructions.add("%" + name + " = alloca i32 0");
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

    private void makeMain(ParseTree tree) {
        instructions.add("define i32 @main(){");
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
        String val = new Expression(new BroTree(expr), instructions, registers).getValue();
        String dest = tree.getChildren().get(0).getLabel().getValue().toString();
        instructions.add(String.format("store i32 %s, i32* @%s", val, dest));
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
        String cond = new Expression(new BroTree(condTree), instructions, registers).getValue();
        String id = getId(tree);
        System.out.println("id:" + tree.getLabel());
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
        String cond = new Expression(new BroTree(condTree), instructions, registers).getValue();
        jump(cond, insideLabel, outsideLabel);
        label(insideLabel);
        code(codeTree);
        jump(beginLabel);
        label(outsideLabel);
    }

    private void for_(ParseTree tree) {
        instructions.add("FOR LOOP");
    }

    private void read(ParseTree tree) {
        // TODO multiple params READ
        String temp = registers.getNewRegister();
        String variable = tree.getChildren().get(2).getChildren().get(0).getLabel().getValue().toString();
        instructions.add(temp + " = call i32 @readInt()");
        instructions.add("store i32 " + temp + ", i32* @" + variable);
    }

    private void print(ParseTree tree) {
        ParseTree explist = tree.getChildren().get(2).getChildren().get(0);
        String temp = new Expression(new BroTree(explist), instructions, registers).getValue();
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