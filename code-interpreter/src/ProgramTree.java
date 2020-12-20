/*
 * Clasa unui Abstract Syntax Tree, care are noduri de tip Program.
 * Acesta va fi evaluat ulterior de un Visitor.
 */
public class ProgramTree {
	private ProgramNode root;
	private String impPPCode;
	private int currentPos;

	public ProgramTree(String impPPCode) {
		this.impPPCode = impPPCode;
		this.root = buildTree();
	}

	//metoda auxiliata pentru a extrage urmatorul program. (ex: ';' , '=', etc).
	private String getProgram() {
		int pos = currentPos;
		currentPos = impPPCode.indexOf("[", pos + 1);
		if (currentPos < 0) // daca nu mai gasesc '[' inseamna ca am ajuns la sfarsit si caut ']'
			return impPPCode.substring(pos, impPPCode.indexOf("]", pos + 1) + 1);
		return impPPCode.substring(pos, impPPCode.indexOf("[", pos + 1));
	}

	//metoda auxiliara pentru a extrage o expresia care contine subexpresii in ea.
	private String getBlock() {
		int par = 0; //numarul de paranteze.
		int end = 0;
		int index = currentPos;
		for (int i = index; i < impPPCode.length(); i++) {
			if (impPPCode.charAt(i) == '[')
				par++;
			else if (impPPCode.charAt(i) == ']')
				par--;
			if (par == 0) {
				end = i + 1;
				currentPos = impPPCode.indexOf('[', end); // se actualieaza pozitia.
				break;
			}
		}
		return impPPCode.substring(index, end);
	}

	//metoda de creare a arborelui
	private ProgramNode buildTree() {
		String nextProgram = getProgram(); // se ia primul program gasit
		String variableName;

		//nod de tip assign
		if (nextProgram.charAt(1) == '=') {
			int nextIndex = nextProgram.indexOf(' ', 3);
			variableName = nextProgram.substring(3, nextIndex);
			if (nextIndex + 1 == nextProgram.length()) { //daca urmeaza o subexpresie, trebuie extrasa si adaugata in nod.
				String expression = getBlock();
				return new AssignNode(variableName, expression);
			} else { //daca este o valoare sau variabile
				return new AssignNode(variableName, nextProgram.substring(nextIndex + 1, nextProgram.indexOf(']')));
			}
		
		//nod de tip return
		} else if (nextProgram.charAt(1) == 'r') {
			if (nextProgram.length() == 8) { // daca urmeaza o subexpresie
				String prog = getBlock();
				return new ReturnNode(prog);
			} else { //cazul in care avem o valoare sau variabila
				variableName = nextProgram.substring(8, nextProgram.length() - 1);
				return new ReturnNode(variableName);
			}
		//nod de tip assert	
		} else if (nextProgram.charAt(1) == 'a') {
			String prog = getBlock();
			return new AssertNode(prog);

		//nod de tip if
		} else if (nextProgram.charAt(1) == 'i') {
			String condition = getBlock();
			String thenProg = getBlock();
			String elseProg = getBlock();
			return new IfNode(condition, thenProg, elseProg);
		//nod de tip for
		} else if (nextProgram.charAt(1) == 'f') {
			String init = getBlock();
			String condition = getBlock();
			String increment = getBlock();
			String prog = getBlock();
			return new ForNode(init, condition, increment, prog);
		//nod de tip sequence
		} else if (nextProgram.charAt(1) == ';') {
			ProgramNode left = buildTree();
			ProgramNode right = buildTree();
			return new SequenceNode(left, right);
		}
		return null;
	}

	public int getCurrentPos() {
		return currentPos;
	}
	
	public ProgramNode getRoot() {
		return root;
	}
}
