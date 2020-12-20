
public interface Visitor {
	public void visit(SequenceNode n);
	public void visit(AssignNode n);
	public void visit(AssertNode n);
	public void visit(IfNode n);
	public void visit(ForNode n);
	public void visit(ReturnNode n);
}
