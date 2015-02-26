package com.sudoku.gui.board;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.sudoku.cell.Cell;

public class BoardInternalPanel extends JPanel {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.board.BoardInternalPanel.class);
	private static final long serialVersionUID = 1L;
	private ArrayList<BoardQuadPanel> quadPanels = new ArrayList<BoardQuadPanel>();
	private JPanel centerBoardPanel = new JPanel(new GridLayout((int)Math.sqrt(Board.BOARD_HEIGHT),
			(int)Math.sqrt(Board.BOARD_WIDTH),3,3));

	/**
	 * Constructor BoardInternalFrame
	 */
	public BoardInternalPanel() {
		initializeBoard();
		this.setSize(250,250);
		this.setLayout(new BorderLayout());
		this.setVisible(true);
		this.add(centerBoardPanel, BorderLayout.CENTER);
	}
	
	/**
	 * Initializes the board is initial cells (JTextFields)
	 * @param board
	 */
	private void initializeBoard() {
		
		for (int i=1; i<=Board.BOARD_HEIGHT;i++) {
			BoardQuadPanel quad = new BoardQuadPanel();
			quadPanels.add(quad);
		}
		
		for (Cell[] row : Board.getBoard().getCells()) {
			for (Cell cell : row) {			
				quadPanels.get(cell.getQuad()).add(cell);
			}
		}
		
		for (BoardQuadPanel quadrants: quadPanels) {
			centerBoardPanel.add(quadrants);
		}
		
	}
}
