package com.sudoku.gui.board;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Random;

import org.apache.log4j.Logger;

import com.sudoku.cell.Cell;
import com.sudoku.cell.CellHistory;
import com.sudoku.gui.statusbar.SudokuStatusLabel;
import com.sudoku.rules.Rules;
import com.sudoku.solver.Solver;


public class Board implements Printable, Runnable {
	static final Logger logger = Logger.getLogger(com.sudoku.gui.board.Board.class);
	public static int BOARD_HEIGHT = 9;
	public static int BOARD_WIDTH = 9;
	private int difficultyLevel = 0;
	private String difficultyText = "";
	private int recursiveCallsToSolve=0;
	private int numOfUnsolvedCells = Board.BOARD_HEIGHT*Board.BOARD_WIDTH;
	private int numOfSolvedCells = 0;
	private int numOfRandomValues=0;
	private boolean isColoringEnabled=false;
	private static boolean syncInProgress=false;
	private double initialAvgPossPerCell=0.0;	
	private static Cell[][] cells = new Cell[Board.BOARD_HEIGHT][Board.BOARD_WIDTH];
	private static Board board = null;

	/**
	 * Board Constructor for cloning board.
	 * @param parm
	 */
	public Board() {
	}
	
	/**
	 * Board Constructor
	 */
	private Board(String parm) {
		buildBoard();
	}
	
	/**
	 * Board Constructor - used by Solver JUnit test suite
	 */
	private Board(int difficultyLevel, int[][] array) {
		this.difficultyLevel=difficultyLevel;
		this.difficultyText=getDifficultyTextFromLevel(this.difficultyLevel);
		buildBoard(array);
	}
	
	 /**
	 * Initializes the board based on difficulty level that is chosen.
	 */
	private void buildBoard() {
		for (int row = 0; row < Board.BOARD_HEIGHT; row++) {
			for (int col = 0; col < Board.BOARD_WIDTH; col++) {
				cells[row][col] = new Cell(row, col);
			}
		}
	}

	 /**
	 * Initializes the passed in board based on difficulty level that is chosen.
	 */
	private void buildBoard(int[][] initialBoard) {
		int[][] array = initialBoard;
		for (int row = 0; row < Board.BOARD_HEIGHT; row++) {
			for (int col = 0; col < Board.BOARD_WIDTH; col++) {
				cells[row][col] = new Cell(row, col);
				if (array[row][col] > 0) {
					numOfUnsolvedCells--;
					numOfSolvedCells++;
				}
				cells[row][col].setValue(array[row][col]);
			}
		}
		this.recursiveCallsToSolve=0;
		updateBoard();
	}
	
	/**
	 * Initializes the passed in board without creating new Cell objects.
	 * @param outsideBoard
	 */
	public void setCurrentState(int[][] outsideBoard) {
		int[][] array = outsideBoard;
		for (int row = 0; row < Board.BOARD_HEIGHT; row++) {
			for (int col = 0; col < Board.BOARD_WIDTH; col++) {
				if (array[row][col] > 0) {
					numOfUnsolvedCells--;
					numOfSolvedCells++;
				} 
				cells[row][col].setValue(array[row][col]);
			}
		}
		this.recursiveCallsToSolve=0;
		updateBoard();
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
	 * Calculates the average number of possibilities per cell.
	 */
	public void calcAvgPossPerCell() {
		double numOfPossibilities = 0.0;
		double numOfCellOnBoard = Board.BOARD_HEIGHT*Board.BOARD_WIDTH;
		
		for (Cell[] rows : cells) {
			for (Cell cell : rows) {
				if (cell.isEmpty()) {
					numOfPossibilities+=cell.getNum0fPossibilitiesLeft();
				}
			}
		}
		Math.round(initialAvgPossPerCell = numOfPossibilities/numOfCellOnBoard);
	}
	
	/**
	 * Wrapper for updating possibilities on board.
	 * Calls updatePossibilities method.
	 * @param functionUpdating
	 * @param cells
	 */
	public void updateBoard() {
		for (int row = 0; row < Board.BOARD_WIDTH; row++) {
			for (int col = 0; col < Board.BOARD_HEIGHT; col++) {
				if (cells[row][col].isLocked() 
						&& !cells[row][col].isEmpty()) {
					eliminatePossibilities(cells[row][col]);
				}	
			}
		}
		calcAvgPossPerCell();
	}

	/**
	 * Loops through each cell in the board and eliminates
	 * impossible values based on rules of the game.
	 * This method does not set any value, it only eliminates
	 * possibilities in the cell.
	 * @param cells
	 * @param cell
	 */
	private void eliminatePossibilities(final Cell cell) {
		int valuePlaced = cell.getValue();
		for (int row = 0; row < Board.BOARD_WIDTH; row++) {
			for (int col = 0; col < Board.BOARD_HEIGHT; col++) {
				if ((cells[row][col].getQuad() == cell.getQuad() || 
						cells[row][col].getRow() == cell.getRow() ||
						cells[row][col].getCol() == cell.getCol()) && !cells[row][col].isLocked()) {
					cells[row][col].removePossibility(valuePlaced,"Rules of Game");
				}	
			}
		}
	}
	
	/**
	 * Clear the text for all cells
	 * that are not locked.
	 */
	public void clearUnsolvedCellText() {
		for (int row = 0; row < Board.BOARD_WIDTH; row++) {
			for (int col = 0; col < Board.BOARD_HEIGHT; col++) {
				if (!cells[row][col].isLocked()) {
					cells[row][col].setForeground(Color.blue);
					cells[row][col].setFont(new Font("Lucida Sans Typewriter", Font.BOLD, 14));
					cells[row][col].setText("");
					cells[row][col].setBackground(Color.white);
				}	
			}
		}
	}
	
	/**
	 * Clear the background color for all cells
	 * that are not locked.
	 */
	public void clearUnsolvedCellColors() {
		for (int row = 0; row < Board.BOARD_WIDTH; row++) {
			for (int col = 0; col < Board.BOARD_HEIGHT; col++) {
				if (!cells[row][col].isLocked()) {
					cells[row][col].setBackground(Color.white);
				}	
			}
		}
	}
	
	/**
	 *  Method called to clear the current board before opening a new one
	 */
	public void clear() {
		this.difficultyLevel=0;
		this.difficultyText="NO DIFFICULTY";
		this.numOfUnsolvedCells=Board.BOARD_HEIGHT*Board.BOARD_WIDTH;
		this.recursiveCallsToSolve=0;
		this.numOfSolvedCells=0;
		this.isColoringEnabled=false;
		this.initialAvgPossPerCell=0.0;
		this.numOfRandomValues=0;
		this.calcAvgPossPerCell();
		
		for (Cell[] row : cells) {
			for (Cell cell : row) {			
				cell.clear();
			}
		}
	}
	
	/**
	 * Returns true if the board is completely filled in.  Meaning there
	 * are no empty cells on the current board.
	 * @param cells
	 * @return
	 */
	public boolean isEmpty() {
		boolean isEmpty=true;
		for (int row = 0; row < Board.BOARD_WIDTH; row++) {
			for (int col = 0; col < Board.BOARD_HEIGHT; col++) {
				if (!cells[row][col].isEmpty()) {
					return false;
				}
			}
		}
		return isEmpty;
	}
	
	/**
	 * Returns the cell with the least number of possibilities.
	 * @param allCells
	 * @return
	 */
	public Cell getCellWithLeastPossibilities() {
		ArrayList<Cell> allEmptyCells = this.getUnsolvedCells();
		Cell bestCell = allEmptyCells.get(new Random().nextInt(allEmptyCells.size()));
		int leastNumOfPossibilities=bestCell.getNum0fPossibilitiesLeft();

		for (Cell cell : allEmptyCells) {
			if (!cell.equals(bestCell) && cell.getNum0fPossibilitiesLeft()<leastNumOfPossibilities && cell.getNum0fPossibilitiesLeft()!=0) {
				leastNumOfPossibilities=cell.getNum0fPossibilitiesLeft();
				bestCell=cell;
			}
		}		
		return bestCell;
	}
	
	/**
	 * Returns the cell with the largest number of possibilities.
	 * @param allCells
	 * @return
	 */
	public Cell getCellWithMostPossibilities() {
		ArrayList<Cell> allEmptyCells = this.getUnsolvedCells();
		Cell bestCell = allEmptyCells.get(new Random().nextInt(allEmptyCells.size()));
		int leastNumOfPossibilities=bestCell.getNum0fPossibilitiesLeft();

		for (Cell cell : allEmptyCells) {
			if (!cell.equals(bestCell) && cell.getNum0fPossibilitiesLeft()>leastNumOfPossibilities && cell.getNum0fPossibilitiesLeft()!=0) {
				leastNumOfPossibilities=cell.getNum0fPossibilitiesLeft();
				bestCell=cell;
			}
		}		
		return bestCell;
	}
	
	/**
	 * Returns all empty cells on the board.
	 * Used for random solve routine.
	 * @return
	 */
	public ArrayList<Cell> getUnsolvedCells() {
		ArrayList<Cell> allUnsolvedCells = new ArrayList<Cell>();
		for (int row = 0; row < Board.BOARD_WIDTH; row++) {
			for (int col = 0; col < Board.BOARD_HEIGHT; col++) {
				if (cells[row][col].isEmpty()) {
					allUnsolvedCells.add(cells[row][col]);
				}
			}
		}
		return allUnsolvedCells;
	}
	
	/**
	 * Returns all empty cells on the board.
	 * Used for random solve routine.
	 * @return
	 */
	public ArrayList<Cell> getAllCells() {
		ArrayList<Cell> allCells = new ArrayList<Cell>();
		
		for (int row = 0; row < Board.BOARD_WIDTH; row++) {
			for (int col = 0; col < Board.BOARD_HEIGHT; col++) {	
				allCells.add(cells[row][col]);
			}
		}
		return allCells;
	}
	
	/**
	 * Returns true if the board is fully solved.
	 * @param cells
	 */
	public boolean isSolved() {
		for (int row = 0; row < Board.BOARD_WIDTH; row++) {
			for (int col = 0; col < Board.BOARD_HEIGHT; col++) {
				if (cells[row][col].isEmpty() 
						|| !cells[row][col].containsValidValue()) 
				{
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Returns true if the board has valid values.
	 * @param cells
	 */
	public boolean isBoardValid(Rules rules) {
		for (int row = 0; row < Board.BOARD_WIDTH; row++) {
			for (int col = 0; col < Board.BOARD_HEIGHT; col++) {
				int value = cells[row][col].getValue();
				if (!cells[row][col].isEmpty()) {
					if (!cells[row][col].containsValidValue() 
						|| !rules.runRules(cells[row][col], value)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	
	/**
	 * Show all possibilites for each cell
	 * that is not locked.
	 */
	public void showUnsolvedCellPossibilities() {
		Font font = new Font("Time New Roman", Font.PLAIN, 10);
		Color fontColor = Color.red;
		
		for (int row = 0; row < Board.BOARD_WIDTH; row++) {
			for (int col = 0; col < Board.BOARD_HEIGHT; col++) {
				if (!cells[row][col].isLocked()) {
					cells[row][col].setFont(font);
					cells[row][col].setForeground(fontColor);
					cells[row][col].setText(cells[row][col].getPossibilities().toString());
				}	
			}
		}
	}
	
	/**
	 * Show only the given possibility for each cell
	 * that is not locked.
	 */
	public void showUnsolvedCellPossibilities(int possibility) {
		Color fontColor = Color.red;
		
		ArrayList<Cell> unSolvedCells = Board.getBoard().getUnsolvedCells();
		
		for (Cell cell : unSolvedCells) {
			cell.setText("");
			if (cell.getPossibilities().contains(possibility)) {
	//			cell.setFont(font);  //This doesn't perform well.
				cell.setForeground(fontColor);
				cell.setText(possibility + "");
			}
		}
	}
	
	/**
	 * Show all possibilities left for each cell
	 * that is not locked.
	 */
	public void showAllUnsolvedCellPossibilities() {
		Color fontColor = Color.red;
		
		ArrayList<Cell> unsolvedCells = Board.getBoard().getUnsolvedCells();
		
		for (Cell cell : unsolvedCells) {
			cell.setText("");
			// cell.setFont(font); //This doesn't perform well.
			cell.setForeground(fontColor);
			cell.setText(cell.getPossibilities() + "");
		}
	}
	
	/**
	 * Returns the total number of possibilities left on the current board.
	 * @param cells
	 * @return
	 */
	public int getNumberOfPossibilitiesRemaining() {
		int numOfPoss = 0;
		for (int row = 0; row < Board.BOARD_WIDTH; row++) {
			for (int col = 0; col < Board.BOARD_HEIGHT; col++) {
				if (cells[row][col].isEmpty()) {
					numOfPoss=numOfPoss+cells[row][col].getNum0fPossibilitiesLeft();
				}
			}
		}
		return numOfPoss;
	}
	
	/**
	 * Returns the cells in a specified quadrant.
	 * @param cells
	 * @param quad
	 * @return
	 */
	public ArrayList<Cell> getCellsInQuad(int quad) {
		ArrayList<Cell> cellsInQuad = new ArrayList<Cell>(9);

		for (int row = 0; row < Board.BOARD_HEIGHT; row++) {
			for (int col = 0; col < Board.BOARD_WIDTH; col++) {
				if ((cells[row][col].getQuad()) == quad) {
					cellsInQuad.add(cells[row][col]);
				}

			}
		}
		return cellsInQuad;
	}

	/**
	 * Returns the cells in a specified row.
	 * @param cells
	 * @param row
	 * @return
	 */
	public ArrayList<Cell> getCellsInRow(int row) {
		ArrayList<Cell> cellInRow = new ArrayList<Cell>(9);

		for (int col = 0; col < Board.BOARD_HEIGHT; col++) {
				if (cells[row][col].getRow() == row) {
					cellInRow.add(cells[row][col]);
				}
		}
		return cellInRow;
	}
	
	/**
	 * Returns the cells in a specified column.
	 * @param cells
	 * @param col
	 * @return
	 */
	public ArrayList<Cell> getCellsInCol(int col) {
		ArrayList<Cell> cellInCol = new ArrayList<Cell>(9);

		for (int row = 0; row < Board.BOARD_HEIGHT; row++) {
				if (cells[row][col].getCol() == col) {
					cellInCol.add(cells[row][col]);
				}
		}
		return cellInCol;
	}
	
	/**
	 * Returns all possibilities for the specified quadrant.
	 * @param cells
	 * @param quadInQuestion
	 * @return
	 */
	public ArrayList<Integer> getPossibilitiesForQuad(int quadInQuestion) {
		ArrayList<Integer> quadPossibilities = new ArrayList<Integer>();
//		System.out.println("Possible values for QUAD: " + quadInQuestion + "\n");
		for (Cell[] row : cells) {
			for (Cell cell : row) {
				if (cell.getQuad() == quadInQuestion
						&& cell.isEmpty()) {
					quadPossibilities.addAll(cell.getPossibilities());
				}

			}
		}
		return quadPossibilities;
	}

	/**
	 * Returns all possibilities for the specified row.
	 * @param cells
	 * @param rowInQuestion
	 * @return
	 */
	public ArrayList<Integer> getPossibilitiesForRow(int rowInQuestion) {
		ArrayList<Integer> rowPossibilities = new ArrayList<Integer>();
		//System.out.println("Possible values for ROW: " + rowInQuestion+ "\n");
		for (Cell cell : cells[rowInQuestion]) {
			rowPossibilities.addAll(cell.getPossibilities());
		}
		return rowPossibilities;
	}

	/**
	 * Returns all possibilities for the specified column.
	 * @param cells
	 * @param colInQuestion
	 * @return
	 */
	public ArrayList<Integer> getPossibilitiesForCol(int colInQuestion) {
		ArrayList<Integer> colPossibilities = new ArrayList<Integer>();
//		System.out.println("Possible values for COL: " + colInQuestion+ "\n");
		for (Cell cell : cells[colInQuestion]) {
			colPossibilities.addAll(cell.getPossibilities());
		}
		return colPossibilities;
	}
		
	/**
	 * Returns all cells the specified column with only 2 possibilities.
	 * @param cells
	 * @param colInQuestion
	 * @return
	 */
	public ArrayList<Cell> getCellsInColWithTwoPossibilities(int colInQuestion) {
		ArrayList<Cell> nakedCells = new ArrayList<Cell>();
		
		for (int i = 0; i < Board.BOARD_HEIGHT; i++) {
			if (cells[i][colInQuestion].getNum0fPossibilitiesLeft() == 2) {
				if (logger.isDebugEnabled()) {
//					logger.debug("ADDED");
				}
				nakedCells.add(cells[i][colInQuestion]);
			}
		}
		
		return nakedCells;
	}
	
	/**
	 * Returns all cells in the specified row with 3 possibilities.
	 * @param cells
	 * @param colInQuestion
	 * @return
	 */
	public ArrayList<Cell> getCellsInColWithThreePossibilities(int colInQuestion) {
		ArrayList<Cell> nakedCells = new ArrayList<Cell>();
		
		for (int i = 0; i < Board.BOARD_HEIGHT; i++) {
			if (cells[i][colInQuestion].getNum0fPossibilitiesLeft() == 3) {
				if (logger.isDebugEnabled()) {
//					logger.debug("ADDED");
				}
				nakedCells.add(cells[i][colInQuestion]);
			}
		}
		
		return nakedCells;
	}
	
	/**
	 * Returns all cells in the specified row with 2 possibilities.
	 * @param cells
	 * @param rowInQuestion
	 * @return
	 */
	public ArrayList<Cell> getCellsInRowWithTwoPossibilities(int rowInQuestion) {
		ArrayList<Cell> nakedCells = new ArrayList<Cell>();
		
		for (int i = 0; i < Board.BOARD_HEIGHT; i++) {
			if (cells[rowInQuestion][i].getNum0fPossibilitiesLeft() == 2) {
				if (logger.isDebugEnabled()) {
//					logger.debug("ADDED "+cells[rowInQuestion][i].getCellCoordinates());
				}
				nakedCells.add(cells[rowInQuestion][i]);
			}
		}
		
		return nakedCells;
	}
	
	/**
	 * Returns all cells in the specified row with 3 possibilities.
	 * @param cells
	 * @param rowInQuestion
	 * @return
	 */
	public ArrayList<Cell> getCellsInRowWithThreePossibilities(int rowInQuestion) {
		ArrayList<Cell> nakedCells = new ArrayList<Cell>();
		
		for (int i = 0; i < Board.BOARD_HEIGHT; i++) {
			if (cells[rowInQuestion][i].getNum0fPossibilitiesLeft() == 3) {
				if (logger.isDebugEnabled()) {
//					logger.debug("ADDED "+cells[rowInQuestion][i].getCellCoordinates());
				}
				nakedCells.add(cells[rowInQuestion][i]);
			}
		}
		
		return nakedCells;
	}
	
	/**
	 * Returns all cells in the specified quadrant with 2 possibilities.
	 * @param cells
	 * @param quadInQuestion
	 * @return
	 */
	public ArrayList<Cell> getCellsInQuadWithTwoPossibilities(int quadInQuestion) {
		ArrayList<Cell> nakedCells = new ArrayList<Cell>();
		
		for (int j=0; j < Board.BOARD_WIDTH; j++) {
			for (int i = 0; i < Board.BOARD_HEIGHT; i++) {
				if (cells[j][i].getQuad()==quadInQuestion) {
					if (cells[j][i].getNum0fPossibilitiesLeft() == 2) {
						if (logger.isDebugEnabled()) {
//							logger.debug("ADDED "+cells[rowInQuestion][i].getCellCoordinates());
						}
						nakedCells.add(cells[j][i]);
					}
				}
			}
		}
		
		return nakedCells;
	}
	
	/**
	 * Returns all cells in the specified quadrant with 3 possibilities.
	 * @param cells
	 * @param quadInQuestion
	 * @return
	 */
	public ArrayList<Cell> getCellsInQuadWithThreePossibilities(int quadInQuestion) {
		ArrayList<Cell> nakedCells = new ArrayList<Cell>();
		
		for (int j=0; j < Board.BOARD_WIDTH; j++) {
			for (int i = 0; i < Board.BOARD_HEIGHT; i++) {
				if (cells[j][i].getQuad()==quadInQuestion) {
					if (cells[j][i].getNum0fPossibilitiesLeft() == 3) {
						if (logger.isDebugEnabled()) {
//							logger.debug("ADDED "+cells[rowInQuestion][i].getCellCoordinates());
						}
						nakedCells.add(cells[j][i]);
					}
				}
			}
		}
		
		return nakedCells;
	}
	
	/**
	 * Given the board, remove the possible value from all of the cells
	 * in the specified quad.
	 * @param cellsInQuad
	 * @param valueToRemove
	 */
	public void removePossibilityFromQuad(int valueToRemove, int quad) {
		ArrayList<Cell> cellsInQuad = getCellsInQuad(quad);
		for (Cell cell : cellsInQuad) {
			cell.removePossibility(valueToRemove,"Rules of Game");
		}
	}
	
	/**
	 * Given the board, add the possible value to all of the cells
	 * in the specified quad.
	 * @param cellsInQuad
	 * @param valueToAdd
	 */
	public void addPossibilityToQuad(int valueToAdd, int quad) {
		ArrayList<Cell> cellsInQuad = getCellsInQuad(quad);
		for (Cell cell : cellsInQuad) {
			cell.addPossibility(0,valueToAdd);
		}
	}
	
	/**
	 * Given the board, remove the possible value from all of the cells
	 * in the specified row.
	 * @param cellsInRow
	 * @param valueToRemove
	 */
	public void removePossibilityFromRow(int valueToRemove, int row) {
		ArrayList<Cell> cellsInRow = getCellsInRow(row);
		for (Cell cell : cellsInRow) {
			cell.removePossibility(valueToRemove,"Rules of Game");
		}
	}
	
	/**
	 * Given the board, add the possible value to all of the cells
	 * in the specified row.
	 * @param cellsInRow
	 * @param valueToAdd
	 */
	public void addPossibilityToRow(int valueToAdd, int row) {
		ArrayList<Cell> cellsInRow = getCellsInRow(row);
		for (Cell cell : cellsInRow) {
			cell.addPossibility(0,valueToAdd);
		}
	}
	
	/**
	 * Given the board, remove the possible value from all of the cells
	 * in the specified col.
	 * @param cells
	 * @param valueToRemove
	 * @param col
	 */
	public void removePossibilityFromCol(int valueToRemove, int col) {
		ArrayList<Cell> cellsInCol = getCellsInCol(col);
		for (Cell cell : cellsInCol) {
			cell.removePossibility(valueToRemove,"Rules of Game");
		}
	}
	
	/**
	 * Given the board, add the possible value to all of the cells
	 * in the specified col.
	 * @param cells
	 * @param valueToAdd
	 * @param col
	 */
	public void addPossibilityToCol(int valueToAdd, int col) {
		ArrayList<Cell> cellsInCol = getCellsInCol(col);
		for (Cell cell : cellsInCol) {
			cell.addPossibility(0,valueToAdd);
		}
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
		return cells;
	}

	/**
	 * Sets the cells on the board given the cells array
	 * @param cells
	 */
	public void setCells(Cell[][] cells) {
		Board.cells = cells;
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
	public static void setBoard(Board board) {
		Board.board = board;
	}

	/**
	 * @return the board
	 */
	public static Board getBoard() {
		if (board==null) {
			board=new Board("PRIVATE");
//			syncInProgress=true;
			Thread thread = new Thread(board);
			thread.start();
		}
		return board;
	}
	
	/**
	 * @return the board given
	 */
	public static Board getBoard(int difficultyLevel, int[][] array) {
		if (board==null) {
			board=new Board(difficultyLevel, array);
		} else {
			board.difficultyLevel=difficultyLevel;
			board.difficultyText=board.getDifficultyTextFromLevel(board.difficultyLevel);
			board.setCurrentState(array);
		}
		return board;
	}

	/**
	 * Takes all values from the board and places in a 
	 * String buffer.
	 */
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		
		for (int row = 0; row < Board.BOARD_HEIGHT; row++) {
			for (int col = 0; col < Board.BOARD_WIDTH; col++) {
				if (cells[row][col].isEmpty()) {
					strBuf.append("0" + Printable.SEPERATOR);
				} else {
					strBuf.append(cells[row][col].getValue() + Printable.SEPERATOR);
				}
			}
		}	
		
		return strBuf.toString();
	}

	/**
	 * Prints the statistics of the current board
	 */
	public void printInProgressStatistics() {
		if (logger.isDebugEnabled()) {
			logger.debug("\t----------------------------------------------------------------");
			logger.debug("\tRecursive Call ["+this.recursiveCallsToSolve+"]");
			logger.debug("\tNumber of Unsolved Cells ["+this.numOfUnsolvedCells+"]");
			logger.debug("\tNumber of Solved Cells ["+this.numOfSolvedCells+"]");
			logger.debug("\tNumber of Possibilities Left ["+this.getNumberOfPossibilitiesRemaining()+"]");
			logger.debug("\tNumber of Randomly Placed Values ["+this.numOfRandomValues+"]");
			logger.debug("\t----------------------------------------------------------------");
		}
	}
	
	/**
	 * Prints the final statistics of the solved board.
	 */
	public void printFinalStatistics() {
		if (logger.isDebugEnabled()) {
			logger.debug("****************************************************************");
			logger.debug(this.getDifficultyText()+" BOARD SOLVED");
			logger.debug("INITIAL AVG POSS / CELL ["+this.getInitialAvgPosPerCell()+"]");
			logger.debug("Recursive Calls Needed To Solve ["+this.recursiveCallsToSolve+"]");
			logger.debug("Number of Randomly Placed Values ["+this.numOfRandomValues+"]");
			logger.debug("****************************************************************");
		}
	}
	
	/**
	 * Shows cells on the board with only 1 possibility left.
	 */
	public void printCellsWithOnePossibility() {
		if (logger.isDebugEnabled()) {
			logger.debug("\nCELLS W/ONE POSSIBILITY LEFT ON BOARD\n");
		}
		for (int row = 0; row < Board.BOARD_HEIGHT; row++) {
			for (int col = 0; col < Board.BOARD_WIDTH; col++) {
				if (cells[row][col].getNum0fPossibilitiesLeft() == 1) {
					if (logger.isDebugEnabled()) {
						System.out.println("[" + cells[row][col].getQuad()+","+cells[row][col].getRow() + ","
							+ cells[row][col].getCol() + "]");
						System.out.println(cells[row][col].getPossibilities()
							.get(0));
					}
				}
			}
			if (logger.isDebugEnabled()) {
				System.out.println('\n');
			}
		}
	}
	
	/**
	 * Shows solver steps taken on the board.
	 */
	public void printSolverSteps() {
		if (logger.isDebugEnabled()) {
			logger.debug("\nSTEPS TAKEN TO SOLVE BOARD\n");
		}
		
		int counter=1;
		for (CellHistory cellHistory: Solver.getSolveSteps()) {
			Cell cell = cellHistory.getCell();	
			int value = cellHistory.getValue();
			int addRemove = cellHistory.getActionFlag();
			
			if (logger.isDebugEnabled()) {
				if (addRemove == 0) {
					logger.debug(counter+": ["+value+"]"+" ELIMINATED FROM ["+cell.getRow()+","+cell.getCol()+"]");
				} else if (addRemove == 1) {
					logger.debug(counter+": ["+value+"]"+" PLACED IN ["+cell.getRow()+","+cell.getCol()+"]");
					logger.debug(counter+": ["+value+"]"+" ELIMINATED FROM QUAD ["+cell.getQuad()+"], ROW ["+cell.getRow()+"], COL ["+cell.getCol()+"]");
				} else {
					logger.error("Unrecognized Action Variable in CellHistory["+addRemove+"]");
				}
			}
			counter++;
		}
	}

	/**
	 * Shows the current values that are placed on the board.
	 */
	public void printCurrentValues() {
		if (logger.isDebugEnabled()) {
			logger.debug("VALUES ON BOARD");
		}
		for (int row = 0; row < Board.BOARD_HEIGHT; row++) {
			for (int col = 0; col < Board.BOARD_WIDTH; col++) {
				if (cells[row][col].isEmpty()) {
					if (logger.isDebugEnabled()) {
						System.out.print(Printable.FILLER + Printable.SEPERATOR);
					}
				} else {
					if (logger.isDebugEnabled()) {
						System.out.print(cells[row][col].getValue()
							+ Printable.SEPERATOR);
					}
				}
			}
			if (logger.isDebugEnabled()) {
				System.out.println();
			}
		}		
	}

	/**
	 * Shows the locked values on the board.
	 */
	public void printLockedCells() {
		if (logger.isDebugEnabled()) {
			logger.debug("\nLOCKED VALUES ON BOARD\n");
		}
		for (int row = 0; row < Board.BOARD_HEIGHT; row++) {
			for (int col = 0; col < Board.BOARD_WIDTH; col++) {
				if (cells[row][col].isLocked()) {
					if (logger.isDebugEnabled()) {
						System.out.print(cells[row][col].getValue()
							+ Printable.SEPERATOR);
					}
				} else {
					if (logger.isDebugEnabled()) {
						System.out.print(Printable.FILLER + Printable.SEPERATOR);
					}
				}
			}
			if (logger.isDebugEnabled()) {
				System.out.println();
			}
		}
	}

	/**
	 * Prints the possibilities left on the board.
	 */
	public void printPossibilities() {
		logger.debug("\nPOSSIBILITIES LEFT ON BOARD\n");
		for (int row = 0; row < Board.BOARD_HEIGHT; row++) {
			for (int col = 0; col < Board.BOARD_WIDTH; col++) {
				if (logger.isDebugEnabled()) {
					System.out.print(cells[row][col].getPossibilities()
							+ Printable.SEPERATOR);
				}
			}
			if (logger.isDebugEnabled()) {
				System.out.print('\n');
			}
		}
	}

	/**
	 * Prints the quadrant values on the board.
	 */
	public void printQuadrantValues() {
		logger.debug("QUADRANT VALUES ON BOARD\n");
		for (int row = 0; row < Board.BOARD_HEIGHT; row++) {
			for (int col = 0; col < Board.BOARD_WIDTH; col++) {
				if (logger.isDebugEnabled()) {
					System.out.print(cells[row][col].getQuad()
							+ Printable.SEPERATOR);
				}
			}
			if (logger.isDebugEnabled()) {
				System.out.print('\n');
			}
		}
	}

	/**
	 * Prints the empty cells left on the board.
	 */
	public void printEmptyCells() {
		logger.debug("EMPTY CELLS ON BOARD\n");
		for (int row = 0; row < Board.BOARD_HEIGHT; row++) {
			for (int col = 0; col < Board.BOARD_WIDTH; col++) {
				if (cells[row][col].isEmpty()) {
					if (logger.isDebugEnabled()) {
						System.out.print(cells[row][col].getValue()
								+ Printable.SEPERATOR);
					}
				}
			}
			if (logger.isDebugEnabled()) {
				System.out.print('\n');
			}
		}
	}
	
	/**
	 * Synchronizes the user facing board with the back in board 
	 * used to check the solution.
	 */
	private static synchronized void syncBoardCopy() {
		BackGroundBoard.getBackGroundBoard().setCells(board.getCells());
		//BackGroundBoard.getBackGroundBoard().calcAvgPossPerCell();
		BackGroundBoard.getBackGroundBoard().setColoringEnabled(board.isColoringEnabled());
		BackGroundBoard.getBackGroundBoard().setDifficultyLevel(board.getDifficultyLevel());
		BackGroundBoard.getBackGroundBoard().setDifficultyText(board.getDifficultyText());
		BackGroundBoard.getBackGroundBoard().setNumOfRandomValues(board.getNumOfRandomValues());
		BackGroundBoard.getBackGroundBoard().setNumOfSolvedCells(board.getNumOfSolvedCells());
		BackGroundBoard.getBackGroundBoard().setNumOfUnsolvedCells(board.getNumOfUnsolvedCells());
		BackGroundBoard.getBackGroundBoard().setRecursiveCallsToSolve(board.getRecursiveCallsToSolve());
		System.out.println("GUI Board Values");
		board.printCurrentValues();
		System.out.println("Backend Board Values");
		//BackGroundBoard.getBackGroundBoard().printCurrentValues();
	}

	@Override
	public void run() {
		SudokuStatusLabel.getStatusBar().setMessage("Synchronizing Board");
		while (syncInProgress) {	
			syncBoardCopy();
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				logger.error("Error Synchronizing Board and Board Copy");
			}
			SudokuStatusLabel.getStatusBar().setMessage("Board Copy Synchronized, Ready");	
		}
		
	}
}
