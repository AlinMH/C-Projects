/*
 * Clasa unui nod de tip if.
 */
public class IfNode extends ProgramNode {
	private String condition; //conditia care trebuie satisfacuta
	private ProgramNode thenTree; //programul pentru then
	private ProgramNode elseTree; //programul pentru else

	public IfNode(String condition, String thenProg, String elseProg) {
		super(null, null);
		this.condition = condition;
		this.thenTree = new ProgramTree(thenProg).getRoot();
		this.elseTree = new ProgramTree(elseProg).getRoot();
	}
	
	public ProgramNode getThenTree() {
		return thenTree;
	}
	
	public ProgramNode getElseTree() {
		return elseTree;
	}
	
	public String getCondition() {
		return condition;
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}

}
