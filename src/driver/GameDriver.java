package driver;

import java.util.Comparator;
import java.util.Scanner;
import java.util.TreeSet;

import data.DestinationTicket;
import mcts.MCTS;
import state.Board;
import state.ColorDeck;
import state.DestinationTicketDeck;
import state.Player;
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
					System.out.println("List of available connections: ");
					final TreeSet<Board.Connection> sorted = new TreeSet<>(new Comparator<Board.Connection>() {

						@Override
						public int compare(final Board.Connection c1, final Board.Connection c2) {
							if (c1.getLength() < c2.getLength())
								return -1;
							if (c1.getLength() > c2.getLength())
								return 1;

							final int colorComp = c1.getColor().compareTo(c2.getColor());
							if (colorComp != 0)
								return colorComp;

							final int startComp = c1.getStart().compareTo(c2.getStart());
							if (startComp != 0)
								return startComp;

							return c1.getEnd().compareTo(c2.getEnd());
						}

					});
					for (final Board.Connection connection : gameState.getBoard()
							.getPossibleConnectionsForOwner(gameState.getCurrentPlayer())) {
						sorted.add(connection);
					}
					for (final Board.Connection connection : sorted) {
						System.out.println(connection.getLength() + " " + connection.getColor() + " "
								+ connection.getStart() + " - " + connection.getEnd());
					}
					System.out.println();

					System.out.print("Start: ");
					final String start = in.nextLine().toUpperCase();
					System.out.print("End: ");
					final String end = in.nextLine().toUpperCase();
					System.out.print("Color: ");
					final String color = in.next().toUpperCase();
					in.nextLine(); // consume new line
					System.out.print("Length: ");
					final long length = in.nextLong();
					in.nextLine(); // consume new line
					System.out.print("How many wilds used? ");
					final long wilds = in.nextLong();
					in.nextLine();// consume new line

					for (final Board.Connection connection : sorted) {
						if (connection.getStart().equals(start) && connection.getEnd().equals(end)
								&& connection.getColor().equals(color) && connection.getLength() == length) {

							String optColor = connection.getColor();

							if (optColor.equals("GRAY")) {
								System.out.print("What color used to pay? ");
								optColor = in.next().toUpperCase();
								in.nextLine(); // consume new line
							}
							gameState.buildConnectionForCurrentHumanPlayer(connection, optColor, wilds);
						}
					}
					break;
				case "color":
					System.out.print("Which color? Or top? ");
					final String color1 = in.next().toUpperCase();
					in.nextLine(); // consume new line

					if (color1.equals("TOP")) {
						gameState.giveCurrentHumanPlayerTopColor();
					} else {
						gameState.giveCurrentHumanPlayerFaceUp(color1);
					}

					gameState.replenishFaceUp(in);

					if (!color1.equals("WILD")) {
						System.out.print("Which color? Or top? ");
						final String color2 = in.next().toUpperCase();
						in.nextLine(); // consume new line

						if (color2.equals("TOP")) {
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
		System.out.println("Scores:");
		for (final Player player : gameState.getPlayers()) {
			System.out.println(
					player.getScore() + " with " + player.getNumCompletedTickets() + " completed tickets out of:");
			for (final DestinationTicket ticket : player.getKnownDestinationTickets()) {
				System.out.println(ticket.getStart() + " - " + ticket.getEnd() + " " + ticket.getPoints());
			}
		}
	}
}
