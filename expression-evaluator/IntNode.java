
/**
 * Clasa unui nod ce tine informatia de tip integer.
 * 
 * @author Alin
 *
 */
public class IntNode extends Node {
	private Integer info;

	public IntNode() {
	}

	public Integer getInfo() {
		return info;
	}

	public void setInfo(Integer info) {
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
