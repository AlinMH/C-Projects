import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.Semaphore;



public class Table {
	String tableName;
	LinkedHashMap<String, String> cols; // hash de la numele listelor la tipul lor (este LinkedHashMap pentru a tine ordinea coloanelor)
	ArrayList<ArrayList<Object>> entries; // intrarile din tabela (tinute ca o lista de linii) 
	int readCount; // numarul cititorilor
	int processesCount; // numarul de procese din tabela
	long transactionThreadId; // id-ul threadului care e facut request la tranzactie
	Boolean transactionRunning; // flag pentru a tine evidenta daca este o tranzactie activa in tabela
	
	Semaphore w = new Semaphore(1); // semafor pentru cititori
	Semaphore tranSem = new Semaphore(1); // semafor pentru tranzactii
	
	
	public Table(String tableName, LinkedHashMap<String, String> cols) {
		this.tableName = tableName;
		this.entries = new ArrayList<>();
		this.cols = cols;
		this.readCount = 0;
		this.processesCount = 0;
		this.transactionThreadId = -1;
		this.transactionRunning = false;
	}
	
}
