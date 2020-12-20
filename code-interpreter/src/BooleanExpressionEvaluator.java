import java.util.HashMap;
/*
 * Clasa cu care se evalueaza o expresie booleana.
 */
public class BooleanExpressionEvaluator {
	private String expression;
	private HashMap<String, Integer> variables; //In hashmap se tin variabilele

	private boolean error;
	private boolean resultValue;
	private int pos;

	//metoda auxiliara pentru a extrage o subexpresie
	private String getBlock() {
		int par = 0; //numar de paranteze
		int end = 0;
		int index = pos;
		for (int i = index; i < expression.length(); i++) {
			if (expression.charAt(i) == '[')
				par++;
			else if (expression.charAt(i) == ']')
				par--;
			if (par == 0) {
				end = i + 1;
				pos = end; // se actualizeaza pozitia
				break;
			}
		}
		return expression.substring(index, end);
	}

	public BooleanExpressionEvaluator(String expression, HashMap<String, Integer> variables) {
		this.expression = expression;
		this.variables = variables;
		this.error = false;
		evaluate();
	}

	//functia care evalueaza expresia data
	public void evaluate() {
		//eval1 si eval2 sunt folosite deoarece se poate evalua o expresie de forma [= <expr> <expr>]
		ArithmeticExpressionEvaluator eval1 = null;
		ArithmeticExpressionEvaluator eval2 = null;
		pos = expression.indexOf(' ') + 1;

		//cazul in care primul operand este tot o expresie se foloseste metoda getBloc pentru a o extrage
		if (expression.charAt(pos) == '[') {
			eval1 = new ArithmeticExpressionEvaluator(getBlock(), variables);
			if (eval1.isError()) {
				error = eval1.isError();
				return;
			}
		//cazul in care este o variabila/valoare	
		} else {
			int index = expression.indexOf(' ', pos);
			eval1 = new ArithmeticExpressionEvaluator(expression.substring(pos, index), variables);
			pos = index;
			if (eval1.isError()) {
				error = eval1.isError();
				return;
			}
		}
		//aceleasi cazuri, doar ca sunt evaluate pentru al doilea operand
		if (expression.charAt(pos + 1) == '[') {
			pos = pos + 1;
			eval2 = new ArithmeticExpressionEvaluator(getBlock(), variables);
			if (eval2.isError()) {
				error = eval2.isError();
				return;
			}
		} else {
			eval2 = new ArithmeticExpressionEvaluator(expression.substring(pos + 1, expression.length() - 1), variables);
			if (eval2.isError()) {
				error = eval2.isError();
				return;
			}
		}
		
		//cele doua cazuri posibile '<' si '=', se verifica daca valoarea este adevarat sau fals.
		if (expression.charAt(1) == '<') {
			resultValue = eval1.getResultValue().intValue() < eval2.getResultValue().intValue();
		} else if (expression.charAt(1) == '=') {
			resultValue = eval1.getResultValue().intValue() == eval2.getResultValue().intValue();
		}

	}

	public boolean isError() {
		return error;
	}

	public boolean isResultValue() {
		return resultValue;
	}
}
