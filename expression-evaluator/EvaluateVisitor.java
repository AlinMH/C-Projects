import java.util.Stack;

/**
 * Clasa visitor-ului de evaluare, ce este la randul lui un Visitor.
 * 
 * @author Alin
 * 
 */
public class EvaluateVisitor implements Visitor {
	/*
	 * Stiva rezultat unde se tin variabile de tip String, Double, Integer,
	 * NaN_double, NaN_int.
	 * 
	 * Dupa numeroase prelucrari ea va avea la sfarsit doar rezultatul final al
	 * evaluarii.
	 * 
	 */
	private Stack<Object> resultStack;

	public EvaluateVisitor() {
		resultStack = new Stack<Object>();
	}

	/*
	 * Metoda visit al unui nod de adunare.
	 * 
	 * Extrage primii doi operanzi de pe stiva si se verifica fiecare caz
	 * posibil si se introduce in stiva rezultatul corespunzator al adunarii
	 * dintre cele doua tipuri.
	 * 
	 * @see Visitor#visit(AddNode)
	 */
	@Override
	public void visit(AddNode add) {
		Object a = resultStack.pop();
		Object b = resultStack.pop();

		if (a instanceof Integer) {
			if (b instanceof Integer)
				resultStack.push((Integer) b + (Integer) a);

			else if (b instanceof Double)
				resultStack.push((Double) b + (Integer) a);

			else if (b instanceof String)
				resultStack.push(((String) b).concat(a.toString()));

			else if (b instanceof NaN_int || b instanceof NaN_double)
				resultStack.push(b);
		}

		else if (a instanceof Double) {
			if (b instanceof Integer)
				resultStack.push((Integer) b + (Double) a);

			else if (b instanceof Double)
				resultStack.push((Double) b + (Double) a);

			else if (b instanceof String)
				resultStack.push((String) b + Math.round((Double) a * 100.0) / 100.0);

			else if (b instanceof NaN_int || b instanceof NaN_double)
				resultStack.push(new NaN_double());
		}

		else if (a instanceof String) {
			if (b instanceof Integer)
				resultStack.push(b.toString().concat((String) a));

			else if (b instanceof Double)
				resultStack.push(Math.round((Double) b * 100.0) / 100.0 + (String) a);

			else if (b instanceof String)
				resultStack.push(((String) b).concat((String) a));

			else if (b instanceof NaN_int || b instanceof NaN_double)
				resultStack.push(b.toString().concat((String) a));

		}

		else if (a instanceof NaN_int) {
			if (b instanceof Integer)
				resultStack.push(a);

			else if (b instanceof Double)
				resultStack.push(new NaN_double());

			else if (b instanceof String)
				resultStack.push(((String) b).concat(a.toString()));

			else if (b instanceof NaN_int || b instanceof NaN_double)
				resultStack.push(b);
		}

		else if (a instanceof NaN_double) {
			if (b instanceof Integer || b instanceof Double)
				resultStack.push(a);

			else if (b instanceof String)
				resultStack.push(((String) b).concat(a.toString()));

			else if (b instanceof NaN_int || b instanceof NaN_double)
				resultStack.push(a);
		}

	}

	/*
	 * Metoda visit al unui nod scadere.
	 * 
	 * Se extrag primii doi operanzi de pe stiva si se verifica fiecare caz
	 * posibil, apoi se introduce in stiva rezultatul scaderii corespunzator
	 * celor doua tipuri.
	 * 
	 * @see Visitor#visit(SubNode)
	 */
	@Override
	public void visit(SubNode sub) {
		Object a = resultStack.pop();
		Object b = resultStack.pop();

		if (a instanceof Integer) {
			if (b instanceof Integer)
				resultStack.push((Integer) b - (Integer) a);

			else if (b instanceof Double)
				resultStack.push((Double) b - (Integer) a);

			else if (b instanceof String) {
				if ((Integer) a >= ((String) b).length())
					resultStack.push("");
				else if ((Integer) a < 0) {
					for (int i = 0; i < (Integer) a * (-1); i++)
						b = ((String) b).concat("#");
					resultStack.push(b);
				} else
					resultStack.push(((String) b).substring(0, ((String) b).length() - (Integer) a));
			}

			if (b instanceof NaN_int || b instanceof NaN_double)
				resultStack.push(b);
		}

		else if (a instanceof Double) {
			if (b instanceof Integer)
				resultStack.push((Integer) b - (Double) a);

			else if (b instanceof Double)
				resultStack.push((Double) b - (Double) a);

			else if (b instanceof String)
				resultStack.push(((String) b).length() - (Double) a);

			else if (b instanceof NaN_int || b instanceof NaN_double)
				resultStack.push(new NaN_double());
		}

		else if (a instanceof String) {
			if (b instanceof Integer)
				resultStack.push((Integer) b - ((String) a).length());

			else if (b instanceof Double)
				resultStack.push((Double) b - ((String) a).length());

			else if (b instanceof String)
				resultStack.push((Integer) ((String) b).length() - ((String) a).length());

			else if (b instanceof NaN_int || b instanceof NaN_double)
				resultStack.push(b);
		}

		else if (a instanceof NaN_int) {
			if (b instanceof Integer || b instanceof String)
				resultStack.push(a);

			else if (b instanceof Double)
				resultStack.push(new NaN_double());

			else if (b instanceof NaN_int || b instanceof NaN_double)
				resultStack.push(b);
		}

		else if (a instanceof NaN_double)
			resultStack.push(a);
	}

	/*
	 * Metoda visit al unui nod inmultire.
	 * 
	 * Se extrag primii doi operanzi de pe stiva si se verifica fiecare caz
	 * posibil, apoi sa introduce in stiva rezultatul inmultirii corespunzator
	 * celor doua tipuri.
	 * 
	 * @see Visitor#visit(MulNode)
	 */
	@Override
	public void visit(MulNode mul) {
		Object a = resultStack.pop();
		Object b = resultStack.pop();

		if (a instanceof Integer) {
			if (b instanceof Integer)
				resultStack.push((Integer) b * (Integer) a);

			else if (b instanceof Double)
				resultStack.push((Double) b * (Integer) a);

			else if (b instanceof String) {
				if ((Integer) a <= 0)
					resultStack.push("");
				else {
					String s = new String();
					s = (String) b;
					for (int i = 0; i < (Integer) a - 1; i++) {
						b += s;
					}
					resultStack.push(b);
				}

			} else if (b instanceof NaN_int || b instanceof NaN_double)
				resultStack.push(b);
		}

		else if (a instanceof Double) {
			if (b instanceof Integer)
				resultStack.push((Integer) b * (Double) a);

			else if (b instanceof Double)
				resultStack.push((Double) b * (Double) a);

			else if (b instanceof String)
				resultStack.push((Double) (((String) b).length() * (Double) a));

			else if (b instanceof NaN_int || b instanceof NaN_double)
				resultStack.push(new NaN_double());
		}

		else if (a instanceof String) {
			if (b instanceof Integer) {
				if ((Integer) b <= 0)
					resultStack.push("");
				else {
					String s = new String();
					s = (String) a;
					for (int i = 0; i < (Integer) b - 1; i++)
						a += s;
					resultStack.push(a);
				}
			} else if (b instanceof Double)
				resultStack.push((Double) ((Double) b * ((String) a).length()));

			else if (b instanceof String)
				resultStack.push((Integer) ((String) b).length() * ((String) a).length());

			else if (b instanceof NaN_int)
				resultStack.push("");

			else if (b instanceof NaN_double)
				resultStack.push(b);
		}

		else if (a instanceof NaN_int) {
			if (b instanceof Integer || b instanceof NaN_int)
				resultStack.push(a);

			else if (b instanceof Double || b instanceof NaN_double)
				resultStack.push(new NaN_double());

			else if (b instanceof String)
				resultStack.push("");
		}

		else if (a instanceof NaN_double)
			resultStack.push(a);
	}

	/*
	 * Metoda visit al unui nod impartire.
	 * 
	 * Se extrag primii doi operanzi de pe stiva si se verifica fiecare caz
	 * posibil, apoi sa introduce in stiva rezultatul impartirii corespunzator
	 * celor doua tipuri.
	 * 
	 * @see Visitor#visit(DivNode)
	 */
	@Override
	public void visit(DivNode div) {
		Object a = resultStack.pop();
		Object b = resultStack.pop();

		if (a instanceof Integer) {
			if ((Integer) a == 0) {
				if (b instanceof Integer)
					resultStack.push(new NaN_int());

				else if (b instanceof Double)
					resultStack.push(new NaN_double());

				else if (b instanceof String)
					resultStack.push(b);

				else if (b instanceof NaN_int || b instanceof NaN_double)
					resultStack.push(b);
			}

			else {
				if (b instanceof Integer)
					resultStack.push((Integer) b / (Integer) a);

				else if (b instanceof Double)
					resultStack.push((Double) b / (Integer) a);

				else if (b instanceof String) {
					if ((Integer) a < 0)
						resultStack.push(b);
					else
						resultStack.push(((String) b).substring(0, ((String) b).length() / (Integer) a));

				} else if (b instanceof NaN_int || b instanceof NaN_double)
					resultStack.push(b);
			}
		}

		else if (a instanceof Double) {
			if ((Double) a == 0)
				resultStack.push(new NaN_double());
			else {
				if (b instanceof Integer)
					resultStack.push((Integer) b / (Double) a);

				else if (b instanceof Double)
					resultStack.push((Double) b / (Double) a);

				else if (b instanceof String)
					resultStack.push(((String) b).length() / (Double) a);

				else if (b instanceof NaN_int || b instanceof NaN_double)
					resultStack.push(new NaN_double());
			}

		}

		else if (a instanceof String) {
			if (b instanceof Integer) {
				if (((String) a).length() == 0)
					resultStack.push(new NaN_int());
				else
					resultStack.push((Integer) b / ((String) a).length());
			} else if (b instanceof Double) {
				if (((String) a).length() == 0)
					resultStack.push(new NaN_double());
				else
					resultStack.push((Double) b / ((String) a).length());

			} else if (b instanceof String)
				resultStack.push((Integer) ((String) b).length() / ((String) a).length());

			else if (b instanceof NaN_int || b instanceof NaN_double)
				resultStack.push(b);
		}

		else if (a instanceof NaN_int) {
			if (b instanceof Integer || b instanceof NaN_int || b instanceof String)
				resultStack.push(a);

			else if (b instanceof Double || b instanceof NaN_double)
				resultStack.push(new NaN_double());
		}

		else if (a instanceof NaN_double)
			resultStack.push(a);
	}

	/*
	 * Metoda visit al unui nod Int.
	 * 
	 * Informatia din nod se introduce in stiva de rezultate.
	 * 
	 * @see Visitor#visit(IntNode)
	 */
	@Override
	public void visit(IntNode n) {
		resultStack.push(n.getInfo());
	}

	/*
	 * Metoda visit al unui nod String.
	 * 
	 * Informatia din nod se introduce in stiva de rezultate.
	 * 
	 * @see Visitor#visit(StringNode)
	 */
	@Override
	public void visit(StringNode n) {
		resultStack.push(n.getInfo());

	}

	/*
	 * Metoda visit al unui nod Double.
	 * 
	 * Informatia din nod se introduce in stiva de rezultate.
	 * 
	 * @see Visitor#visit(DoubleNode)
	 */
	@Override
	public void visit(DoubleNode n) {
		resultStack.push(n.getInfo());
	}

	/*
	 * Metoda de returnare a rezultatului final.
	 * 
	 * Daca rezultatul este de tip double, acesta sa rotunjeste folosind metoda
	 * round din clasa Math si se returneaza ulterior.
	 */
	public String getTotalResult() {
		Object res = resultStack.peek();

		if (res instanceof Double)
			return ((Double) (Math.round((Double) res * 100.0) / 100.0)).toString();

		return res.toString();
	}

}
