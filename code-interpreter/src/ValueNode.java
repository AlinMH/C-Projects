/*
 * Clasa unui nod de tip valoare.
 * Utilizat pentru a evalua o expresie aritmetica.
 */
public class ValueNode extends ArithmeticNode {
	private Integer value;

	public ValueNode(Integer value) {
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
	
}
