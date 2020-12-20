import java.util.HashMap;
import java.util.Stack;

/**
 * Clasa unui arbore de parsare.
 * 
 * @author Alin
 * 
 */
public class ExpressionTree {
	/*
	 * Expresia in forma postfixata sau RPN ( Reverse Polish Notation )
	 */
	private String postfixExpression;
	private Node root;

	/*
	 * Stiva de noduri, ajutatoare pentru crearea arborelui
	 */
	private Stack<Node> nodeStack;

	/*
	 * Transformarea ajutoatore pentru a ajunge la forma postfixata
	 *
	 */
	private InfixToPostfix transf;

	/*
	 * Variabilele sunt tinute intr-un hashmap pentru eficienta accesarii
	 * acestora.
	 */
	private HashMap<String, Object> vars;

	/**
	 * Constructorul primeste expresia infixata, cu ajutorul transformarii, o
	 * transforma in forma postfixata si membrului postfixExpression ii este
	 * atribuit rezultatul transformarii.
	 * 
	 * @param expression
	 *            Expresia algebrica in forma infixata
	 * @param vars
	 *            variabilele stocate
	 * 
	 */
	public ExpressionTree(String expression, HashMap<String, Object> vars) {
		nodeStack = new Stack<Node>();
		this.vars = vars;

		transf = new InfixToPostfix(expression);
		transf.doTrans();

		postfixExpression = transf.getOutput();

	}

	/**
	 * Metoda care genereaza arborele de parsare.
	 * 
	 * Se parcurge fiecare caracter din expresie, daca e operand se creaza nodul
	 * corespunzator si i se seteaza valoarea gasita din hashmap, se adauga in
	 * stiva de noduri, daca e operatie, se creaza nodul operatie, se extrag
	 * primele 2 noduri din stiva de noduri si se seteaza membrul stang si drept
	 * al nodului operatie care a fost creat anterior.
	 * 
	 * La sfarsit vom avea pe stiva un singur nod care reprezinta radacina
	 * intregului arbore pe care-l setam cu membrul root din clasa.
	 */
	public void createTree() {
		for (int i = 0; i < postfixExpression.length(); i++) {
			Character ch = postfixExpression.charAt(i);
			if (transf.isOperator(ch)) {
				Node op = SingletonNodeFactory.getInstance().createNode(ch.toString());
				op.setRight(nodeStack.pop());
				op.setLeft(nodeStack.pop());
				nodeStack.push(op);

			} else if (ch == ' ') {
				continue;
			} else {
				int begin = i;
				int end = i;
				for (int j = i; j < postfixExpression.length(); j++) {
					if (!transf.isOperator(postfixExpression.charAt(j)) && postfixExpression.charAt(j) != ' ')
						end++;
					else
						break;
				}
				String varName = postfixExpression.substring(begin, end);
				Object value = vars.get(varName);

				Node n = null;

				if (value instanceof Integer) {
					n = (IntNode) SingletonNodeFactory.getInstance().createNode("int");
					((IntNode) n).setInfo((Integer) value);
				}

				else if (value instanceof Double) {
					n = (DoubleNode) SingletonNodeFactory.getInstance().createNode("double");
					((DoubleNode) n).setInfo((Double) value);
				}

				else if (value instanceof String) {
					n = (StringNode) SingletonNodeFactory.getInstance().createNode("string");
					((StringNode) n).setInfo((String) value);
				}

				nodeStack.push(n);
				i = end - 1;
			}
		}
		setRoot(nodeStack.pop());
	}

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	/**
	 * Se viziteaza recursiv fiecare nod din abore in postordine cu ajutorul
	 * visit-orului, folosing metoda accept din fiecare nod care este
	 * "vizitabil".
	 * 
	 * @param n
	 *            nodul din arbore
	 * @param v
	 *            visit-orul
	 */
	void traverseWithVisitor(Node n, Visitor v) {
		if (n == null)
			return;
		traverseWithVisitor(n.getLeft(), v);
		traverseWithVisitor(n.getRight(), v);
		n.accept(v);
	}

}
