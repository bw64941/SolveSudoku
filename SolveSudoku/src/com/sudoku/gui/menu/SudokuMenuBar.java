package com.sudoku.gui.menu;

import javax.swing.JMenuBar;

import org.apache.log4j.Logger;

public class SudokuMenuBar extends JMenuBar {
	
	static final Logger logger = Logger.getLogger(com.sudoku.gui.menu.SudokuMenuBar.class);
	private static final long serialVersionUID = 1L;
	
	/**
	 * SudokuMenuBar Constructor
	 */
	public SudokuMenuBar() {
		this.add(new BoardMenu());
		this.add(new ToolMenu());
		this.add(new HelpMenu());
	}
}
