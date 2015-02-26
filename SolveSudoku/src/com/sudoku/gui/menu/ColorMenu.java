package com.sudoku.gui.menu;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;

import org.apache.log4j.Logger;

public class ColorMenu extends JMenu {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.menu.ColorMenu.class);
	private static final long serialVersionUID = 1L;
	
	public ColorMenu() {
		this.setText("Color");
		this.setMnemonic(KeyEvent.VK_C); //Allows the user to press Ctrl + 'C' to open the color menu
		this.add(new ToggleColoringMenuItem());
		this.add(new ClearColorMenuItem());
	}
	
}
