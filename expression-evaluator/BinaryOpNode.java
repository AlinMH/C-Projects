
/**
 * Clasa abstracta a unui nod operatie.
 * 
 * @author Alin
 *
 */
public abstract class BinaryOpNode extends Node {
	private String operator;

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

}
