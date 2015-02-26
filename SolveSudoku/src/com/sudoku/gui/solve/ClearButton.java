package com.sudoku.gui.solve;

import java.awt.Color;
import java.lang.reflect.Method;

import javax.swing.JButton;

import org.apache.log4j.Logger;

import com.sudoku.gui.board.Board;
import com.sudoku.gui.eventHandler.SudokuEventHandler;

public class ClearButton extends JButton {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.solve.ClearButton.class);
	private static final long serialVersionUID = 1L;

	/**
	 * SolverButton Constructor
	 */
	public ClearButton() {
		this.setText("Clear");
		this.setActionCommand("Clear");
		this.setForeground(Color.blue);
		this.setToolTipText("Clear the board");
		initializeListeners();
	}
	
	/**
	 * Initializes Action Listener and Item Listener
	 * for buttons and checkboxes on window.
	 */
	private void initializeListeners() {
		Class<?> clazz = this.getClass();
		Class<?>[] paramTypes = {String.class};
		String remoteclearBoardMethod = "clearBoard";

		try {
			Method clearBoardMethod = clazz.getMethod(remoteclearBoardMethod, paramTypes);
			this.addActionListener(new SudokuEventHandler(clearBoardMethod, this));			
		} catch (Exception e) {
		logger.error("Could not find the "+remoteclearBoardMethod+" Method Exiting...");
			System.exit(-1);
		}
	}
	
	/**
	 *  Method called by CustomActionLister when Solve Button is pressed by User
	 */
	public void clearBoard(String buttonPressed) {
		Board.getBoard().clearUnsolvedCellText();
	}
	
}
