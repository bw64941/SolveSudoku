package com.sudoku.gui.board;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

public class BoardOuterPanel extends JPanel {
	
	static final Logger logger = Logger.getLogger(com.sudoku.gui.board.BoardOuterPanel.class);
	private static final long serialVersionUID = 1L;
	private BoardInternalPanel boardInternalFrame = new BoardInternalPanel();

	/**
	 * Constructor BoardDesktopPane
	 */
	public BoardOuterPanel() {
		this.add(boardInternalFrame);
	}

}
