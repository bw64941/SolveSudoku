package com.sudoku.gui.menu;

import java.awt.Event;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import com.sudoku.db.DatabaseWorker;
import com.sudoku.gui.eventHandler.SudokuEventHandler;

public class DeleteBoardMenuItem extends JMenuItem {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.menu.DeleteBoardMenuItem.class);
	private static final long serialVersionUID = 1L;
	
	public DeleteBoardMenuItem() {
		this.setText("Delete");
		this.setToolTipText("Delete an existing game");
		this.setActionCommand(this.getText().toUpperCase());
		this.setMnemonic(KeyEvent.VK_D); // Allows the user to press 'O' when inside the File menu to open an existing new board
		this.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, Event.CTRL_MASK)); //Allows the user to press Ctrl + 'O' to open an existing board
		initializeListeners();
	}
	
	/**
	 * Initializes Action Listener and Item Listener
	 * for buttons and checkboxes on window.
	 */
	private void initializeListeners() {
		Class<?> clazz = this.getClass();
		Class<?>[] paramTypes = {String.class};	
		String remoteDeleteBoardMethod = "deleteBoard";
		
		try {	
			Method deleteBoardMethod = clazz.getMethod(remoteDeleteBoardMethod, paramTypes);
			this.addActionListener(new SudokuEventHandler(deleteBoardMethod,this));
		} catch (Exception e) {
		logger.error("Could not find the "+remoteDeleteBoardMethod+" Method Exiting...");
			System.exit(-1);
		}
	}
	
	/**
	 * Method called by SudokuEventHandler when the Open Board button is pressed.
	 */
	public void deleteBoard(String buttonPressed) {
		DatabaseWorker.getDbWorker().setAction(buttonPressed);
		Thread thread = new Thread(DatabaseWorker.getDbWorker());
		thread.setDaemon(true);
		thread.start();
	}
}

