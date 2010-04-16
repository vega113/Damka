/**
 * 
 */
package mystuff.checkers.test;

import static org.junit.Assert.*;

import java.util.Arrays;

import mystuff.checkers.base.HeuristicTable;
import mystuff.checkers.table.TableState;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * @author vega
 *
 */
public class TestTableNew {
	protected HeuristicTable m_htable = null;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		m_htable = new HeuristicTable();
	}

	/**
	 * Test method for {@link mystuff.checkers.base.BaseTableState#calcReverseHTableFromHTable()}.
	 */
	@Test
	public void testGetReverseHTable() {
		m_htable.print2dArr(m_htable.gethTable());
		double[] arr1 = m_htable.getTableHeuristic();
		double[] arr2 = m_htable.flatten2dArr( m_htable.calcReverseHTableFromHTable( m_htable.gethTable()));
		m_htable.setTableHeuristic(arr2);
		double[] arr3 = m_htable.getTableHeuristicReverse();
		System.out.println(m_htable.print2dArr(m_htable.getReversehTable())); 
		assertTrue(Arrays.equals(arr1, arr3));
	}

}
