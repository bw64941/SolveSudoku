package com.sudoku.gui.board;

import org.apache.log4j.Logger;


public class PreDefinedBoard {
	
	static final Logger logger = Logger.getLogger(com.sudoku.gui.board.PreDefinedBoard.class);
	
	// Easy Board
	private int[][] easyBoard = {  		{ 9, 0, 0, 0, 0, 2, 0, 0, 3 },
										{ 0, 1, 3, 6, 0, 0, 0, 2, 0 }, 
										{ 0, 7, 0, 0, 1, 5, 0, 0, 9 },
										{ 0, 0, 0, 0, 0, 6, 4, 0, 8 }, 
										{ 0, 0, 8, 7, 0, 4, 3, 0, 0 },
										{ 6, 0, 5, 1, 0, 0, 0, 0, 0 }, 
										{ 4, 0, 0, 9, 5, 0, 0, 7, 0 },
										{ 0, 2, 0, 0, 0, 7, 5, 8, 0 }, 
										{ 7, 0, 0, 2, 0, 0, 0, 0, 4 } };

	// Medium Board
	private int[][] mediumBoard = {		{ 1, 0, 9, 0, 0, 8, 0, 0, 6 },
										{ 0, 0, 0, 0, 0, 3, 0, 0, 2 }, 
										{ 0, 0, 0, 0, 2, 0, 0, 0, 0 },
										{ 0, 5, 3, 7, 0, 0, 0, 6, 0 }, 
										{ 0, 0, 0, 0, 0, 0, 2, 0, 0 },
										{ 0, 9, 0, 0, 5, 0, 1, 0, 0 }, 
										{ 0, 0, 7, 3, 0, 4, 9, 0, 0 },
										{ 0, 0, 0, 0, 9, 0, 6, 7, 0 }, 
										{ 0, 1, 0, 0, 8, 7, 0, 3, 0 } };

	// Medium Board2
	private int[][] mediumBoard2 = {	{ 9, 0, 0, 0, 0, 0, 4, 8, 6 },
										{ 0, 6, 0, 8, 9, 0, 1, 5, 2 },
										{ 2, 0, 0, 5, 0, 6, 0, 0, 0 },
										{ 0, 1, 6, 0, 3, 5, 0, 4, 0 },
										{ 0, 2, 0, 0, 0, 0, 0, 6, 0 },
										{ 0, 9, 0, 6, 8, 0, 2, 3, 0 },
										{ 0, 0, 0, 9, 0, 8, 0, 0, 4 },
										{ 0, 0, 2, 0, 5, 0, 0, 0, 0 },
										{ 6, 4, 9, 0, 0, 0, 0, 0, 8 } };
	
	// Hard Board
	private int[][] hardBoard = { 		{ 0, 0, 0, 0, 0, 0, 0, 5, 6 },
									   	{ 1, 0, 0, 0, 2, 0, 0, 0, 3 },
									   	{ 8, 0, 0, 1, 0, 0, 0, 7, 0 },
									   	{ 0, 0, 0, 9, 0, 0, 2, 0, 0 },
									   	{ 0, 7, 6, 5, 0, 0, 0, 0, 0 },
									   	{ 0, 0, 4, 7, 0, 0, 0, 0, 8 },
									   	{ 0, 0, 0, 0, 0, 0, 0, 0, 1 },
									   	{ 0, 4, 1, 0, 0, 0, 0, 0, 0 },
									   	{ 0, 5, 0, 0, 3, 0, 9, 0, 0 } };
	
//	 Hard Board2
	private int[][] hardBoard2 = {		{ 8, 0, 0, 6, 0, 0, 5, 0, 0 },
										{ 7, 0, 0, 9, 0, 0, 0, 4, 0 },
										{ 0, 0, 0, 8, 0, 0, 3, 0, 0 },
										{ 0, 0, 6, 1, 0, 0, 0, 2, 0 },
										{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
										{ 0, 5, 0, 0, 0, 7, 8, 0, 0 },
										{ 0, 0, 3, 0, 0, 6, 0, 0, 0 },
										{ 0, 2, 0, 0, 0, 5, 0, 0, 9 },
										{ 0, 0, 1, 0, 0, 4, 0, 0, 7 } };

	// Evil Board	
	private int[][] evilBoard = { 		{ 8, 0, 0, 0, 9, 0, 0, 0, 2 },
	   									{ 0, 0, 6, 0, 8, 7, 4, 0, 0 },
	   									{ 0, 3, 0, 0, 0, 0, 0, 1, 0 },
	   									{ 0, 1, 0, 0, 0, 0, 0, 0, 0 },
	   									{ 9, 7, 0, 0, 2, 0, 0, 5, 8 },
	   									{ 0, 0, 0, 0, 0, 0, 0, 6, 0 },
	   									{ 0, 5, 0, 0, 0, 0, 0, 2, 0 },
	   									{ 0, 0, 3, 8, 5, 0, 7, 0, 0 },
	   									{ 7, 0, 0, 0, 4, 0, 0, 0, 6 } };
	// Don't Know Board	
	private int[][] dontKnow = { 		{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
	   									{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
	   									{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
	   									{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
	   									{ 0, 0, 0, 0, 2, 0, 0, 0, 0 },
	   									{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
	   									{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
	   									{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
	   									{ 8, 0, 0, 0, 0, 0, 0, 0, 0 } };
	
	// Fiendish Board
	private int[][] fiendishBoard = { 	{ 2, 0, 0, 0, 6, 0, 0, 0, 1 },
	   									{ 0, 0, 3, 0, 9, 8, 2, 0, 0 },
	   									{ 0, 7, 0, 0, 0, 0, 0, 9, 0 },
	   									{ 0, 2, 0, 0, 0, 0, 0, 0, 0 },
	   									{ 6, 4, 0, 0, 2, 0, 0, 5, 3 },
	   									{ 0, 0, 0, 0, 0, 0, 0, 4, 0 },
	   									{ 0, 9, 0, 0, 0, 0, 0, 3, 0 },
	   									{ 0, 0, 5, 6, 1, 0, 7, 0, 0 },
	   									{ 7, 0, 0, 0, 4, 0, 0, 0, 8 } };
	
	// Hardest Board - This board cuases a guess to occur.
	private int[][] insane = {			{ 0, 0, 1, 0, 8, 0, 6, 0, 4 },
										{ 0, 3, 7, 6, 0, 0, 0, 0, 0 },  
										{ 5, 0, 0, 0, 0, 0, 0, 0, 0 },  
										{ 0, 0, 0, 0, 0, 5, 0, 0, 0 },  
										{ 0, 0, 6, 0, 1, 0, 8, 0, 0 },  
										{ 0, 0, 0, 4, 0, 0, 0, 0, 0 },  
										{ 0, 0, 0, 0, 0, 0, 0, 0, 3 }, 
										{ 0, 0, 0, 0, 0, 7, 5, 2, 0 },  
										{ 8, 0, 2, 0, 9, 0, 7, 0, 0 } };
	
	// US Today Board 12/15/2008
	private int[][] usToday = {			{ 6, 0, 8, 3, 7, 2, 0, 5, 4 },
										{ 0, 0, 9, 5, 0, 8, 0, 0, 0 },  
										{ 0, 0, 0, 1, 6, 0, 8, 7, 2 },  
										{ 0, 0, 1, 0, 0, 4, 3, 2, 0 },  
										{ 2, 0, 0, 9, 0, 6, 0, 0, 8 },  
										{ 0, 6, 4, 2, 0, 0, 7, 0, 0 },  
										{ 1, 8, 2, 0, 9, 7, 0, 0, 0 }, 
										{ 0, 0, 0, 4, 0, 5, 6, 0, 0 },  
										{ 5, 4, 0, 8, 3, 1, 2, 0, 7 } };
	
	/**
	 * PreDefinedBoard Constructor
	 */
	public PreDefinedBoard() {
	}
	
	/**
	 * Returns the Easy Board.
	 * @return
	 */
	public int[][] getEasyBoard() {
		return easyBoard;
	}
	
	/**
	 * Sets the Easy Board.
	 * @param easyBoard
	 */
	public void setEasyBoard(int[][] easyBoard) {
		this.easyBoard = easyBoard;
	}
	
	/**
	 * Returns the Medium Board.
	 * @return
	 */
	public int[][] getMediumBoard() {
		return mediumBoard;
	}
	
	/**
	 * Sets the Medium Board.
	 * @param mediumBoard
	 */
	public void setMediumBoard(int[][] mediumBoard) {
		this.mediumBoard = mediumBoard;
	}
	
	/**
	 * Set the Medium Board 2.
	 * @param medium2Board
	 */
	public void setMediumBoard2(int[][] mediumBoard2) {
		this.mediumBoard2 = mediumBoard2;
	}
	
	/**
	 * Returns the Medium Board 2.
	 * @return the fiendish2Board
	 */
	public int[][] getMediumBoard2() {
		return mediumBoard2;
	}
	
	/**
	 * Gets the Hard Board.
	 * @return
	 */
	public int[][] getHardBoard() {
		return hardBoard;
	}
	
	/**
	 * Sets the Hard Board.
	 * @param hardBoard
	 */
	public void setHardBoard(int[][] hardBoard) {
		this.hardBoard = hardBoard;
	}
	
	/**
	 * Gets the Hard Board 2.
	 * @return
	 */
	public int[][] getHardBoard2() {
		return hardBoard2;
	}
	
	/**
	 * Sets the Hard Board 2.
	 * @param hardBoard2
	 */
	public void setHardBoard2(int[][] hardBoard2) {
		this.hardBoard2 = hardBoard2;
	}
	
	/**
	 * Returns the Evil Board.
	 * @return
	 */
	public int[][] getEvilBoard() {
		return evilBoard;
	}
	
	/**
	 * Sets the Evil Board.
	 * @param evilBoard
	 */
	public void setEvilBoard(int[][] evilBoard) {
		this.evilBoard = evilBoard;
	}
	
	/**
	 * Returns the Fiendish Board.
	 * @return
	 */
	public int[][] getFiendishBoard() {
		return fiendishBoard;
	}
	
	/**
	 * Sets the Fiendish Board.
	 * @param fiendishBoard
	 */
	public void setFiendishBoard(int[][] fiendishBoard) {
		this.fiendishBoard = fiendishBoard;
	}

	/**
	 * Returns the Insane Board
	 * @return
	 */
	public int[][] getInsaneBoard() {
//		insane[1][8]=8;
//		insane[3][1]=8;
//		insane[5][5]=8;
//		insane[6][7]=8;
//		insane[7][3]=8;
//		
//		insane[3][3]=7;
//		insane[4][0]=7;
//		insane[5][8]=7;
//		insane[6][1]=7;
		return insane;
	}

	/**
	 * Sets the Insane Board
	 * @param insane
	 */
	public void setInsane(int[][] insane) {
		this.insane = insane;
	}

	/**
	 * @param usToday the usToday to set
	 */
	public void setUsToday(int[][] usToday) {
		this.usToday = usToday;
	}

	/**
	 * @return the usToday
	 */
	public int[][] getUsToday() {
		return usToday;
	}

	/**
	 * @param dontKnow the dontKnow to set
	 */
	public void setDontKnow(int[][] dontKnow) {
		this.dontKnow = dontKnow;
	}

	/**
	 * @return the dontKnow
	 */
	public int[][] getDontKnow() {
		return dontKnow;
	}
}
