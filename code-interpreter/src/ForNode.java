/*
 * Clasa unui nod de tip for.
 */
public class ForNode extends ProgramNode {

	private String condition; //conditia care trebuie sa fie satisfacuta
	private ProgramNode progTree; // programul din for care trebuie executat
	private ProgramNode initNode; // initializarea, in cazul nostru va fi un nod Assign
	private ProgramNode incrementNode; // programul care realizeaza incrementarea (tot de tipul Assign)
	
	public ForNode(String init, String condition, String increment, String prog) {
		super(null, null);
		this.condition = condition;
		this.progTree = new ProgramTree(prog).getRoot();
		this.initNode = new ProgramTree(init).getRoot();
		this.incrementNode = new ProgramTree(increment).getRoot();
	}
	
	public ProgramNode getInitNode() {
		return initNode;
	}
	
	public ProgramNode getIncrementNode() {
		return incrementNode;
	}
	
	public ProgramNode getProgTree() {
		return progTree;
	}
	
	public String getCondition() {
		return condition;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
