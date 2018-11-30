import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class NodeVisitor {

    private List<NodeReplace> replacers = new ArrayList<>();

    public NodeVisitor() {
        // replacers.add(new Read());
        replacers.add(new SingleTon());
    }

    public void Visit(ParseTree tree) {
        List<ParseTree> childs = tree.getChildren();
        for (int i = 0; i < childs.size(); ++i) {
            for (NodeReplace replacer : replacers) {
                ParseTree original = childs.get(i);
                ParseTree modified = replacer.replace(original);
                childs.set(i, modified);
            }
            Visit(childs.get(i));
        }
    }
/*
    public void Simplify(ParseTree tree) {
        List<ParseTree> childs = tree.getChildren();

        for (int i = 0; i < childs.size(); ++i) {
            ParseTree child = childs.get(i);
            List<ParseTree> grand_childs = child.getChildren();

            if (grand_childs.size() == 1) {
                childs.set(i, grand_childs.get(0));
            }
        }
    }
*/
}

class SingleTon extends NodeReplace {
    @Override
    public ParseTree replace(ParseTree original) {
        ParseTree current = original;

        while (current.getChildren().size() == 1) {
            current = current.getChildren().get(0);
        }

        return current;
    }
}

abstract class NodeReplace {
    public abstract ParseTree replace(ParseTree original);
}
/*
// Not a replacer
class Variables extends NodeReplace {
    @Override
    public ParseTree replace(ParseTree original) {
        if ("<Variables>".equals(original.getLabel().getValue())){
            
        }
        return original;
    }
}
*/
/**
 * Will be removed if we can define a variadic number of functions
 *//*
class Read extends NodeReplace {

    @Override
    public ParseTree replace(ParseTree original) {
        List<ParseTree> variablesName = new ArrayList<>();

        if ("<Read>".equals(original.getLabel().getValue())) {
            ParseTree varlist = original.getChildren().get(2);
            variablesName.add(varlist.getChildren().get(0));
            
            ParseTree varlistPrim = varlist.getChildren().get(1);

            while (varlistPrim.getChildren().size() == 3){
                variablesName.add(varlistPrim.getChildren().get(1));
                varlistPrim = varlistPrim.getChildren().get(2);
            }
            if (variablesName.size() > 1){
                return new ParseTree("<Read>", variablesName);
            }
            variablesName.forEach(System.out::println);
        }
        return original;
    }
}
*/