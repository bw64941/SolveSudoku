package com.sudoku.gui.solve;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.apache.log4j.Logger;


public class SolverPanel extends JPanel {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.solve.SolverPanel.class);
	private static final long serialVersionUID = 1L;
	
	/**
	 * SolverPanel Constructor
	 */
	public SolverPanel() {
		this.setLayout(new GridLayout(1,2,5,5));
		this.setBorder(BorderFactory.createLoweredBevelBorder());
		this.setBackground(Color.LIGHT_GRAY);
		this.add(new ClearButton());
		this.add(new SolverButton());
	}
}
