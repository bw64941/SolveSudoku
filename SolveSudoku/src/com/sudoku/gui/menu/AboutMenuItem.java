package com.sudoku.gui.menu;

import java.awt.Event;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import com.sudoku.gui.eventHandler.SudokuEventHandler;
import com.sudoku.gui.images.ImageHandler;

public class AboutMenuItem extends JMenuItem {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.menu.AboutMenuItem.class);
	private static final long serialVersionUID = 1L;
	
	public AboutMenuItem() {
		this.setText("About");
		this.setToolTipText("Show information this this application");
		this.setActionCommand(this.getText().toUpperCase());
		this.setMnemonic(KeyEvent.VK_A); // Allows the user to press 'A' when inside the File menu to show the application information
		this.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.CTRL_MASK)); //Allows the user to press Ctrl + 'A' to show the application information			
		initializeListeners();
	}
	
	/**
	 * Initializes Action Listener and Item Listener
	 * for buttons and checkboxes on window.
	 */
	private void initializeListeners() {
		Class<?> clazz = this.getClass();
		Class<?>[] paramTypes = {String.class};
		String remoteAboutInfoMethod = "aboutInfo";
		
		try {
			Method applicationInfoMethod = clazz.getMethod(remoteAboutInfoMethod, paramTypes);
			this.addActionListener(new SudokuEventHandler(applicationInfoMethod, this));
		} catch (Exception e) {
		logger.error("Could not find the "+remoteAboutInfoMethod+" Method Exiting...");
			System.exit(-1);
		}
	}
	
	/**
	 * Display Application information
	 */
	public void aboutInfo(String buttonPressed) {
		JOptionPane.showMessageDialog(null,"Developer:   Brandon S. Winters\n" + 
				"Application Name:  Sudoku Solver\n" + 
				"Version:  1.0\n" + 
				"Date Completed:  December 12, 2008","Sudoku Solver Information",JOptionPane.INFORMATION_MESSAGE,ImageHandler.getImageHandler().getApplicationImage()); 
	}
	

}
