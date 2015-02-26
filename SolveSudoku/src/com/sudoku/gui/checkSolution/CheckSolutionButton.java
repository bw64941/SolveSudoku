package com.sudoku.gui.checkSolution;

import java.awt.Color;
import java.lang.reflect.Method;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import com.sudoku.gui.board.Board;
import com.sudoku.gui.eventHandler.SudokuEventHandler;
import com.sudoku.gui.statusbar.SudokuStatusLabel;
import com.sudoku.solver.Solver;

public class CheckSolutionButton extends JButton {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.checkSolution.CheckSolutionButton.class);
	private static final long serialVersionUID = 1L;
	private Solver solver = null;
	
	/**
	 * CheckSolutionButton Constructor
	 */
	public CheckSolutionButton() {
		this.setText("Check Solution");
		this.setForeground(Color.blue);
		this.setToolTipText("Click Here to check your answers");
		this.setHorizontalTextPosition(SwingConstants.CENTER);
		this.setVerticalTextPosition(SwingConstants.BOTTOM);
		initializeListeners();
	}
	
	/**
	 * Initializes Action Listener for buttons on window.
	 */
	private void initializeListeners() {
		Class<?> clazz = this.getClass();
		Class<?>[] paramTypes = {String.class};
		String remoteCheckSolutionMethod = "checkSolution";
		
		try {
			Method checkSolutionMethod = clazz.getMethod(remoteCheckSolutionMethod, paramTypes);
			this.addActionListener(new SudokuEventHandler(checkSolutionMethod,this));
			
		} catch (Exception e) {
		logger.error("Could not find the "+remoteCheckSolutionMethod+" Method Exiting...");
			System.exit(-1);
		}
	}
	

	/**
	 *  Method called by CustomActionLister when Check Solution Button is pressed by User
	 */
	public void checkSolution(String buttonPressed) {
//		if (Board.getBoard().isSolved()) {
//			int counter=1;
//			for (CellHistory cellHistory: Solver.getSolveSteps()) {
//				Cell cell = cellHistory.getCell();	
//				int value = cellHistory.getValue();
//				int addRemove = cellHistory.getActionFlag();
//				
//				if (addRemove == 0) {
//					cell.addPossibility(value);
//				} else if (addRemove == 1) {
//					cell.setValue(0);
//					cell.addPossibility(value);
//				}
//				
//				if (logger.isDebugEnabled()) {
//					if (addRemove == 0) {
//						logger.debug(counter+": ["+value+"]"+" POSSIBILITY ADDED TO ["+cell.getRow()+","+cell.getCol()+"]");
//					} else if (addRemove == 1) {
//						logger.debug(counter+": [0]"+" PLACED IN ["+cell.getRow()+","+cell.getCol()+"]");
//						logger.debug(counter+": ["+value+"]"+" POSSIBILITY ADDED TO QUAD ["+cell.getQuad()+"], ROW ["+cell.getRow()+"], COL ["+cell.getCol()+"]");
//					} else {
//						logger.error("Unrecognized Action Variable in CellHistory ["+addRemove+"]");
//					}
//				}
//				counter++;
//			}
//		}
		if (!Board.getBoard().isEmpty()) {
			solver = Solver.getSolver();
			solver.setSilentMode(true);
			if (solver.solve()) {
				JOptionPane.showConfirmDialog(null, "So far so GOOD!",
						"Solution Verification", JOptionPane.OK_CANCEL_OPTION);
			} else {
				JOptionPane.showConfirmDialog(null, "You have made a mistake!",
						"Solution Verification", JOptionPane.OK_CANCEL_OPTION);
			}
		} else {
			SudokuStatusLabel.getStatusBar().setMessage(
					"Use Board->Generate to Generate a Board From Scratch");
		}
	}
}
