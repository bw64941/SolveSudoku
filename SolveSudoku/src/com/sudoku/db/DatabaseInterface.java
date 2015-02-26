package com.sudoku.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.sudoku.error.SudokuException;

public class DatabaseInterface implements Runnable {
	
	static final Logger logger = Logger.getLogger(com.sudoku.db.DatabaseInterface.class);
	private final static String DRIVER_CLASS = "sun.jdbc.odbc.JdbcOdbcDriver";
	private final static String DB_URL = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=sudokuDB.mdb";
	private final static String USER_NAME = "admin";
	private final static String PASSWORD = "bsabsa";
	private static DatabaseInterface dbInterface = null;
	private Connection connection = null;
	private String action = "";	

	/**
	 * DatabaseInterface Constructor
	 */
	private DatabaseInterface() {
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
		}
	}

	/**
	 * @param dbInterface the dbInterface to set
	 */
	public static void setDbWorker(DatabaseInterface dbInterface) {
		DatabaseInterface.dbInterface = dbInterface;
	}

	/**
	 * @return the dbInterface
	 */
	public static DatabaseInterface getDbInterface() {
		if (dbInterface==null) {
			dbInterface=new DatabaseInterface();
		}
		return dbInterface;
	}

	/**
	 * Sets the action text from the button that was pressed.
	 * @param action
	 */
	public void setAction(String action) {
		this.action = action;
	}

}
