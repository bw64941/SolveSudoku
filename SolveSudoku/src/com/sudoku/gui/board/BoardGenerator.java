package com.sudoku.gui.board;

import java.util.Random;

import org.apache.log4j.Logger;

import com.sudoku.cell.Cell;
import com.sudoku.gui.statusbar.SudokuStatusLabel;
import com.sudoku.solver.Solver;

public class BoardGenerator extends Solver {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.board.BoardGenerator.class); 
	private boolean solveInProgress = false;
	private static BoardGenerator boardGenerator = null;
	private Random rand = new Random();
	
	private BoardGenerator() {
		super(1);
	}
	
	/**
	 * Generate a static Sudoku Board
	 */
	public void generateStaticBoard() {
		Cell[][] cells = Board.getBoard().getCells();
		int x = 0;
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				for (int k = 0; k < 9; k++) {
					cells[3 * row + col][k].setValue(x % 9 + 1);
					x++;
				}
				x += Math.sqrt(Board.BOARD_HEIGHT);
			}
			x++;
		}
	}
	
	/**
	 * Randomly Generate a Sudoku Board
	 */
	public void randomSolve() {		
		while (!solve()) {
			Cell cell = board.getCellWithLeastPossibilities();
			if (cell.getNum0fPossibilitiesLeft()==0) {
				SudokuStatusLabel.getStatusBar().setMessage("Unable to find a cell with possibilities left");
				solveInProgress=false;
				break;
			} else {
				int value = cell.getPossibilities().get(rand.nextInt(cell.getNum0fPossibilitiesLeft()));
				if (rule.runRules(cell, value)) {
					cell.setValue(value);
					board.setNumOfSolvedCells(board.getNumOfSolvedCells() + 1);
					board.setNumOfRandomValues(board.getNumOfRandomValues()+1);
					board.updateBoard();
				}
			}
		}
	}
	
	/**
	 * @return the solveInProgress
	 */
	public boolean isSolveInProgress() {
		return solveInProgress;
	}
	/**
	 * @param solveInProgress the solveInProgress to set
	 */
	public void setSolveInProgress(boolean solveInProgress) {
		this.solveInProgress = solveInProgress;
	}
	
	/**
	 * Runs the random solver routine
	 */
	public void run() {
		SudokuStatusLabel.getStatusBar().setMessage("Generating Board");
		while (solveInProgress) {	
			int solveStartTime = (int)System.currentTimeMillis();
			randomSolve();
			int solveEndTime = (int)System.currentTimeMillis();
			logger.debug("Board Generated in "+((solveEndTime-solveStartTime))+" msec");
			solveInProgress=false;
		}
		Board.getBoard().printSolverSteps();
		SudokuStatusLabel.getStatusBar().setMessage("Board Generated, Ready");
	}

	/**
	 * Public Board Generator Constructor
	 * @return
	 */
	public static BoardGenerator getBoardGenerator() {
		if (boardGenerator==null) {
			boardGenerator=new BoardGenerator();
		}
		return boardGenerator;
	}

}
