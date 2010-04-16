package mystuff.checkers.base;

public class HeuristicTablev2 extends HeuristicTable {

	/* (non-Javadoc)
	 * @see mystuff.checkers.base.HeuristicTable#nullifyStartPos(double[][])
	 */
	@Override
	protected double[][] nullifyStartPos(double[][] rtable) {
		return null;
	}

	public HeuristicTablev2() {
		setAVE_FACTOR(2.35);
		setDIV_FACTOR(1);
		hTable = initTableHeursitc();
		setTableHeuristic(flatten2dArr(hTable));
	}
	public static void main(String[] args) {
		HeuristicTable heuristicTable = new HeuristicTablev2();
		System.out.println(heuristicTable.toString());
	}

}
