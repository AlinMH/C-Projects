import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

public class Database implements MyDatabase {
	int numWorkerThreads;
	HashMap<String, Table> tables; // hash de tabele

	public Database() {
		this.tables = new HashMap<>();
	}

	@Override
	public void initDb(int numWorkerThreads) {
		this.numWorkerThreads = numWorkerThreads;

	}

	@Override
	public void stopDb() {
	}

	@Override
	public void createTable(String tableName, String[] columnNames, String[] columnTypes) {
		LinkedHashMap<String, String> lm = new LinkedHashMap<>();

		// se creaza o tabela si se vor adauga coloanele
		for (int i = 0; i < columnNames.length; i++) {
			lm.put(columnNames[i], columnTypes[i]);
		}

		Table newTable = new Table(tableName, lm);
		tables.put(tableName, newTable);
	}

	@Override
	public ArrayList<ArrayList<Object>> select(String tableName, String[] operations, String condition) {
		Table t = tables.get(tableName);

		// se verifica daca este o tranzactie activa, iar daca id-ul threadului curent nu corespunde cu id-ul threadului de a facut tranzactie, o sa-l puna in asteptare
		synchronized (t) {
			if (t.transactionRunning && t.transactionThreadId != Thread.currentThread().getId()) {
				try {
					t.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		//se incrementeaza numarul de cititori si de procese din tabela
		synchronized (t) {
			t.readCount++;
			t.processesCount++;
			if (t.readCount == 1) { // primul cititor este responsabil sa blocheze scriitorii
				try {
					t.w.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		ArrayList<ArrayList<Object>> partialEntries = new ArrayList<>();
		OperationSolver opSolver;
		if (condition.equals("")) {
			partialEntries = (ArrayList<ArrayList<Object>>) t.entries.clone(); // daca nu avem conditie, inseamna ca se vor face operatii pe toata tabela

		} else {
			StringTokenizer toks = new StringTokenizer(condition, " ");
			String colName = toks.nextToken();
			String comparator = toks.nextToken();
			String val = toks.nextToken();
			String type = t.cols.get(colName);

			int pos = new ArrayList<String>(t.cols.keySet()).indexOf(colName); // pozitia coloanei intr-o inregistrare (lista)

			switch (type) {
			case "int":
				Integer i = Integer.parseInt(val);
				switch (comparator) {
				case "<":
					for (ArrayList<Object> objList : t.entries) {
						if ((int) objList.get(pos) < i) { // daca se respecta conditia, se va adauga intrarea in lista filtrata
							partialEntries.add(objList);
						}
					}
					break;
					//la fel si mai jos
				case ">":
					for (ArrayList<Object> objList : t.entries) {
						if ((int) objList.get(pos) > i) {
							partialEntries.add(objList);
						}
					}
					break;

				case "==":
					for (ArrayList<Object> objList : t.entries) {
						if ((int) objList.get(pos) == i) {
							partialEntries.add(objList);
						}
					}
					break;

				default:
					break;
				}

				break;

			case "bool":
				Boolean b = Boolean.parseBoolean(val);
				switch (comparator) {
				case "==":
					for (ArrayList<Object> objList : t.entries) {
						if ((Boolean) objList.get(pos) == b) {
							partialEntries.add(objList);

						}
					}
					break;
				default:
					break;
				}
				break;

			case "string":
				String s = val;
				switch (comparator) {
				case "==":
					for (ArrayList<Object> objList : t.entries) {
						if (((String) objList.get(pos)).compareTo(s) == 0) {
							partialEntries.add(objList);
						}
					}
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
		}

		opSolver = new OperationSolver(operations, partialEntries, ((Table) t).cols);

		// se decrementeaza numarul de cititori si numarul de procese din tabela
		synchronized (t) {
			t.readCount--;
			t.processesCount--;

			if (t.readCount == 0) { // ultimul cititor este responsabil sa dea voie scriitorilor in tabela
				t.w.release();
			}
		}
		return opSolver.solve();
	}

	@Override
	public void update(String tableName, ArrayList<Object> values, String condition) {
		Table t = tables.get(tableName);

		// se verifica daca este o tranzactie activa, iar daca id-ul threadului curent nu corespunde cu id-ul threadului de a facut tranzactie, o sa-l puna in asteptare
		synchronized (t) {
			if (t.transactionRunning && t.transactionThreadId != Thread.currentThread().getId()) {
				try {
					t.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			t.processesCount++;
		}

		try {
			t.w.acquire(); // sunt blocati ceilalti scriitori
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//identic ca la select, doar ca nu mai este nevoie de o lista auxiliara

		StringTokenizer toks = new StringTokenizer(condition, " ");
		String colName = toks.nextToken();
		String comparator = toks.nextToken();
		String val = toks.nextToken();
		String type = t.cols.get(colName);

		int pos = new ArrayList<String>(t.cols.keySet()).indexOf(colName);
		int index = 0;

		switch (type) {
		case "int":
			Integer i = Integer.parseInt(val);
			switch (comparator) {
			case "<":
				for (ArrayList<Object> objList : t.entries) {
					if ((int) objList.get(pos) < i) {
						t.entries.set(index, values);
					}
					index++;
				}
				break;

			case ">":
				for (ArrayList<Object> objList : t.entries) {
					if ((int) objList.get(pos) > i) {
						t.entries.set(index, values);
					}
					index++;
				}
				break;

			case "==":
				for (ArrayList<Object> objList : t.entries) {
					if ((int) objList.get(pos) == i) {
						t.entries.set(index, values);
					}
					index++;
				}
				break;

			default:
				break;
			}

			break;

		case "bool":
			Boolean b = Boolean.parseBoolean(val);
			switch (comparator) {
			case "==":
				for (ArrayList<Object> objList : t.entries) {
					if ((Boolean) objList.get(pos) == b) {
						t.entries.set(index, values);
					}
					index++;
				}
				break;
			default:
				break;
			}
			break;

		case "string":
			String s = val;
			switch (comparator) {
			case "==":
				for (ArrayList<Object> objList : t.entries) {
					if (((String) objList.get(pos)).compareTo(s) == 0) {
						t.entries.set(index, values);
					}
					index++;
				}
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}

		t.processesCount--;

		t.w.release();

	}

	@Override
	public void insert(String tableName, ArrayList<Object> values) {
		Table t = tables.get(tableName);

		// la fel ca la celelalte operatii pe tabela
		synchronized (t) {
			if (t.transactionRunning && t.transactionThreadId != Thread.currentThread().getId()) {
				try {
					t.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			t.processesCount++;
		}

		try {
			t.w.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		tables.get(tableName).entries.add(values);
		t.processesCount--;

		t.w.release();
	}

	@Override
	public void startTransaction(String tableName) {

		Table t = tables.get(tableName);

		try {
			t.tranSem.acquire(); // se vor bloca ceilalti care vor sa mai faca tranzactii
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		synchronized (this) {
			synchronized (t) {
				while (t.processesCount > 0) { // cat timp mai sunt procese in tabela, se vor pune in asteptare
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				t.transactionRunning = true; // se marcheaza ca se aplica o tranzactie in tabela
				t.transactionThreadId = Thread.currentThread().getId(); // se se retine id-ul threadului
			}
			
		}
	}

	@Override
	public void endTransaction(String tableName) {
		Table t = tables.get(tableName);

		synchronized (t) {
			t.transactionRunning = false; // trazactia s-a terminat
			t.notifyAll(); // vor fi notificate procesele ce au asteptat inainte de tranzactie
		}

		t.tranSem.release(); // se va da voie celorlalte procese sa faca tranzactii
	}
}
