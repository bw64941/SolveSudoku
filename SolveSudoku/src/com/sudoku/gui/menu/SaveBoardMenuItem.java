package com.sudoku.gui.menu;

import java.awt.Event;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import com.sudoku.db.DatabaseWorker;
import com.sudoku.gui.board.Board;
import com.sudoku.gui.eventHandler.SudokuEventHandler;
import com.sudoku.gui.statusbar.SudokuStatusLabel;

public class SaveBoardMenuItem extends JMenuItem {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.menu.SaveBoardMenuItem.class);
	private static final long serialVersionUID = 1L;
	
	public SaveBoardMenuItem() {
		this.setText("Save");
		this.setToolTipText("Save the current board");
		this.setActionCommand(this.getText().toUpperCase());
		this.setMnemonic(KeyEvent.VK_S); // Allows the user to press 'S' when inside the File menu to reset to a new board
		this.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK)); //Allows the user to press Ctrl + 'S' to create a new board
		initializeListeners();
	}
	
	/**
	 * Initializes Action Listener and Item Listener
	 * for buttons and checkboxes on window.
	 */
	private void initializeListeners() {
		Class<?> clazz = this.getClass();
		Class<?>[] paramTypes = {String.class};

		String remoteSaveBoardMethod = "saveBoard";

		try {
						
			Method saveBoardMethod = clazz.getMethod(remoteSaveBoardMethod, paramTypes);
			this.addActionListener(new SudokuEventHandler(saveBoardMethod, this));
		} catch (Exception e) {
		logger.error("Could not find the "+remoteSaveBoardMethod+" Method Exiting...");
			System.exit(-1);
		}
	}
	
	/**
	 * Method called by SudokuEventHandler when Save Board button is pressed 
	 * by User
	 */
	public void saveBoard(String buttonPressed) {
		if (promptForBoardDetails()==0) {
			DatabaseWorker.getDbWorker().setAction(buttonPressed);
		    Thread thread = new Thread(DatabaseWorker.getDbWorker());
		    thread.setDaemon(true);
		    thread.start();
		}
	}
	
	private int promptForBoardDetails() {
		int cont = 0;
		Object[] difficulties = {"1","2","3","4","5","6","7","8","9","10"};
		String diffLevel = (String)JOptionPane.showInputDialog(null,"Enter a difficulty level for the board (1 (Easy) - 10 (Hard))",null,JOptionPane.QUESTION_MESSAGE,null,difficulties,difficulties[0]);
		if ((diffLevel != null) && (diffLevel.length() > 0)) {
			Board.getBoard().setDifficultyLevel(Integer.parseInt(diffLevel));
			String diffText = (String)JOptionPane.showInputDialog(null,"Enter a description for the board");
			
			if ((diffText != null) && (diffText.length() > 0)) {
				Board.getBoard().setDifficultyText(diffText);
			} else {
				SudokuStatusLabel.getStatusBar().setMessage("Save Cancelled");
				cont=-1;
			}
		} else {
			SudokuStatusLabel.getStatusBar().setMessage("Save Cancelled");
			cont=-1;
		}
		return cont;
	}
	
}
