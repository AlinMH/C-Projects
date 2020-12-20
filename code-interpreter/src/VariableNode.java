/*
 * Clasa unui nod de tip varabila.
 * Utilizat pentru a evalua o expresie aritmetica.
 */
public class VariableNode extends ArithmeticNode {
	private String variableName;
	
	public VariableNode(String variableName) {
		this.variableName = variableName;
	}
	
	public String getVariableName() {
		return variableName;
	}

}
