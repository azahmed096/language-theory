import java.util.List;

/**
 * Left sibling right child tree
 */
public class BroTree {
    public BroTree son;
    public BroTree bro;
    public Symbol symbol;
    public int rule;

    public BroTree(ParseTree tree) {
        this.symbol = tree.getLabel();
        this.rule = tree.getRule();
        List<ParseTree> childs = tree.getChildren();
        if (childs.size() > 0) {
            this.son = new BroTree(childs.get(0));
        }
        BroTree current = son;
        for (int i = 1; i < childs.size(); ++i){
            BroTree next = new BroTree(childs.get(i));
            current.bro = next;
            current = next;
        }
    }
}