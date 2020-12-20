
/**
 * Clasa unui nod operator de inmultire.
 * 
 * @author Alin
 *
 */
public class MulNode extends BinaryOpNode {
	/**
	 * Se creaza un nod operator de inmultire si i se seteaza semnul operatiei
	 * corespunzatoare.
	 */
	public MulNode() {
		super.setOperator("*");
	}

	/*
	 * @see Node#accept(Visitor)
	 */
	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
