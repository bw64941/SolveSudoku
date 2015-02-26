package com.sudoku.gui.solve;

import java.awt.Color;
import java.lang.reflect.Method;

import javax.swing.JButton;

import org.apache.log4j.Logger;

import com.sudoku.gui.board.Board;
import com.sudoku.gui.eventHandler.SudokuEventHandler;
import com.sudoku.gui.statusbar.SudokuStatusLabel;
import com.sudoku.solver.Solver;

public class SolverButton extends JButton {
	
	static final Logger logger = Logger.getLogger(com.sudoku.gui.solve.SolverButton.class);
	private static final long serialVersionUID = 1L;
	private Solver solver = null;

	/**
	 * SolverButton Constructor
	 */
	public SolverButton() {
		this.setText("Solve");
		this.setActionCommand("SOLVE");
		this.setForeground(Color.blue);
		this.setToolTipText("Solve the board");
		initializeListeners();
	}
	
	/**
	 * Initializes Action Listener and Item Listener
	 * for buttons and checkboxes on window.
	 */
	private void initializeListeners() {
		Class<?> clazz = this.getClass();
		Class<?>[] paramTypes = {String.class};
		String remoteSolveMethod = "solveBoard";

		try {
			Method solveMethod = clazz.getMethod(remoteSolveMethod, paramTypes);
			this.addActionListener(new SudokuEventHandler(solveMethod, this));			
		} catch (Exception e) {
			logger.error("Could not find the remote Method, Exiting...");
			System.exit(-1);
		}
	}
	
	/**
	 *  Method called by CustomActionLister when Solve Button is pressed by User
	 */
	public synchronized void solveBoard(String buttonPressed) {
		if (!Board.getBoard().isEmpty()) {
			solver = Solver.getSolver();
			solver.setSolveInProgress(true);
			Thread thread = new Thread(solver);
			thread.start();
		} else {
			SudokuStatusLabel.getStatusBar().setMessage("Use Board->Generate to Generate a Board From Scratch");
		}
	}

}
