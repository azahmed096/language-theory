import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Extract items of list from <ExpList> or <VarList>
 */
public class VarListExtract {
    private List<ParseTree> nodes;

    public VarListExtract(ParseTree tree) {
        nodes = new ArrayList<ParseTree>();
        if (tree.getRule() == 2){ // <Variables>
            varList(tree.getChildren().get(1));
        } else {
            assert tree.getRule() == 4 || tree.getRule() == 49;
            varList(tree);
        }
    }

    /**
     * Parse from <varList> or <ExpList>
     */
    private void varList(ParseTree tree) {
        nodes.add(tree.getChildren().get(0));
        if (tree.getChildren().size() > 1) {
            ParseTree varlistPrim = tree.getChildren().get(1);
            varListPrim(varlistPrim);
        }
    }

    /**
     * Parse from <varListPrim> or <ExpListPrim>
     */
    private void varListPrim(ParseTree varlistPrim) {
        while (varlistPrim.getChildren().size() == 3) {
            nodes.add(varlistPrim.getChildren().get(1));
            varlistPrim = varlistPrim.getChildren().get(2);
        }
        if (varlistPrim.getChildren().size() == 2) {
            nodes.add(varlistPrim.getChildren().get(1));
        }
    }

    /**
     * (used for expressions in explist)
     * @return extracted nodes
     */
    public List<ParseTree> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    /**
     * (used for varlist)
     * @return list of variables that have been extracted
     */
    public List<String> getAsStrings() {
        List<String> s = new ArrayList<String>();
        for (ParseTree tree: nodes) {
            s.add(tree.getLabel().getValue().toString());
        }
        return s;
    }
}