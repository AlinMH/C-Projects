
/**
 * Clasa singleton factory care creaza orice tip de nod.
 * 
 * @author Alin
 *
 */
public class SingletonNodeFactory {
	/*
	 * Membrul instance este folosit deoarece clasa este Singleton.
	 */
	private static SingletonNodeFactory instance = null;

	private SingletonNodeFactory() {
	}

	/**
	 * Lazy initialization
	 * 
	 * @return instanta factory.
	 */
	public static SingletonNodeFactory getInstance() {
		if (instance == null) {
			instance = new SingletonNodeFactory();
		}
		return instance;
	}

	/**
	 * Metoda factory pentru noduri.
	 * @param type
	 *            tipul de nod
	 * @return nodul cerut
	 */
	public Node createNode(String type) {
		switch (type) {
		case "+":
			return new AddNode();
		case "-":
			return new SubNode();
		case "*":
			return new MulNode();
		case "/":
			return new DivNode();
		case "int":
			return new IntNode();
		case "double":
			return new DoubleNode();
		case "string":
			return new StringNode();
		default:
			return null;
		}
	}

}
