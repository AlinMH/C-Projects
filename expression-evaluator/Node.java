
/**
 * Clasa abstracta nod, care este "vizitabil", implementeaza metoda accept din
 * interfata Visitable pentru a respeta pattern-ul Visitor.
 * 
 * Clasa are doar doi membrii left si right, get-eri si set-eri.
 * 
 * @author Alin
 *
 */
public abstract class Node implements Visitable {
	private Node left = null;
	private Node right = null;

	public Node getLeft() {
		return left;
	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public Node getRight() {
		return right;
	}

	public void setRight(Node right) {
		this.right = right;
	}

	/*
	 * @see Visitable#accept(Visitor)
	 */
	@Override
	public abstract void accept(Visitor v);
}
