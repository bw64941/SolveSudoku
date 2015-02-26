package com.sudoku.gui.menu;

import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import com.sudoku.cell.Cell;
import com.sudoku.gui.eventHandler.SudokuEventHandler;

public class UndoMenuItem extends JMenuItem {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.menu.UndoMenuItem.class);
	private static final long serialVersionUID = 1L;
	private Cell cell = null;
	
	public UndoMenuItem(Cell cell) {
		this.cell=cell;
		this.setText("Undo");
		this.setToolTipText("Undo last placed value");
		this.setActionCommand(this.getText().toUpperCase());
		this.setMnemonic(KeyEvent.VK_U); // Allows the user to press 'U' when inside the File menu to show the application information
		this.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, Event.CTRL_MASK)); //Allows the user to press Ctrl + 'A' to show the application information
		initializeListeners();
	}
	
	/**
	 * Initializes Action Listener menu 
	 * buttons on window.
	 */
	private void initializeListeners() {
		Class<?> clazz = this.getClass();
		Class<?>[] paramTypes = {String.class};
		String remoteUndoMethod = "undo";
		
		try {
			Method undoMethod = clazz.getMethod(remoteUndoMethod, paramTypes);
			this.addActionListener(new SudokuEventHandler(undoMethod, this));
		} catch (Exception e) {
		logger.error("Could not find the "+remoteUndoMethod+" Method Exiting...");
			System.exit(-1);
		}
	}
	
	/**
	 * Undo last value placed information
	 */
	public void undo(String buttonPressed) {
		System.out.println("here");
//		cell.getCellValueHist().topAndPop();
//		Integer previousValue = cell.getCellValueHist().top();
//		cell.setValue(previousValue);
//		cell.getCellValueHist().pop();
		cell.setFont(new Font("Lucida Sans Typewriter", Font.BOLD, 14));
		cell.setHorizontalAlignment(JTextField.CENTER);
		cell.setForeground(Color.blue);
		cell.setBackground(null);
//		if (previousValue.equals(Integer.valueOf(0))) {
//			cell.setBackground(Color.white);
//		}
		cell.setBorder(BorderFactory.createLineBorder(Color.black));
		cell.getPossibilities().clear();
		cell.populateInitialPossibilities();
	}
	
	
}
