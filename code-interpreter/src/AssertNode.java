/*
 * Clasa unui nod de tip assert.
 */
public class AssertNode extends ProgramNode {
	private String expression;

	public AssertNode(String expression) {
		super(null, null);
		this.expression = expression;
	}

	public String getExpression() {
		return expression;
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
