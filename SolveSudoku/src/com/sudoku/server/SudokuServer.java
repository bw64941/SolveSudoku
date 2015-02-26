package com.sudoku.server;

import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.sudoku.client.SudokuPlayer;
import com.sudoku.db.DatabaseWorker;

public class SudokuServer {
	
	static final Logger logger = Logger.getLogger(com.sudoku.server.SudokuServer.class);
	
	public static void main(String[] args) {
		System.out.println("Sudoku Sever started.");
		int playerNum = 1;
		SudokuPlayer playEvenHandler = null, playOddHandler = null;
		try {
			ServerSocket s = new ServerSocket(8069);
			for (;;) {
				Socket player_even = s.accept();
				Socket player_odd = s.accept();

				System.out.println("Spawning client thread " + playerNum);
				System.out.println("Spawning client thread " + (playerNum + 1));

				playEvenHandler = new SudokuPlayer(player_even, player_odd, playerNum);
				playOddHandler = new SudokuPlayer(player_odd, player_even, (playerNum + 1));

				playEvenHandler.start();
				playOddHandler.start();

				playerNum += 2;
				DatabaseWorker.getDbWorker();
			}
		} catch (Exception e) {
			System.out.println("Error: " + e);
		}

		System.out.println("Sudoku Server stopped.");
		playEvenHandler = null;
		playOddHandler = null;
	}

}
