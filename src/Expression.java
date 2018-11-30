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
    private Registers registers;

    public Expression(BroTree tree, List<String> instructions, Registers registers) {
        this.tree = new BinaryConverter().fromBro(tree);
        this.instructions = instructions;
        this.registers = registers;
    }

    private static String binOpFormat = "%s = %s %s, %s";

    private void emitBinOp( String operator, String dest, String op1, String op2) {
        String line = String.format(binOpFormat, dest, operator, op1, op2);
        instructions.add(line);
    }

    public String getValue() {
        return getValue(tree);
    }

    private String literal(String val) {
        try {
            Integer.valueOf(val);
            return val;
        } catch (NumberFormatException ignored) {
            String newRegister = registers.getNewRegister();
            val = "@" + val;
            instructions.add(newRegister + " = load i32, i32* " + val);
            return newRegister;
        }
    }

    private String getValue(BinaryTree tree) {
        if (tree.isLeaf()) {
            return literal(tree.value);
        }
        String operator = operators.get(tree.value);
        String left = getValue(tree.getLeft());
        String right = getValue(tree.getRight());
        String newRegister = registers.getNewRegister();

        emitBinOp(operator, newRegister, left, right);
        return newRegister;
    }
}
