package com.sudoku.gui.board;

import org.apache.log4j.Logger;

import com.sudoku.cell.Cell;


public class BackGroundBoard {
	static final Logger logger = Logger.getLogger(com.sudoku.gui.board.BackGroundBoard.class);
	public static int BACKGROUND_BOARD_HEIGHT = 9;
	public static int BACKGROUND_BOARD_WIDTH = 9;
	private int difficultyLevel = 0;
	private String difficultyText = "";
	private int recursiveCallsToSolve=0;
	private int numOfUnsolvedCells = BackGroundBoard.BACKGROUND_BOARD_HEIGHT*BackGroundBoard.BACKGROUND_BOARD_WIDTH;
	private int numOfSolvedCells = 0;
	private int numOfRandomValues=0;
	private boolean isColoringEnabled=false;
	private double initialAvgPossPerCell=0.0;	
	private static Cell[][] backGroundCells = new Cell[BackGroundBoard.BACKGROUND_BOARD_HEIGHT][BackGroundBoard.BACKGROUND_BOARD_HEIGHT];
	private static BackGroundBoard backGroundBoard = null;

	/**
	 * BackGroundBoard Constructor
	 */
	private BackGroundBoard() {
	}
		
	/**
	 * Get difficulty text from difficulty level.
	 * @return
	 */
	public String getDifficultyTextFromLevel(int difficultyLevel) {
		if (difficultyLevel == 0) {
		}else if (difficultyLevel==1) {
			difficultyText="Easy";			
		} else if (difficultyLevel==2) {
			difficultyText="Medium";			
		} else if (difficultyLevel==3) {
			difficultyText="Hard";			
		} else if (difficultyLevel==4) {
			difficultyText="Evil";			
		} else if (difficultyLevel==5) {
			difficultyText="Fiendish";			
		} else if (difficultyLevel==6) {
			difficultyText="INSANE";
		} else {
			difficultyText="EXTRA HARD";
		}
		return difficultyText;
	}
	
	/**
	 * Get difficulty text from difficulty level.
	 * @return
	 */
	public int getDifficultyLevelFromText(String difficultyText) {
		if (difficultyText.equalsIgnoreCase("Easy")) {
			difficultyLevel=1;
		}else if (difficultyText.equalsIgnoreCase("Medium")) {
			difficultyLevel=2;		
		} else if (difficultyText.equalsIgnoreCase("Hard")) {
			difficultyLevel=3;			
		} else if (difficultyText.equalsIgnoreCase("Evil")) {
			difficultyLevel=4;			
		} else if (difficultyText.equalsIgnoreCase("Fiendish")) {
			difficultyLevel=5;			
		} else if (difficultyText.equalsIgnoreCase("Insane")) {
			difficultyLevel=6;			
		} else {
			difficultyLevel=0;
		}
		return difficultyLevel;
	}
	
	/**
	 * @return the difficultyLevel
	 */
	public int getDifficultyLevel() {
		return difficultyLevel;
	}
	
	/**
	 * @param difficultyLevel the difficultyLevel to set
	 */
	public void setDifficultyLevel(int difficultyLevel) {
		this.difficultyLevel = difficultyLevel;
	}

	/**
	 * @return the difficultyText
	 */
	public String getDifficultyText() {
		return difficultyText.toUpperCase();
	}
	
	/**
	 * @param difficultyText the difficultyText to set
	 */
	public void setDifficultyText(String difficultyText) {
		this.difficultyText = difficultyText;
	}

	/**
	 * @return the recursiveCallsToSolve
	 */
	public int getRecursiveCallsToSolve() {
		return recursiveCallsToSolve;
	}
	
	/**
	 * Set the average possibilities per cell value
	 */
	public void setAvgPossPerCell(int avgPossPerCell) {
		this.initialAvgPossPerCell=avgPossPerCell;
	}

	/**
	 * @param recursiveCallsToSolve the recursiveCallsToSolve to set
	 */
	public void setRecursiveCallsToSolve(int recursiveCallsToSolve) {
		this.recursiveCallsToSolve = recursiveCallsToSolve;
	}

	/**
	 * @return the initialAvgPosPerCell
	 */
	public double getInitialAvgPosPerCell() {
		return initialAvgPossPerCell;
	}
	
	/**
	 * @param initialAvgPossPerCell the initialAvgPosPerCell to set
	 */
	public void setInitialAvgPosPerCell(double initialAvgPossPerCell) {
		this.initialAvgPossPerCell = initialAvgPossPerCell;
	}

	/**
	 * Returns the current array of cells on board.
	 * @return
	 */
	public Cell[][] getCells() {
		return backGroundCells;
	}

	/**
	 * Sets the cells on the board given the cells array
	 * @param cells
	 */
	public void setCells(Cell[][] cells) {
		BackGroundBoard.backGroundCells = cells;
	}
	
	/**
	 * Returs the number of unsolved cells on the board
	 * @return
	 */
	public int getNumOfUnsolvedCells() {
		return numOfUnsolvedCells;
	}

	/**
	 * Sets the number of unsolved cells on the board
	 * @param numOfUnsolvedCells
	 */
	public void setNumOfUnsolvedCells(int numOfUnsolvedCells) {
		this.numOfUnsolvedCells = numOfUnsolvedCells;
	}
	
	/**
	 * @return the isColoringEnabled
	 */
	public boolean isColoringEnabled() {
		return isColoringEnabled;
	}
	
	/**
	 * @param isColoringEnabled the isColoringEnabled to set
	 */
	public void setColoringEnabled(boolean isColoringEnabled) {
		this.isColoringEnabled = isColoringEnabled;
	}
	
	/**
	 * @param numOfSolvedCells the numOfSolvedCells to set
	 */
	public void setNumOfSolvedCells(int numOfSolvedCells) {
		this.numOfSolvedCells = numOfSolvedCells;
		this.numOfUnsolvedCells=81-numOfSolvedCells;
	}

	/**
	 * @return the numOfSolvedCells
	 */
	public int getNumOfSolvedCells() {
		return numOfSolvedCells;
	}
	
	/**
	 * @param numOfRandomValues
	 */
	public void setNumOfRandomValues(int numOfRandomValues) {
		this.numOfRandomValues = numOfRandomValues;
	}

	/**
	 * 
	 * @return the number of randomly placed values
	 */
	public int getNumOfRandomValues() {
		return numOfRandomValues;
	}

	/**
	 * @param board the board to set
	 */
	public static void setBackGroundBoard(BackGroundBoard board) {
		BackGroundBoard.backGroundBoard = board;
	}

	/**
	 * @return the board
	 */
	public static BackGroundBoard getBackGroundBoard() {
		if (backGroundBoard==null) {
			backGroundBoard=new BackGroundBoard();
		}
		return backGroundBoard;
	}	
}
