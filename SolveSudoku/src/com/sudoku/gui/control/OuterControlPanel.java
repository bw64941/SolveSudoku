package com.sudoku.gui.control;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.sudoku.gui.chat.ChatPanel;
import com.sudoku.gui.solve.SolverPanel;

public class OuterControlPanel extends JPanel {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.control.OuterControlPanel.class);
	private static final long serialVersionUID = 1L;
	private static final String BORDER_TITLE = "View Options";

	/**
	 * OuterControlPanel Constructor
	 */
	public OuterControlPanel() {
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(OuterControlPanel.BORDER_TITLE),
				BorderFactory.createLoweredBevelBorder()));
		this.add(new InnerControlPanel(),BorderLayout.NORTH);
		this.add(new ChatPanel(),BorderLayout.CENTER);
		this.add(new SolverPanel(),BorderLayout.SOUTH);
	}

}
