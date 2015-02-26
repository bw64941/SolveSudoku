package com.sudoku.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.sudoku.cell.Cell;
import com.sudoku.gui.board.Board;
import com.sudoku.gui.statusbar.SudokuStatusLabel;

public class SaveBoard implements Runnable {
	
	static final Logger logger = Logger.getLogger(com.sudoku.db.SaveBoard.class);
	private final static String SAVE_BOARD_SQL = "INSERT INTO BOARD (BOARD_HEIGHT,BOARD_WIDTH,BOARD_DIFFICULTY_LEVEL,BOARD_DIFFICULTY_TEXT) VALUES (?,?,?,?)";
	private final static String SAVE_BOARD_DETAIL = "INSERT INTO BOARD_DETAIL (BOARD_ID,BOARD_FULLARRAY,BOARD_QUADRANT_1,BOARD_QUADRANT_2,BOARD_QUADRANT_3,BOARD_QUADRANT_4,BOARD_QUADRANT_5,BOARD_QUADRANT_6,BOARD_QUADRANT_7,BOARD_QUADRANT_8,BOARD_QUADRANT_9) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
	private final static String GET_BOARD_ID_BY_DIFFICULTY_LEVEL = "SELECT BOARD_ID FROM BOARD WHERE BOARD_DIFFICULTY_LEVEL = ?";
	private Connection connection = null;
	private Board board = Board.getBoard();


	/**
	 * SaveBoard Constructor
	 * @param board
	 */
	public SaveBoard(Connection connection) {
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
		SudokuStatusLabel.getStatusBar().setMessage("Saving "+board.getDifficultyText()+" Board to Sudoku Database...");
		PreparedStatement headerStmt = connection.prepareStatement(SaveBoard.SAVE_BOARD_SQL);
		PreparedStatement detailStmt = connection.prepareStatement(SaveBoard.SAVE_BOARD_DETAIL);
		
		try {
			headerStmt.setInt(1, Board.BOARD_HEIGHT);
			headerStmt.setInt(2, Board.BOARD_WIDTH);
			headerStmt.setInt(3,board.getDifficultyLevel());
			headerStmt.setString(4,board.getDifficultyText());
			
			headerStmt.executeUpdate();
			if (logger.isDebugEnabled()) {
				logger.debug(board.getDifficultyText()+" Header Save Complete");
			}
			
			
			detailStmt.setInt(1,getBoardIdFromHeader());
			detailStmt.setString(2, getFullBoardValuesToInsert());
			for (int i=3; i<=Board.BOARD_HEIGHT+2; i++) {
				detailStmt.setString(i, getQuadrantValuesToInsert(i-3));
			}
			
			detailStmt.executeUpdate();
			if (logger.isDebugEnabled()) {
				logger.debug(board.getDifficultyText()+" Detail Save Complete");
			}
			
			
			connection.commit();			
			SudokuStatusLabel.getStatusBar().setMessage(board.getDifficultyText()+" Board Saved");
		} catch (SQLException e) {
			logger.error(e.getMessage());
			connection.rollback();
		} finally {
			headerStmt.close();
			detailStmt.close();
		}
	}
	
	/**
	 * Returns the full board values formated for insert into
	 * the BOARD_DETAIL table.
	 * @return
	 */
	private String getFullBoardValuesToInsert() {
		StringBuffer boardValues = new StringBuffer();
		
		for (Cell[] row : board.getCells()) {
			for (Cell cell : row) {
				boardValues.append(cell.getValue()+",");
			}
		}
		
		return boardValues.substring(0, boardValues.length()-1);
	}

	/**
	 * Returns the quadrant values in a string formated for insert into
	 * the BOARD_DETAIL table.
	 * @param quadrant
	 * @return
	 */
	private String getQuadrantValuesToInsert(int quadrant) {
		StringBuffer quadrantValues = new StringBuffer();
		
		for (Cell[] row : board.getCells()) {
			for (Cell cell : row) {
				if (cell.getQuad()==quadrant) {
					quadrantValues.append(cell.getValue()+",");
				}
			}
		}
		
		return quadrantValues.substring(0, quadrantValues.length()-1);
	}
	
	/**
	 * Returns the BOARD_ID from header table to use in the detail table.
	 */
	private int getBoardIdFromHeader() {
		int board_id = 0;
		
		try {
			//"SELECT BOARD_ID FROM BOARD WHERE BOARD_DIFFICULTY_LEVEL = ?"
			PreparedStatement idStmt = connection.prepareStatement(SaveBoard.GET_BOARD_ID_BY_DIFFICULTY_LEVEL);
			
			
			idStmt.setInt(1, board.getDifficultyLevel());
			ResultSet rs = idStmt.executeQuery();
			logger.debug(board.getDifficultyText()+" Got Board ID from Header");
			
			int rowsFound=0;
			while (rs.next()) {
				board_id=rs.getInt(1);
				rowsFound++;
				if (rowsFound>1) {
					if (logger.isDebugEnabled()) {
						logger.info("More than 1 row found with given difficulty level ["+board.getDifficultyLevel()+"]");
					}
				}
			}
			
			
			idStmt.close();
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		
		return board_id;
	}
}


