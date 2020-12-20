/*
 * Clasa abstracta a unui nod artimetic. 
 */
public abstract class ArithmeticNode {
	private ArithmeticNode left;
	private ArithmeticNode right;
	
	public ArithmeticNode getLeft() {
		return left;
	}
	public void setLeft(ArithmeticNode left) {
		this.left = left;
	}
	public ArithmeticNode getRight() {
		return right;
	}
	public void setRight(ArithmeticNode right) {
		this.right = right;
	}
	
	
}
