/*
 * Clasa unui nod de tip return.
 */
public class ReturnNode extends ProgramNode {
	private String expression;

	public ReturnNode(String expression) {
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
