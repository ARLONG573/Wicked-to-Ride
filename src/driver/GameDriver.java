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
				gameState = (TicketToRideState) MCTS.search(gameState, 15, 1);
				gameState.resolveUnknownsForPlayerManually(aiPlayer, in);
				gameState.printPlayerInfo(aiPlayer);

				if (gameState.isGameOver()) {
					gameState.revealHumanDestinationTickets(aiPlayer, in);
				}
			}
			// Get human turn
			else {
				// TODO
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
