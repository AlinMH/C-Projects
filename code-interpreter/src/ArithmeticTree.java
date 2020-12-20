import java.util.Stack;


/*
 * Clasa unui arbore aritmetic.
 */
public class ArithmeticTree {
	private ArithmeticNode root;
	private String expression;
	private Stack<ArithmeticNode> stack; //stiva este folosita pentru a construi arborele
	
	public ArithmeticTree(String expression) {
		this.expression = expression;
		this.stack = new Stack<ArithmeticNode>();
		buildArithmeticTree();
	}

	
	private void buildArithmeticTree() {
		if(Character.isLetter(expression.charAt(0))) { // cazul in care se primeste o variabila ca expresie ex: "x"
			root = new VariableNode(expression);
			return;
		}
		else if(Character.isDigit(expression.charAt(0))) {
			root = new ValueNode(Integer.valueOf(expression));  // cazul in care se primeste un numar ca expresie ex: "2"
			return;
		}
		//fiind expresia prefixata, am parcurs expresia de la sfarsit
		for (int i = expression.length() - 1; i > 0; i--) {
			char c = expression.charAt(i);

			// daca intalnim o operatie, se face pop pentru cei doi operatori
			// se seteaza fiul stanga si dreapta, apoi se pune nodul inapoi pe stiva
			if (c == '*' || c == '+') {
				ArithmeticNode n = new OperatorNode(c);
				n.setLeft(stack.pop());
				n.setRight(stack.pop());
				stack.push(n);
			//ignoram ' ', ']', '['
			} else if (c == ' ' || c == ']' || c == '[')
				continue;
			//daca gasim un numar, se creaza nodul valoare, cu valoarea intreaga respectiva
			if (Character.isDigit(c)) {
				int loc = expression.lastIndexOf(' ', i);
				Integer value = Integer.valueOf(expression.substring(loc + 1, i + 1));
				i = loc;
				ArithmeticNode valueNode = new ValueNode(value);
				stack.push(valueNode);
			//daca este o variabila, se creaza nodul variabila
			//este tratat si cazul in care variabila are mai mult de o litera
			} else if(Character.isLetter(c)){
				int loc = expression.lastIndexOf(' ', i);
				String variableName = expression.substring(loc + 1, i + 1);
				i = loc;
				ArithmeticNode variableNode = new VariableNode(variableName);
				stack.push(variableNode);
			}
		}
		//la sfarsit o sa fie un singur nod pe stiva, iar acela va deveni radacina arborelui
		root = stack.pop();
	}
	

	public ArithmeticNode getRoot() {
		return root;
	}

	public void setRoot(ArithmeticNode root) {
		this.root = root;
	}

}
