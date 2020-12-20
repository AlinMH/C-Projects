import java.util.ArrayList;
import java.util.LinkedHashMap;

public class OperationSolver {
	String[] operations;
	ArrayList<ArrayList<Object>> partialEntries;
	LinkedHashMap<String, String> cols;

	public OperationSolver(String[] operations, ArrayList<ArrayList<Object>> partialEntries,
			LinkedHashMap<String, String> cols) {
		this.operations = operations;
		this.partialEntries = partialEntries;
		this.cols = cols;
	}
	
	
	ArrayList<ArrayList<Object>> solve() {
		ArrayList<ArrayList<Object>> solvedList = new ArrayList<>();
		
		for(int i = 0; i < operations.length; i++) {
			int idx = operations[i].indexOf('('); // se verifica daca este o operatie de agregare sau o coloana
			
			if(idx != -1) { // daca este o operatie de agregare (are paranteze)
				String op = operations[i].substring(0, idx);
				String colName = operations[i].substring(idx + 1, operations[i].lastIndexOf(')'));
				
				int pos = new ArrayList<String>(cols.keySet()).indexOf(colName);
	
				if(op.equals("avg")) {
					int sum = 0;
					for(int j = 0; j < partialEntries.size(); j++) {
						sum += (int)partialEntries.get(j).get(pos);
					}
					
					sum /= partialEntries.size();
					
					ArrayList<Object> arr = new ArrayList<Object>();
					arr.add(sum);
					solvedList.add(arr);
					
					
				} else if(op.equals("min")) {
					int min = Integer.MAX_VALUE;
					
					for (ArrayList<Object> arrayList : partialEntries) {
						if((int)arrayList.get(pos) < min) {
							min = (int)arrayList.get(pos);
						}
					}
					ArrayList<Object> arr = new ArrayList<Object>();
					arr.add(min);
					solvedList.add(arr);
				} else if(op.equals("max")) {
					int max = Integer.MIN_VALUE;
					
					for (ArrayList<Object> arrayList : partialEntries) {
						if((int)arrayList.get(pos) > max) {
							max = (int)arrayList.get(pos);
						}
					}
					ArrayList<Object> arr = new ArrayList<Object>();
					arr.add(max);
					solvedList.add(arr);
				} else if(op.equals("sum")) {
					int sum = 0;
					for(int j = 0; j < partialEntries.size(); j++) {
						sum += (int)partialEntries.get(j).get(pos);
					}
					
					ArrayList<Object> arr = new ArrayList<Object>();
					arr.add(sum);
					solvedList.add(arr);
				} else if(op.equals("count")) {
					ArrayList<Object> arr = new ArrayList<Object>();
					arr.add(partialEntries.size());
					solvedList.add(arr);
				}
			} else { // daca este o coloana, se va scoate coloana respectiva din intrari (de pe pozitia pos)
				int pos = new ArrayList<String>(cols.keySet()).indexOf(operations[i]);
				ArrayList<Object> arr = new ArrayList<Object>();
				
				for (ArrayList<Object> objList : partialEntries) {
					arr.add(objList.get(pos));
				}
				
				solvedList.add(arr);
			}
		}
		return solvedList;
	}
	
}
