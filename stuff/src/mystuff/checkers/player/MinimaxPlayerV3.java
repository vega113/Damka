package mystuff.checkers.player;

import mystuff.checkers.base.HeuristicTable;
import mystuff.checkers.base.HeuristicTablev2;

public class MinimaxPlayerV3 extends MinimaxPlayerV2 {
	
	HeuristicTable  heuristicTable =  new HeuristicTablev2();
	public MinimaxPlayerV3(PlayerSide side) {
		super(side);
	}
	public MinimaxPlayerV3(PlayerSide side, int depth) {
		super(side, depth);
	}
	public static void main(String[] args) {
		HeuristicTable heuristicTable = new HeuristicTablev2();
		System.out.println(heuristicTable.toString());
	}	

}
