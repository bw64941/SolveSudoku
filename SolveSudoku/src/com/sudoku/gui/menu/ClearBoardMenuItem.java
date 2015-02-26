package com.sudoku.gui.menu;

import java.awt.Event;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import com.sudoku.gui.board.Board;
import com.sudoku.gui.eventHandler.SudokuEventHandler;
import com.sudoku.gui.statusbar.SudokuStatusLabel;

public class ClearBoardMenuItem extends JMenuItem {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.menu.GenerateBoardMenuItem.class);
	private static final long serialVersionUID = 1L;
	
	public ClearBoardMenuItem() {
		this.setText("Clear");
		this.setToolTipText("Clears the current board");
		this.setActionCommand(this.getText().toUpperCase());
		this.setMnemonic(KeyEvent.VK_N); // Allows the user to press 'N' when inside the File menu to reset to a new board
		this.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK)); //Allows the user to press Ctrl + 'N' to create a new board
		initializeListeners();
	}
	
	/**
	 * Initializes Action Listener and Item Listener
	 * for buttons and checkboxes on window.
	 */
	private void initializeListeners() {
		Class<?> clazz = this.getClass();
		Class<?>[] paramTypes = {String.class};
		String remoteClearBoardMethod = "clearBoard";
		
		try {
			Method clearBoardMethod = clazz.getMethod(remoteClearBoardMethod, paramTypes);
			this.addActionListener(new SudokuEventHandler(clearBoardMethod,this));
			
		} catch (Exception e) {
			
			System.out.println("COULD NOT FIND FUNC");
			logger.error("Could not find the "+remoteClearBoardMethod+" Method Exiting...");
			System.exit(-1);
		}
	}
	

	/**
	 *  Method called by CustomActionLister when Start Over Button is pressed by User
	 */
	public void clearBoard(String buttonPressed) {
		Board.getBoard().clear();
		SudokuStatusLabel.getStatusBar().setMessage("Board Cleared");
	}
	
}
