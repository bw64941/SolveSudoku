package com.sudoku.gui.possibilities;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.sudoku.gui.board.Board;

public class PossibilitiesPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	static final Logger logger = Logger.getLogger(com.sudoku.gui.possibilities.PossibilitiesPanel.class);
	private GridBagLayout grid = new GridBagLayout();
	private GridBagConstraints c = new GridBagConstraints();
	private ButtonGroup possibilitiesButtonGroup = new ButtonGroup();

	/**
	 * ColoringPanel Constructor
	 */
	public PossibilitiesPanel() {
		this.setLayout(grid);
		this.setBackground(Color.lightGray);
		this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Possibilities"),BorderFactory.createLoweredBevelBorder()));
		initializeButtonGroup();
		initializePanel();
	}
	
	/**
	 * Initializes the buttons on the value options panel
	 */
	private void initializeButtonGroup() {
		for (int i = 1; i <= Board.BOARD_HEIGHT; i++) {
			possibilitiesButtonGroup.add(new PossibilityButton(i+""));
		}
	}
	
	/**
	 * Returns the panel containing the ALL, CLEAR, and CLEAR COLOR buttons
	 * @return
	 */
	private void initializePanel() {					
		int Xcount = 0;
		int Ycount = 0;
		
		Enumeration<AbstractButton> possibilityButton = possibilitiesButtonGroup.getElements();
		while (possibilityButton.hasMoreElements()) {
			if (Xcount == 3) {
				Ycount++;
				Xcount=0;
			}			
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = Xcount;
			c.gridy = Ycount;
			c.ipady = 0;
			c.ipadx = 0;
			c.weightx = 20.5;
			c.insets = new Insets(2,0,2,0); //top,left,bottom,right
			this.add(possibilityButton.nextElement(),c);
			Xcount++;
		}
		Xcount=1;
		Ycount++;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = Xcount;
		c.gridy = Ycount;
		c.insets = new Insets(3,3,3,3);  //top,left,bottom,right
		this.add(new AllPossibilitiesButton(),c);
	}
}
