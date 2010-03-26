package mystuff.checkers.base;

import java.util.Arrays;

import mystuff.checkers.player.PlayerSide;

public class HeuristicTable {
	
	//array to contain the heuristic values table
	private double[][] hTable;
	private double[][] reversehTable;
	private double[] flatHTable;
	private double[] revFlatHTable;
	
	public HeuristicTable() {
		hTable = initTableHeursitc();
		setTableHeuristic(flatten2dArr(hTable));
		
	}
	
	public void setTableHeuristic(double[] arr){
		int length = (int) Math.sqrt(arr.length);
		double[][] htable = new double[length][length];
		int counter = 0;
		for(int i = 0; i < length; i++)
			for(int j = 0; j < length; j++){
				htable[i][j] = arr[counter];
				counter++;
			}
		if(false){
			this.hTable = htable;
			this.reversehTable = calcReverseHTableFromHTable(hTable);
		}else{
			this.reversehTable = htable;
			this.hTable= calcReverseHTableFromHTable(hTable);
		}
	}
	
	public double[] flatten2dArr(double[][] arr){
		double[] res = null;

		int lenX = arr.length;
		int lenY = arr[0].length;
		res = new double[lenX * lenY];
		int counter = 0;
		for (int i = 0; i < lenX; i++) {
			for (int j = 0; j < lenY; j++) {
				res[counter] = arr[i][j];
				counter++;
			}
		}
		return res;
	
	}
	
	public double[] getTableHeuristic(){
		return flatHTable;
	}
	public double[] getTableHeuristicReverse(){
		return revFlatHTable;
	}
	
	public double[][] gethTable() {
		return hTable;
	}
	
	public double caclTableHeuristicValue(PlayerSide side){
		if (gethTable() == null)
			return 0;
		else{
			
			return 0;
		}
	}
	/**
	 * @return the reversehTable
	 */
	public double[][] getReversehTable() {
		return reversehTable;
	}
	
	public static double[][] initTableHeursitc(){
		int lenX = 10;
		int lenY = 10;
		double[][] htable = new double[lenX][lenY];
		
		htable[lenX-2][1] = 103;
		htable[lenX-2][2] = 103;
		htable[lenX-2][3] = 103;
		
		htable[lenX-3][1] = 102;
		htable[lenX-3][2] = 102;
		htable[lenX-3][3] = 102;
		
		htable[lenX-4][1] = 101;
		htable[lenX-4][2] = 101;
		htable[lenX-4][3] = 101;
		
		
		for (int i = lenX; i> -1; i--){
			for(int j = 0; j < lenY; j++){
				if( i > 0 && j > 0 && i < lenX-1 && j < lenY-1 && htable[i][j] == 0){
					double left = htable[i][j-1];
					double leftDown = htable[i+1][j-1];
					double down = htable[i+1][j];
					double val = 0;
					if(j < lenY/2){
						val = ((left + 2*leftDown + 2*down) /5)/1.2;
					}else{
						val = ((2*left + 2*leftDown + down) /5)/1.2;
					}
					htable[i][j] = val;
				}
			}
		}
		
		htable[0][5] = 0;
		htable[0][6] = 0;
		htable[0][7] = 0;
		
		
		
		//final table without zeros
		int lenX1 = lenX-2;
		int lenY1 = lenY-2;
		double[][] rtable = new double[lenX1][lenY1];
		for (int i = 0; i < lenX1; i++) {
			for (int j = 0; j < lenY1; j++) {
				rtable[i][j] = htable[i+1][j+1];
			}
		}
		double startValue = 0;
		rtable[0][5] = startValue;
		rtable[0][6] = startValue;
		rtable[0][7] = startValue;
		
		rtable[1][5] = startValue+ 0.1;
		rtable[1][6] = startValue+0.1;
		rtable[1][7] =startValue + 0.1;
		
		rtable[2][5] = startValue + 0.2;
		rtable[2][6] = startValue + 0.2;
		rtable[2][7] = startValue + 0.2;
		
		return rtable;
	}
	
	public static  double[][] calcReverseHTableFromHTable(double[][] fromTable){
		
		int len1 = fromTable.length;
		int len2 = fromTable[0].length;
		double[][] reversehTableTmp = new double[len1][len2];
		for (int i = 0; i < len1; i++) {
			for (int j = 0; j < len2; j++) {
				reversehTableTmp[j][i] = fromTable[i][j];
			}
		}
		return reversehTableTmp;
	
	}
	
	public String  print2dArr(double[][] arr){
		StringBuffer b = new StringBuffer();
		if (arr != null) {
			int lenX = arr.length;
			int lenY = arr[0].length;
			for (int i = 0; i < lenX; i++) {
				for (int j = 0; j < lenY; j++) {
					b.append(arr[i][j] + " ");
				}
				b.append("\n");
			}
		}
		return b.toString();
//		System.out.println(b.toString());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(hTable);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		HeuristicTable other = (HeuristicTable) obj;
		if (!Arrays.equals(hTable, other.hTable)) {
			return false;
		}
		return true;
	}

	public double getValueAt(boolean isOurSide, int i, int j) {
		if(isOurSide)
			return gethTable()[i][j];
		else
			return getReversehTable()[i][j];
	}
	
	public double getValueAt(int i, int j) {
		return gethTable()[i][j];
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return print2dArr(gethTable());
	}

}

