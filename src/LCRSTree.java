import java.util.List;

/**
 * Left child right sibling tree
 */
public class LCRSTree {
    private LCRSTree son;
    private LCRSTree bro;
    private Symbol symbol;
    private int rule;

    public LCRSTree(ParseTree tree) {
        this.symbol = tree.getLabel();
        this.rule = tree.getRule();
        List<ParseTree> childs = tree.getChildren();
        if (childs.size() > 0) {
            this.son = new LCRSTree(childs.get(0));
        }
        LCRSTree current = son;
        for (int i = 1; i < childs.size(); ++i) {
            LCRSTree next = new LCRSTree(childs.get(i));
            current.bro = next;
            current = next;
        }
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public LCRSTree getRight() {
        return bro;
    }

    public LCRSTree getSon() {
        return son;
    }

    public boolean hasBrother() {
        return bro != null;
    }

    public int getRule() {
        return rule;
    }
}