
/**
 * Clasa unui nod ce tine informatia de tip double.
 * 
 * @author Alin
 * 
 */
public class DoubleNode extends Node {
	private Double info;

	public DoubleNode() {
	}

	public Double getInfo() {
		return info;
	}

	public void setInfo(Double info) {
		this.info = info;
	}

	/*
	 * @see Node#accept(Visitor)
	 */
	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}