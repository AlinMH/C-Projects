
/**
 * Clasa unui nod ce tine informatia de tip string.
 * 
 * @author Alin
 *
 */
public class StringNode extends Node {
	private String info;

	public StringNode() {
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
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
