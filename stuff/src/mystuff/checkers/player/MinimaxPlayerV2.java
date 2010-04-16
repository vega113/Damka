package mystuff.checkers.player;

import java.util.Iterator;
import java.util.Map;

import mystuff.checkers.base.HeuristicTable;
import mystuff.checkers.base.UgolkiConstants;
import mystuff.checkers.table.CellIndex;
import mystuff.checkers.table.TableState;

public class MinimaxPlayerV2 extends MinimaxPlayer {
	
	HeuristicTable  heuristicTable =  new HeuristicTable();

	public MinimaxPlayerV2(PlayerSide side) {
		super(side);

		//this is to ensure that the heuristic table is right for this side
		setHeuristicTable(heuristicTable);
		if(side.equals(UgolkiConstants.PlayerSideBlue)){
			getHeuristicTable().setTableHeuristic(getHeuristicTable().flatten2dArr(getHeuristicTable().getReversehTable()));
		}
	}

	public MinimaxPlayerV2(PlayerSide side, int depth) {
		super(side, depth);
		
		//this is to ensure that the heuristic table is right for this side
		setHeuristicTable(new HeuristicTable());
		if(side.equals(UgolkiConstants.PlayerSideRed)){
			getHeuristicTable().setTableHeuristic(getHeuristicTable().flatten2dArr(getHeuristicTable().getReversehTable()));
		}
	}

	/* (non-Javadoc)
	 * @see mystuff.checkers.base.BaseAutomatePlayer#getHeuristicValueForBoardState(mystuff.checkers.player.PlayerSide)
	 */
	@Override
	public double getHeuristicValueForBoardState(PlayerSide playerSide) {
		Map map = getClonedTable().getFastAllCellIndexesForPlayerSide(playerSide);
		double hval = 0;
		Iterable iter = map.keySet();
		StringBuffer b = new StringBuffer();
		boolean isOurSide = playerSide.equals(getSide());
		for(Object o : iter){
			CellIndex index = (CellIndex)map.get(o);
			b.append(index.toString());
			hval+=getHeuristicTable().getValueAt(isOurSide,index.getXCoord(), index.getYCoord());
		}
		return hval;
	}

	/* (non-Javadoc)
	 * @see mystuff.checkers.base.BaseAutomatePlayer#preExecute()
	 */
	@Override
	public void preExecute() {
		super.preExecute();
		if (!isHeuristicTable())
		throw new RuntimeException("isHeuristicTable is false! ");
	}
}
