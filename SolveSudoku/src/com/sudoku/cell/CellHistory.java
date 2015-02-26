/**
 * 
 */
package com.sudoku.cell;

/**
 * @author BWinters
 *
 */
public class CellHistory {

	private Cell cell = null;
	private int value = 0;
	private int actionFlag = 0;
	
	public CellHistory() {
	}
	
	/**
	 * @param cell the cell to set
	 */
	public void setCell(Cell cell) {
		this.cell = cell;
	}
	/**
	 * @return the cell
	 */
	public Cell getCell() {
		return cell;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}
	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
	/**
	 * @param actionFlag the actionFlag to set
	 */
	public void setActionFlag(int actionFlag) {
		this.actionFlag = actionFlag;
	}
	/**
	 * @return the actionFlag
	 */
	public int getActionFlag() {
		return actionFlag;
	}
	
}
