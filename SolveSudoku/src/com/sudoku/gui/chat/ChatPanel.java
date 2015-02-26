package com.sudoku.gui.chat;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

public class ChatPanel extends JPanel {
	
	static final Logger logger = Logger.getLogger(com.sudoku.gui.chat.ChatPanel.class);
	private static final long serialVersionUID = 1L;
	private JScrollPane scrollPanel = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

	public ChatPanel() {
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Sudoku Talk"),BorderFactory.createRaisedBevelBorder()));
		scrollPanel.setViewportView(ChatReceiveBox.getChatReceiveBox());
		this.add(scrollPanel,BorderLayout.CENTER);
		this.add(new ChatSendBox(),BorderLayout.SOUTH);
	}
}
