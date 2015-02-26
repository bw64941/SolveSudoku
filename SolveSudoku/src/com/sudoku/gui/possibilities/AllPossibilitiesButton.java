package com.sudoku.gui.possibilities;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.sudoku.gui.board.Board;
import com.sudoku.gui.eventHandler.SudokuEventHandler;

public class AllPossibilitiesButton extends PossibilityButton {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.possibilities.AllPossibilitiesButton.class);
	private static final long serialVersionUID = 1L;

	/**
	 * SolverButton Constructor
	 */
	public AllPossibilitiesButton() {
		super("All");
		this.setToolTipText("Show All Possibilities left on the board");
		initializeListeners();
	}
	
	/**
	 * Initializes Action Listener 
	 * for All button.
	 */
	private void initializeListeners() {
		Class<?> clazz = this.getClass();
		Class<?>[] paramTypes = {String.class};
		String remoteShowAllPossibilitiesMethod = "showAllPossibilities";

		try {
			Method showAllPossibilitiesMethod = clazz.getMethod(remoteShowAllPossibilitiesMethod, paramTypes);
			this.addActionListener(new SudokuEventHandler(showAllPossibilitiesMethod, this));			
		} catch (Exception e) {
		logger.error("Could not find the "+remoteShowAllPossibilitiesMethod+" Method Exiting...");
			System.exit(-1);
		}
	}
	
	/**
	 *  Method called by CustomActionLister when All Button is pressed by User
	 */
	public void showAllPossibilities(String buttonPressed) {
		if (!Board.getBoard().isEmpty()) {
			Board.getBoard().showUnsolvedCellPossibilities();
		}
	}

	
}
