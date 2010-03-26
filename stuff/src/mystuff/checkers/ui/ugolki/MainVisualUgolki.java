
/**
 * 
 */
package mystuff.checkers.ui.ugolki;
import mystuff.checkers.table.TableState;
import mystuff.checkers.ui.plaincheckers.CheckersCanvas;
import mystuff.checkers.ui.plaincheckers.MainPlainCheckers;

/**
 * @author Vega
 *
 */
public class MainVisualUgolki extends MainPlainCheckers {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4650747131459514098L;
	protected TableState ugolkiState;
//
//	public MainVisualUgolki(String string) {
//		super(string);
//	}
	/**
	 * @return the ugolkiState
	 */
	public TableState getUgolkiState() {
		return ugolkiState;
	}
	/**
	 * @param ugolkiState the ugolkiState to set
	 */
	public void setUgolkiState(TableState ugolkiState) {
		this.ugolkiState = ugolkiState;
	}
	
	protected CheckersCanvas initBoard() {
		UgolkiCanvas board = new UgolkiCanvas(getUgolkiState());
		board.init();
		board.setApplet(this);
		return board;
	}
	
//	protected  static MainPlainCheckers initMainClass() {
//		return new MainVisualUgolki(getMainClassName());
//	}
	protected  static MainPlainCheckers initMainClass() {
		return new MainVisualUgolki();
	}
	protected  static MainPlainCheckers initMainClass(String mainClass) {
		return new MainVisualUgolki();
	}
	protected  static String getMainClassName() {
		String mainClassName = "MainVisualUgolki";
		return mainClassName;
	}
	/**
	 * @param frame
	 */
	protected static void initFrame(MainPlainCheckers frame) {
		((MainVisualUgolki)frame).setUgolkiState(new TableState());
		MainPlainCheckers.initFrame(frame);
		
	}
	
	public  CheckersCanvas  initFrameNonStatic(MainPlainCheckers frame) {
		((MainVisualUgolki)frame).setUgolkiState(new TableState());
		return super.initFrameNonStatic(frame);
		
	}
	
	public static void main(String[] args) {
		MainPlainCheckers frame = initMainClass();
		initFrame(frame);
	}
	
	
}
