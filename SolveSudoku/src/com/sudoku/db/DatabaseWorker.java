package com.sudoku.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.sudoku.error.SudokuException;

public class DatabaseWorker implements Runnable {

	static final Logger logger = Logger.getLogger(com.sudoku.db.DatabaseWorker.class);
	private final static String DRIVER_CLASS = "org.apache.derby.jdbc.EmbeddedDriver";
	private final static String DB_URL = "jdbc:derby:sudoku;create=true;upgrade=true";
	private final static String USER_NAME = "sudoku";
	private final static String PASSWORD = "sudoku";
	private static DatabaseWorker dbWorker = null;
	private Connection connection = null;
	private String action = "";	

	/**
	 * DatabaseWorker Constructor
	 */
	private DatabaseWorker() {
		try {
			establishDBConnection();
		} catch (SudokuException e) {
			logger.error(e.getCause());
		}
	}
	
	/**
	 * Establish initial Database Connection for Saves and Opens.
	 * @throws SudokuException 
	 */
	private void establishDBConnection() throws SudokuException {
		try {
			Class.forName(DRIVER_CLASS);
			connection = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
			connection.setAutoCommit(false);
		} catch (ClassNotFoundException e) {
			throw new SudokuException(e.getMessage());
		} catch (SQLException e) {
			throw new SudokuException(e.getMessage());
		} catch (NullPointerException ne) {
			throw new SudokuException(ne.getMessage());
		}
	}
	
	public void run() {
		if (action.equals("SAVE")) {			
			SwingUtilities.invokeLater(new SaveBoard(connection));
		} else if (action.equals("OPEN")) {
			SwingUtilities.invokeLater(new OpenBoard(connection));
		} else if (action.equals("DELETE")) {
			SwingUtilities.invokeLater(new DeleteBoard(connection));
		}
	}

	/**
	 * @param dbWorker the dbWorker to set
	 */
	public static void setDbWorker(DatabaseWorker dbWorker) {
		DatabaseWorker.dbWorker = dbWorker;
	}

	/**
	 * @return the dbWorker
	 */
	public static DatabaseWorker getDbWorker() {
		if (dbWorker==null) {
			dbWorker=new DatabaseWorker();
		}
		return dbWorker;
	}

	/**
	 * Sets the action text from the button that was pressed.
	 * @param action
	 */
	public void setAction(String action) {
		this.action = action;
	}

}
