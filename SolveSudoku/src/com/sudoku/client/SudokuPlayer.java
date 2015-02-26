package com.sudoku.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

public class SudokuPlayer extends Thread {
	
	static final Logger logger = Logger.getLogger(com.sudoku.client.SudokuPlayer.class);
	protected Socket incoming1, incoming2;
	protected int id;
	protected BufferedReader in;
	protected PrintWriter out;

	public SudokuPlayer(Socket incoming1, Socket incoming2, int id) {
		this.incoming1 = incoming1;
		this.incoming2 = incoming2;
		this.id = id;

		try {

			if (incoming1 != null) {
				in = new BufferedReader(new InputStreamReader(incoming1.getInputStream()));
				out = new PrintWriter(new OutputStreamWriter(incoming2.getOutputStream()));
			}

		} catch (Exception e) {
			System.out.println("Error: Player " + e);
		}
	}

	/**
	 @param void
	 Function to run the threads
	 */
	public void run() {

		if (in != null && out != null) {
			
			try {
				out.println(id);
				out.flush();

				for (;;) {
					String str = in.readLine();
					logger.debug("Received message in SudokuPlayer " + str);
					
					out.println(str);
					out.flush();
				}			
			} catch (IOException e) {
				System.err.println("BROKEN IN SERVER");
			}
		}

	}
}
