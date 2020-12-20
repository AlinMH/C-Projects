import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
	public static void main(String[] args) {
		BufferedReader br = null;
		BufferedWriter bw = null;
		ProgramTree progTree = null;
		Interpreter inter = new Interpreter();

		try {
			br = new BufferedReader(new FileReader(args[0]));
			bw = new BufferedWriter(new FileWriter(args[1]));

			String line;
			String impPPCode = "";

			while ((line = br.readLine()) != null) {
				impPPCode += line + " ";
			}
			// se elimina caracterele tab si new-line
			impPPCode = impPPCode.replace("\t", "").replace("\n", "");
			progTree = new ProgramTree(impPPCode);
			progTree.getRoot().accept(inter);
			
			if(inter.isScopeFlag())
				bw.write("Check failed");
			else if(inter.isReturnFlag())
				bw.write("Missing return");
			else if(inter.isAssertFlag())
				bw.write("Assert failed");
			else
				bw.write(inter.getResult().toString());

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
