
/**
 * Clasa Visitor are cate o metoda visit pentru orice tip de nod (operatie sau
 * nod cu informatie).
 * 
 * @author Alin
 *
 */
public interface Visitor {
	public void visit(AddNode add);
	public void visit(SubNode sub);
	public void visit(MulNode mul);
	public void visit(DivNode div);

	public void visit(IntNode n);
	public void visit(StringNode n);
	public void visit(DoubleNode n);
}
