
/**
 * Interfata Visitable, care va fi implementata de orice clasa care va fi
 * "vizitabila".
 * 
 * @author Alin
 *
 */
public interface Visitable {
	/**
	 * Metoda accept nu va face altceva decat sa viziteze obiectul (this).
	 * @param v
	 *            Visit-orul
	 */
	public void accept(Visitor v);
}
