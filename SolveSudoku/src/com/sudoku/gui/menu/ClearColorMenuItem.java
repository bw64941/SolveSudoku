package com.sudoku.gui.menu;

import java.awt.Event;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import com.sudoku.gui.board.Board;
import com.sudoku.gui.eventHandler.SudokuEventHandler;

public class ClearColorMenuItem extends JMenuItem {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.menu.ClearColorMenuItem.class);
	private static final long serialVersionUID = 1L;
	
	public ClearColorMenuItem() {
		this.setText("Clear");
		this.setToolTipText("Clear current coloring on the board");
		this.setActionCommand(this.getText().toUpperCase());
		this.setMnemonic(KeyEvent.VK_C); // Allows the user to press 'C' when inside the Color menu to this the application
		this.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK)); //Allows the user to press Ctrl + 'Q' to quit the application			
		initializeListeners();
	}
	
	/**
	 * Initializes Action Listener and Item Listener
	 * for buttons and checkboxes on window.
	 */
	private void initializeListeners() {
		Class<?> clazz = this.getClass();
		Class<?>[] paramTypes = {String.class};
		
		String remoteClearColorMethod = "clearColor";
		
		try {			
			Method clearColorMethod = clazz.getMethod(remoteClearColorMethod, paramTypes);
			this.addActionListener(new SudokuEventHandler(clearColorMethod, this));
		} catch (Exception e) {
		logger.error("Could not find the "+remoteClearColorMethod+" Method Exiting...");
			System.exit(-1);
		}
	}
	
	/**
	 * Exits the application.
	 */
	public void clearColor(String buttonPressed) {
		Board.getBoard().clearUnsolvedCellColors();
	}
}
