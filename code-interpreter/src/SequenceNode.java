/*
 * Clasa unui nod de tip Sequence.
 * Rolul acestui nod este de tine ca fiu stanga si dreapta cate un program.
 * Acest tip de nod este necesar pentru a construi arborele binar.
 */
public class SequenceNode extends ProgramNode {
	public SequenceNode(ProgramNode left, ProgramNode right) {
		super(left, right);
	}
	
	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
