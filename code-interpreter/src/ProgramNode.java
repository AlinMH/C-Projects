/*
 * Clasa decorator al temei.
 * Este extinsa de fiecare nod concret (AssignNode, AssertNode, etc). 
 * Acesta poate sa aiba fiu stanga si dreapta (doar in cazul SequenceNode) sau sa ii aibe pe amandoi pe null (celelalte cazuri).
 */
public abstract class ProgramNode implements Visitable {
	private ProgramNode left;
	private ProgramNode right;
	
	public ProgramNode(ProgramNode left, ProgramNode right) {
		this.left = left;
		this.right = right;
	}
	
	public ProgramNode getLeft() {
		return left;
	}
	public void setLeft(ProgramNode left) {
		this.left = left;
	}
	public ProgramNode getRight() {
		return right;
	}
	public void setRight(ProgramNode right) {
		this.right = right;
	}
}
