package com.sudoku.gui.control;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.sudoku.gui.possibilities.PossibilitiesPanel;

public class InnerControlPanel extends JPanel {
	static final Logger logger = Logger.getLogger(com.sudoku.gui.control.InnerControlPanel.class);
	private static final long serialVersionUID = 1L;	
	
	/**
	 * InnerControlPanel Constructor
	 */
	public InnerControlPanel() {
		this.setLayout(new BorderLayout());
		this.add(new PossibilitiesPanel(), BorderLayout.SOUTH);
	}
}
