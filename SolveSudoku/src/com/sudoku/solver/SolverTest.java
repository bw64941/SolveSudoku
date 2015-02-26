package com.sudoku.solver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.sudoku.db.OpenBoard;
import com.sudoku.error.SudokuException;
import com.sudoku.gui.board.Board;
import com.sudoku.gui.board.PreDefinedBoard;

public class SolverTest extends TestCase {
	
	static final Logger logger = Logger.getLogger(com.sudoku.solver.SolverTest.class);
	private PreDefinedBoard preDefined = new PreDefinedBoard();
	private final static String DRIVER_CLASS = "org.apache.derby.jdbc.EmbeddedDriver";
	private final static String DB_URL = "jdbc:derby:sudoku;create=true;upgrade=true";
	private final static String USER_NAME = "sudoku";
	private final static String PASSWORD = "sudoku";
	private Connection connection = null;
	private Board board = null;
	private Solver solver = null;
	private static final int EASY = 1;
	private static final int MEDIUM = 2;
	private static final int MEDIUM2 = 3;
	private static final int HARD = 4;
	private static final int HARD2 = 5;
	private static final int EVIL = 6;
	private static final int FIENDISH = 7;
	private static final int USA_TODAY = 8;
	private static final int JUNIT_TEST = 9;
	
	public SolverTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testEasy() {
		board = Board.getBoard(SolverTest.EASY,preDefined.getEasyBoard());	
		solver = Solver.getSolver();
		boolean solved = solver.solve();
		assertEquals("["+Board.getBoard().getDifficultyText()+"]"+" BOARD WAS NOT SOLVED",true, solved);
		assertEquals("NUMBER OF CALLS TO RECURSION CHANGED WAS:["+2+"] NOW ["+Board.getBoard().getRecursiveCallsToSolve()+"]",2, Board.getBoard().getRecursiveCallsToSolve());
		board.clear();
	}

	public void testMedium() {
		board = Board.getBoard(SolverTest.MEDIUM, preDefined.getMediumBoard());	
		solver = Solver.getSolver();		
		boolean solved = solver.solve();
		assertEquals("["+board.getDifficultyText()+"]"+" BOARD WAS NOT SOLVED",true, solved);
		assertEquals("NUMBER OF CALLS TO RECURSION CHANGED WAS:["+4+"] NOW ["+board.getRecursiveCallsToSolve()+"]", 4,  board.getRecursiveCallsToSolve());
		board.clear();
	}
	
	public void testMedium2() {
		board = Board.getBoard(SolverTest.MEDIUM2, preDefined.getMediumBoard2());	
		solver = Solver.getSolver();		
		boolean solved = solver.solve();
		assertEquals("["+board.getDifficultyText()+"]"+" BOARD WAS NOT SOLVED",true, solved);
		assertEquals("NUMBER OF CALLS TO RECURSION CHANGED WAS:["+4+"] NOW ["+board.getRecursiveCallsToSolve()+"]", 4,  board.getRecursiveCallsToSolve());
		board.clear();
	}

	public void testHard() {
		board = Board.getBoard(SolverTest.HARD, preDefined.getHardBoard());	
		solver = Solver.getSolver();
		boolean solved = solver.solve();
		assertEquals("["+board.getDifficultyText()+"]"+" BOARD WAS NOT SOLVED",true, solved);
		assertEquals("NUMBER OF CALLS TO RECURSION CHANGED WAS:["+4+"] NOW ["+board.getRecursiveCallsToSolve()+"]", 4,  board.getRecursiveCallsToSolve());
		board.clear();
	}

	public void testHard2() {
		board = Board.getBoard(SolverTest.HARD2, preDefined.getHardBoard2());
		solver = Solver.getSolver();
		boolean solved = solver.solve();
		assertEquals("["+board.getDifficultyText()+"]"+" BOARD WAS NOT SOLVED",true, solved);
		assertEquals("NUMBER OF CALLS TO RECURSION CHANGED WAS:["+4+"] NOW ["+board.getRecursiveCallsToSolve()+"]", 4,  board.getRecursiveCallsToSolve());
		board.clear();
	}

	public void testEvil() {
		board = Board.getBoard(SolverTest.EVIL, preDefined.getEvilBoard());	
		solver = Solver.getSolver();
		boolean solved = solver.solve();
		assertEquals("["+board.getDifficultyText()+"]"+" BOARD WAS NOT SOLVED",true, solved);
		assertEquals("NUMBER OF CALLS TO RECURSION CHANGED WAS:["+4+"] NOW ["+board.getRecursiveCallsToSolve()+"]", 4,  board.getRecursiveCallsToSolve());
		board.clear();
	}

	public void testFiendish() {
		board = Board.getBoard(SolverTest.FIENDISH, preDefined.getFiendishBoard());	
		solver = Solver.getSolver();
		boolean solved = solver.solve();
		assertEquals("["+board.getDifficultyText()+"]"+" BOARD WAS NOT SOLVED",true, solved);
		assertEquals("NUMBER OF CALLS TO RECURSION CHANGED WAS:["+6+"] NOW ["+board.getRecursiveCallsToSolve()+"]", 6,  board.getRecursiveCallsToSolve());
		board.clear();
	}
	
	public void testUSAToday() {
		board = Board.getBoard(SolverTest.USA_TODAY, preDefined.getUsToday());	
		solver = Solver.getSolver();
		boolean solved = solver.solve();
		assertEquals("["+board.getDifficultyText()+"]"+" BOARD WAS NOT SOLVED",true, solved);		
		assertEquals("NUMBER OF CALLS TO RECURSION CHANGED WAS:["+2+"] NOW ["+board.getRecursiveCallsToSolve()+"]", board.getRecursiveCallsToSolve(), 2);
		board.clear();
	}
	
	public void testAllBoardsInDB() throws SudokuException {
		try {
			Class.forName(DRIVER_CLASS);
			connection = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
			connection.setAutoCommit(false);
			
			OpenBoard openBoard = new OpenBoard(connection);
			HashMap<Integer,String> allBoardsInDB = openBoard.getAllBoardIds();
			
			for (Integer boardId : allBoardsInDB.keySet()) {
				String boardDescription = allBoardsInDB.get(boardId);
				if (logger.isDebugEnabled()) {
					logger.debug("Working on ["+boardDescription+"]");
				}
				int[][] array = openBoard.process(boardId);
				board = Board.getBoard(SolverTest.JUNIT_TEST, array);	
				solver = Solver.getSolver();
				boolean solved = solver.solve();
				assertEquals("["+boardDescription+"] ["+board.getDifficultyText()+"] ["+boardId+"] BOARD WAS NOT SOLVED",true, solved);		
				board.clear();
			}
		} catch (ClassNotFoundException e) {
			throw new SudokuException(e.getMessage());
		} catch (SQLException e) {
			throw new SudokuException(e.getMessage());
		} catch (NullPointerException ne) {
			throw new SudokuException(ne.getMessage());
		}
	}	
	
}
