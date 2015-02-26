package com.sudoku.gui.menu;

import java.awt.Event;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import com.sudoku.gui.board.Board;
import com.sudoku.gui.eventHandler.SudokuEventHandler;

public class ToggleColoringMenuItem extends JCheckBoxMenuItem {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.menu.ToggleColoringMenuItem.class);
	private static final long serialVersionUID = 1L;
	
	public ToggleColoringMenuItem() {
		this.setText("Toggle Cell Coloring");
		this.setToolTipText("Toggle Cell coloring on the board.");
		this.setActionCommand(this.getText().toUpperCase());
		this.setMnemonic(KeyEvent.VK_T); // Allows the user to press 'T' when inside the Tool menu to toggle the color functionality on/off
		this.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, Event.CTRL_MASK)); //Allows the user to press Ctrl + 'T' to toggle the color functionality on/off
		initializeListeners();
	}
	
	/**
	 * Initializes Action Listener and Item Listener
	 * for buttons and checkboxes on window.
	 */
	private void initializeListeners() {
		Class<?> clazz = this.getClass();
		Class<?>[] paramTypes = {String.class};
		String remoteEnableCellColoringdMethod = "enableCellColoring";
		
		try {
			Method enableCellColoringMethod = clazz.getMethod(remoteEnableCellColoringdMethod, paramTypes);
			this.addActionListener(new SudokuEventHandler(enableCellColoringMethod,this));
			
		} catch (Exception e) {
		logger.error("Could not find the "+remoteEnableCellColoringdMethod+" Method Exiting...");
			System.exit(-1);
		}
	}
	

	/**
	 *  Method called by CustomActionLister when Start Over Button is pressed by User
	 */
	public void enableCellColoring(String buttonPressed) {
		if (Board.getBoard().isColoringEnabled()) {
			Board.getBoard().setColoringEnabled(false);
			Board.getBoard().clearUnsolvedCellColors();
		} else {
			Board.getBoard().setColoringEnabled(true);
		}
	}
	
}
