package com.sudoku.gui.menu;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;

import org.apache.log4j.Logger;

public class BoardMenu extends JMenu {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.menu.BoardMenu.class);
	private static final long serialVersionUID = 1L;
	
	public BoardMenu() {
		this.setText("Board");
		this.setMnemonic(KeyEvent.VK_B); //Allows the user to press Ctrl + 'B' to open the board menu
		this.add(new GenerateBoardMenuItem());
		this.add(new BoardStatisticsMenuItem());
		this.addSeparator();
		this.add(new OpenBoardMenuItem());
		this.add(new SaveBoardMenuItem());
		this.add(new DeleteBoardMenuItem());
		this.addSeparator();
		this.add(new ClearBoardMenuItem());
		this.add(new QuitMenuItem());
	}
}
