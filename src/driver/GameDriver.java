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

		// Create initial game state
		TicketToRideState gameState = new TicketToRideState(numPlayers, numCarsPerPlayer, colorDeck,
				destinationTicketDeck, board, longestRoutePoints, globetrotterPoints);

		// Game loop
		while (gameState.getWinningPlayers().isEmpty()) {
			// AI turn
			if (gameState.getCurrentPlayer() == aiPlayer) {
				System.out.println("AI is thinking...");
				gameState = (TicketToRideState) MCTS.search(gameState, 30, 1);
				gameState.resolveUnknownsForAI(aiPlayer);
			}
			// Get human turn
			else {

			}
		}

		in.close();
		System.out.println("Winning players: " + gameState.getWinningPlayers());
	}
}
