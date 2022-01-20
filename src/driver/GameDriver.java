package driver;

import java.util.Scanner;

import mcts.MCTS;
import state.Board;
import state.ColorDeck;
import state.DestinationTicketDeck;
import state.TicketToRideState;

public class GameDriver {

	public static void runGame(final int numPlayers, final long numCarsPerPlayer, final ColorDeck colorDeck,
			final DestinationTicketDeck destinationTicketDeck, final Board board, final long longestRoutePoints,
			final long globetrotterPoints) {

		final Scanner in = new Scanner(System.in);

		// Get AI player
		System.out.print("Which player is AI? (0-" + (numPlayers - 1) + "): ");
		final int aiPlayer = in.nextInt();
		in.nextLine(); // consume new line

		// Create initial game state
		TicketToRideState gameState = new TicketToRideState(numPlayers, aiPlayer, numCarsPerPlayer, colorDeck,
				destinationTicketDeck, board, longestRoutePoints, globetrotterPoints);
		gameState.dealStartingHands(aiPlayer, in);

		// AI needs to figure out which destination tickets to keep
		System.out.println("AI is thinking...");
		gameState = (TicketToRideState) MCTS.search(gameState, 30, 1);
		gameState.printPlayerInfo(aiPlayer);
		gameState.getNumDestinationTicketsForHumanPlayers(aiPlayer, in);

		// Game loop
		while (gameState.getWinningPlayers().isEmpty()) {
			// AI turn
			if (gameState.getCurrentPlayer() == aiPlayer) {
				gameState.replenishFaceUp(in);
				System.out.println("AI is thinking...");
				gameState = (TicketToRideState) MCTS.search(gameState, 30, 1);
				gameState.resolveUnknownsForPlayerManually(aiPlayer, in);
				gameState.printPlayerInfo(aiPlayer);

				if (gameState.isGameOver()) {
					gameState.revealHumanDestinationTickets(aiPlayer, in);
				}
			}
			// Get human turn
			else {
				gameState.replenishFaceUp(in);

				System.out.println("Player " + gameState.getCurrentPlayer() + "'s turn!");
				System.out.print("build, color, or ticket? ");
				final String response = in.next();
				in.nextLine(); // consume new line

				// for all of these cases, just assume that it's possible since the onus is on
				// the user to make sure input is valid
				switch (response) {
				case "build":
					break;
				case "color":
					System.out.print("Which color? Or top? ");
					final String color1 = in.next().toUpperCase();
					in.nextLine(); // consume new line
					
					if(color1.equals("TOP")) {
						gameState.giveCurrentHumanPlayerTopColor();
					} else {
						gameState.giveCurrentHumanPlayerFaceUp(color1);
					}
					
					if(!color1.equals("WILD")) {
						System.out.print("Which color? Or top? ");
						final String color2 = in.next().toUpperCase();
						in.nextLine(); //consume new line
						
						if(color2.equals("TOP")) {
							gameState.giveCurrentHumanPlayerTopColor();
						} else {
							gameState.giveCurrentHumanPlayerFaceUp(color2);
						}
					}
					
					gameState.setGameOver(gameState.getCurrentPlayerCarsRemaining() < 3);
					gameState.setLastPlayer(gameState.getCurrentPlayer());
					gameState.setCurrentPlayer(gameState.getNextPlayer());
					break;
				case "ticket":
					System.out.print("How many did they keep? ");
					final int numKept = in.nextInt();
					in.nextLine(); // consume new line

					gameState.drawAndKeepDestinationTicketsForCurrentHuman(numKept);
					break;
				}

				gameState.printPlayerInfo(gameState.getLastPlayer());

				if (gameState.isGameOver()) {
					gameState.revealHumanDestinationTickets(aiPlayer, in);
				}
			}
		}

		in.close();
		System.out.println("Winning players: " + gameState.getWinningPlayers());
	}
}
