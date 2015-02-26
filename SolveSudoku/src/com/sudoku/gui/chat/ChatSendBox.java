package com.sudoku.gui.chat;

import java.awt.Color;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.Socket;

import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.sudoku.gui.eventHandler.SudokuEventHandler;

public class ChatSendBox extends JTextField {
	
	private static final long serialVersionUID = 1L;
	static final Logger logger = Logger.getLogger(com.sudoku.gui.chat.ChatSendBox.class);
	private static Socket currentSocket = null;
	private static PrintWriter out = null;

	public ChatSendBox() {
		this.setForeground(Color.blue);
		this.setText("Type here");
		initializeListeners();
	}
	
	/**
	 * Initializes Action Listener and Item Listener
	 * for buttons and checkboxes on window.
	 */
	private void initializeListeners() {
		Class<?> clazz = this.getClass();
		Class<?>[] paramTypes = {String.class};
		String remoteSendTextMethod = "sendMessage";

		try {
			Method sendTextMethod = clazz.getMethod(remoteSendTextMethod, paramTypes);
			this.addActionListener(new SudokuEventHandler(sendTextMethod, this));			
		} catch (Exception e) {
		logger.error("Could not find the "+remoteSendTextMethod+" Method Exiting...");
			System.exit(-1);
		}
	}
	
	/**
	 *  Method called by CustomActionLister when Solve Button is pressed by User
	 */
	public synchronized void sendMessage(String buttonPressed) {
		logger.debug("Sending Message " + this.getText());
		out.println("TALK [" + this.getText()+"]");
		out.flush();
		
		this.setText("");
	}
	
	/**
	 * @param void Function to set the socket for communications
	 */
    public static void setsocket(Socket socketId) {
		try {
			currentSocket = socketId;
			out = new PrintWriter(new OutputStreamWriter(currentSocket.getOutputStream()));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}
	
	
}
