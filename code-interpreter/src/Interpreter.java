import java.util.HashMap;
/*
 * Clasa concreta visitor, care interpreteaza un program. (un arbore de expresie ce tine un program in structura lui).
 */
public class Interpreter implements Visitor {
	//variabilele sunt tinute intr-un hashmap pentru eficienta
	private HashMap<String, Integer> variables;
	
	//flag-uri pentru erori
	private boolean assertFlag = false;
	private boolean scopeFlag = false; // la fiecare vizitare de nod acesta va fi actualizat. 
								       //(fiind cel mai prioritar, daca se intalneste o eroare de scope, nu se mai evalueaza nimic)
	private boolean returnFlag = true; //acest flag este true la inceput, iar daca se viziteaza un nod de tip return, devine false.
	
	//rezultatul final
	private Integer result;

	public Interpreter() {
		this.variables = new HashMap<String, Integer>();
	}

	@Override
	// In cazul unui nod sequence, se "lanseaza" accept pe stanga si dreapta, cat timp nu avem erori de scope.
	public void visit(SequenceNode n) {
		if (n.getLeft() != null && !scopeFlag) {
			n.getLeft().accept(this);
		}
		if (n.getRight() != null && !scopeFlag) {
			n.getRight().accept(this);
		}
	}

	@Override
	// In cazul unui nod assign, se adauga variabila cu valoarea corespunzatoare in hashmap.
	public void visit(AssignNode n) {
		String v = n.getVariable();
		String e = n.getExpression();
		ArithmeticExpressionEvaluator eval = new ArithmeticExpressionEvaluator(e, variables);
		if (eval.isError()) {
			scopeFlag = true;
			return;
		}
		variables.put(v, eval.getResultValue()); //in cazul in care este o subexpresie, se evalueaza.
	}

	@Override
	// In cazul unui nod assert, se evalueaza expresia si se updateaza flag-ul. 
	public void visit(AssertNode n) {
		String e = n.getExpression();
		BooleanExpressionEvaluator eval = new BooleanExpressionEvaluator(e, variables);
		if (eval.isError()) {
			scopeFlag = true;
			return;
		}
		assertFlag = !eval.isResultValue();
	}

	@Override
	// In cazul unui nod if, se evalueaza conditia, si daca este true sau false, se viziteaza programul then sau else.
	public void visit(IfNode n) {
		String cond = n.getCondition();
		BooleanExpressionEvaluator eval = new BooleanExpressionEvaluator(cond, variables);
		if (eval.isError()) {
			scopeFlag = true;
			return;
		}
		if (eval.isResultValue()) {
			n.getThenTree().accept(this);
		} else {
			n.getElseTree().accept(this);
		}
	}

	@Override
	// In cazul unui nod for, se viziteaza nodul init, apoi se verifica conditia,
	//daca este satisfacuta, pentru a stii daca e cazul sa continuam la incrementare
	public void visit(ForNode n) {
		n.getInitNode().accept(this);
		
		if(!scopeFlag) {
			BooleanExpressionEvaluator eval = new BooleanExpressionEvaluator(n.getCondition(), variables);
			if(eval.isError()) {
				scopeFlag = true;
				return;
			}
			
			//cat timp conditia este satisfacuta, se viziteaza programul din for, si se incrementeaza.
			while(eval.isResultValue() && !scopeFlag) {
				n.getProgTree().accept(this);
				n.getIncrementNode().accept(this);
				eval.evaluate();
			}
		}
	}

	@Override
	// In cazul unui nod return, se returneaza valoarea evaluata si se seteaza returnFlag pe false.
	public void visit(ReturnNode n) {
		String e = n.getExpression();
		ArithmeticExpressionEvaluator eval = new ArithmeticExpressionEvaluator(e, variables);
		if (eval.isError()) {
			scopeFlag = true;
			return;
		}
		returnFlag = false;
		result = eval.getResultValue();
	}
	
	public Integer getResult() {
		return result;
	}
	
	public boolean isScopeFlag() {
		return scopeFlag;
	}

	public boolean isAssertFlag() {
		return assertFlag;
	}
	
	public boolean isReturnFlag() {
		return returnFlag;
	}

}
