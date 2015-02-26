package com.sudoku.gui.possibilities;

import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.Method;

import javax.swing.JButton;

import org.apache.log4j.Logger;

import com.sudoku.gui.board.Board;
import com.sudoku.gui.eventHandler.SudokuEventHandler;

public class PossibilityButton extends JButton {
	
	private static final long serialVersionUID = 1L;
	static final Logger logger = Logger.getLogger(com.sudoku.gui.possibilities.PossibilityButton.class);
	
	/**
	 * ViewOptionsButton Constructor
	 * @param text
	 */
	public PossibilityButton(String text) {
		this.setFont(new Font("Monospaced",Font.BOLD,15));
		this.setText(text);
		this.setActionCommand(text);
		this.setForeground(Color.white);
		this.setBackground(Color.DARK_GRAY);
		this.setFocusable(false);
		initializeListeners();
	}
	

	/**
	 * Initializes Action Listener and Item Listener
	 * for buttons and checkboxes on window.
	 */
	private void initializeListeners() {
		Class<?> clazz = this.getClass();
		Class<?>[] paramTypes = {String.class};

		String remoteShowPossibilityMethod = "showPossibility";

		try {
		
			Method showPossibilityMethod = clazz.getMethod(remoteShowPossibilityMethod, paramTypes);
			this.addActionListener(new SudokuEventHandler(showPossibilityMethod,this));
			
		} catch (Exception e) {
		logger.error("Could not find the "+remoteShowPossibilityMethod+" Method Exiting...");
			System.exit(-1);
		}
	}
	
	/**
	 * Method called by SudokuEventHandler when Value Buttons are pressed by User
	 * Number(1-9) Buttons - Show respective possibilities on board.
	 * ALL Button		   - Show ALL possibilities on board.
	 * CLEAR Button		   - Clears all possibilities on board.
	 * @param button Text
	 */
	
	public void showPossibility(String buttonPressed) {
		if (!Board.getBoard().isEmpty()) {
			if (buttonPressed.equalsIgnoreCase("ALL")) {
				Board.getBoard().showAllUnsolvedCellPossibilities();
			} else {
				Board.getBoard().showUnsolvedCellPossibilities(Integer.parseInt(buttonPressed));
			}
		}
	}
}
