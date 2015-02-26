package com.sudoku.rules;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.sudoku.cell.Cell;
import com.sudoku.gui.board.Board;

public class RulesTest extends TestCase {
	
	static final Logger logger = Logger.getLogger(com.sudoku.rules.RulesTest.class);
	private Cell[][] cells = new Cell[Board.BOARD_HEIGHT][Board.BOARD_WIDTH];
	private Rules rule = new Rules();
	private int[][] startBoard = {	{9,0,0,0,0,2,0,0,3},
  									{0,1,3,6,0,0,0,2,0},
  									{0,7,0,0,1,5,0,0,9},
  									{0,0,0,0,0,6,4,0,8},
  									{0,0,8,7,0,4,3,0,0},
  									{6,0,5,1,0,0,0,0,0},
  									{4,0,0,9,5,0,0,7,0},
  									{0,2,0,0,0,7,5,8,0},
  									{7,0,0,2,0,0,0,0,4}};
	
	/**
	 * JUnit Contructor
	 * @param name
	 */
	public RulesTest(String name) {
		super(name);
	}

	/**
	 * JUnit Setup Method
	 */
	protected void setUp() throws Exception {
		super.setUp();
		for (int row = 0; row < Board.BOARD_HEIGHT; row++) {
			for (int col = 0; col < Board.BOARD_WIDTH; col++) {
					cells[row][col] = new Cell(row, col);
					cells[row][col].assignQuadrant();
					cells[row][col].setValue(startBoard[row][col]);
			}
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * JUnit Test method for isRowOK()
	 */
	public void testIsRowOK() {
		// Should Not Place
		assertFalse("assertFalse(rule.isRowOK(cells,cells[0][1],9)) failed", rule.runRules(cells[0][1], 9));
		assertFalse("assertFalse(rule.isRowOK(cells,cells[3][1],6)) failed", rule.runRules(cells[3][1], 6));
		assertFalse("assertFalse(rule.isRowOK(cells,cells[6][6],5)) failed", rule.runRules(cells[6][6], 5));
		
		// Should Place
		assertTrue("assertTrue(rule.isRowOK(cells,cells[0][2],1)) failed", rule.runRules(cells[0][2], 1));
		assertTrue("assertTrue(rule.isRowOK(cells,cells[4][0],9)) failed", rule.runRules(cells[4][0], 9));
		assertTrue("assertTrue(rule.isRowOK(cells,cells[7][1],3)) failed", rule.runRules(cells[7][1], 3));
	}

	/**
	 * JUnit Test method for isColOK()
	 */
	public void testIsColOK() {
		// Should Not Place
		assertFalse("assertFalse(rule.isColOK(cells,cells[0][1],7)) failed", rule.runRules(cells[0][1], 7));
		assertFalse("assertFalse(rule.isColOK(cells,cells[3][1],2)) failed", rule.runRules(cells[3][1], 2));
		assertFalse("assertFalse(rule.isColOK(cells,cells[6][6],5)) failed", rule.runRules(cells[6][6], 5));
		
		// Should Place
		assertTrue("assertTrue(rule.isColOK(cells,cells[0][2],1)) failed", rule.runRules(cells[0][2], 1));
		assertTrue("assertTrue(rule.isColOK(cells,cells[4][0],8)) failed", rule.runRules(cells[4][0], 8));
		assertTrue("assertTrue(rule.isColOK(cells,cells[7][8],5)) failed", rule.runRules(cells[7][8], 5));
	}

	/**
	 * JUnit Test method for isQuadOK()
	 */
	public void testIsQuadOK() {
		// Should Not Place
		assertFalse("assertFalse(rule.isQuadOK(cells,cells[0][1],3)) failed", rule.runRules(cells[0][1], 3));
		assertFalse("assertFalse(rule.isQuadOK(cells,cells[3][1],5)) failed", rule.runRules(cells[3][1], 5));
		assertFalse("assertFalse(rule.isQuadOK(cells,cells[6][6],8)) failed", rule.runRules(cells[6][6], 8));
		
		// Should Place
		assertTrue("assertTrue(rule.isQuadOK(cells,cells[0][2],2)) failed", rule.runRules(cells[0][2], 2));
		assertTrue("assertTrue(rule.isQuadOK(cells,cells[4][0],4)) failed", rule.runRules(cells[4][0], 4));
		assertTrue("assertTrue(rule.isQuadOK(cells,cells[7][8],9)) failed", rule.runRules(cells[7][8], 9));
	}

}
