/**
 * 
 */
package com.sudoku.gui.board;

import org.apache.log4j.Logger;

/**
 * @author BWinters
 *
 */
public interface Printable {	
	
	static final Logger logger = Logger.getLogger(com.sudoku.gui.board.Printable.class);

	public static String SEPERATOR = "|";
	public static String FILLER = "X";
	
	/**
	 * Prints the possibilities left on the board.
	 */
	public void printPossibilities();
	
	/**
	 * Shows cells on the board with only 1 possibility left.
	 */
	public void printCellsWithOnePossibility();
	
	/**
	 * Shows the current values that are placed on the board.
	 */
	public void printCurrentValues();
	
	/**
	 * Shows the locked values on the board.
	 */
	public void printLockedCells();
	
	/**
	 * Prints the statistics of the current board
	 */
	public void printInProgressStatistics();
	
	/**
	 * Prints the final statistics of the solved board.
	 */
	public void printFinalStatistics();
	
	/**
	 * Prints current quadrant values.
	 */
	public void printQuadrantValues();
	
	/**
	 * Prints current empty cells.
	 */
	public void printEmptyCells();
	
	/**
	 * Prints the solver steps taken to solve the board.
	 */
	public void printSolverSteps();
}
