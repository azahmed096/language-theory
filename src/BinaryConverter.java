/**
 * for infixe notations LL(1) derivation trees
 * are hard to use for code generating.
 * This class convert a derivation tree (left child right sibling)
 * that is an expression to a binary tree with atoms as leafs
 * and other nodes are operators.
 * 
 * For some rules a simple top down parse could be sufficient
 * (24, 25 and 26, 27).
 * 
 * For other rules (16, 20, 31..) we need to first compute the left child
 * and then the right child, in binary trees this is the equivalent to plug
 * the left child as a left child of the binary tree that comes from right child.
 * 
 * For this reason we need a binary tree (exp_prim, term_prim...) in which we can plug
 * a binary tree at its left.
 */
public class BinaryConverter {
    public BinaryTree fromBro(LCRSTree tree) {
        return getFilled(tree).root();
    }

    /**
     * The corresponding binary tree
     * @param exp
     * @return
     */
    private BinaryTree getFilled(LCRSTree exp) {
        BinaryTree result = null;
        switch (exp.getRule()) {
        case 16:
        case 20: // Expr || Term
            // Term || Atom
            result = getFilled(exp.getSon());
            // expr_prim || term_prim
            if (exp.getSon().hasBrother()) {
                BinaryTree pluggable = getPluggable(exp.getSon().getRight());
                assert pluggable.getLeft() == null;
                pluggable.setLeft(result);
                result = pluggable.root();
            }
            break;
        case 24:
        case 25:
            // Number or variable literal.
            result = new BinaryTree();
            result.setValue(exp.getSon().getSymbol().getValue().toString());
            break;
        case 26: // ( expression )
            result = getFilled(exp.getSon().getRight());
            break;
        case 27:
            // A negatif number -n is the same as 0 - n
            result = new BinaryTree();
            result.setLeft(new BinaryTree());
            result.setRight(new BinaryTree());
            result.setValue("-");
            result.getLeft().setValue("0");
            result.setRight(getFilled(exp.getSon().getRight()));
            break;
        case 31: // cond
        case 34: // andcond
            result = getFilled(exp.getSon());
            if (exp.getSon().hasBrother()) {
                BinaryTree pluggable = getPluggable(exp.getSon().getRight());
                pluggable.setLeft(result);
                result = pluggable.root();
            }
            break;
        case 37:
            result = new BinaryTree();
            result.setValue(exp.getSon().getRight().getSon().getSymbol().getValue().toString());
            result.setLeft(getFilled(exp.getSon()));
            result.setRight(getFilled(exp.getSon().getRight().getRight()));
            break;
        case 38:
            // NOT bit == 1 xor bit
            result = new BinaryTree();
            BinaryTree left = new BinaryTree();
            left.setValue("1");
            result.setValue("NOT");
            result.setLeft(left);
            result.setRight(getFilled(exp.getSon().getRight()));
            break;
        default:
            // we reach this only if we forgot a rule
            assert false;
            break;
        }
        return result;
    }

    /**
     * @param prim derivation subtree of prim (or tail) rules.
     * @return Build a plugable tree (with no left child)
     */
    private BinaryTree getPluggable(LCRSTree prim) {
        BinaryTree result = null;

        result = new BinaryTree();
        result.setValue(prim.getSon().getSymbol().getValue().toString());
        result.setRight(getFilled(prim.getSon().getRight()));
        if (prim.getSon().getRight().hasBrother()) {
            BinaryTree pluggable = getPluggable(prim.getSon().getRight().getRight());
            assert pluggable.getLeft() == null;
            pluggable.setLeft(result);
        }
        assert result != null;
        return result;
    }
}
