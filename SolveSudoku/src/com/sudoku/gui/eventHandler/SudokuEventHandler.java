package com.sudoku.gui.eventHandler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

public class SudokuEventHandler implements ActionListener {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.eventHandler.SudokuEventHandler.class);
	private Method method;
//	private Object[] methodArguments;
	private Object target;

	/**
	 * SudokuEventHandler Constructor
	 * @param method
	 * @param target
	 */
	public SudokuEventHandler(Method method, Object target) {
		this.method = method;
		this.target = target;
	}
	
	/**
	 * SudokuEventHandler Constructor
	 * @param method
	 * @param target
	 * @param args
	 */
	public SudokuEventHandler(Method method, Object target, Object[] args) {
		this.method = method;
		this.target = target;
//		this.methodArguments = args;
	}

	/**
	 * Action Performed method for action listener.
	 */
	public void actionPerformed(ActionEvent ae) {
		try {
			method.invoke(target, ae.getActionCommand());
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
}
