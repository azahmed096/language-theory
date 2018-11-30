import java.util.Arrays;

public class BinaryConverter {
    public BinaryTree fromBro(BroTree tree) {
        return getFilled(tree).root();
    }

    private BinaryTree getFilled(BroTree exp) {
        BinaryTree result = null;
        switch (exp.rule) {
        case 16: // Expr
            // Term
            result = getFilled(exp.son);
            // expr_prim
            if (exp.son.bro != null) {
                BinaryTree pluggable = getPluggable(exp.son.bro);
                assert pluggable.getLeft() == null;
                pluggable.setLeft(result);
                result = pluggable.root();
            }
            break;
        case 20: // Term
            // Atom
            result = getFilled(exp.son);
            // term_prim
            if (exp.son.bro != null) {
                BinaryTree pluggable = getPluggable(exp.son.bro);
                assert pluggable.getLeft() == null;
                pluggable.setLeft(result);
                result = pluggable.root();
            }
            break;
        case 24:
        case 25:
            result = new BinaryTree();
            result.value = exp.son.symbol.getValue().toString();
            break;
        case 27:
            result = new BinaryTree();
            result.setLeft(new BinaryTree());
            result.setRight(new BinaryTree());
            result.value = "-";
            result.getLeft().setValue("0");
            result.setRight(getFilled(exp.son.bro));
            break;
        case 26:
            result = getFilled(exp.son);
            break;
        case 31: // cond
            result = getFilled(exp.son);
            if (exp.son.bro != null) {
                BinaryTree pluggable = getPluggable(exp.son.bro);
                pluggable.setLeft(result);
                result = pluggable.root();
            }
            break;
        case 34:
            result = getFilled(exp.son);
            if (exp.son.bro != null) {
                BinaryTree pluggable = getPluggable(exp.son.bro);
                pluggable.setLeft(result);
                result = pluggable.root();
            }
            break;
        case 37:
            result = new BinaryTree();
            result.value = "<"; // TODO
            result.setLeft(getFilled(exp.son));
            result.setRight(getFilled(exp.son.bro.bro));
            break;
        case 38:
            result = new BinaryTree();
            BinaryTree left = new BinaryTree();
            left.value = "1";
            result.value = "NOT";
            result.setLeft(left);
            result.setRight(getFilled(exp.son.bro));
            break;
        default:
            assert false;
            break;
        }
        return result;
    }

    private BinaryTree getPluggable(BroTree prim) {
        BinaryTree result = null;
        switch (prim.rule) {
        case 17:
            result = new BinaryTree();
            result.value = "+";
            result.setRight(getFilled(prim.son.bro));
            if (prim.son.bro.bro != null) {
                BinaryTree pluggable = getPluggable(prim.son.bro.bro);
                assert pluggable.getLeft() == null;
                pluggable.setLeft(result);
            }
            break;
        case 18:
            result = new BinaryTree();
            result.value = "-";
            result.setRight(getFilled(prim.son.bro));
            if (prim.son.bro.bro != null) {
                BinaryTree pluggable = getPluggable(prim.son.bro.bro);
                assert pluggable.getLeft() == null;
                pluggable.setLeft(result);
            }
            break;
        case 21:
            result = new BinaryTree();
            result.value = "*";
            result.setRight(getFilled(prim.son.bro));
            if (prim.son.bro.bro != null) {
                BinaryTree pluggable = getPluggable(prim.son.bro.bro);
                assert pluggable.getLeft() == null;
                pluggable.setLeft(result);
            }
            break;
        case 22:
            result = new BinaryTree();
            result.value = "/";
            result.setRight(getFilled(prim.son.bro));
            if (prim.son.bro.bro != null) {
                BinaryTree pluggable = getPluggable(prim.son.bro.bro);
                assert pluggable.getLeft() == null;
                pluggable.setLeft(result);
            }
            break;
        case 32:
            result = new BinaryTree();
            result.value = "OR";
            result.setRight(getFilled(prim.son.bro));
            if (prim.son.bro.bro != null) {
                BinaryTree pluggable = getPluggable(prim.son.bro.bro);
                assert pluggable.getLeft() == null;
                pluggable.setLeft(result);
            }
            break;
        case 35:
            result = new BinaryTree();
            result.setRight(getFilled(prim.son.bro));
            if (prim.son.bro.bro != null) {
                BinaryTree pluggable = getPluggable(prim.son.bro.bro);
                assert pluggable.getLeft() == null;
                pluggable.setLeft(result);
            }
            break;
        default:
            break;
        }
        assert result != null;
        return result;
    }
}

class BinaryTree {
    public BinaryTree left;
    public BinaryTree right;
    public BinaryTree parent;
    public String value;

    public BinaryTree getLeft() {
        return left;
    }

    public BinaryTree getRight() {
        return right;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

    public void setRight(BinaryTree right) {
        this.right = right;
        right.parent = this;
    }

    public void setLeft(BinaryTree left) {
        this.left = left;
        left.parent = this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public BinaryTree root() {
        BinaryTree res = this;
        while (res.parent != null) {
            res = res.parent;
        }
        return res;
    }

    public void print() {
        if (left != null) {
            left.print(1);
            right.print(1);
        }
    }

    private void print(int x) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < x; i++) {
            builder.append(' ');
        }
        if (left != null) {
            left.print(x + 1);
            right.print(x + 1);
        }
    }

    public ParseTree toParseTree() {
        ParseTree res = new ParseTree(value);
        if (left != null) {
            assert right != null;
            res.setChildern(Arrays.asList(left.toParseTree(), right.toParseTree()));
        }
        return res;
    }

    public String toTikZPicture() {
        return "\\begin{tikzpicture}[tree layout]\n\\" + toTikZ() + ";\n\\end{tikzpicture}";
    }

    public String toTikZ() {
        StringBuilder treeTikZ = new StringBuilder();
        treeTikZ.append("node {");
        treeTikZ.append(value);
        treeTikZ.append("}\n");
        if (left != null) {
            treeTikZ.append("child { ");
            treeTikZ.append(left.toTikZ());
            treeTikZ.append(" }\n");
            treeTikZ.append("child { ");
            treeTikZ.append(right.toTikZ());
            treeTikZ.append(" }\n");
        }
        return treeTikZ.toString();
    }

    public String toLaTeX() {
        return "\\RequirePackage{luatex85}\n\\documentclass{standalone}\n\n\\usepackage{tikz}\n\n\\usetikzlibrary{graphdrawing, graphdrawing.trees}\n\n\\begin{document}\n\n"
                + toTikZPicture() + "\n\n\\end{document}\n%% Local Variables:\n%% TeX-engine: luatex\n%% End:";
    }
}
