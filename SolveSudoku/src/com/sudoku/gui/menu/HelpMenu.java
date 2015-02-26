package com.sudoku.gui.menu;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;

import org.apache.log4j.Logger;

public class HelpMenu extends JMenu {
	
	static final Logger logger = Logger.getLogger(com.sudoku.gui.menu.HelpMenu.class);
	private static final long serialVersionUID = 1L;
	
	public HelpMenu() {
		this.setText("Help");
		this.setMnemonic(KeyEvent.VK_H); //Allows the user to press Ctrl + 'H' to open the help menu
		this.add(new AboutMenuItem());
	}

}
