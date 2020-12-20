import java.util.Stack;

/**
 * Clasa transformarii din expresie infixata in postfixata.
 * 
 * @author Alin
 *
 */
public class InfixToPostfix {
	private String input;
	private String output;
	private Stack<String> stack;

	/**
	 * Constructorul transformarii.
	 * 
	 * Expresiei infixata ii sunt eliminate space-urile, pentru usurinta.
	 * 
	 * @param input
	 *            expresia in forma infixata
	 */
	public InfixToPostfix(String input) {
		this.input = input.replaceAll("\\s+", "");
		this.output = new String();
		stack = new Stack<String>();
	}

	/**
	 * Metoda care face transformarea din forma infixata in postfixata.
	 * 
	 * Se parcurge fiecare caracter din expresie, daca se gaseste un simbol al
	 * operariei, i se parseaza operatia si prioritarea ( reprezantata de o
	 * valoarea intreaga 1 pentru adunare/scadere, 2 pentru inmultire/impartire
	 * ).
	 * 
	 * Daca se gaseste un operand i se salveaza indexul din string (initializand
	 * variabilei begin cu indexul corespunzator) si se incrementeaza variabila
	 * end pentru fiecare caracter care nu este operator sau paranteza, apoi se
	 * introduce in stiva numele variabilei folosing metoda substring aplicata
	 * pe begin si end si i-ul este updatat la final pentru a continua iteratia.
	 */
	public void doTrans() {
		for (int i = 0; i < input.length(); i++) {
			Character ch = input.charAt(i);
			switch (ch) {
			case '+':
			case '-':
				processingOperator(ch, 1);
				break;
			case '*':
			case '/':
				processingOperator(ch, 2);
				break;
			case '(':
				stack.push(ch.toString());
				break;
			case ')':
				closeParenthesis();
				break;
			default:
				int begin = i;
				int end = i;
				for (int j = i; j < input.length(); j++) {
					if (!isOperator(input.charAt(j)) && input.charAt(j) != '(' && input.charAt(j) != ')')
						end++;
					else
						break;
				}
				output = output + " " + input.substring(begin, end);
				i = end - 1;
				break;
			}
		}
		while (!stack.isEmpty()) {
			output = output + " " + stack.pop();
		}
		output = output.substring(1);
	}

	boolean isOperator(char c) {
		if (c == '*' || c == '/' || c == '+' || c == '-')
			return true;
		return false;
	}

	/**
	 * Se introduce operatorul in stiva in functie de prioritatea data.
	 * 
	 * @param operator
	 *            simbolul operatiei
	 * @param priority1
	 *            prioritatea
	 */
	public void processingOperator(Character operator, int priority1) {
		while (!stack.isEmpty()) {
			Character opTop = stack.pop().charAt(0);
			if (opTop == '(') {
				stack.push("(");
				break;
			} else {
				int priority2;
				if (opTop == '+' || opTop == '-')
					priority2 = 1;
				else
					priority2 = 2;
				if (priority2 < priority1) {
					stack.push(opTop.toString());
					break;
				} else
					output = output + " " + opTop;
			}
		}
		stack.push(operator.toString());
	}

	/**
	 * In cazul in care se ajunge la inchiderea parantezei, tot ce e pe stiva se
	 * concateneaza la rezultatul final (output) pana cand se ajunge la
	 * paranteza deschisa.
	 */
	public void closeParenthesis() {
		while (!stack.isEmpty()) {
			Character c = stack.pop().charAt(0);
			if (c == '(')
				break;
			else
				output = output + " " + c;
		}
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

}
