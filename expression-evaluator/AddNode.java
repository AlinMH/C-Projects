
/**
 * Clasa unui nod operator de adunare.
 * 
 * @author Alin
 * 
 */
public class AddNode extends BinaryOpNode {

	/*
	 * Se creaza un nod operator de adunare si i se seteaza semnul operatiei
	 * corespunzator.
	 * 
	 */
	public AddNode() {
		super.setOperator("+");
	}

	/*
	 * @see Node#accept(Visitor)
	 */
	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}

}
