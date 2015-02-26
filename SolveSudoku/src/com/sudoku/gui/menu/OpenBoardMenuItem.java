package com.sudoku.gui.menu;

import java.awt.Event;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import com.sudoku.db.DatabaseWorker;
import com.sudoku.gui.eventHandler.SudokuEventHandler;

public class OpenBoardMenuItem extends JMenuItem {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.menu.OpenBoardMenuItem.class);
	private static final long serialVersionUID = 1L;
	
	public OpenBoardMenuItem() {
		this.setText("Open");
		this.setToolTipText("Open an existing game");
		this.setActionCommand(this.getText().toUpperCase());
		this.setMnemonic(KeyEvent.VK_O); // Allows the user to press 'O' when inside the File menu to open an existing new board
		this.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK)); //Allows the user to press Ctrl + 'O' to open an existing board
		initializeListeners();
	}
	
	/**
	 * Initializes Action Listener and Item Listener
	 * for buttons and checkboxes on window.
	 */
	private void initializeListeners() {
		Class<?> clazz = this.getClass();
		Class<?>[] paramTypes = {String.class};	
		String remoteOpenBoardMethod = "openBoard";
		
		try {	
			Method openBoardMethod = clazz.getMethod(remoteOpenBoardMethod, paramTypes);
			this.addActionListener(new SudokuEventHandler(openBoardMethod,this));
		} catch (Exception e) {
		logger.error("Could not find the "+remoteOpenBoardMethod+" Method Exiting...");
			System.exit(-1);
		}
	}
	
	/**
	 * Method called by SudokuEventHandler when the Open Board button is pressed.
	 */
	public void openBoard(String buttonPressed) {
		DatabaseWorker.getDbWorker().setAction(buttonPressed);
		Thread thread = new Thread(DatabaseWorker.getDbWorker());
		thread.setDaemon(true);
		thread.start();
	}
}
