import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/** A skeleton class to represent parse trees.
 *  The arity is not fixed: a node can have 0, 1 or more children.
 *  Trees are represented in the following way:
 *                        Tree :== Symbol * List<Tree>
 *  In other words, trees are defined recursively:
 *  A tree is a root (with a label of type Symbol) and a list of trees children.
 *  Thus, a leave is simply a tree with no children (its list of children is empty).
 *  This class can also be seen as representing the Node of a tree,
 *  in which case a tree is simply represented as its root.
 * @author Léo Exibard
 */

public class ParseTree {
    private Symbol label;             // The label of the root of the tree
    private List<ParseTree> children; // Its children, which are trees themselves
    private int rule = -1;

    /** Creates a singleton tree with only a root labeled by lbl.
     * @param lbl The label of the root
     */
    public ParseTree(Symbol lbl) {
        this.label = lbl;
        this.children = new ArrayList<ParseTree>(); // This tree has no children
    }

    /** Creates a singleton tree with only a root labeled by nonTerm.
     * @param nonTerm The label of the root, given as a String
     */
    public ParseTree(String nonTerm) {
        this.label = new Symbol(nonTerm);
        this.children = new ArrayList<ParseTree>();
    }

    public List<ParseTree> getChildren() {
        // return Collections.unmodifiableList(this.children);
        return children;
    }

    public void setChildern(List<ParseTree> children) {
        this.children = new ArrayList<ParseTree>(children);
    }

    public Symbol getLabel(){
        return this.label;
    }

    /** Creates a tree with root labeled by lbl and children chdn.
     * @param lbl The label of the root
     * @param chdn Its children
     */
    public ParseTree(Symbol lbl, List<ParseTree> chdn) {
        this.label = lbl;
        this.children = chdn;
    }

    public int getRule() {
        return this.rule;
    }

    /** Creates a singleton tree with root labeled by nonTerm and children chdn.
     * TODO check doc
     * @param nonTerm The label of the root, given as a String
     * @param chdn Its children
     */
    public ParseTree(int ruleNumber, List<ParseTree> chdn) {
        this.rule = ruleNumber;
        String nonTerm = Grammar.rule(ruleNumber);
        nonTerm = nonTerm.substring(0, nonTerm.indexOf('>') + 1);
        this.label = new Symbol(nonTerm);
        this.children = chdn;
    }

    /** Writes the tree as LaTeX code.
     */
    public String toTeX() {
        StringBuilder treeTeX = new StringBuilder();
        treeTeX.append("[.");
        if (label.isEpsilon()){
            treeTeX.append("$\\varepsilon$");
        }
        else  {
            treeTeX.append(label);
        }
        treeTeX.append(" ");
        for (ParseTree child: children) {
            treeTeX.append(child.toTeX());
                }
        treeTeX.append("]");
        return treeTeX.toString();
    }

    /** Writes the tree as TikZ code.
     *  TikZ is a language to specify drawings in LaTeX files.
     */
    public String toTikZ() {
        StringBuilder treeTikZ = new StringBuilder();
        treeTikZ.append("node {");
        treeTikZ.append(labelToTex(label));
        treeTikZ.append("}\n");
        for (ParseTree child: children) {
            treeTikZ.append("child { ");
            treeTikZ.append(child.toTikZ());
            treeTikZ.append(" }\n");
        }
        return treeTikZ.toString();
    }
    /** Return better latex formatting for terminals
     * 
     */
    private String labelToTex(Symbol label){
        if (label.isEpsilon()){
            return "$\\varepsilon$";
        } else if (!label.isTerminal()){
            return "$" + getRule() + label
            .getValue()
            .toString().replace("_", "\\_") + "$";
        }
        return String.format("\\textbf{%s} \\textit{%s}", label.getType(), label.getValue());
    }

    /** Writes the tree as a TikZ picture.
     *  A TikZ picture embeds TikZ code so that LaTeX undertands it.
     */
    public String toTikZPicture() {
        return "\\begin{tikzpicture}[tree layout]\n\\" + toTikZ() + ";\n\\end{tikzpicture}";
    }

    public static void print(ParseTree tree, int level){
        String space  = "";
        for (int i = 0; i < level; ++i){
            space = space + "\t";
        }
        if (tree.children == null) return;
        for (ParseTree son: tree.children){
            print(son, level + 1);
        }

    }

    /** Writes the tree as a LaTeX document which can be compiled (using the LuaLaTeX engine).
     *  Be careful that such code will not compile with PDFLaTeX,
     *  since the tree drawing algorithm is written in Lua.
     *  The code is not very readable as such, but you can have a look at the outputted file
     *  if you want to understand better.
     */
    public String toLaTeX() {
        return "\\RequirePackage{luatex85}\n\\documentclass{standalone}\n\n\\usepackage{tikz}\n\n\\usetikzlibrary{graphdrawing, graphdrawing.trees}\n\n\\begin{document}\n\n" + toTikZPicture() + "\n\n\\end{document}\n%% Local Variables:\n%% TeX-engine: luatex\n%% End:";
    }
}
