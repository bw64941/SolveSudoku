package com.sudoku.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.sudoku.gui.board.Board;
import com.sudoku.gui.images.ImageHandler;
import com.sudoku.gui.statusbar.SudokuStatusLabel;

public class DeleteBoard implements Runnable {
	
	static final Logger logger = Logger.getLogger(com.sudoku.db.DeleteBoard.class);
	private final static String DELETE_BOARD_SQL = "DELETE FROM BOARD WHERE BOARD_ID = ?";
	private final static String DELETE_BOARD_DETAIL_SQL = "DELETE FROM BOARD_DETAIL WHERE BOARD_ID = ?";
	private final static String GET_BOARD_DESCRIPTIONS = "SELECT BOARD_ID, BOARD_DIFFICULTY_TEXT FROM BOARD";
	private Connection connection = null;
	private Board board = Board.getBoard();


	/**
	 * SaveBoard Constructor
	 * @param board
	 */
	public DeleteBoard(Connection connection) {
		this.connection=connection;
	}
	
	public void run() {
		try {
			process();
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	}
	
	/**
	 * Process method used to save the board to the database.
	 * @throws SQLException
	 */
	private void process() throws SQLException {
		int boardId = promptForBoardToDelete();
		SudokuStatusLabel.getStatusBar().setMessage("Deleting "+board.getDifficultyText()+" Board from Sudoku Database...");
		PreparedStatement headerStmt = connection.prepareStatement(DeleteBoard.DELETE_BOARD_SQL);
		PreparedStatement detailStmt = connection.prepareStatement(DeleteBoard.DELETE_BOARD_DETAIL_SQL);
		
		int answer = JOptionPane.showConfirmDialog(null,
				"Are you sure you want to delete this board?:\n", 
		       "Sudoku Board Chooser 1.0",
		       JOptionPane.YES_NO_CANCEL_OPTION,
		       JOptionPane.QUESTION_MESSAGE, 
		       ImageHandler.getImageHandler().getFrameIconIcon());
		
		if (answer==JOptionPane.NO_OPTION || answer==JOptionPane.CANCEL_OPTION) {
			return;
		}
		try {
			detailStmt.setInt(1, boardId);
			headerStmt.setInt(1, boardId);
			
			detailStmt.execute();
			if (logger.isDebugEnabled()) {
				logger.debug(board.getDifficultyText()+" Detail Delete Complete");
			}
			headerStmt.execute();
			if (logger.isDebugEnabled()) {
				logger.debug(board.getDifficultyText()+" Header Delete Complete");
			}
			
			connection.commit();			
			SudokuStatusLabel.getStatusBar().setMessage(board.getDifficultyText()+" Board Deleted");
		} catch (SQLException e) {
			logger.error(e.getMessage());
			connection.rollback();
		} finally {
			headerStmt.close();
			detailStmt.close();
		}
	}
	
	/**
	 * Prompt user for difficulty of board.
	 * @return
	 */
	private int promptForBoardToDelete() throws SQLException {	
		int boardIdSelected = 0;
		SudokuStatusLabel.getStatusBar().setMessage("Choose board to delete..");
		HashMap<Integer,String> boardsFound = new HashMap<Integer,String>();
		
		PreparedStatement headerStmt = connection.prepareStatement(DeleteBoard.GET_BOARD_DESCRIPTIONS);
	
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
					"Select Board to Delete:\n", 
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
				SudokuStatusLabel.getStatusBar().setMessage("Delete Cancelled");
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
}


