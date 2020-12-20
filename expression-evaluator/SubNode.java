
/**
 * Clasa unui nod operator de scadere.
 * 
 * @author Alin
 *
 */
public class SubNode extends BinaryOpNode {
	/**
	 * Se creaza un nod operator de scadere si i se seteaza semnul operatiei
	 * corespunzator.
	 */
	public SubNode() {
		super.setOperator("-");
	}

	/*
	 * @see Node#accept(Visitor)
	 */
	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
