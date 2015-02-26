package com.sudoku.gui.board;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.sudoku.gui.drag.DragPanel;


public class BoardPanel extends JPanel {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.board.BoardPanel.class);
	private static final long serialVersionUID = 1L;
	private BoardInternalPanel boardInternalPanel = new BoardInternalPanel();

	/**
	 * BoardPanel Constructor
	 */
	public BoardPanel() {
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),BorderFactory.createEtchedBorder()));				
		this.add(boardInternalPanel, BorderLayout.CENTER);
		this.add(new DragPanel(), BorderLayout.EAST);
		this.setVisible(true);
	}
}
