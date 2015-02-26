package com.sudoku.gui.menu;

import java.awt.Event;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import com.sudoku.gui.eventHandler.SudokuEventHandler;

public class QuitMenuItem extends JMenuItem {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.menu.QuitMenuItem.class);
	private static final long serialVersionUID = 1L;
	
	public QuitMenuItem() {
		this.setText("Quit");
		this.setToolTipText("Exit the game");
		this.setActionCommand(this.getText().toUpperCase());
		this.setMnemonic(KeyEvent.VK_Q); // Allows the user to press 'Q' when inside the File menu to this the application
		this.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK)); //Allows the user to press Ctrl + 'Q' to quit the application			
		initializeListeners();
	}
	
	/**
	 * Initializes Action Listener and Item Listener
	 * for buttons and checkboxes on window.
	 */
	private void initializeListeners() {
		Class<?> clazz = this.getClass();
		Class<?>[] paramTypes = {String.class};
		
		String remoteQuitMethod = "quit";
		
		try {			
			Method quitMethod = clazz.getMethod(remoteQuitMethod, paramTypes);
			this.addActionListener(new SudokuEventHandler(quitMethod, this));
		} catch (Exception e) {
		logger.error("Could not find the "+remoteQuitMethod+" Method Exiting...");
			System.exit(-1);
		}
	}
	
	/**
	 * Exits the application.
	 */
	public void quit(String buttonPressed) {
		System.exit(0);
	}
}
