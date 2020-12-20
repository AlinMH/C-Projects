/*
 * Clasa unui nod de tip operator.
 * Utilizat pentru a evalua o expresie aritmetica.
 */
public class OperatorNode extends ArithmeticNode {
	private char operator; //operatii posibile '+' sau '*'

	public OperatorNode(char operator) {
		this.operator = operator;
	}

	public char getOperator() {
		return operator;
	}

	public void setOperator(char operator) {
		this.operator = operator;
	}
	
}
