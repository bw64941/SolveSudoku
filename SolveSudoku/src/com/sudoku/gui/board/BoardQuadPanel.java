package com.sudoku.gui.board;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.apache.log4j.Logger;


public class BoardQuadPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	static final Logger logger = Logger.getLogger(com.sudoku.gui.board.BoardQuadPanel.class);
	
	/**
	 * BoardQuadPanel Constructor
	 */
	public BoardQuadPanel() {
		this.setLayout(new GridLayout((int)Math.sqrt(Board.BOARD_HEIGHT),(int)Math.sqrt(Board.BOARD_WIDTH)));
		this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createMatteBorder(4,4,4,4,Color.blue)));
	}

}
