import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Expression
 */
public class Expression {
    private static Map<String, String> operators = new HashMap<>();
    static {
        operators.put("+", "add i32");
        operators.put("-", "sub i32");
        operators.put("*", "mul i32");
        operators.put("/", "sdiv i32");
        operators.put(">", "icmp sgt i32");
        operators.put(">=", "icmp sge i32");
        operators.put("<", "icmp slt i32");
        operators.put("<=", "icmp sle i32");
        operators.put("=", "icmp eq i32");
        operators.put("<>", "icmp ne i32");
        operators.put("AND", "and i1");
        operators.put("OR", "or i1");
        operators.put("NOT", "xor i1");
    }

    private BinaryTree tree;
    private List<String> instructions;
    private ContextManager contextManager;
    private String value;

    public Expression(LCRSTree tree, List<String> instructions, ContextManager contextManager) {
        this.tree = new BinaryConverter().fromBro(tree);
        this.instructions = instructions;
        this.contextManager = contextManager;
    }

    private static String binOpFormat = "%s = %s %s, %s";

    private void emitBinOp( String operator, String dest, String op1, String op2) {
        String line = String.format(binOpFormat, dest, operator, op1, op2);
        instructions.add(line);
    }

    public String getValue() {
        if (value == null) {
            value = getValue(tree);
        }
        return value;
    }

    private String literalOrRegister(String val) {
        if (val.charAt(0) == '%') {
            // already inside a register
            return val;
        }
        try {
            // literal
            Integer.valueOf(val);
            return val;
        } catch (NumberFormatException ignored) {
            return contextManager.getLoaded(val);
        }
    }

    private String getValue(BinaryTree tree) {
        if (tree.isLeaf()) {
            return literalOrRegister(tree.getValue());
        }
        // System.out.println("the value of "+ tree.value + " is " + operators.get(tree.value));
        String operator = operators.get(tree.getValue());
        String left = getValue(tree.getLeft());
        String right = getValue(tree.getRight());
        String newRegister = contextManager.newVar();

        emitBinOp(operator, newRegister, left, right);
        return newRegister;
    }

    private Expression(BinaryTree tree, List<String> instructions, ContextManager contextManager) {
        this.tree = tree;
        this.instructions = instructions;
        this.contextManager = contextManager;
    }

    public static Expression LessThan(String left, String right, List<String> instructions, ContextManager contextManager) {
        BinaryTree tree = new BinaryTree();
        BinaryTree leftTree = new BinaryTree();
        BinaryTree rightTree = new BinaryTree();
        
        leftTree.setValue(left);
        rightTree.setValue(right);

        tree.setValue("<");
        tree.setLeft(leftTree);
        tree.setRight(rightTree);

        return new Expression(tree, instructions, contextManager);
    }

    public static Expression sum(String left, String right, List<String> instructions, ContextManager contextManager) {
        BinaryTree tree = new BinaryTree();
        BinaryTree leftTree = new BinaryTree();
        BinaryTree rightTree = new BinaryTree();
        
        leftTree.setValue(left);
        rightTree.setValue(right);

        tree.setValue("+");
        tree.setLeft(leftTree);
        tree.setRight(rightTree);

        return new Expression(tree, instructions, contextManager);
    }

    public static Expression eq(String left, String right, List<String> instructions, ContextManager contextManager) {
        BinaryTree tree = new BinaryTree();
        BinaryTree leftTree = new BinaryTree();
        BinaryTree rightTree = new BinaryTree();
        
        leftTree.setValue(left);
        rightTree.setValue(right);

        tree.setValue("=");
        tree.setLeft(leftTree);
        tree.setRight(rightTree);

        return new Expression(tree, instructions, contextManager);
    }
}
