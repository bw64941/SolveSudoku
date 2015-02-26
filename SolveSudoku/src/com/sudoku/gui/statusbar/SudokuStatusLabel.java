package com.sudoku.gui.statusbar;

import java.awt.Color;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

public final class SudokuStatusLabel extends JLabel implements Runnable {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.statusbar.SudokuStatusLabel.class);
	private static Thread internalThread;
	private static boolean noStopRequested;
	private static final long serialVersionUID = 1L;
	private static String message;
	private static SudokuStatusLabel statusBar = null;
	private static int SLEEP_ITERATIONS_BEFORE_CLEAR = 50;
	private static int SLEEP_TIME = 2000;

	/** Creates a new singleton instance of StatusBar */
	private SudokuStatusLabel() {
		super();
		super.setPreferredSize(new Dimension(100, 16));
		super.setForeground(Color.BLUE);

		noStopRequested = true;
		internalThread = new Thread(this, "SudokuStatusLabel");
		internalThread.start();
	}

	/**
	 * Returns the Sudoku Status Bar
	 * @return
	 */
	public static synchronized SudokuStatusLabel getStatusBar() {
		if (statusBar == null) {
			statusBar = new SudokuStatusLabel();
		}
		return statusBar;
	}

	/**
	 * Set the current message to be displayed.
	 * @param message
	 */
	public final void setMessage(final String message) {
		SudokuStatusLabel.message = message;
	}

	/**
	 * Clear the current message
	 */
	public void clearMessage() {
		this.setText("");
	}

	/**
	 * @return the current message
	 */
	public String getMessage() {
		return message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Runnable updateText = new Runnable() {
			public void run() {
				setText(getMessage());
			}
		};

		int sleepClearNumber=0;
		while (noStopRequested) {
			try {
				Thread.sleep(SudokuStatusLabel.SLEEP_TIME);
				SwingUtilities.invokeAndWait(updateText);
				this.repaint();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			sleepClearNumber++;
			//Clear the statusbar after 20 seconds
			if (sleepClearNumber > SudokuStatusLabel.SLEEP_ITERATIONS_BEFORE_CLEAR) {
				setMessage("Ready");
			}
		}
	}
}
