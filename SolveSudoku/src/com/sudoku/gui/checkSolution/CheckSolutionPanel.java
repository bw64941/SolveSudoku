package com.sudoku.gui.checkSolution;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.apache.log4j.Logger;


public class CheckSolutionPanel extends JPanel {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.checkSolution.CheckSolutionPanel.class);
	private static final long serialVersionUID = 1L;

	/**
	 * CheckSolutionPanel Constructor
	 */
	public CheckSolutionPanel() {
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		this.add(new CheckSolutionButton(),BorderLayout.SOUTH);
	}	
}
