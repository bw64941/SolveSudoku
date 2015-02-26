package com.sudoku.gui.drag;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.apache.log4j.Logger;

import com.sudoku.gui.images.ImageHandler;
import com.sudoku.gui.possibilities.PossibilityButton;

public class TrashButton extends PossibilityButton {

	private static final long serialVersionUID = 1L;
	static final Logger logger = Logger.getLogger(com.sudoku.gui.drag.TrashButton.class);

	public TrashButton(String text) {
		super(text);
		this.setIcon(ImageHandler.getImageHandler().getTrashCan());
		this.setContentAreaFilled(false);
		this.setToolTipText("Drag to clear a value on the board");
		this.setBorder(null);
		// Specify that the label's text property be transferable; the value of
	    // this property will be used in any drag-and-drop involving this label
		this.setTransferHandler(new TransferHandler("text"));
		this.getDropTarget().setActive(false);
		
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
	            JComponent comp = (JComponent)evt.getSource();
	            TransferHandler th = comp.getTransferHandler();
	    
	            // Start the drag operation
	            th.exportAsDrag(comp, evt, TransferHandler.COPY);
	        }
		});
	}
	
	

}
