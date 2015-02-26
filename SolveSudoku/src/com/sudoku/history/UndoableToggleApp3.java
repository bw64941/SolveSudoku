package com.sudoku.history;

/*
 Java Swing, 2nd Edition
 By Marc Loy, Robert Eckstein, Dave Wood, James Elliott, Brian Cole
 ISBN: 0-596-00408-7
 Publisher: O'Reilly 
 */
// UndoableToggleApp3.java
//A sample app showing the use of UndoManager.
//
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class UndoableToggleApp3 extends JFrame {

	private static final long serialVersionUID = 1L;

	private UndoManager manager = new UndoManager();

	private JButton undoButton;

	private JButton redoButton;

	// Create the main frame and everything in it.
	public UndoableToggleApp3() {

		// Create some toggle buttons.
		UndoableJToggleButton tog1 = new UndoableJToggleButton("One");
		UndoableJToggleButton tog2 = new UndoableJToggleButton("Two");
		UndoableJToggleButton tog3 = new UndoableJToggleButton("Three");

		// Add our listener to each toggle button.
		SimpleUEListener sl = new SimpleUEListener();
		tog1.addUndoableEditListener(sl);
		tog2.addUndoableEditListener(sl);
		tog3.addUndoableEditListener(sl);

		// Lay out the buttons.
		Box buttonBox = new Box(BoxLayout.Y_AXIS);
		buttonBox.add(tog1);
		buttonBox.add(tog2);
		buttonBox.add(tog3);

		// Create undo and redo buttons (initially disabled).
		undoButton = new JButton("Undo");
		redoButton = new JButton("Redo");
		undoButton.setEnabled(false);
		redoButton.setEnabled(false);

		// Add a listener to the undo button. It attempts to call undo() on the
		// UndoManager, then enables/disables the undo/redo buttons as
		// appropriate.
		undoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				try {
					manager.undo();
				} catch (CannotUndoException ex) {
					ex.printStackTrace();
				} finally {
					updateButtons();
				}
			}
		});

		// Add a redo listener: just like the undo listener.
		redoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				try {
					manager.redo();
				} catch (CannotRedoException ex) {
					ex.printStackTrace();
				} finally {
					updateButtons();
				}
			}
		});

		// Lay out the undo/redo buttons.
		Box undoRedoBox = new Box(BoxLayout.X_AXIS);
		undoRedoBox.add(Box.createGlue());
		undoRedoBox.add(undoButton);
		undoRedoBox.add(Box.createHorizontalStrut(2));
		undoRedoBox.add(redoButton);
		undoRedoBox.add(Box.createGlue());

		// Lay out the main frame.
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buttonBox, BorderLayout.CENTER);
		getContentPane().add(undoRedoBox, BorderLayout.SOUTH);
		setSize(400, 150);
	}

	public class SimpleUEListener implements UndoableEditListener {
		// When an UndoableEditEvent is generated (each time one of the buttons
		// is pressed), we add it to the UndoManager and then get the manager's
		// undo/redo names and set the undo/redo button labels. Finally, we
		// enable/disable these buttons by asking the manager what we are
		// allowed to do.
		public void undoableEditHappened(UndoableEditEvent ev) {
			manager.addEdit(ev.getEdit());
			updateButtons();
		}
	}

	// Method to set the text and state of the undo/redo buttons.
	protected void updateButtons() {
		undoButton.setText(manager.getUndoPresentationName());
		redoButton.setText(manager.getRedoPresentationName());
		undoButton.getParent().validate();
		undoButton.setEnabled(manager.canUndo());
		redoButton.setEnabled(manager.canRedo());
	}

	// Main program just creates the frame and displays it.
	public static void main(String[] args) {
		JFrame f = new UndoableToggleApp3();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}

// UndoableJToggleButton.java
// Sample undoable toggle button class. Supports only a single listener to
// simplify the code.
//

class UndoableJToggleButton extends JToggleButton {
	
	private static final long serialVersionUID = 1L;
	private UndoableEditListener listener;

	// For this example, we'll just provide one constructor . . .
	public UndoableJToggleButton(String txt) {
		super(txt);
	}

	// Set the UndoableEditListener.
	public void addUndoableEditListener(UndoableEditListener l) {
		listener = l; // Should ideally throw an exception if listener != null
	}

	// Remove the UndoableEditListener.
	public void removeUndoableEditListener(UndoableEditListener l) {
		listener = null;
	}

	// We override this method to call the super implementation first (to fire
	// the
	// action event) and then fire a new UndoableEditEvent to our listener.
	protected void fireActionPerformed(ActionEvent ev) {

		// Fire the ActionEvent as usual.
		super.fireActionPerformed(ev);

		if (listener != null) {
			listener.undoableEditHappened(new UndoableEditEvent(this,
					new UndoableToggleEdit(this)));
		}
	}
}

// UndoableToggleEdit.java
// An UndoableEdit used to undo the pressing of a JToggleButton.
//

class UndoableToggleEdit extends AbstractUndoableEdit {

	private static final long serialVersionUID = 1L;

	private JToggleButton button;

	private boolean selected;

	// Create a new edit for a JToggleButton that has just been toggled.
	public UndoableToggleEdit(JToggleButton button) {
		this.button = button;
		selected = button.isSelected();
	}

	// Return a reasonable name for this edit.
	public String getPresentationName() {
		return "Toggle " + button.getText() + " " + (selected ? "on" : "off");
	}

	// Redo by setting the button state as it was initially.
	public void redo() throws CannotRedoException {
		super.redo();
		button.setSelected(selected);
	}

	// Undo by setting the button state to the opposite value.
	public void undo() throws CannotUndoException {
		super.undo();
		button.setSelected(!selected);
	}
}
