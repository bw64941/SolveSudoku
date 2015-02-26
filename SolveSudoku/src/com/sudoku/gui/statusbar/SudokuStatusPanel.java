package com.sudoku.gui.statusbar;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.sudoku.gui.checkSolution.CheckSolutionPanel;

public class SudokuStatusPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	static final Logger logger = Logger.getLogger(com.sudoku.gui.statusbar.SudokuStatusPanel.class);
	
	public SudokuStatusPanel() {
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createLoweredBevelBorder());
		this.add(SudokuStatusLabel.getStatusBar(), BorderLayout.CENTER);
		this.add(new CheckSolutionPanel(), BorderLayout.EAST);
	}

}
