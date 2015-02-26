/**
 * 
 */
package com.sudoku.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.sudoku.cell.Cell;
import com.sudoku.cell.CellHistory;
import com.sudoku.gui.board.Board;
import com.sudoku.rules.Rules;

/**
 * @author BWinters
 * 
 */
public class Solver implements Runnable {

	static final Logger logger = Logger.getLogger(com.sudoku.solver.Solver.class); 
	protected Board board = null;
	protected Rules rule = new Rules();
	private boolean solveInProgress = false;
	private int solveStartTime=0;
	private int solveEndTime=0;
	private static ArrayList<CellHistory> solveSteps = new ArrayList<CellHistory>();
	private static boolean silentMode=false;
	private static Solver solver = null;
	
	/**
	 * Public constructor for inheritence in BoardGenerator
	 */
	public Solver(int one) {};
	
	/**
	 * Solver Contructor
	 * @param board
	 */
	private Solver() {
	}
		
	/**
	 * Recursive function to solve sudoku board.
	 * Places obvious values in cells with only 1 possibility.
	 * Then cross checks each row, column, and quadrant in which a 
	 * possibility only occurs once in that row, column, or quadrant.
	 * @return
	 */
	public boolean solve() {
		int valuePlaced = 0;
		if (silentMode) {
			board = Board.getBoard();
		} else {
			board = Board.getBoard();
		}

		Cell[][] cells = board.getCells();
		int startingNumOfPossibilities=board.getNumberOfPossibilitiesRemaining();
		
		if (board.isSolved()) {
			if (logger.isDebugEnabled()) {
				board.printFinalStatistics();
			}		
			return true;
		} else {
			board.setRecursiveCallsToSolve(board.getRecursiveCallsToSolve()+1);
			board.printInProgressStatistics();
			
			for (int row = 0; row < Board.BOARD_HEIGHT; row++) {
				for (int col = 0; col < Board.BOARD_WIDTH; col++) {
					Cell cell = cells[row][col];
					if (cell.getNum0fPossibilitiesLeft() == 1 && cell.isEmpty()	&& !(cell.isLocked())) {
						valuePlaced = cell.getPossibilities().get(0);
						if (rule.runRules(cell, valuePlaced)) {
							cell.setValue(valuePlaced);
							board.setNumOfSolvedCells(board.getNumOfSolvedCells()+1);
							board.updateBoard();
						}
					}
				}
			}

			for (int quad = 0; quad < Board.BOARD_HEIGHT; quad++) {
				loneValueInQuad(quad);
				nakedPairsInQuad(quad);
				nakedTripleInQuad(quad);
			}
			
			for (int row = 0; row < Board.BOARD_HEIGHT; row++) {
				loneValueInRow(row);
				reduceRow(row);
				nakedPairsInRow(row);
				nakedTripleInRow(row);
			}
			
			for (int col = 0; col < Board.BOARD_HEIGHT; col++) {
				loneValueInColumn(col);
				reduceColumn(col);
				nakedPairsInCol(col);
				nakedTripleInCol(col);
			}
		}
		
		int endingNumOfPossibilities=board.getNumberOfPossibilitiesRemaining();
		if (startingNumOfPossibilities == endingNumOfPossibilities) {
			if (logger.isDebugEnabled()) {
				logger.debug("*************Unable to eliminate a possibility on the board*****************");
			}
			return false;
		}		
		return solve();
	}
	
	/**
	 * Recursive function to solve sudoku board.
	 * Places obvious values in cells with only 1 possibility.
	 * Then cross checks each row, column, and quadrant in which a 
	 * possibility only occurs once in that row, column, or quadrant.
	 * @return
	 */
	public boolean solveSilent(int[][] cells) {
		return false;
	}
			
	/**
	 * Identify a possibility that only occurs once accros a row.
	 * @param cells
	 * @param row
	 */
	protected void loneValueInRow(final int row) {
		final ArrayList<Cell> cellsInRow = board.getCellsInRow(row);
		final Set<Integer> uniques = new HashSet<Integer>();
		final Set<Integer> dups = new HashSet<Integer>();

		for (Cell cell : cellsInRow) {
			for (Integer posibility : cell.getPossibilities()) {
				if (!uniques.add(posibility)) {
					dups.add(posibility);
				}
			}
		}

		uniques.removeAll(dups); // Destructive set-difference

//		 System.out.println(uniques.size() + " distinct possibilities detected in ROW ["+row+"]: " + uniques);
//		 System.out.println(dups.size() + " dups possibilities detected in ROW ["+row+"]: " + dups);

		for (Integer valuePlaced : uniques) {
			for (Cell cell : cellsInRow) {
				if (cell.getPossibilities().contains(valuePlaced)) {
					cell.setValue(valuePlaced);
					board.setNumOfSolvedCells(board.getNumOfSolvedCells()+1);
					board.removePossibilityFromQuad(valuePlaced, cell.getQuad());
					board.removePossibilityFromRow(valuePlaced, cell.getRow());
					board.removePossibilityFromCol(valuePlaced, cell.getCol());
				}
			}
		}
	}

	/**
	 * Identify a possibility that only occurs once accros a col.
	 * @param cells
	 * @param col
	 */
	protected void loneValueInColumn(final int col) {
		final ArrayList<Cell> cellsInCol = board.getCellsInCol(col);
		final Set<Integer> uniques = new HashSet<Integer>();
		final Set<Integer> dups = new HashSet<Integer>();

		for (Cell cell : cellsInCol) {
			for (Integer posibility : cell.getPossibilities()) {
				if (!uniques.add(posibility)) {
					dups.add(posibility);
				}
			}
		}

		uniques.removeAll(dups); // Destructive set-difference

//		 System.out.println(uniques.size() + " distinct possibilities detected: " + uniques);
//		 System.out.println(dups.size() + " dups possibilities detected : " + dups);

		for (Integer valuePlaced : uniques) {
			for (Cell cell : cellsInCol) {
				if (cell.getPossibilities().contains(valuePlaced)) {
					cell.setValue(valuePlaced);
					board.setNumOfSolvedCells(board.getNumOfSolvedCells()+1);
					board.removePossibilityFromQuad(valuePlaced, cell.getQuad());
					board.removePossibilityFromRow(valuePlaced, cell.getRow());
					board.removePossibilityFromCol(valuePlaced, cell.getCol());
				}
			}
		}
	}

	/**
	 * Identify a possibility that only occurs once inside a
	 * quadrant, but is limited to 1 row or column.
	 * @param cells
	 * @param quad 
	 */
	protected void loneValueInQuad(final int quad) {
		final ArrayList<Cell> cellsInQuad = board.getCellsInQuad(quad);
		final Set<Integer> uniques = new HashSet<Integer>();
		final Set<Integer> dups = new HashSet<Integer>();

		for (Cell cell: cellsInQuad) {
			for (Integer posibility : cell.getPossibilities()) {
				if (!uniques.add(posibility)) {
					dups.add(posibility);
				}
			}
		}

		uniques.removeAll(dups); // Destructive set-difference

//		 System.out.println(uniques.size() + " distinct possibilities detected in QUAD ["+quad+"]: " + uniques);
//		 System.out.println(dups.size() + " dups possibilities detected in QUAD ["+quad+"]: " + dups);

		for (Integer valuePlaced : uniques) {
			for (Cell cell : cellsInQuad) {
				if (cell.getPossibilities().contains(valuePlaced)) {
					cell.setValue(valuePlaced);
					board.setNumOfSolvedCells(board.getNumOfSolvedCells()+1);
					board.removePossibilityFromQuad(valuePlaced, cell.getQuad());
					board.removePossibilityFromRow(valuePlaced, cell.getRow());
					board.removePossibilityFromCol(valuePlaced, cell.getCol());
				}
			}
		}
	}

	
	/**
	 * Identify a posibility that occurs more than once on a col, but it limited
	 * to 1 quadrant.
	 * @param cells
	 * @param colInQuestion
	 */
	protected void reduceColumn(final int colInQuestion) {	
		final ArrayList<Cell> cellsInCol = board.getCellsInCol(colInQuestion);
		Set<Integer> uniques = new HashSet<Integer>();
		Set<Integer> uniqueQuads = new HashSet<Integer>();
		Set<Integer> dups = new HashSet<Integer>();

		for (int i=0; i < cellsInCol.size(); i++) {
			ArrayList<Integer> possibleValues = cellsInCol.get(i).getPossibilities();
			for (Integer posibility : possibleValues) {
				if (!uniques.add(posibility)) {
					dups.add(posibility);
				}
			}
		}
		
		//Loop through each possibility that occurs more than once
		//in row and add the quadrants for those cells that contain that possibility
		//to a unique set.  Afterward, if the set only has 1 item in it then all cells
		//identified have the same quad, and we can eliminate that possiblity from all
		//other cells in quad excluding the rowInQuestion.
		for (Integer possibility : dups) {
			ArrayList<Cell> cellsContainingPossibility = getCellsContainingPossibility(cellsInCol, possibility);
			for (Cell cell : cellsContainingPossibility) {
				uniqueQuads.add(cell.getQuad());
			}
			
			//For any value possibility that is in the array, remove that possibility from 
			//any cell in the quad, excluding the row in question.
			if (uniqueQuads.size()==1) {
				for (Integer quad : uniqueQuads) {
					ArrayList<Cell> quadCells = board.getCellsInQuad(quad);
//					System.out.println("COL - ["+possibility+"] OCCURS IN QUAD ["+quad+"]");
					for (Cell cell : quadCells) {
						if ((cell.getCol() != colInQuestion) && cell.isEmpty()&& cell.getPossibilities().contains(possibility)) {
//							System.out.println("COL Removing " + possibility + " from quadrant " + cell.getQuad());
//							System.out.println(cell.getCellCoordinates());
							cell.removePossibility(possibility);
							
						}
					}
				}
				
			} 
			uniqueQuads.clear();
		}
	}
	
	/**
	 * Identify a posibility that occurs more than once on a row, but it limited
	 * to 1 quadrant.
	 * @param cells
	 * @param rowInQuestion
	 */
	protected void reduceRow(final int rowInQuestion) {
		final ArrayList<Cell> cellsInRow = board.getCellsInRow(rowInQuestion);
		Set<Integer> uniques = new HashSet<Integer>();
		Set<Integer> uniqueQuads = new HashSet<Integer>();
		Set<Integer> dups = new HashSet<Integer>();

		for (int i=0; i < cellsInRow.size(); i++) {
			ArrayList<Integer> possibleValues = cellsInRow.get(i).getPossibilities();
			for (Integer posibility : possibleValues) {
				if (!uniques.add(posibility)) {
					dups.add(posibility);
				}
			}
		}
		
		//Loop through each possibility that occurs more than once
		//in row and add the quadrants for those cells that contain that possibility
		//to a unique set.  Afterward, if the set only has 1 item in it then all cells
		//identified have the same quad, and we can eliminate that possiblity from all
		//other cells in quad excluding the rowInQuestion.
		for (Integer possibility : dups) {
			ArrayList<Cell> cellsContainingPossibility = getCellsContainingPossibility(cellsInRow, possibility);
			for (Cell cell : cellsContainingPossibility) {
				uniqueQuads.add(cell.getQuad());
			}
			
			//For any value possibility that is in the array, remove that possibility from 
			//any cell in the quad, excluding the row in question.
			if (uniqueQuads.size()==1) {
				for (Integer quad : uniqueQuads) {
					ArrayList<Cell> quadCells = board.getCellsInQuad(quad);
//					System.out.println("ROW - ["+possibility+"] OCCURS IN QUAD ["+quad+"]");
					for (Cell cell : quadCells) {
						if ((cell.getRow() != rowInQuestion) && cell.isEmpty()&& cell.getPossibilities().contains(possibility)) {
//							System.out.println("ROW - Removing " + possibility + " from quadrant " + cell.getQuad());
//							System.out.println(cell.getCellCoordinates());
							cell.removePossibility(possibility);
							
						}
					}
				}
				
			} 
			uniqueQuads.clear();
		}
	}
	
	/**
	 * Identify any 2 cells in a quad that have the same 2 identical
	 * possibilities left.  Remove those possibilities from the quad 
	 * excluding the 2 cells identified
	 * @param cells
	 * @param quadInQuestion
	 */
	protected void nakedPairsInQuad(int quadInQuestion) {	
		ArrayList<Cell> cellsWith2Pos = new ArrayList<Cell>();
		Set<ArrayList<Integer>> uniquePosSets = new HashSet<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> duplicatePosSets = new ArrayList<ArrayList<Integer>>();
		HashMap<Cell,ArrayList<Integer>> nakedPairs = new HashMap<Cell,ArrayList<Integer>>();
		ArrayList<Integer> nakedPossibilities = new ArrayList<Integer>();
		
		//Gather all cells in the quadInQuestion that have 2 possibilities
		cellsWith2Pos = board.getCellsInQuadWithTwoPossibilities(quadInQuestion);
		if (cellsWith2Pos.isEmpty() || cellsWith2Pos.size() < 2) {
			if (logger.isDebugEnabled()) {
//				logger.debug("No Naked Pairs Found in QUAD " + quadInQuestion);
			}
			return;
		}
		
		for (Cell cell : cellsWith2Pos) {
			if (!uniquePosSets.add(cell.getPossibilities())) {
				if (logger.isDebugEnabled()) {
//					logger.debug("FOUND " + cellsWith2Pos.get(cell).getPosibleValues());
				}
				duplicatePosSets.add(cell.getPossibilities());
			}
		}
		
//		logger.debug(duplicatePosSets.size() + " = DUPLICATE POS SETS SIZE");
		
		if (!duplicatePosSets.isEmpty()) {
			for (Cell cell : cellsWith2Pos) {
				if (cell.getPossibilities().equals(duplicatePosSets.get(0))) {
					if (logger.isDebugEnabled()) {
//						logger.debug("NAKED PAIR CELL " + cell.getCellCoordinates()+ " WITH POSSIBILITIES " + cell.getPosibleValues());
					}
					nakedPairs.put(cell, cell.getPossibilities());
				}
			}

			for (Cell cell : nakedPairs.keySet()) {
				for (Integer possibility : nakedPairs.get(cell)) {
					nakedPossibilities.add(possibility);
				}
			}

			ArrayList<Cell> cellsInQuad = board.getCellsInQuad(quadInQuestion);
			for (Cell cell : cellsInQuad) {
				for (Integer possibility : nakedPossibilities) {
					if (!(nakedPairs.keySet().contains(cell)) && (cell.getPossibilities().contains(possibility))) {
						if (logger.isDebugEnabled()) {
//							logger.debug("REMOVING " + possibility + " FROM CELL " + cell.getCellCoordinates());
						}
						cell.removePossibility(possibility);
					}
				}
			}
		}
	}
	
	/**
	 * Identify any 2 cells in a column that have the same 2 identical
	 * possibilities left.  Remove those possibilities from the column 
	 * excluding the 2 cells identified.
	 * @param cells
	 * @param colInQuestion
	 */
	protected void nakedPairsInCol(int colInQuestion) {	
		ArrayList<Cell> cellsWith2Pos = new ArrayList<Cell>();
		Set<ArrayList<Integer>> uniquePosSets = new HashSet<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> duplicatePosSets = new ArrayList<ArrayList<Integer>>();
		HashMap<Cell,ArrayList<Integer>> nakedPairs = new HashMap<Cell,ArrayList<Integer>>();
		ArrayList<Integer> nakedPossibilities = new ArrayList<Integer>();
		
		//Gather all cells in the colInQuestion that have 2 possibilities
		cellsWith2Pos = board.getCellsInColWithTwoPossibilities(colInQuestion);
		if (cellsWith2Pos.isEmpty() || cellsWith2Pos.size() < 2) {
			if (logger.isDebugEnabled()) {
//				logger.debug("No Naked Pairs Found in COL " + colInQuestion);
			}
			return;
		}
		
		for (Cell cell : cellsWith2Pos) {
			if (!uniquePosSets.add(cell.getPossibilities())) {
				if (logger.isDebugEnabled()) {
//					logger.debug("FOUND " + cellsWith2Pos.get(cell).getPosibleValues());
				}
				duplicatePosSets.add(cell.getPossibilities());
			}
		}
		
//		logger.debug(duplicatePosSets.size() + " = DUPLICATE POS SETS SIZE");
		
		if (!duplicatePosSets.isEmpty()) {
			for (Cell cell : cellsWith2Pos) {
				if (cell.getPossibilities().equals(duplicatePosSets.get(0))) {
					if (logger.isDebugEnabled()) {
//						logger.debug("NAKED PAIR CELL " + cell.getCellCoordinates()+ " WITH POSSIBILITIES " + cell.getPosibleValues());
					}
					nakedPairs.put(cell, cell.getPossibilities());
				}
			}

			for (Cell cell : nakedPairs.keySet()) {
				for (Integer possibility : nakedPairs.get(cell)) {
					nakedPossibilities.add(possibility);
				}
			}

			ArrayList<Cell> cellsInCol = board.getCellsInCol(colInQuestion);
			for (Cell cell : cellsInCol) {
				for (Integer possibility : nakedPossibilities) {
					if (!(nakedPairs.keySet().contains(cell)) && (cell.getPossibilities().contains(possibility))) {
						if (logger.isDebugEnabled()) {
//							logger.debug("REMOVING " + possibility + " FROM CELL " + cell.getCellCoordinates());
						}
						cell.removePossibility(possibility);
					}
				}
			}
		}
	}
	
	/**
	 * Identify any 2 cells in a row that have the same 2 identical
	 * possibilities left.  Remove those possibilities from the row 
	 * excluding the 2 cells identified.
	 * @param cells
	 * @param rowInQuestion
	 */
	protected void nakedPairsInRow(int rowInQuestion) {	
		ArrayList<Cell> cellsWith2Pos = new ArrayList<Cell>();
		Set<ArrayList<Integer>> uniquePosSets = new HashSet<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> duplicatePosSets = new ArrayList<ArrayList<Integer>>();
		HashMap<Cell,ArrayList<Integer>> nakedPairs = new HashMap<Cell,ArrayList<Integer>>();
		ArrayList<Integer> nakedPossibilities = new ArrayList<Integer>();
		
		//Gather all cells in the rowInQuestion that have 2 possibilities
		cellsWith2Pos = board.getCellsInRowWithTwoPossibilities(rowInQuestion);
		if (cellsWith2Pos.isEmpty() || cellsWith2Pos.size() < 2) {
			if (logger.isDebugEnabled()) {
//				logger.debug("No Naked Pairs Found in ROW " + rowInQuestion);
			}
			return;
		}
		
		for (Cell cell : cellsWith2Pos) {
			if (!uniquePosSets.add(cell.getPossibilities())) {
				if (logger.isDebugEnabled()) {
//					logger.debug("FOUND " + cellsWith2Pos.get(cell).getPosibleValues());
				}
				duplicatePosSets.add(cell.getPossibilities());
			}
		}
		
//		logger.debug(duplicatePosSets.size() + " = DUPLICATE POS SETS SIZE");
		
		if (!duplicatePosSets.isEmpty()) {
			for (Cell cell : cellsWith2Pos) {
				if (cell.getPossibilities().equals(duplicatePosSets.get(0))) {
					if (logger.isDebugEnabled()) {
//						logger.debug("NAKED PAIR CELL " + cell.getCellCoordinates()+ " WITH POSSIBILITIES " + cell.getPosibleValues());
					}
					nakedPairs.put(cell, cell.getPossibilities());
				}
			}

			for (Cell cell : nakedPairs.keySet()) {
				for (Integer possibility : nakedPairs.get(cell)) {
					nakedPossibilities.add(possibility);
				}
			}

			ArrayList<Cell> cellsInRow = board.getCellsInRow(rowInQuestion);
			for (Cell cell : cellsInRow) {
				for (Integer possibility : nakedPossibilities) {
					if (!(nakedPairs.keySet().contains(cell)) && (cell.getPossibilities().contains(possibility))) {
						if (logger.isDebugEnabled()) {
//							logger.debug("REMOVING " + possibility + " FROM CELL " + cell.getCellCoordinates());
						}
						cell.removePossibility(possibility);
					}
				}
			}
		}
	}
	
	/**
	 * Identify any 3 cells in a quad that have the same 3 identical
	 * possibilities left.  Remove those possibilities from the cells 
	 * in the qaud excluding the 3 cells identified. If the 3 cells.
	 * @param cells
	 * @param rowInQuestion
	 */
	protected void nakedTripleInQuad(int quadInQuestion) {		
		ArrayList<Cell> nakedTriples = new ArrayList<Cell>();
		ArrayList<Cell> cellsInQuad = board.getCellsInQuad(quadInQuestion);
		Set<Integer> uniquePos = new HashSet<Integer>();
		Set<Integer> rowList = new HashSet<Integer>();
		Set<Integer> colList = new HashSet<Integer>();
		
		//1. Gather all cells in the quadInQuestion with 3 possibilities and search for pattern:
		//	A,B,C | A,B,C | A,B,C
//		System.out.println("1. COL - ["+quadInQuestion+"]");
		int cellsTheSame=0;
		ArrayList<Cell> cellsWith3Pos = board.getCellsInQuadWithThreePossibilities(quadInQuestion);
		for (Cell cell : cellsWith3Pos) {
			ArrayList<Integer> pos3Set = cell.getPossibilities();
			for (Cell cell2 : cellsWith3Pos) {
				if (cell2.getPossibilities().equals(pos3Set)) {
					cellsTheSame++;
				} 
			}
			if (cellsTheSame==3 && !nakedTriples.contains(cell)) {
//				System.out.println("1. COL - ["+quadInQuestion+"] FOUND CELL "+cell.getCoordinates());
				nakedTriples.add(cell);
			}
			cellsTheSame=0;
		}
		
		//Make sure there are only 3 cells found.
		if (nakedTriples.size() == 3) {
//			System.out.println("1. Triples FOUND - COL ["+quadInQuestion+"] RIGHT NUMBER OF CELLS");
			//For each cell save its quadrant and possibilities.
			for (Cell cell : nakedTriples) {
				rowList.add(cell.getRow());
				colList.add(cell.getCol());
				for (Integer pos : cell.getPossibilities()) {
					uniquePos.add(pos);
				}
			}
			
			//Make sure there are only 3 unique possibilities in the 3 cells found.
			if (uniquePos.size()==3) {
//				System.out.println("1. Triples FOUND - COL ["+quadInQuestion+"] RIGHT NUMBER OF UNIQUE NUMBERS");
				//For each cell in the column, and if the cell in the column is not in the nakedTriples list,
				//then remove the unique possibilities found above from that cell.
				for (Cell cell : cellsInQuad) {
					if (!nakedTriples.contains(cell)) {
						for (Integer pos : uniquePos) {
//							System.out.println("1. COL ["+quadInQuestion+"] - Removing " + pos + " from " + cell.getCoordinates());
							if (cell.isEmpty()) {
								cell.removePossibility(pos);
								
							}
						}
					
					}
				}
				
				//If all 3 cells are within the same quadrant, remove the 3 unique possibilities from the quadrant 
				//as well.
				if (rowList.size()==1) {
					int row = rowList.iterator().next();
					ArrayList<Cell> cellsInRow = board.getCellsInRow(row);
					for (Cell cell : cellsInRow) {
						if (!nakedTriples.contains(cell)) {
							for (Integer pos : uniquePos) {
//								System.out.println("1. ROW ["+row+"]- Removing " + pos + " from " + cell.getCoordinates());
								if (cell.isEmpty()) {
									cell.removePossibility(pos);
									
								}
							}
						}
						
					}
				}
				
				if (colList.size()==1) {
					int col = colList.iterator().next();
					ArrayList<Cell> cellsInCol = board.getCellsInCol(col);
					for (Cell cell : cellsInCol) {
						if (!nakedTriples.contains(cell)) {
							for (Integer pos : uniquePos) {
//								System.out.println("4. COL ["+col+"]- Removing " + pos + " from " + cell.getCoordinates());
								if (cell.isEmpty()) {
									cell.removePossibility(pos);
									
								}
							}
						}
						
					}
				}
			}	
		}
		
		uniquePos.clear();
		nakedTriples.clear();
		rowList.clear();
		colList.clear();
			
		//2. Gather all cells in the rowInQuestion with 2 or 3 possibilities and search for pattern:
		//	A,B | A,B,C | A,B,C
//		System.out.println("2. COL - ["+quadInQuestion+"]");
		cellsTheSame=0;
		ArrayList<Cell> cellsWith2Pos = board.getCellsInQuadWithTwoPossibilities(quadInQuestion);
		for (Cell cell : cellsWith2Pos) {
			for (Cell cell2 : cellsWith3Pos) {
				if (cell2.getPossibilities().containsAll(cell.getPossibilities())) {
					cellsTheSame++;
				}
			}
			if (cellsTheSame==2) {
				ArrayList<Cell> cellsContaining = getCellsContainingPossibilityList(cellsWith3Pos, cell.getPossibilities());
				if (cellsContaining.get(0).getPossibilities().equals(cellsContaining.get(1).getPossibilities())) {
//					System.out.println("2. COL - ["+quadInQuestion+"] FOUND CELL "+cell.getCoordinates()+"PLUS "+cellsContaining.size());
					nakedTriples.add(cell);
					nakedTriples.addAll(cellsContaining);
				}
			}
			cellsTheSame=0;
		}
		
		//Make sure there are only 3 cells found.
		if (nakedTriples.size() == 3) {
//			System.out.println("2. Triples FOUND - COL ["+quadInQuestion+"] RIGHT NUMBER OF CELLS");
	
			//For each cell save its quadrant and possibilities.
			for (Cell cell : nakedTriples) {
				rowList.add(cell.getRow());
				colList.add(cell.getCol());
				for (Integer pos : cell.getPossibilities()) {
					uniquePos.add(pos);
				}
			}
			
			//Make sure there are only 3 unique possibilities in the 3 cells found.
			if (uniquePos.size() == 3) {
//				System.out.println("2. Triples FOUND - COL [" + quadInQuestion
//						+ "] RIGHT NUMBER OF UNIQUE NUMBERS");

				// For each cell in the column, and if the cell in the column is
				// not in the nakedTriples list, then remove the unique possibilities 
				//found above from that cell.
				for (Cell cell : cellsInQuad) {
					if (!nakedTriples.contains(cell)) {
						for (Integer pos : uniquePos) {
//							System.out.println("2. COL [" + quadInQuestion
//									+ "] - Removing " + pos + " from "
//									+ cell.getCoordinates());
							if (cell.isEmpty()) {
								cell.removePossibility(pos);
							}
						}

					}
				}
				
				//If all 3 cells are within the same quadrant, remove the 3 unique possibilities from the quadrant 
				//as well.
				if (rowList.size()==1) {
					int row = rowList.iterator().next();
					ArrayList<Cell> cellsInRow = board.getCellsInRow(row);
					for (Cell cell : cellsInRow) {
						if (!nakedTriples.contains(cell)) {
							for (Integer pos : uniquePos) {
//								System.out.println("2. ROW ["+row+"]- Removing " + pos + " from " + cell.getCoordinates());
								if (cell.isEmpty()) {
									cell.removePossibility(pos);
									
								}
							}
						}
						
					}
				}
				
				if (colList.size()==1) {
					int col = colList.iterator().next();
					ArrayList<Cell> cellsInCol = board.getCellsInCol(col);
					for (Cell cell : cellsInCol) {
						if (!nakedTriples.contains(cell)) {
							for (Integer pos : uniquePos) {
//								System.out.println("4. COL ["+col+"]- Removing " + pos + " from " + cell.getCoordinates());
								if (cell.isEmpty()) {
									cell.removePossibility(pos);
									
								}
							}
						}
						
					}
				}
			}
		}
		
		uniquePos.clear();
		nakedTriples.clear();
		rowList.clear();
		colList.clear();
		
		//3. Gather all cells in the quadInQuestion with 2 or 3 possibilities and search for pattern:
		//	A,B | A,B | A,B,C
//		System.out.println("3. COL - ["+quadInQuestion+"]");
		cellsTheSame=0;
		for (Cell cell : cellsWith2Pos) {			
			for (Cell cell2 : cellsInQuad) {
				if (!cell.equals(cell2) && cell2.getPossibilities().equals(cell.getPossibilities())) {
					cellsTheSame++;
				}
			}
			if (cellsTheSame==1) {
				int cellCount=0;
				for (Cell cell3 : cellsWith3Pos) {
					if (cell3.getPossibilities().containsAll(cell.getPossibilities())) {
						cellCount++;
					}
				}
				if (cellCount==1) {
					ArrayList<Cell> cellsContaining = getCellsContainingPossibilityList(cellsWith3Pos, cell.getPossibilities());
//						System.out.println("3. COL - ["+quadInQuestion+"] FOUND CELL "+cell.getCoordinates());
						nakedTriples.add(cell);
						nakedTriples.addAll(cellsContaining);
				}
				cellCount=0;
			}
			cellsTheSame=0;
		}
		
		//Make sure there are only 3 cells found.
		if (nakedTriples.size() == 3) {
//			System.out.println("3. Triples FOUND - COL ["+quadInQuestion+"] RIGHT NUMBER OF CELLS");
	
			//For each cell save its quadrant and possibilities.
			for (Cell cell : nakedTriples) {
				for (Integer pos : cell.getPossibilities()) {
					rowList.add(cell.getRow());
					colList.add(cell.getCol());
					uniquePos.add(pos);
				}
			}
			
			//Make sure there are only 3 unique possibilities in the 3 cells found.
			if (uniquePos.size()==3) {
//				System.out.println("3. Triples FOUND - COL ["+quadInQuestion+"] RIGHT NUMBER OF UNIQUE NUMBERS");
			
			
				//For each cell in the column, and if the cell in the column is not in the nakedTriples list,
				//then remove the unique possibilities found above from that cell.
				for (Cell cell : cellsInQuad) {
					if (!nakedTriples.contains(cell)) {
						for (Integer pos : uniquePos) {
//							System.out.println("3. COL ["+quadInQuestion+"] - Removing " + pos + " from " + cell.getCoordinates());
							if (cell.isEmpty()) {
								cell.removePossibility(pos);
								
							}
						}
				
					}
				}		
			
				//If all 3 cells are within the same quadrant, remove the 3 unique possibilities from the quadrant 
				//as well.
				if (rowList.size()==1) {
					int row = rowList.iterator().next();
					ArrayList<Cell> cellsInRow = board.getCellsInRow(row);
					for (Cell cell : cellsInRow) {
						if (!nakedTriples.contains(cell)) {
							for (Integer pos : uniquePos) {
//								System.out.println("3. ROW ["+row+"]- Removing " + pos + " from " + cell.getCoordinates());
								if (cell.isEmpty()) {
									cell.removePossibility(pos);
									
								}
							}
						}
						
					}
				}
				
				if (colList.size()==1) {
					int col = colList.iterator().next();
					ArrayList<Cell> cellsInCol = board.getCellsInCol(col);
					for (Cell cell : cellsInCol) {
						if (!nakedTriples.contains(cell)) {
							for (Integer pos : uniquePos) {
//								System.out.println("4. COL ["+col+"]- Removing " + pos + " from " + cell.getCoordinates());
								if (cell.isEmpty()) {
									cell.removePossibility(pos);
									
								}
							}
						}
						
					}
				}
			}
		}
		
		uniquePos.clear();
		nakedTriples.clear();
		rowList.clear();
		colList.clear();
		
		//4. Gather all cells in the quadInQuestion with 2 possibilities and search for pattern:
		//	A,B | B,C | A,C
//		System.out.println("4. COL - ["+quadInQuestion+"]");
		if (cellsWith2Pos.size() < 3) {
			return;
		}
		
		cellsTheSame=0;
		for (Cell cell : cellsWith2Pos) {			
			for (Cell cell2 : cellsWith2Pos) {
				if (!cell.equals(cell2) && cell2.getPossibilities().contains(cell.getPossibilities().get(0))) {
					cellsTheSame++;
				}
			}
			if (cellsTheSame==1) {
				ArrayList<Cell> cellsContaining = getCellsContainingPossibility(cellsWith2Pos, cell.getPossibilities().get(0));	
				cellsTheSame=0;
				for (Cell cell3 : cellsContaining) {			
					if (!cell3.equals(cell) && (cell3.getPossibilities().contains(cell.getPossibilities().get(0)) || 
							cell3.getPossibilities().contains(cell.getPossibilities().get(1)) && (cell3.getPossibilities().contains(cellsContaining.get(1))
									|| cell3.getPossibilities().contains(cellsContaining.get(0))))) {
//						System.out.println("4. COL - ["+quadInQuestion+"] FOUND CELL "+cell.getCoordinates()+" PLUS " + cellsContaining.size());
						nakedTriples.add(cell);
						//nakedTriples.addAll(cellsContaining);
					}
				}
			}
		}

		
		//Make sure there are only 3 cells found.
		if (nakedTriples.size() == 3) {
//			System.out.println("4. Triples FOUND - COL ["+quadInQuestion+"] RIGHT NUMBER OF CELLS");
	
			//For each cell save its quadrant and possibilities.
			for (Cell cell5 : nakedTriples) {
				for (Integer pos : cell5.getPossibilities()) {
					rowList.add(cell5.getRow());
					colList.add(cell5.getCol());
					uniquePos.add(pos);
				}
			}
			
			//Make sure there are only 3 unique possibilities in the 3 cells found.
			if (uniquePos.size()==3) {
//				System.out.println("4. Triples FOUND - COL ["+quadInQuestion+"] RIGHT NUMBER OF UNIQUE NUMBERS");
			
			
				//For each cell in the column, and if the cell in the column is not in the nakedTriples list,
				//then remove the unique possibilities found above from that cell.
				for (Cell cell6 : cellsInQuad) {
					if (!nakedTriples.contains(cell6)) {
						for (Integer pos : uniquePos) {
//							System.out.println("4. COL ["+quadInQuestion+"] - Removing " + pos + " from " + cell6.getCoordinates());
							if (cell6.isEmpty()) {
								cell6.removePossibility(pos);
								
							}
						}
				
					}
				}
				
				//If all 3 cells are within the same quadrant, remove the 3 unique possibilities from the quadrant 
				//as well.
				if (rowList.size()==1) {
					int row = rowList.iterator().next();
					ArrayList<Cell> cellsInRow = board.getCellsInRow(row);
					for (Cell cell : cellsInRow) {
						if (!nakedTriples.contains(cell)) {
							for (Integer pos : uniquePos) {
//								System.out.println("4. ROW ["+row+"]- Removing " + pos + " from " + cell.getCoordinates());
								if (cell.isEmpty()) {
									cell.removePossibility(pos);
									
								}
							}
						}
						
					}
				}
				
				if (colList.size()==1) {
					int col = colList.iterator().next();
					ArrayList<Cell> cellsInCol = board.getCellsInCol(col);
					for (Cell cell : cellsInCol) {
						if (!nakedTriples.contains(cell)) {
							for (Integer pos : uniquePos) {
//								System.out.println("4. COL ["+col+"]- Removing " + pos + " from " + cell.getCoordinates());
								if (cell.isEmpty()) {
									cell.removePossibility(pos);
									
								}
							}
						}
						
					}
				}
			}
		}
	}
	
	
	/**
	 * Identify any 3 cells in a column that have the same 3 identical
	 * possibilities left.  Remove those possibilities from the cells 
	 * in the column excluding the 3 cells identified. If the 3 cells 
	 * identified all fall inside the same quadrant.  
	 * Remove the possibilities from the cells in the quadrant, 
	 * excluding the 3 cells identified. 
	 * @param cells
	 * @param colInQuestion
	 */
	protected void nakedTripleInCol(int colInQuestion) {	
		ArrayList<Cell> nakedTriples = new ArrayList<Cell>();
		ArrayList<Cell> cellsInCol = board.getCellsInCol(colInQuestion);
		Set<Integer> uniquePos = new HashSet<Integer>();
		Set<Integer> quadList = new HashSet<Integer>();
		
		//1. Gather all cells in the colInQuestion with 3 possibilities and search for pattern:
		//	A,B,C | A,B,C | A,B,C
//		System.out.println("1. COL - ["+colInQuestion+"]");
		int cellsTheSame=0;
		ArrayList<Cell> cellsWith3Pos = board.getCellsInColWithThreePossibilities(colInQuestion);
		for (Cell cell : cellsWith3Pos) {
			ArrayList<Integer> pos3Set = cell.getPossibilities();
			for (Cell cell2 : cellsWith3Pos) {
				if (cell2.getPossibilities().equals(pos3Set)) {
					cellsTheSame++;
				} 
			}
			if (cellsTheSame==3 && !nakedTriples.contains(cell)) {
//				System.out.println("1. COL - ["+colInQuestion+"] FOUND CELL "+cell.getCoordinates());
				nakedTriples.add(cell);
			}
			cellsTheSame=0;
		}
		
		//Make sure there are only 3 cells found.
		if (nakedTriples.size() == 3) {
//			System.out.println("1. Triples FOUND - COL ["+colInQuestion+"] RIGHT NUMBER OF CELLS");
			//For each cell save its quadrant and possibilities.
			for (Cell cell : nakedTriples) {
				quadList.add(cell.getQuad());
				for (Integer pos : cell.getPossibilities()) {
					uniquePos.add(pos);
				}
			}
			
			//Make sure there are only 3 unique possibilities in the 3 cells found.
			if (uniquePos.size()==3) {
//				System.out.println("1. Triples FOUND - COL ["+colInQuestion+"] RIGHT NUMBER OF UNIQUE NUMBERS");
				//For each cell in the column, and if the cell in the column is not in the nakedTriples list,
				//then remove the unique possibilities found above from that cell.
				for (Cell cell : cellsInCol) {
					if (!nakedTriples.contains(cell)) {
						for (Integer pos : uniquePos) {
//							System.out.println("1. COL ["+colInQuestion+"] - Removing " + pos + " from " + cell.getCoordinates());
							if (cell.isEmpty()) {
								cell.removePossibility(pos);
								
							}
						}
					
					}
				}
				
				//If all 3 cells are within the same quadrant, remove the 3 unique possibilities from the quadrant 
				//as well.
				if (quadList.size()==1) {
					int quad = quadList.iterator().next();
					ArrayList<Cell> cellsInQuad = board.getCellsInQuad(quad);
					for (Cell cell : cellsInQuad) {
						if (!nakedTriples.contains(cell)) {
							for (Integer pos : uniquePos) {
//								System.out.println("1. QUAD ["+quad+"]- Removing " + pos + " from " + cell.getCoordinates());
								if (cell.isEmpty()) {
									cell.removePossibility(pos);
									
								}
							}
						}
						
					}
				}
			}	
		}
		
		uniquePos.clear();
		nakedTriples.clear();
		quadList.clear();
			
		//2. Gather all cells in the rowInQuestion with 2 or 3 possibilities and search for pattern:
		//	A,B | A,B,C | A,B,C
//		System.out.println("2. COL - ["+colInQuestion+"]");
		cellsTheSame=0;
		ArrayList<Cell> cellsWith2Pos = board.getCellsInColWithTwoPossibilities(colInQuestion);
		for (Cell cell : cellsWith2Pos) {
			for (Cell cell2 : cellsWith3Pos) {
				if (cell2.getPossibilities().containsAll(cell.getPossibilities())) {
					cellsTheSame++;
				}
			}
			if (cellsTheSame==2) {
				ArrayList<Cell> cellsContaining = getCellsContainingPossibilityList(cellsWith3Pos, cell.getPossibilities());
				if (cellsContaining.get(0).getPossibilities().equals(cellsContaining.get(1).getPossibilities())) {
//					System.out.println("2. COL - ["+colInQuestion+"] FOUND CELL "+cell.getCoordinates()+"PLUS "+cellsContaining.size());
					nakedTriples.add(cell);
					nakedTriples.addAll(cellsContaining);
				}
			}
			cellsTheSame=0;
		}
		
		//Make sure there are only 3 cells found.
		if (nakedTriples.size() == 3) {
//			System.out.println("2. Triples FOUND - COL ["+colInQuestion+"] RIGHT NUMBER OF CELLS");
	
			//For each cell save its quadrant and possibilities.
			for (Cell cell : nakedTriples) {
				quadList.add(cell.getQuad());
				for (Integer pos : cell.getPossibilities()) {
					uniquePos.add(pos);
				}
			}
			
			//Make sure there are only 3 unique possibilities in the 3 cells found.
			if (uniquePos.size() == 3) {
//				System.out.println("2. Triples FOUND - COL [" + colInQuestion
//						+ "] RIGHT NUMBER OF UNIQUE NUMBERS");

				// For each cell in the column, and if the cell in the column is
				// not in the nakedTriples list, then remove the unique possibilities 
				//found above from that cell.
				for (Cell cell : cellsInCol) {
					if (!nakedTriples.contains(cell)) {
						for (Integer pos : uniquePos) {
//							System.out.println("2. COL [" + colInQuestion
//									+ "] - Removing " + pos + " from "
//									+ cell.getCoordinates());
							if (cell.isEmpty()) {
								cell.removePossibility(pos);
							}
						}

					}
				}
				
				//If all 3 cells are within the same quadrant, remove the 3 unique possibilities from the quadrant 
				//as well.
				if (quadList.size()==1) {
					int quad = quadList.iterator().next();
					ArrayList<Cell> cellsInQuad = board.getCellsInQuad(quad);
					for (Cell cell : cellsInQuad) {
						if (!nakedTriples.contains(cell)) {
							for (Integer pos : uniquePos) {
//								System.out.println("2. QUAD ["+quad+"]- Removing " + pos + " from " + cell.getCoordinates());
								if (cell.isEmpty()) {
									cell.removePossibility(pos);
									
								}
							}
						}
						
					}
				}
			}
		}
		
		uniquePos.clear();
		nakedTriples.clear();
		quadList.clear();
		
		//3. Gather all cells in the colInQuestion with 2 or 3 possibilities and search for pattern:
		//	A,B | A,B | A,B,C
//		System.out.println("3. COL - ["+colInQuestion+"]");
		cellsTheSame=0;
		for (Cell cell : cellsWith2Pos) {			
			for (Cell cell2 : cellsInCol) {
				if (!cell.equals(cell2) && cell2.getPossibilities().equals(cell.getPossibilities())) {
					cellsTheSame++;
				}
			}
			if (cellsTheSame==1) {
				int cellCount=0;
				for (Cell cell3 : cellsWith3Pos) {
					if (cell3.getPossibilities().containsAll(cell.getPossibilities())) {
						cellCount++;
					}
				}
				if (cellCount==1) {
					ArrayList<Cell> cellsContaining = getCellsContainingPossibilityList(cellsWith3Pos, cell.getPossibilities());
//						System.out.println("3. COL - ["+colInQuestion+"] FOUND CELL "+cell.getCoordinates());
						nakedTriples.add(cell);
						nakedTriples.addAll(cellsContaining);
				}
				cellCount=0;
			}
			cellsTheSame=0;
		}
		
		//Make sure there are only 3 cells found.
		if (nakedTriples.size() == 3) {
//			System.out.println("3. Triples FOUND - COL ["+colInQuestion+"] RIGHT NUMBER OF CELLS");
	
			//For each cell save its quadrant and possibilities.
			for (Cell cell : nakedTriples) {
				for (Integer pos : cell.getPossibilities()) {
					quadList.add(cell.getQuad());
					uniquePos.add(pos);
				}
			}
			
			//Make sure there are only 3 unique possibilities in the 3 cells found.
			if (uniquePos.size()==3) {
//				System.out.println("3. Triples FOUND - COL ["+colInQuestion+"] RIGHT NUMBER OF UNIQUE NUMBERS");
			
			
				//For each cell in the column, and if the cell in the column is not in the nakedTriples list,
				//then remove the unique possibilities found above from that cell.
				for (Cell cell : cellsInCol) {
					if (!nakedTriples.contains(cell)) {
						for (Integer pos : uniquePos) {
//							System.out.println("3. COL ["+colInQuestion+"] - Removing " + pos + " from " + cell.getCoordinates());
							if (cell.isEmpty()) {
								cell.removePossibility(pos);
								
							}
						}
				
					}
				}		
			
				//If all 3 cells are within the same quadrant, remove the 3 unique possibilities from the quadrant 
				//as well.
				if (quadList.size()==1) {
					int quad = quadList.iterator().next();
					ArrayList<Cell> cellsInQuad = board.getCellsInQuad(quad);
					for (Cell cell : cellsInQuad) {
						if (!nakedTriples.contains(cell)) {
							for (Integer pos : uniquePos) {
//								System.out.println("3. QUAD ["+quad+"]- Removing " + pos + " from " + cell.getCoordinates());
								if (cell.isEmpty()) {
									cell.removePossibility(pos);
									
								}
							}
						}
						
					}
				}
			}
		}
		
		uniquePos.clear();
		nakedTriples.clear();
		quadList.clear();
		
		//4. Gather all cells in the colInQuestion with 2 possibilities and search for pattern:
		//	A,B | B,C | A,C
//		System.out.println("4. COL - ["+colInQuestion+"]");
		if (cellsWith2Pos.size() < 3) {
			return;
		}
		
		cellsTheSame=0;
		for (Cell cell : cellsWith2Pos) {			
			for (Cell cell2 : cellsWith2Pos) {
				if (!cell.equals(cell2) && cell2.getPossibilities().contains(cell.getPossibilities().get(0))) {
					cellsTheSame++;
				}
			}
			if (cellsTheSame==1) {
				ArrayList<Cell> cellsContaining = getCellsContainingPossibility(cellsWith2Pos, cell.getPossibilities().get(0));	
				cellsTheSame=0;
				for (Cell cell3 : cellsContaining) {			
					if (!cell3.equals(cell) && (cell3.getPossibilities().contains(cell.getPossibilities().get(0)) || 
							cell3.getPossibilities().contains(cell.getPossibilities().get(1)) && (cell3.getPossibilities().contains(cellsContaining.get(1))
									|| cell3.getPossibilities().contains(cellsContaining.get(0))))) {
//						System.out.println("4. COL - ["+colInQuestion+"] FOUND CELL "+cell.getCoordinates()+" PLUS " + cellsContaining.size());
						nakedTriples.add(cell);
						//nakedTriples.addAll(cellsContaining);
					}
				}
			}
		}

		
		//Make sure there are only 3 cells found.
		if (nakedTriples.size() == 3) {
//			System.out.println("4. Triples FOUND - COL ["+colInQuestion+"] RIGHT NUMBER OF CELLS");
	
			//For each cell save its quadrant and possibilities.
			for (Cell cell5 : nakedTriples) {
				for (Integer pos : cell5.getPossibilities()) {
					quadList.add(cell5.getQuad());
					uniquePos.add(pos);
				}
			}
			
			//Make sure there are only 3 unique possibilities in the 3 cells found.
			if (uniquePos.size()==3) {
//				System.out.println("4. Triples FOUND - COL ["+colInQuestion+"] RIGHT NUMBER OF UNIQUE NUMBERS");
			
			
				//For each cell in the column, and if the cell in the column is not in the nakedTriples list,
				//then remove the unique possibilities found above from that cell.
				for (Cell cell6 : cellsInCol) {
					if (!nakedTriples.contains(cell6)) {
						for (Integer pos : uniquePos) {
//							System.out.println("4. COL ["+colInQuestion+"] - Removing " + pos + " from " + cell6.getCoordinates());
							if (cell6.isEmpty()) {
								cell6.removePossibility(pos);
								
							}
						}
				
					}
				}
				
				//If all 3 cells are within the same quadrant, remove the 3 unique possibilities from the quadrant 
				//as well.
				if (quadList.size()==1) {
					int quad = quadList.iterator().next();
					ArrayList<Cell> cellsInQuad = board.getCellsInQuad(quad);
					for (Cell cell : cellsInQuad) {
						if (!nakedTriples.contains(cell)) {
							for (Integer pos : uniquePos) {
//								System.out.println("4. QUAD ["+quad+"]- Removing " + pos + " from " + cell.getCoordinates());
								if (cell.isEmpty()) {
									cell.removePossibility(pos);
									
								}
							}
						}
						
					}
				}
			}
		}
	}
	
	
	/**
	 * Identify any 3 cells in a row that have the same 3 identical
	 * possibilities left.  Remove those possibilities from the cells 
	 * in the row excluding the 3 cells identified. If the 3 cells 
	 * identified all fall inside the same quadrant.  
	 * Remove the possibilities from the cells in the quadrant, 
	 * excluding the 3 cells identified. 
	 * @param cells
	 * @param rowInQuestion
	 */
	protected void nakedTripleInRow(int rowInQuestion) {		
		ArrayList<Cell> nakedTriples = new ArrayList<Cell>();
		ArrayList<Cell> cellsInRow = board.getCellsInRow(rowInQuestion);
		Set<Integer> uniquePos = new HashSet<Integer>();
		Set<Integer> quadList = new HashSet<Integer>();
		
		//1. Gather all cells in the rowInQuestion with 3 possibilities and search for pattern:
		//	A,B,C | A,B,C | A,B,C
//		System.out.println("1. ROW - ["+rowInQuestion+"]");
		int cellsTheSame=0;
		ArrayList<Cell> cellsWith3Pos = board.getCellsInRowWithThreePossibilities(rowInQuestion);
		for (Cell cell : cellsWith3Pos) {
			ArrayList<Integer> pos3Set = cell.getPossibilities();
			for (Cell cell2 : cellsWith3Pos) {
				if (cell2.getPossibilities().equals(pos3Set)) {
					cellsTheSame++;
				} 
			}
			if (cellsTheSame==3 && !nakedTriples.contains(cell)) {
//				System.out.println("1. ROW - ["+rowInQuestion+"] FOUND CELL "+cell.getCoordinates());
				nakedTriples.add(cell);
			}
			cellsTheSame=0;
		}
		
		//Make sure there are only 3 cells found.
		if (nakedTriples.size() == 3) {
//			System.out.println("1. Triples FOUND - ROW ["+rowInQuestion+"] RIGHT NUMBER OF CELLS");
			//For each cell save its quadrant and possibilities.
			for (Cell cell : nakedTriples) {
				quadList.add(cell.getQuad());
				for (Integer pos : cell.getPossibilities()) {
					uniquePos.add(pos);
				}
			}
			
			//Make sure there are only 3 unique possibilities in the 3 cells found.
			if (uniquePos.size()==3) {
//				System.out.println("1. Triples FOUND - ROW ["+rowInQuestion+"] RIGHT NUMBER OF UNIQUE NUMBERS");
				//For each cell in the column, and if the cell in the column is not in the nakedTriples list,
				//then remove the unique possibilities found above from that cell.
				for (Cell cell : cellsInRow) {
					if (!nakedTriples.contains(cell)) {
						for (Integer pos : uniquePos) {
//							System.out.println("1. ROW ["+rowInQuestion+"] - Removing " + pos + " from " + cell.getCoordinates());
							if (cell.isEmpty()) {
								cell.removePossibility(pos);
								
							}
						}
					
					}
				}
				
				//If all 3 cells are within the same quadrant, remove the 3 unique possibilities from the quadrant 
				//as well.
				if (quadList.size()==1) {
					int quad = quadList.iterator().next();
					ArrayList<Cell> cellsInQuad = board.getCellsInQuad(quad);
					for (Cell cell : cellsInQuad) {
						if (!nakedTriples.contains(cell)) {
							for (Integer pos : uniquePos) {
//								System.out.println("1. QUAD ["+quad+"]- Removing " + pos + " from " + cell.getCoordinates());
								if (cell.isEmpty()) {
									cell.removePossibility(pos);
									
								}
							}
						}
						
					}
				}
			}	
		}
		
		uniquePos.clear();
		nakedTriples.clear();
		quadList.clear();
			
		//2. Gather all cells in the rowInQuestion with 2 or 3 possibilities and search for pattern:
		//	A,B | A,B,C | A,B,C
//		System.out.println("2. ROW - ["+rowInQuestion+"]");
		cellsTheSame=0;
		ArrayList<Cell> cellsWith2Pos = board.getCellsInRowWithTwoPossibilities(rowInQuestion);
		for (Cell cell : cellsWith2Pos) {
			for (Cell cell2 : cellsWith3Pos) {
				if (cell2.getPossibilities().containsAll(cell.getPossibilities())) {
					cellsTheSame++;
				}
			}
			if (cellsTheSame==2) {
				ArrayList<Cell> cellsContaining = getCellsContainingPossibilityList(cellsWith3Pos, cell.getPossibilities());
				if (cellsContaining.get(0).getPossibilities().equals(cellsContaining.get(1).getPossibilities())) {
//					System.out.println("2. ROW - ["+rowInQuestion+"] FOUND CELL "+cell.getCoordinates()+"PLUS "+cellsContaining.size());
					nakedTriples.add(cell);
					nakedTriples.addAll(cellsContaining);
				}
			}
			cellsTheSame=0;
		}
		
		//Make sure there are only 3 cells found.
		if (nakedTriples.size() == 3) {
//			System.out.println("2. Triples FOUND - ROW ["+rowInQuestion+"] RIGHT NUMBER OF CELLS");
	
			//For each cell save its quadrant and possibilities.
			for (Cell cell : nakedTriples) {
				quadList.add(cell.getQuad());
				for (Integer pos : cell.getPossibilities()) {
					uniquePos.add(pos);
				}
			}
			
			//Make sure there are only 3 unique possibilities in the 3 cells found.
			if (uniquePos.size() == 3) {
//				System.out.println("2. Triples FOUND - ROW [" + rowInQuestion
//						+ "] RIGHT NUMBER OF UNIQUE NUMBERS");

				// For each cell in the row, and if the cell in the row is
				// not in the nakedTriples list, then remove the unique possibilities 
				//found above from that cell.
				for (Cell cell : cellsInRow) {
					if (!nakedTriples.contains(cell)) {
						for (Integer pos : uniquePos) {
//							System.out.println("2. ROW [" + rowInQuestion
//									+ "] - Removing " + pos + " from "
//									+ cell.getCoordinates());
							if (cell.isEmpty()) {
								cell.removePossibility(pos);
							}
						}

					}
				}
				
				//If all 3 cells are within the same quadrant, remove the 3 unique possibilities from the quadrant 
				//as well.
				if (quadList.size()==1) {
					int quad = quadList.iterator().next();
					ArrayList<Cell> cellsInQuad = board.getCellsInQuad(quad);
					for (Cell cell : cellsInQuad) {
						if (!nakedTriples.contains(cell)) {
							for (Integer pos : uniquePos) {
//								System.out.println("2. QUAD ["+quad+"]- Removing " + pos + " from " + cell.getCoordinates());
								if (cell.isEmpty()) {
									cell.removePossibility(pos);
									
								}
							}
						}
						
					}
				}
			}
		}
		
		uniquePos.clear();
		nakedTriples.clear();
		quadList.clear();
		
		//3. Gather all cells in the rowInQuestion with 2 or 3 possibilities and search for pattern:
		//	A,B | A,B | A,B,C
//		System.out.println("3. ROW - ["+rowInQuestion+"]");
		cellsTheSame=0;
		for (Cell cell : cellsWith2Pos) {			
			for (Cell cell2 : cellsInRow) {
				if (!cell.equals(cell2) && cell2.getPossibilities().equals(cell.getPossibilities())) {
					cellsTheSame++;
				}
			}
			if (cellsTheSame==1) {
				int cellCount=0;
				for (Cell cell3 : cellsWith3Pos) {
					if (cell3.getPossibilities().containsAll(cell.getPossibilities())) {
						cellCount++;
					}
				}
				if (cellCount==1) {
					ArrayList<Cell> cellsContaining = getCellsContainingPossibilityList(cellsWith3Pos, cell.getPossibilities());
//						System.out.println("3. ROW - ["+rowInQuestion+"] FOUND CELL "+cell.getCoordinates());
						nakedTriples.add(cell);
						nakedTriples.addAll(cellsContaining);
				}
				cellCount=0;
			}
			cellsTheSame=0;
		}
		
		//Make sure there are only 3 cells found.
		if (nakedTriples.size() == 3) {
//			System.out.println("3. Triples FOUND - ROW ["+rowInQuestion+"] RIGHT NUMBER OF CELLS");
	
			//For each cell save its quadrant and possibilities.
			for (Cell cell : nakedTriples) {
				for (Integer pos : cell.getPossibilities()) {
					quadList.add(cell.getQuad());
					uniquePos.add(pos);
				}
			}
			
			//Make sure there are only 3 unique possibilities in the 3 cells found.
			if (uniquePos.size()==3) {
//				System.out.println("3. Triples FOUND - ROW ["+rowInQuestion+"] RIGHT NUMBER OF UNIQUE NUMBERS");
			
			
				//For each cell in the row, and if the cell in the row is not in the nakedTriples list,
				//then remove the unique possibilities found above from that cell.
				for (Cell cell : cellsInRow) {
					if (!nakedTriples.contains(cell)) {
						for (Integer pos : uniquePos) {
//							System.out.println("3. ROW ["+rowInQuestion+"] - Removing " + pos + " from " + cell.getCoordinates());
							if (cell.isEmpty()) {
								cell.removePossibility(pos);
								
							}
						}
				
					}
				}		
			
				//If all 3 cells are within the same quadrant, remove the 3 unique possibilities from the quadrant 
				//as well.
				if (quadList.size()==1) {
					int quad = quadList.iterator().next();
					ArrayList<Cell> cellsInQuad = board.getCellsInQuad(quad);
					for (Cell cell : cellsInQuad) {
						if (!nakedTriples.contains(cell)) {
							for (Integer pos : uniquePos) {
//								System.out.println("3. QUAD ["+quad+"]- Removing " + pos + " from " + cell.getCoordinates());
								if (cell.isEmpty()) {
									cell.removePossibility(pos);
									
								}
							}
						}
						
					}
				}
			}
		}
		
		uniquePos.clear();
		nakedTriples.clear();
		quadList.clear();
		
		//4. Gather all cells in the rowInQuestion with 2 possibilities and search for pattern:
		//	A,B | B,C | A,C
//		System.out.println("4. ROW - ["+rowInQuestion+"]");
		if (cellsWith2Pos.size() < 3) {
			return;
		}
		
		cellsTheSame=0;
		for (Cell cell : cellsWith2Pos) {			
			for (Cell cell2 : cellsWith2Pos) {
				if (!cell.equals(cell2) && cell2.getPossibilities().contains(cell.getPossibilities().get(0))) {
					cellsTheSame++;
				}
			}
			if (cellsTheSame==1) {
				ArrayList<Cell> cellsContaining = getCellsContainingPossibility(cellsWith2Pos, cell.getPossibilities().get(0));	
				cellsTheSame=0;
				for (Cell cell3 : cellsContaining) {			
					if (!cell3.equals(cell) && (cell3.getPossibilities().contains(cell.getPossibilities().get(0)) || 
							cell3.getPossibilities().contains(cell.getPossibilities().get(1)) && (cell3.getPossibilities().contains(cellsContaining.get(1))
									|| cell3.getPossibilities().contains(cellsContaining.get(0))))) {
//						System.out.println("4. ROW - ["+rowInQuestion+"] FOUND CELL "+cell.getCoordinates()+" PLUS " + cellsContaining.size());
						nakedTriples.add(cell);
						//nakedTriples.addAll(cellsContaining);
					}
				}
			}
		}

		
		//Make sure there are only 3 cells found.
		if (nakedTriples.size() == 3) {
//			System.out.println("4. Triples FOUND - ROW ["+rowInQuestion+"] RIGHT NUMBER OF CELLS");
	
			//For each cell save its quadrant and possibilities.
			for (Cell cell5 : nakedTriples) {
				for (Integer pos : cell5.getPossibilities()) {
					quadList.add(cell5.getQuad());
					uniquePos.add(pos);
				}
			}
			
			//Make sure there are only 3 unique possibilities in the 3 cells found.
			if (uniquePos.size()==3) {
//				System.out.println("4. Triples FOUND - ROW ["+rowInQuestion+"] RIGHT NUMBER OF UNIQUE NUMBERS");
			
			
				//For each cell in the column, and if the cell in the column is not in the nakedTriples list,
				//then remove the unique possibilities found above from that cell.
				for (Cell cell6 : cellsInRow) {
					if (!nakedTriples.contains(cell6)) {
						for (Integer pos : uniquePos) {
//							System.out.println("4. ROW ["+rowInQuestion+"] - Removing " + pos + " from " + cell6.getCoordinates());
							if (cell6.isEmpty()) {
								cell6.removePossibility(pos);
								
							}
						}
				
					}
				}
				
				//If all 3 cells are within the same quadrant, remove the 3 unique possibilities from the quadrant 
				//as well.
				if (quadList.size()==1) {
					int quad = quadList.iterator().next();
					ArrayList<Cell> cellsInQuad = board.getCellsInQuad(quad);
					for (Cell cell : cellsInQuad) {
						if (!nakedTriples.contains(cell)) {
							for (Integer pos : uniquePos) {
//								System.out.println("4. QUAD ["+quad+"]- Removing " + pos + " from " + cell.getCoordinates());
								if (cell.isEmpty()) {
									cell.removePossibility(pos);
									
								}
							}
						}
						
					}
				}
			}
		}
	}
	
	/** Identify any unique possibilities that occur only 2 times in a quadrant,
	 *  If the 2 unique possibilities occur in the same cell, then remove any other
	 *  possibility from those 2 cells.
	 * @param cells
	 * @param quadInQuestion
	 */
	@SuppressWarnings("unused")
	private void hiddenPairsInQuad(int quadInQuestion) {
		ArrayList<Cell> cellsInQuad = board.getCellsInQuad(quadInQuestion);
		ArrayList<Integer> possibilitiesInQuad = board.getPossibilitiesForQuad(quadInQuestion);
		Set<Integer> possibilitiesThatOccurTwice = new HashSet<Integer>();
		Set<Cell> map = new HashSet<Cell>();
		ArrayList<Integer> hiddenPair = new ArrayList<Integer>();
		Set<Integer> posToRemove = new HashSet<Integer>();
		
		
		for (Integer possibility : possibilitiesInQuad) {
			int occurrances = getNumOfOccurancesOfPossibilityInCellList(cellsInQuad, possibility);
		    if (occurrances == 2) {
		    	possibilitiesThatOccurTwice.add(possibility);
		    }
		} 
			
		for (Integer pos1 : possibilitiesThatOccurTwice) {
			for (Integer pos2 : possibilitiesThatOccurTwice) {
				if (pos1 != pos2) {
					for (Cell cell : cellsInQuad) {		
						if (cell.getPossibilities().contains(pos1) && 
								cell.getPossibilities().contains(pos2)) {
//								System.out.println(pos1 + " " + pos2 + " occurs in cell " + cell.getCellCoordinates());
								map.add(cell);
						}
					}
				}
			}
		}
		
		hiddenPair.addAll(possibilitiesThatOccurTwice);
		for (Cell cell : map) {
//			System.out.println("Cell " + cell.getCellCoordinates() + " has a hidden pair");
			for (Integer in : possibilitiesThatOccurTwice) {
				if (!cell.getPossibilities().contains(in)) {
//					System.out.println("Removing "+in);
					hiddenPair.remove(in);
				}
			}
		
		}
		
		for (Cell cell : cellsInQuad) {
			for (Integer in : hiddenPair) {
				if (!map.contains(cell) && cell.getPossibilities().contains(in)) {
					return;
				}
			}
		}
	
		if (hiddenPair.size()==2) {
//			System.out.println(hiddenPair.size());
//			System.out.println(hiddenPair.get(0) + " and " + hiddenPair.get(1) + " is one of the hidden pair candidates");
			for (Cell cell : map) {
				for (Integer possible : cell.getPossibilities()) {
					if (possible != hiddenPair.get(0) && possible != hiddenPair.get(1) ) {
						posToRemove.add(possible);
					}
				}
			}
			
			for (Cell cell : map) {
				for (Integer in : posToRemove) {
//					System.out.println("Removing " + in + " from " + cell.getCellCoordinates());
					cell.removePossibility(in);
					
				}
			}
		}
	}

	/** Identify any unique possibilities that occur only 2 times in a column,
	 *  If the 2 unique possibilities occur in the same cell, then remove any other
	 *  possibility from those 2 cells.
	 * @param cells
	 * @param colInQuestion
	 */
	@SuppressWarnings("unused")
	private void hiddenPairsInCol(int colInQuestion) {
		ArrayList<Cell> cellsInCol = board.getCellsInCol(colInQuestion);
		Set<Integer> possibilitiesThatOccurTwice = new HashSet<Integer>();
		Set<Cell> map = new HashSet<Cell>();
		ArrayList<Integer> hiddenPair = new ArrayList<Integer>();
		Set<Integer> posToRemove = new HashSet<Integer>();
		
		
		for (Cell cell : cellsInCol) {
			for (Integer possibility: cell.getPossibilities()) {
				int count = getNumOfOccurancesOfPossibilityInCellList(cellsInCol, possibility);
			    if (count == 2) {
			    	possibilitiesThatOccurTwice.add(possibility);
			    }
			}
		} 
			
		for (Integer pos1 : possibilitiesThatOccurTwice) {
			for (Integer pos2 : possibilitiesThatOccurTwice) {
			for (Cell cell : cellsInCol) {
					if (pos1 != pos2) {
						if (cell.getPossibilities().contains(pos1) && 
								cell.getPossibilities().contains(pos2)) {
//								System.out.println(pos1 + " " + pos2 + " occurs in cell " + cell.getCellCoordinates());
								map.add(cell);
						}
					}
				}
			}
		}
		
		hiddenPair.addAll(possibilitiesThatOccurTwice);
		for (Cell cell : map) {
//			System.out.println("Cell " + cell.getCellCoordinates() + " has a hidden pair");
			for (Integer in : possibilitiesThatOccurTwice) {
				if (!cell.getPossibilities().contains(in)) {
//					System.out.println("Removing "+in);
					hiddenPair.remove(in);
				}
			}
		
		}
		
		for (Cell cell : cellsInCol) {
			for (Integer in : hiddenPair) {
				if (!map.contains(cell) && cell.getPossibilities().contains(in)) {
					return;
				}
			}
		}
	
		if (hiddenPair.size()==2) {
//			System.out.println(hiddenPair.size());
//			System.out.println(hiddenPair.get(0) + " and " + hiddenPair.get(1) + " is one of the hidden pair candidates");
			for (Cell cell : map) {
				for (Integer possible : cell.getPossibilities()) {
					if (possible != hiddenPair.get(0) && possible != hiddenPair.get(1) ) {
						posToRemove.add(possible);
					}
				}
			}
			
			for (Cell cell : map) {
				for (Integer in : posToRemove) {
//					System.out.println("Removing " + in + " from " + cell.getCellCoordinates());
					cell.removePossibility(in);
					
				}
			}
		}
	}
	
	/** Identify any unique possibilities that occur only 2 times in a row,
	 *  If the 2 unique possibilities occur in the same cell, then remove any other
	 *  possibility from those 2 cells.
	 * @param cells
	 * @param colInQuestion
	 */
	@SuppressWarnings("unused")
	private void hiddenPairsInRow(int rowInQuestion) {
		ArrayList<Cell> cellsInRow = board.getCellsInRow(rowInQuestion);
		ArrayList<Integer> rowPossibilities = board.getPossibilitiesForRow(rowInQuestion);
		Set<Integer> possibilitiesThatOccurTwice = new HashSet<Integer>();
		Set<Cell> map = new HashSet<Cell>();
		ArrayList<Integer> hiddenPair = new ArrayList<Integer>();
		Set<Integer> posToRemove = new HashSet<Integer>();
		
		
		for (Integer possibility : rowPossibilities) {
			int occurrances = getNumOfOccurancesOfPossibilityInCellList(cellsInRow, possibility);
		    if (occurrances == 2) {
		    	possibilitiesThatOccurTwice.add(possibility);
		    }
		} 
			
		for (Integer pos1 : possibilitiesThatOccurTwice) {
			for (Integer pos2 : possibilitiesThatOccurTwice) {
				if (pos1 != pos2) {
					for (Cell cell : cellsInRow) {		
						if (cell.getPossibilities().contains(pos1) && 
								cell.getPossibilities().contains(pos2)) {
//								System.out.println(pos1 + " " + pos2 + " occurs in cell " + cell.getCellCoordinates());
								map.add(cell);
						}
					}
				}
			}
		}
		
		hiddenPair.addAll(possibilitiesThatOccurTwice);
		for (Cell cell : map) {
//			System.out.println("Cell " + cell.getCellCoordinates() + " has a hidden pair");
			for (Integer in : possibilitiesThatOccurTwice) {
				if (!cell.getPossibilities().contains(in)) {
//					System.out.println("Removing "+in);
					hiddenPair.remove(in);
				}
			}
		
		}
		
		for (Cell cell : cellsInRow) {
			for (Integer in : hiddenPair) {
				if (!map.contains(cell) && cell.getPossibilities().contains(in)) {
					return;
				}
			}
		}
	
		if (hiddenPair.size()==2) {
//			System.out.println(hiddenPair.size());
//			System.out.println(hiddenPair.get(0) + " and " + hiddenPair.get(1) + " is one of the hidden pair candidates");
			for (Cell cell : map) {
				for (Integer possible : cell.getPossibilities()) {
					if (possible != hiddenPair.get(0) && possible != hiddenPair.get(1) ) {
						posToRemove.add(possible);
					}
				}
			}
			
			for (Cell cell : map) {
				for (Integer in : posToRemove) {
//					System.out.println("Removing " + in + " from " + cell.getCellCoordinates());
					cell.removePossibility(in);
					
				}
			}
		}
	}
	
	/**
	 *  Identify any unique possibilities that occur only 3 times in a row,
	 *  If the 3 unique possibilities occur in the same cell, then remove any other
	 *  possibility from those 3 cells.
	 * @param cells
	 * @param rowInQuestion
	 */
	@SuppressWarnings("unused")
	private void hiddenTriplesInRow(int rowInQuestion) {	
		ArrayList<Cell> cellsInRow = board.getCellsInRow(rowInQuestion);
		Set<Integer> possibilitiesThatOccurThreeTimes = new HashSet<Integer>();
		Set<Cell> map = new HashSet<Cell>();
		ArrayList<Integer> hiddenTriple = new ArrayList<Integer>();
		Set<Integer> posToRemove = new HashSet<Integer>();
		
		
		for (Cell cell : cellsInRow) {
			for (Integer possibility: cell.getPossibilities()) {
				int count = getNumOfOccurancesOfPossibilityInCellList(cellsInRow, possibility);
			    if (count == 3) {
			    	possibilitiesThatOccurThreeTimes.add(possibility);
			    }
			}
		} 
			
		for (Integer pos1 : possibilitiesThatOccurThreeTimes) {
			for (Integer pos2 : possibilitiesThatOccurThreeTimes) {
				for (Integer pos3 : possibilitiesThatOccurThreeTimes) {
					for (Cell cell : cellsInRow) {
						if (pos1 != pos2 && pos1 != pos3) {
							if (cell.getPossibilities().contains(pos1) && 
								cell.getPossibilities().contains(pos2) &&
								cell.getPossibilities().contains(pos3)) {
								System.out.println(pos1 + " " + pos2 + " " + pos3 + " " + "occurs in cell " + cell.getCoordinates());
								map.add(cell);
							}
						}
					}
				}
			}
		}
		
		hiddenTriple.addAll(possibilitiesThatOccurThreeTimes);
		for (Cell cell : map) {
			System.out.println("Cell " + cell.getCoordinates() + " has a hidden triple");
			for (Integer in : possibilitiesThatOccurThreeTimes) {
				if (!cell.getPossibilities().contains(in)) {
					System.out.println("Removing "+in);
					hiddenTriple.remove(in);
				}
			}
		
		}
		
		for (Cell cell : cellsInRow) {
			for (Integer in : hiddenTriple) {
				if (!map.contains(cell) && cell.getPossibilities().contains(in)) {
					return;
				}
			}
		}
	
		if (hiddenTriple.size()==3) {
			System.out.println(hiddenTriple.size());
			System.out.println(hiddenTriple.get(0) + " and " + hiddenTriple.get(1) + " is one of the hidden triple candidates");
			for (Cell cell : map) {
				for (Integer possible : cell.getPossibilities()) {
					if (possible != hiddenTriple.get(0) && possible != hiddenTriple.get(1) && possible != hiddenTriple.get(2) ) {
						posToRemove.add(possible);
					}
				}
			}
			
			for (Cell cell : map) {
				for (Integer in : posToRemove) {
					System.out.println("Removing " + in + " from " + cell.getCoordinates());
					cell.removePossibility(in);
					
				}
			}
		}
	}
	
	/**
	 * Returns the cells within the given list of cells that contain the specified possibility.
	 * @param cells
	 * @param possibility
	 * @return
	 */
	public ArrayList<Cell> getCellsContainingPossibility(ArrayList<Cell> cells, Integer possibility) {
		ArrayList<Cell> cellsContainingPossibility = new ArrayList<Cell>();
		
		for (Cell cell : cells) {
			if (cell.getPossibilities().contains(possibility)) {
				cellsContainingPossibility.add(cell);
			}
		}
		return cellsContainingPossibility;
	}
	
	/**
	 * Returns the cells within the given list of cells that contain the specified list of possibilities.
	 * @param cells
	 * @param possibility
	 * @return
	 */
	public ArrayList<Cell> getCellsContainingPossibilityList(ArrayList<Cell> cells, ArrayList<Integer> possibility) {
		ArrayList<Cell> cellsContainingPossibility = new ArrayList<Cell>();
		
		for (Cell cell : cells) {
			if (cell.getPossibilities().containsAll(possibility)) {
				cellsContainingPossibility.add(cell);
			}
		}
		return cellsContainingPossibility;
	}
	
	/**
	 * Returns the number of times the specified possibility occurs in the given cell list.
	 * @param cells
	 * @param possibility
	 * @return
	 */
	public int getNumOfOccurancesOfPossibilityInCellList(ArrayList<Cell> cells, Integer possibility) {
		int numOfOccurrances = 0;
		
		for (Cell cell : cells) {
			for (Integer poss: cell.getPossibilities()) {
				if (poss.equals(possibility)) {
					numOfOccurrances++;
				}
			}
		}
		return numOfOccurrances;
	}

	/**
	 * @param solveInProgress the solveInProgress to set
	 */
	public void setSolveInProgress(boolean solveInProgress) {
		this.solveInProgress = solveInProgress;
	}

	/**
	 * @return the solveInProgress
	 */
	public boolean isSolveInProgress() {
		return solveInProgress;
	}

	/**
	 * Runs the solver routine
	 */
	public void run() {
		solveStartTime = (int)System.currentTimeMillis();
		while (solveInProgress) {
			if (solve()) {
				solveInProgress=false;
			}
		}
		Board.getBoard().printSolverSteps();
		solveEndTime = (int)System.currentTimeMillis();
	}

	/**
	 * @param solveStartTime the solveStartTime to set
	 */
	public void setSolveStartTime(int solveStartTime) {
		this.solveStartTime = solveStartTime;
	}

	/**
	 * @return the solveStartTime
	 */
	public int getSolveStartTime() {
		return solveStartTime;
	}

	/**
	 * @param solveEndTime the solveEndTime to set
	 */
	public void setSolveEndTime(int solveEndTime) {
		this.solveEndTime = solveEndTime;
	}

	/**
	 * @return the solveEndTime
	 */
	public int getSolveEndTime() {
		return solveEndTime;
	}

	/**
	 * @param silentMode the silentMode to set
	 */
	public void setSilentMode(boolean silentMode) {
		Solver.silentMode = silentMode;
	}

	/**
	 * @return the silentMode
	 */
	public boolean isSilentMode() {
		return silentMode;
	}

	/**
	 * @param solver the solver to set
	 */
	public void setSolver(Solver solver) {
		Solver.solver = solver;
	}

	/**
	 * @return the solver
	 */
	public static Solver getSolver() {
		if (solver==null) {
			solver = new Solver();
		}
		return solver;
	}

	/**
	 * @param solveSteps the solveSteps to set
	 */
	public static void setSolveSteps(ArrayList<CellHistory> solveSteps) {
		Solver.solveSteps = solveSteps;
	}

	/**
	 * @return the solveSteps
	 */
	public static ArrayList<CellHistory> getSolveSteps() {
		return solveSteps;
	}
}

