package com.sudoku.gui.menu;

import java.awt.Event;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import com.sudoku.gui.board.Board;
import com.sudoku.gui.eventHandler.SudokuEventHandler;
import com.sudoku.gui.images.ImageHandler;

public class BoardStatisticsMenuItem extends JMenuItem {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.menu.BoardStatisticsMenuItem.class);
	private static final long serialVersionUID = 1L;


	public BoardStatisticsMenuItem() {
		this.setText("Statistics");
		this.setToolTipText("Shows stats for the current board");
		this.setActionCommand(this.getText().toUpperCase());
		this.setMnemonic(KeyEvent.VK_C); // Allows the user to press 'C' when inside the File menu to show stats for board
		this.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK)); //Allows the user to press Ctrl + 'C' to create a new board
		initializeListeners();
	}
	
	/**
	 * Initializes Action Listener and Item Listener
	 * for buttons and checkboxes on window.
	 */
	private void initializeListeners() {
		Class<?> clazz = this.getClass();
		Class<?>[] paramTypes = {String.class};
		String remoteShowBoardStatsMethod = "showBoardStats";
		
		try {
			Method showBoardStatsMethod = clazz.getMethod(remoteShowBoardStatsMethod, paramTypes);
			this.addActionListener(new SudokuEventHandler(showBoardStatsMethod,this));
		} catch (Exception e) {
			System.out.println("COULD NOT FIND FUNC");
			logger.error("Could not find the "+remoteShowBoardStatsMethod+" Method Exiting...");
			System.exit(-1);
		}
	}
	

	/**
	 *  Method called by CustomActionLister when Statistics Menu Item is pressed by User
	 */
	public void showBoardStats(String buttonPressed) {
		if (Board.getBoard().isSolved()) {
			JOptionPane.showMessageDialog(null,
					"\t----------------------------------------------------------------\n"+
					"\tBoard Difficulty ["+Board.getBoard().getDifficultyText()+"]\n"+
					"\tBoard Level ["+Board.getBoard().getDifficultyLevel()+"]\n"+
					"\tRecursive Calls To Solve ["+Board.getBoard().getRecursiveCallsToSolve()+"]\n"+
					"\tNumber of Randomly Placed Values ["+Board.getBoard().getNumOfRandomValues()+"]\n"+
					"\t----------------------------------------------------------------\n"
					,"Sudoku Board Statistics",JOptionPane.INFORMATION_MESSAGE,ImageHandler.getImageHandler().getApplicationImage());   
		} else {
			JOptionPane.showMessageDialog(null,
					"\t----------------------------------------------------------------\n"+
					"\tBoard Difficulty ["+Board.getBoard().getDifficultyText()+"]\n"+
					"\tBoard Level ["+Board.getBoard().getDifficultyLevel()+"]\n"+
					"\tNumber of Unsolved Cells ["+Board.getBoard().getNumOfUnsolvedCells()+"]\n"+
					"\tNumber of Solved Cells ["+Board.getBoard().getNumOfSolvedCells()+"]\n"+
					"\tNumber of Possibilities Left ["+Board.getBoard().getNumberOfPossibilitiesRemaining()+"]\n"+
					"\tInitial Avergae Poss /Cell ["+Board.getBoard().getInitialAvgPosPerCell()+"]\n"+
					"\tNumber of Randomly Placed Values ["+Board.getBoard().getNumOfRandomValues()+"]\n"+
					"\t----------------------------------------------------------------\n"
					,"Sudoku Board Statistics",JOptionPane.INFORMATION_MESSAGE,ImageHandler.getImageHandler().getApplicationImage());
		}
		   
	}
}
