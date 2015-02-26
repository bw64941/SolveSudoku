package com.sudoku.gui.menu;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;

import org.apache.log4j.Logger;

public class ToolMenu extends JMenu {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.menu.ToolMenu.class);
	private static final long serialVersionUID = 1L;
	
	public ToolMenu() {
		this.setText("Tools");
		this.setMnemonic(KeyEvent.VK_T); //Allows the user to press Ctrl + 'T' to open the tools menu
		this.add(new ColorMenu());
	}
}
