package com.sudoku.rules;

import org.apache.log4j.Logger;

import com.sudoku.cell.Cell;
import com.sudoku.gui.board.Board;


public class Rules {

	static final Logger logger = Logger.getLogger(com.sudoku.rules.Rules.class);

	/**
	 * Rules Contructor
	 */
	public Rules() {
	}
	
	/**
	 * Runs methods to check row, column, and quadrant.
	 * @param cell
	 * @param valueToCheck
	 */
	public boolean runRules(Cell cell, int valueToCheck) {
		if (isRowOK(cell, valueToCheck) && isColOK(cell, valueToCheck)
				&& isQuadOK(cell, valueToCheck)) {
			return true;
		}
		return false;
	}

	/**
	 * Returns whether or not the specified value can be placed in the given cell
	 * with respect to the other values in the same row.
	 * @param cells
	 * @param cell
	 * @param tempValue
	 * @return
	 */
	private boolean isRowOK(Cell cell, int tempValue) {
		Cell[][] cells = Board.getBoard().getCells();
		int rowInQuestion = cell.getRow();
		for (int colInRow = 0; colInRow < Board.BOARD_WIDTH; colInRow++) {
			if (cells[rowInQuestion][colInRow].getValue() == tempValue) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns whether or not the specified value can be placed in the given cell
	 * with respect to the other values in the same column.
	 * @param cells
	 * @param cell
	 * @param tempValue
	 * @return
	 */
	private boolean isColOK(Cell cell, int tempValue) {
		Cell[][] cells = Board.getBoard().getCells();
		int colInQuestion = cell.getCol();
		for (int rowInCol = 0; rowInCol < Board.BOARD_HEIGHT; rowInCol++) {
			if (cells[rowInCol][colInQuestion].getValue() == tempValue) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns whether or not the specified value can be placed in the given cell
	 * with respect to the other values in the same quadrant.
	 * @param cells
	 * @param cell
	 * @param tempValue
	 * @return
	 */
	private boolean isQuadOK(Cell cell, int tempValue) {
		Cell[][] cells = Board.getBoard().getCells();
		int quadInQuestion = cell.getQuad();
		for (int row = 0; row < cells.length; row++) {
			for (int col = 0; col < cells.length; col++) {
				if (cells[row][col].getQuad()==quadInQuestion) {
					if (cells[row][col].getValue() == tempValue) {
						return false;
					}
				}
			}
		}
		return true;
	}
}
