package com.sudoku.error;

import org.apache.log4j.Logger;

import com.sudoku.gui.statusbar.SudokuStatusLabel;

public class SudokuException extends Exception {

	private static final long serialVersionUID = 1L;
	static final Logger logger = Logger.getLogger(com.sudoku.error.SudokuException.class);
	
	public SudokuException(Throwable cause) {
		super(cause);
	}
	
	public SudokuException(String errorMessage) {
		super(errorMessage);
		SudokuStatusLabel.getStatusBar().setMessage("Error");
		logger.error(errorMessage);
	}
	
	public SudokuException(String errorMessage,Throwable cause) {
		super(errorMessage,cause);
		logger.error(errorMessage);
	}

}
