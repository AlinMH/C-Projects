import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * Clasa Main in care se face citire din fisierul arbore.in si scrierea in
 * fisierul arbore.out.
 * 
 * Folosim BufferedReader si BufferedWrinter din motive de eficienta, se citeste
 * linie cu linie daca gasim declarari de variabila pe linia respectiva o
 * adaugam in HashMap, daca gasim o evaluare de expresie se creaza aborele de
 * parsare aferent, se parcurge cu visit-orul de evaluar si se scrie rezultatul in fisierul de output.
 * 
 * @author Alin
 *
 */
public class Main {
	public static void main(String[] args) {
		BufferedReader br = null;
		BufferedWriter bw = null;

		try {
			br = new BufferedReader(new FileReader("arbore.in"));
			bw = new BufferedWriter(new FileWriter("arbore.out"));

			HashMap<String, Object> vars = new HashMap<String, Object>();

			String line;
			while ((line = br.readLine()) != null) {
				if (line.charAt(0) == 'i') {
					String varname = line.substring(4, line.indexOf("=") - 1);
					String value = line.substring(line.indexOf("=") + 2, line.indexOf(";"));

					vars.put(varname, new Integer(Integer.parseInt(value)));

				} else if (line.charAt(0) == 'd') {
					String varname = line.substring(7, line.indexOf("=") - 1);
					String value = line.substring(line.indexOf("=") + 2, line.indexOf(";"));

					vars.put(varname, new Double(Double.parseDouble(value)));
				}

				else if (line.charAt(0) == 's') {
					String varname = line.substring(7, line.indexOf("=") - 1);
					String value = line.substring(line.indexOf("=") + 3, line.indexOf(";") - 1);

					vars.put(varname, value);
				}

				else if (line.charAt(0) == 'e') {
					ExpressionTree tree = new ExpressionTree(line.substring(5, line.indexOf(";")), vars);
					EvaluateVisitor eVisitor = new EvaluateVisitor();
					tree.createTree();
					tree.traverseWithVisitor(tree.getRoot(), eVisitor);

					bw.write(eVisitor.getTotalResult());
					bw.newLine();
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
