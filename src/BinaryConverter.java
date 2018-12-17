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
            result.value = exp.son.bro.son.symbol.getValue().toString(); // TODO
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
