package com.sudoku.gui.chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JTextArea;

import org.apache.log4j.Logger;


public class ChatReceiveBox extends JTextArea {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.chat.ChatReceiveBox.class);
	private static final long serialVersionUID = 1L;
	private static ChatReceiveBox chatReceiveBox = null;

		
	/**
	 * Chat Receive Box Constructor
	 */
	private ChatReceiveBox() {
		this.setLayout(new BorderLayout());
		this.setFont(new Font("Arial", Font.PLAIN, 12));
		this.setForeground(Color.blue);
		this.setToolTipText("Chat with your opponent");
		this.setEnabled(false);
	}

	/**
	 * @param chatReceiveBox the chatReceiveBox to set
	 */
	public static void setChatReceiveBox(ChatReceiveBox chatReceiveBox) {
		ChatReceiveBox.chatReceiveBox = chatReceiveBox;
	}


	/**
	 * @return the chatReceiveBox
	 */
	public static ChatReceiveBox getChatReceiveBox() {
		if (chatReceiveBox==null) {
			chatReceiveBox=new ChatReceiveBox();
		}
		return chatReceiveBox;
	}
	
	/**
	 * Set the text in the receive box.
	 */
	public void setMessage(String message) {
		chatReceiveBox.append(message);
	}
}
