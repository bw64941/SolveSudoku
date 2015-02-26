package com.sudoku.gui.drag;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

import org.apache.log4j.Logger;

import com.sudoku.gui.board.Board;
import com.sudoku.gui.possibilities.PossibilityButton;

public class DragPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	static final Logger logger = Logger.getLogger(com.sudoku.gui.drag.DragPanel.class);
	private GridBagLayout grid = new GridBagLayout();
	private GridBagConstraints c = new GridBagConstraints();
	private ButtonGroup possibilitiesButtonGroup = new ButtonGroup();

	/**
	 * ColoringPanel Constructor
	 */
	public DragPanel() {
		this.setLayout(grid);
		this.setBackground(Color.lightGray);
		this.setBorder(BorderFactory.createLoweredBevelBorder());
		initializeButtonGroup();
		initializePanel();
	}
	
	/**
	 * Initializes the buttons on the value options panel
	 */
	private void initializeButtonGroup() {
		for (int i = 1; i <= Board.BOARD_HEIGHT; i++) {
			PossibilityButton possButton = new PossibilityButton(i+"");
			//possButton.setFocusPainted(false);
			
			// Specify that the label's text property be transferable; the value of
		    // this property will be used in any drag-and-drop involving this label
			possButton.setTransferHandler(new TransferHandler("text"));
			possButton.getDropTarget().setActive(false);
			
			possButton.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent evt) {
		            JComponent comp = (JComponent)evt.getSource();
		            TransferHandler th = comp.getTransferHandler();
		    
		            // Start the drag operation
		            th.exportAsDrag(comp, evt, TransferHandler.COPY);
		        }
			});

			possibilitiesButtonGroup.add(possButton);
		}
		
	}
	
	/**
	 * Returns the panel containing the value buttons
	 * @return
	 */
	private void initializePanel() {					
		int Ycount = 0;
		
		Enumeration<AbstractButton> possibilityButton = possibilitiesButtonGroup.getElements();
		while (possibilityButton.hasMoreElements()) {			
			c.fill = GridBagConstraints.VERTICAL;
			c.gridx = 0;
			c.ipady = 7;
			c.ipadx = 0;
			c.gridy = Ycount++;
			c.insets = new Insets(2,0,2,0);  //top padding
			this.add(possibilityButton.nextElement(),c);
		}
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 0;
		c.ipady = 7;
		c.ipadx = 0;
		c.gridy = Ycount++;
		c.insets = new Insets(2,1,10,1);  //top padding
		JPanel panel =new JPanel();
		panel.add(new TrashButton(""));
		panel.setBackground(Color.lightGray);
		panel.setBorder(BorderFactory.createLoweredBevelBorder());
		this.add(panel,c);
	}
}
