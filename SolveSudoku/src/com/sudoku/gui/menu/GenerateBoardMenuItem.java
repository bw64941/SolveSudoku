package com.sudoku.gui.menu;

import java.awt.Event;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import com.sudoku.gui.board.BoardGenerator;
import com.sudoku.gui.eventHandler.SudokuEventHandler;

public class GenerateBoardMenuItem extends JMenuItem {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.menu.GenerateBoardMenuItem.class);
	private static final long serialVersionUID = 1L;
	
	public GenerateBoardMenuItem() {
		this.setText("Generate");
		this.setToolTipText("Generate a random board");
		this.setActionCommand(this.getText().toUpperCase());
		this.setMnemonic(KeyEvent.VK_G); // Allows the user to press 'G' when inside the File menu to reset to a new board
		this.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, Event.CTRL_MASK)); //Allows the user to press Ctrl + 'N' to create a new board
		initializeListeners();
	}
	
	/**
	 * Initializes Action Listener and Item Listener
	 * for buttons and checkboxes on window.
	 */
	private void initializeListeners() {
		Class<?> clazz = this.getClass();
		Class<?>[] paramTypes = {String.class};
		String remoteGenerateBoardMethod = "generateBoard";
		
		try {
			Method generateBoardMethod = clazz.getMethod(remoteGenerateBoardMethod, paramTypes);
			this.addActionListener(new SudokuEventHandler(generateBoardMethod,this));
			
		} catch (Exception e) {
		logger.error("Could not find the "+remoteGenerateBoardMethod+" Method Exiting...");
			System.exit(-1);
		}
	}
	
	/**
	 *  Method called by CustomActionLister when Start Over Button is pressed by User
	 */
	public void generateBoard(String buttonPressed) {
		BoardGenerator boardGen = BoardGenerator.getBoardGenerator();
		boardGen.setSolveInProgress(true);
		Thread thread = new Thread(boardGen);
		thread.start();		
	}
}
