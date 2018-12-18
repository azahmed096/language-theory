import java.util.Arrays;

public class BinaryTree {
    private BinaryTree left;
    private BinaryTree right;
    private BinaryTree parent;
    private String value;

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

    public String getValue() {
        return value;
    }

    public BinaryTree root() {
        BinaryTree res = this;
        while (res.parent != null) {
            res = res.parent;
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