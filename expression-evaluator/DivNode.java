
/**
 * Clasa unui nod operator de impartire.
 * 
 * @author Alin
 * 
 */
public class DivNode extends BinaryOpNode {

	/*
	 * Se creaza un nod operator de impartire si i se seteaza semnul operatiei
	 * corespunzator.
	 * 
	 */
	public DivNode() {
		super.setOperator("/");
	}

	/*
	 * @see Node#accept(Visitor)
	 */
	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
