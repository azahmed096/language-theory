import java.util.ArrayList;
import java.util.List;


public class VarListExtract {
    private List<String> vars;

    public VarListExtract(ParseTree tree) {
        vars = new ArrayList<>();
        if (tree.getRule() == 2){
            varList(tree.getChildren().get(1));
        } else {
            assert tree.getRule() == 4;
            varList(tree);
        }
    }

    private void varList(ParseTree tree) {
        vars.add(tree.getChildren().get(0).getLabel().getValue().toString());
        if (tree.getChildren().size() > 1) {
            ParseTree varlistPrim = tree.getChildren().get(1);
            varListPrim(varlistPrim);
        }
    }

    private void varListPrim(ParseTree varlistPrim) {
        while (varlistPrim.getChildren().size() == 3) {
            vars.add(varlistPrim.getChildren().get(1).getLabel().getValue().toString());
            varlistPrim = varlistPrim.getChildren().get(2);
        }
        if (varlistPrim.getChildren().size() == 2) {
            vars.add(varlistPrim.getChildren().get(1).getLabel().getValue().toString());
        }
    }

    public List<String> getList() {
        return vars;
    }
}