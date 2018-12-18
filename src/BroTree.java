import java.util.List;

/**
 * Left child right sibling tree
 */
public class BroTree {
    private BroTree son;
    private BroTree bro;
    private Symbol symbol;
    private int rule;

    public BroTree(ParseTree tree) {
        this.symbol = tree.getLabel();
        this.rule = tree.getRule();
        List<ParseTree> childs = tree.getChildren();
        if (childs.size() > 0) {
            this.son = new BroTree(childs.get(0));
        }
        BroTree current = son;
        for (int i = 1; i < childs.size(); ++i) {
            BroTree next = new BroTree(childs.get(i));
            current.bro = next;
            current = next;
        }
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public BroTree getRight() {
        return bro;
    }

    public BroTree getSon() {
        return son;
    }

    public boolean hasBrother() {
        return bro != null;
    }

    public int getRule() {
        return rule;
    }
}