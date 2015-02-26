package com.sudoku.cell;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.TransferHandler;

import org.apache.log4j.Logger;

import com.sudoku.gui.board.Board;
import com.sudoku.solver.Solver;

public class Cell extends JTextField implements MouseListener, Colorable, DropTargetListener {

	static final Logger logger = Logger.getLogger(com.sudoku.cell.Cell.class);
	private static final long serialVersionUID = 1L;
	public static final Font LOCKED_VALUE_FONT = new Font("Lucida Sans Typewriter", Font.BOLD, 14);
	public static final Color LOCKED_VALUE_FOREGROUND_COLOR = Color.blue;
	public static final Color LOCKED_VALUE_BACKGROUND_COLOR = null;
	public static final Color BLANK_CELL_BACKGROUND_COLOR = Color.white;
	private int value=0; 
	private int row=0; 
	private int col=0; 
	private int quad=0;
	private boolean isLocked = false;
	private boolean isEmpty = true;
	private ArrayList<Integer> possibilities = new ArrayList<Integer>(Board.BOARD_HEIGHT);
	@SuppressWarnings("unused")
	private DropTarget dropTarget = null;
	
	/**
	 * Cell Contructor
	 * @param row
	 * @param col
	 */
	public Cell(int row, int col) {
		this.row = row;
		this.col = col;
		this.setFont(Cell.LOCKED_VALUE_FONT);
		this.setForeground(Cell.LOCKED_VALUE_FOREGROUND_COLOR);
		this.setBackground(Cell.BLANK_CELL_BACKGROUND_COLOR);
		this.setHorizontalAlignment(JTextField.CENTER);
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		this.setEditable(false);
		this.setCursor(new Cursor(Cursor.HAND_CURSOR));
		this.addMouseListener(this);
		this.setTransferHandler(new TransferHandler("text"));
		this.setLocked(false);
		this.setEmpty(true);
		assignQuadrant();
		populateInitialPossibilities();
		dropTarget = new DropTarget(this,this);
	}
	
	/**
	 * Assigns quadrant values to the cell
	 */
	public void assignQuadrant() {
		// Quadrant 0
		double squarRootOfBoard = Math.sqrt(Board.BOARD_HEIGHT);
		if (this.row <= (int)squarRootOfBoard-1 && this.col <= (int)squarRootOfBoard-1) {
			this.quad = 0;
		}
		// Quadrant 1
		else if (this.row <= (int)squarRootOfBoard-1 && (col >= (int)squarRootOfBoard && col <= (int)squarRootOfBoard+2)) {
			this.quad = (int)squarRootOfBoard-2;
		}
		// Quadrant 2
		else if (this.row <= (int)squarRootOfBoard-1 && (col >= (int)squarRootOfBoard+3 && col <= (int)squarRootOfBoard+5)) {
			this.quad = (int)squarRootOfBoard-1;
		}
		// Quadrant 3
		else if ((this.row >= (int)squarRootOfBoard && this.row <= (int)squarRootOfBoard+2) && (this.col <= (int)squarRootOfBoard-1)) {
			this.quad = (int)squarRootOfBoard;
		}
		// Quadrant 4
		else if ((this.row >= (int)squarRootOfBoard && this.row <= (int)squarRootOfBoard+2)
				&& (this.col >= (int)squarRootOfBoard && this.col <= (int)squarRootOfBoard+2)) {
			this.quad = (int)squarRootOfBoard+1;
		}
		// Quadrant 5
		else if ((this.row >= (int)squarRootOfBoard && this.row <= (int)squarRootOfBoard+2)
				&& (this.col >= (int)squarRootOfBoard+3 && this.col <= (int)squarRootOfBoard+5)) {
			this.quad = (int)squarRootOfBoard+2;
		}
		// Quadrant 6
		else if ((this.row >= (int)squarRootOfBoard+3 && this.col <= (int)squarRootOfBoard-1)) {
			this.quad = (int)squarRootOfBoard+3;
		}
		// Quadrant 7
		else if ((this.row >= (int)squarRootOfBoard+3) && (this.col >= (int)squarRootOfBoard && this.col <= (int)squarRootOfBoard+2)) {
			this.quad = (int)squarRootOfBoard+4;
		}
		// Quadrant 8
		else if (this.row >= (int)squarRootOfBoard+3 && this.col >= (int)squarRootOfBoard+3) {
			this.quad = (int)squarRootOfBoard+5;
		}
	}

	/**
	 * Initializes possibilities for the cell by starting each cell with 
	 * possibilities 1-9.
	 */
	public void populateInitialPossibilities() {
		for (int i = 1; i < Board.BOARD_HEIGHT+1; i++) {
			if (!this.possibilities.contains(i)) {
				this.possibilities.add(i);
			}
			
		}
	}
	
	/**
	 * Returns whether or not the current cell text is a valid value.
	 * @return
	 */
	public boolean containsValidValue() {	
		if (value >=1 && value<=Board.BOARD_HEIGHT) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Re-Initializes the state of the cell
	 */
	public void clear() {
		this.setFont(Cell.LOCKED_VALUE_FONT);
		this.setHorizontalAlignment(JTextField.CENTER);
		this.setForeground(Cell.LOCKED_VALUE_FOREGROUND_COLOR);
		this.setBackground(Cell.BLANK_CELL_BACKGROUND_COLOR);
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		this.setCursor(new Cursor(Cursor.HAND_CURSOR));
		this.setEditable(false);
		this.setLocked(false);
		this.setEmpty(true);
		this.setValue(0);
	}

	/**
	 * Removes the specified possibility from the cells possible values.
	 * @param notPosibleInt
	 * @param stepSolve
	 */
	public void removePossibility(int notPossibleInt) {
		this.possibilities.remove(new Integer(notPossibleInt));
		CellHistory cellHistory = new CellHistory();
		cellHistory.setActionFlag(0); //remove
		cellHistory.setCell(this);
		cellHistory.setValue(notPossibleInt);
		Solver.getSolveSteps().add(cellHistory);
	}
	
	/**
	 * Removes the specified possibility from the cells possible values.
	 * @param notPosibleInt
	 * @param stepSolve
	 */
	public void removePossibility(int notPossibleInt, String rule) {
		this.possibilities.remove(new Integer(notPossibleInt));
	}

	/**
	 * Adds a possibility to the cells possibility list.
	 * @param index
	 * @param possibility
	 */
	public void addPossibility(int index, int possibility) {
		this.possibilities.add(index, possibility);
	}
	
	/**
	 * Adds a possibility to the cells possibility list.
	 * @param possibility
	 */
	public void addPossibility(int possibility) {
		this.possibilities.add(possibility);
	}
	
	/**
	 * Returns the number of possibilities for the cell.
	 * @return
	 */
	public int getNum0fPossibilitiesLeft() {
		return possibilities.size();
	}

	/**
	 * Returns the cell coordinates for the cell.
	 * @return
	 */
	public String getCoordinates() {
		String coord = "[" + this.quad + "," + this.row + "," + this.col + "]";
		return coord;
	}
	
	/**
	 * Returns the current column for the cell.
	 * @return
	 */
	public int getCol() {
		return col;
	}

	/**
	 * Sets the current column for the cell.
	 * @param col
	 */
	public void setCol(int col) {
		this.col = col;
	}

	/**
	 * Returns the current row for the cell.
	 * @return
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Sets the current row for the cell.
	 * @param row
	 */
	public void setRow(int row) {
		this.row = row;
	}
	
	/**
	 * Returns the current value for the cell.
	 * @return
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Sets the current value for the cell, and based on whether
	 * or nor the step solve functionality is being used, it will
	 * prompt for user confirmation on each call.
	 * @param value
	 * @param stepSolve
	 */
	public synchronized void setValue(int value) {
		if (value>0) {
			this.value = value;
			this.setText(this.value+"");		
			this.setFont(Cell.LOCKED_VALUE_FONT);
			this.setForeground(Cell.LOCKED_VALUE_FOREGROUND_COLOR);
			this.setBorder(BorderFactory.createLineBorder(Color.black));	
			this.setEmpty(false);
			this.setEditable(false);
			this.setLocked(true);
			this.setBackground(Cell.LOCKED_VALUE_BACKGROUND_COLOR);
			this.possibilities.clear();
			
			CellHistory cellHistory = new CellHistory();
			cellHistory.setActionFlag(1); //add
			cellHistory.setCell(this);
			cellHistory.setValue(value);
			Solver.getSolveSteps().add(cellHistory);
		} else if (value==0) {
			this.value = value;
			this.setText("");
			this.possibilities.clear();
			this.populateInitialPossibilities();
		} else if (value<0) {
			logger.error("Assigning cell value to "+value);
		} 
	}

	/**
	 * Returns the current list of possibilities for the cell.
	 * @return
	 */
	public ArrayList<Integer> getPossibilities() {
		return possibilities;
	}

	/**
	 * Sets the current possibilities for the cell.
	 * @param posibleValues
	 */
	public void setPossibilities(ArrayList<Integer> possibleValues) {
		this.possibilities = possibleValues;
	}

	/**
	 * Returns the quadrant value for the cell.
	 * @return
	 */
	public int getQuad() {
		return this.quad;
	}

	/**
	 * Sets the quadrant value for the cell.
	 * @param quad
	 */
	public void setQuad(int quad) {
		this.quad = quad;
	}

	/**
	 * Returns whether or not the cell is locked.
	 * @return
	 */
	public boolean isLocked() {
		return isLocked;
	}

	/**
	 * Sets whether or not the cell is locked.
	 * @param isLocked
	 */
	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	/**
	 * Returns whether or not the cell is empty.
	 * @return
	 */
	public boolean isEmpty() {
		return isEmpty;
	}

	/**
	 * Sets whether or not the cell is empty.
	 * @param isEmpty
	 */
	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

	public void mouseClicked(MouseEvent e) {
		if (Board.getBoard().isColoringEnabled() && !this.isLocked()) {
			Color color = null;
			if (!this.isLocked()) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					color = Color.green;
				} else if (e.getButton() == MouseEvent.BUTTON2) {
					color = Color.white;
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					color = Color.gray;
				}
			}
			changeCellColor(color);
		} 
	}

	public void mouseEntered(MouseEvent e) {
		if (!this.isLocked() && !Board.getBoard().isEmpty()) {
			this.setToolTipText(this.getPossibilities().toString());
		}
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if (!this.isEmpty() && e.getButton() == MouseEvent.BUTTON1) {
			JComponent comp = (JComponent) e.getSource();
			TransferHandler th = comp.getTransferHandler();

			// Start the drag operation
			th.exportAsDrag(comp, e, TransferHandler.COPY);
		}
	}

	public void mouseReleased(MouseEvent e) {
	}


	public void changeCellColor(Color color) {
		this.setBackground(color);		
	}

	public void dragEnter(DropTargetDragEvent dtde) {
		this.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Color.yellow));		
	}
		

	
	public void dragExit(DropTargetEvent dte) {
		this.setBorder(BorderFactory.createLineBorder(Color.black));
	}

	
	public void dragOver(DropTargetDragEvent dtde) {
	}


	public void drop(DropTargetDropEvent dtde) {
			Transferable tr = dtde.getTransferable();
			DataFlavor[] flavors = tr.getTransferDataFlavors();
			
			Object o = null;
			String text = "";
			for (int i = 0; i < flavors.length; i++) {
				try {
					o = tr.getTransferData(flavors[i]);
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			text = (String) o;
			}
			
			dtde.dropComplete(true);
			if (text.equals("")) {
				this.clear();
			} else {
				this.setValue(Integer.parseInt(text));
				Board.getBoard().removePossibilityFromQuad(Integer.parseInt(text),this.quad);
				Board.getBoard().removePossibilityFromRow(Integer.parseInt(text),this.row);
				Board.getBoard().removePossibilityFromCol(Integer.parseInt(text),this.col);
			}
	
	}

	/**
	 * Drop changed listener.
	 */
	public void dropActionChanged(DropTargetDragEvent dtde) {
	}
}
