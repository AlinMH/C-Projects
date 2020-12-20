/*
 * Clasa unui nod de tip assign.
 */
public class AssignNode extends ProgramNode {
	private String variable;
	private String expression;

	public AssignNode(String variable, String expression) {
		super(null, null);
		this.variable = variable;
		this.expression = expression;
	}

	public String getVariable() {
		return variable;
	}

	public String getExpression() {
		return expression;
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}

}
