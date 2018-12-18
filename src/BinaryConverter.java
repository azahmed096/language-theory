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
    public BinaryTree fromBro(BroTree tree) {
        return getFilled(tree).root();
    }

    private BinaryTree getFilled(BroTree exp) {
        BinaryTree result = null;
        switch (exp.rule) {
        case 16:
        case 20: // Expr || Term
            // Term || Atom
            result = getFilled(exp.son);
            // expr_prim || term_prim
            if (exp.son.bro != null) {
                BinaryTree pluggable = getPluggable(exp.son.bro);
                assert pluggable.getLeft() == null;
                pluggable.setLeft(result);
                result = pluggable.root();
            }
            break;
        case 24:
        case 25:
            // Number or variable literal.
            result = new BinaryTree();
            result.setValue(exp.son.symbol.getValue().toString());
            break;
        case 26: // ( expression )
            result = getFilled(exp.son.bro);
            break;
        case 27:
            // A negatif number -n is the same as 0 - n
            result = new BinaryTree();
            result.setLeft(new BinaryTree());
            result.setRight(new BinaryTree());
            result.setValue("-");
            result.getLeft().setValue("0");
            result.setRight(getFilled(exp.son.bro));
            break;
        case 31: // cond
        case 34: // andcond
            result = getFilled(exp.son);
            if (exp.son.bro != null) {
                BinaryTree pluggable = getPluggable(exp.son.bro);
                pluggable.setLeft(result);
                result = pluggable.root();
            }
            break;
        case 37:
            result = new BinaryTree();
            result.setValue(exp.son.bro.son.symbol.getValue().toString());
            result.setLeft(getFilled(exp.son));
            result.setRight(getFilled(exp.son.bro.bro));
            break;
        case 38:
            // NOT bit == 1 xor bit
            result = new BinaryTree();
            BinaryTree left = new BinaryTree();
            left.setValue("1");
            result.setValue("NOT");
            result.setLeft(left);
            result.setRight(getFilled(exp.son.bro));
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
    private BinaryTree getPluggable(BroTree prim) {
        BinaryTree result = null;

        result = new BinaryTree();
        result.setValue(prim.son.symbol.getValue().toString());
        result.setRight(getFilled(prim.son.bro));
        if (prim.son.bro.bro != null) {
            BinaryTree pluggable = getPluggable(prim.son.bro.bro);
            assert pluggable.getLeft() == null;
            pluggable.setLeft(result);
        }
        assert result != null;
        return result;
    }
}
