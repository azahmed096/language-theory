import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Expression
 */
public class Expression {
    private static Map<String, String> operators = new HashMap<String, String>();
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

    /**
     * Construct an expression from a left-child right-sibling tree
     * @param tree
     * @param instructions instructions list
     * @param contextManager context manager of variables
     */
    public Expression(LCRSTree tree, List<String> instructions, ContextManager contextManager) {
        this.tree = new BinaryConverter().fromBro(tree);
        this.instructions = instructions;
        this.contextManager = contextManager;
    }

    private static String binOpFormat = "%s = %s %s, %s";

    /**
     * @param operator operator as in keys of {@link Expression#operators}
     * @param dest pointer to the variable where the result is stored
     * @param op1 operand 1
     * @param op2 operand 2
     */
    private void emitBinOp( String operator, String dest, String op1, String op2) {
        String line = String.format(binOpFormat, dest, operator, op1, op2);
        instructions.add(line);
    }

    /**
     * Compute the value of the expression
     * @return a register or a literal
     */
    public String getValue() {
        return getValue(tree);
    }

    /**
     * @param val value
     * @return the value as it should be in the llvm code
     */
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

    /**
     * Compute the value of the tree
     * @return a register or a literal
     */
    private String getValue(BinaryTree tree) {
        if (tree.isLeaf()) {
            return literalOrRegister(tree.getValue());
        }
        String operator = operators.get(tree.getValue());
        String left = getValue(tree.getLeft());
        String right = getValue(tree.getRight());
        String newRegister = contextManager.newVar();

        emitBinOp(operator, newRegister, left, right);
        return newRegister;
    }

    /**
     * Construct an expression from a binary tree
     * @param tree
     * @param instructions instructions list
     * @param contextManager context manager of variables
     */
    private Expression(BinaryTree tree, List<String> instructions, ContextManager contextManager) {
        this.tree = tree;
        this.instructions = instructions;
        this.contextManager = contextManager;
    }

    /**
     * @param op binary operator
     * @param left left operand
     * @param right right operand
     * @return A binary tree of the binary operator
     */
    private static BinaryTree fromOp(String op, String left, String right) {
        BinaryTree tree = new BinaryTree();
        BinaryTree leftTree = new BinaryTree();
        BinaryTree rightTree = new BinaryTree();
        
        leftTree.setValue(left);
        rightTree.setValue(right);

        tree.setValue(op);
        tree.setLeft(leftTree);
        tree.setRight(rightTree);
        return tree;
    }

    /**
     * @param left left operand
     * @param right right operand
     * @param instructions instructions list
     * @param contextManager context manager of variables
     * @return an expression that represent the comparison left < right
     */
    public static Expression LessThan(String left, String right, List<String> instructions, ContextManager contextManager) {
        BinaryTree tree = fromOp("<", left, right);
        return new Expression(tree, instructions, contextManager);
    }

    /**
     * @param left left operand
     * @param right right operand
     * @param instructions instructions list
     * @param contextManager context manager of variables
     * @return an expression that represent the sum left + right
     */
    public static Expression sum(String left, String right, List<String> instructions, ContextManager contextManager) {
        BinaryTree tree = fromOp("+", left, right);
        return new Expression(tree, instructions, contextManager);
    }

    /**
     * @param left left operand
     * @param right right operand
     * @param instructions instructions list
     * @param contextManager context manager of variables
     * @return an expression that represent the comparison left = right
     */
    public static Expression eq(String left, String right, List<String> instructions, ContextManager contextManager) {
        BinaryTree tree = fromOp("=", left, right);
        return new Expression(tree, instructions, contextManager);
    }
}
