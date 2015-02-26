package com.sudoku.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.sudoku.gui.board.Board;
import com.sudoku.gui.images.ImageHandler;
import com.sudoku.gui.statusbar.SudokuStatusLabel;

public class OpenBoard implements Runnable {

	static final Logger logger = Logger.getLogger(com.sudoku.db.SaveBoard.class);
	private final static String OPEN_BOARD_SQL = "SELECT BOARD.BOARD_DIFFICULTY_LEVEL, BOARD_DETAIL.BOARD_FULLARRAY FROM BOARD, BOARD_DETAIL WHERE BOARD.BOARD_ID = BOARD_DETAIL.BOARD_ID AND BOARD.BOARD_ID = ?";
	private final static String GET_BOARD_DESCRIPTIONS = "SELECT BOARD_ID, BOARD_DIFFICULTY_TEXT FROM BOARD";
	private Connection connection = null;
	
	/**
	 * OpenBoard constructor
	 * @param connection
	 * @param board
	 */
	public OpenBoard(Connection connection) {
		this.connection=connection;
	}
	
	/**
	 * Thread Run Method to Open Boards.
	 */
	public void run() {
		try {
			process();
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} 
	}
	
	/**
	 * Process method that is used to retrieve the board from the database.
	 * @throws SQLException
	 */
	public int[][] process(int boardId) throws SQLException {
		ArrayList<String> boardsFound = new ArrayList<String>();
		int[][]array = new int[Board.BOARD_HEIGHT][Board.BOARD_WIDTH];
		
		PreparedStatement headerStmt = connection.prepareStatement(OpenBoard.OPEN_BOARD_SQL);
		
		try {
			headerStmt.setInt(1, boardId);
			
			ResultSet result = headerStmt.executeQuery();

			while (result.next()) {
				boardsFound.add(result.getString(2));
			}
			
			if (boardsFound.size()==0) {
				logger.error("No boards found with board id of ["+boardId+"]");
				System.exit(0);
			} 		
			
			if (logger.isDebugEnabled()) {
				logger.debug(boardsFound.toString());
			}
			
			
			StringTokenizer strToke = new StringTokenizer(boardsFound.get(0),",");
			while (strToke.hasMoreElements()) {
				for (int row = 0; row < Board.BOARD_HEIGHT; row++) {
					for (int col = 0; col < Board.BOARD_WIDTH; col++) {
						array[row][col]=Integer.parseInt(strToke.nextToken());
					}
				}
			}
			
			if (logger.isDebugEnabled()) {
				logger.debug(Board.getBoard().getDifficultyText()+" Board Open Complete");
			}

			connection.commit();
		} catch (SQLException e) {
			logger.error(e.getMessage());
			connection.rollback();
			connection.close();
		} finally {
			headerStmt.close();
		}
		
		return array;
	}
	
	/**
	 * Process method that is used to retrieve the board from the database.
	 * @throws SQLException
	 */
	private void process() throws SQLException {
		int boardId = promptForBoardToOpen();
		if (boardId < 0) {
			return;
		}
		clearCurrentBoard();
		SudokuStatusLabel.getStatusBar().setMessage("Opening Board ["+boardId+"]");
		ArrayList<String> boardsFound = new ArrayList<String>();
		int boardDifficultyLevel = 0;
		int[][]array = new int[Board.BOARD_HEIGHT][Board.BOARD_WIDTH];
		
		PreparedStatement headerStmt = connection.prepareStatement(OpenBoard.OPEN_BOARD_SQL);
		
		try {
			headerStmt.setInt(1, boardId);
			
			ResultSet result = headerStmt.executeQuery();

			while (result.next()) {
				boardDifficultyLevel=result.getInt(1);
				boardsFound.add(result.getString(2));
			}
			
			if (boardsFound.size()==0) {
				logger.error("No boards found with board id of ["+boardId+"]");
				System.exit(0);
			} 		
			
			if (logger.isDebugEnabled()) {
				logger.debug(boardsFound.toString());
			}
			
			
			StringTokenizer strToke = new StringTokenizer(boardsFound.get(0),",");
			while (strToke.hasMoreElements()) {
				for (int row = 0; row < Board.BOARD_HEIGHT; row++) {
					for (int col = 0; col < Board.BOARD_WIDTH; col++) {
						array[row][col]=Integer.parseInt(strToke.nextToken());
					}
				}
			}
			
			Board.getBoard(boardDifficultyLevel, array);
			
			if (logger.isDebugEnabled()) {
				logger.debug(Board.getBoard().getDifficultyText()+" Board Open Complete");
			}

			connection.commit();
			SudokuStatusLabel.getStatusBar().setMessage("Ready");
		} catch (SQLException e) {
			logger.error(e.getMessage());
			connection.rollback();
			connection.close();
		} finally {
			headerStmt.close();
		}
	}
	
	/**
	 * Prompt user for difficulty of board.
	 * @return
	 */
	private int promptForBoardToOpen() throws SQLException {	
		int boardIdSelected = 0;
		SudokuStatusLabel.getStatusBar().setMessage("Choose board to open..");
		HashMap<Integer,String> boardsFound = new HashMap<Integer,String>();
		
		PreparedStatement headerStmt = connection.prepareStatement(OpenBoard.GET_BOARD_DESCRIPTIONS);
	
		try {			
			ResultSet result = headerStmt.executeQuery();

			while (result.next()) {
				boardsFound.put(result.getInt(1),result.getString(2).toUpperCase().trim());
			}			
			
			Object[] boardDescriptions = new Object[boardsFound.size()];
			int index = 0;
			for (Integer boardId : boardsFound.keySet()) {
				boardDescriptions[index++] = boardsFound.get(boardId);
			}
			
			String boardSelected = (String)JOptionPane.showInputDialog(null,
					"Select Board to Play:\n", 
			       "Sudoku Board Chooser 1.0",
			       JOptionPane.QUESTION_MESSAGE, 
			       ImageHandler.getImageHandler().getFrameIconIcon(), // Use default icon
			       boardDescriptions, 
			       null);


			// If a string was returned, say so.
			if ((boardSelected != null && boardSelected.length() > 0)) {
				for (Integer boardId : boardsFound.keySet()) {
					if (boardsFound.get(boardId).equals(boardSelected)) {
						boardIdSelected=boardId;
					}
				}
			} else {
				SudokuStatusLabel.getStatusBar().setMessage("Open Cancelled");
				boardIdSelected=-1;
			}

			
			connection.commit();
			SudokuStatusLabel.getStatusBar().setMessage("Ready");
		} catch (SQLException e) {
			logger.error(e.getMessage());
			connection.rollback();
			connection.close();
		} finally {
			headerStmt.close();
		}
		return boardIdSelected;
	}
	
	/**
	 * Prompt user for difficulty of board.
	 * @return
	 */
	public HashMap<Integer,String> getAllBoardIds() throws SQLException {
		HashMap<Integer,String> boardsFound = new HashMap<Integer,String>();
		
		PreparedStatement headerStmt = connection.prepareStatement(OpenBoard.GET_BOARD_DESCRIPTIONS);
	
		try {			
			ResultSet result = headerStmt.executeQuery();

			while (result.next()) {
				boardsFound.put(result.getInt(1),result.getString(2).toUpperCase().trim());
			}			
						
			connection.commit();
			SudokuStatusLabel.getStatusBar().setMessage("Ready");
		} catch (SQLException e) {
			logger.error(e.getMessage());
			connection.rollback();
			connection.close();
		} finally {
			headerStmt.close();
		}
		return boardsFound;
	}
	
	/**
	 *  Method called to clear the current board before opening a new one
	 */
	private void clearCurrentBoard() {
		Board.getBoard().clear();		
	}
}
