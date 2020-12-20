import java.util.HashMap;
/*
 * Clasa ajutatoare, cu care realizez evaluarea expresiilor aritmetice.
 */
public class ArithmeticExpressionEvaluator {
	private String expression;
	private Integer resultValue;
	private HashMap<String, Integer> variables;
	private ArithmeticTree aTree;
	private boolean error;

	public ArithmeticExpressionEvaluator(String expression, HashMap<String, Integer> variables) {
		this.expression = expression;
		this.variables = variables;
		this.error = false;
		this.aTree = new ArithmeticTree(expression);
		this.resultValue = evaluateExpression(aTree.getRoot());
	}

	private Integer evaluateExpression(ArithmeticNode n) {
		if (n != null && !error) { 
			if (n instanceof ValueNode) { // daca este nod de tip valoare, returneaza valoarea lui.
				return ((ValueNode) n).getValue();
			} else if(n instanceof VariableNode) {
				String varName = ((VariableNode)n).getVariableName(); // daca este variabila, ia valoarea din hashmap si o returneaza
				Integer val = variables.get(varName);
				if(variables.get(varName) == null) {
					error = true;
					return -1;
				}
				return val;
			}

			Integer a = evaluateExpression(n.getLeft()); // se evalueaza recursiv stanga dreapta
			Integer b = evaluateExpression(n.getRight());

			if (n instanceof OperatorNode) {
				if (((OperatorNode) n).getOperator() == '+') // daca operatorul este '+' sau '*', face operatia respectiva 
					return a + b;
				else
					return a * b;
			}
		}
		return -1;
	}

	public Integer getResultValue() {
		return resultValue;
	}

	public boolean isError() {
		return error;
	}
	
}
