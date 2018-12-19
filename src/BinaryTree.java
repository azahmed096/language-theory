import java.util.Arrays;

public class BinaryTree {
    private BinaryTree left;
    private BinaryTree right;
    private BinaryTree parent;
    private String value;

    /**
     * @return left child
     */
    public BinaryTree getLeft() {
        return left;
    }

    /**
     * @return right child
     */
    public BinaryTree getRight() {
        return right;
    }

    /**
     * @return True if the node is a leaf
     */
    public boolean isLeaf() {
        return left == null && right == null;
    }

    /**
     * set the right child to the new value
     */
    public void setRight(BinaryTree right) {
        this.right = right;
        right.parent = this;
    }

    /**
     * set the left child
     */
    public void setLeft(BinaryTree left) {
        this.left = left;
        left.parent = this;
    }

    /**
     * Set the value to the new value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @return the root of the tree
     */
    public BinaryTree root() {
        BinaryTree res = this;
        while (res.parent != null) {
            res = res.parent;
        }
        return res;
    }
}