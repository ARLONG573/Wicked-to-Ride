package state;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import data.DestinationTicket;
import mcts.api.GameState;
import scoring.Scoring;

public class TicketToRideState implements GameState {

	private final Player[] players;
	private final ColorDeck colorDeck;
	private final DestinationTicketDeck destinationTicketDeck;
	private final Board board;
	private final long longestRoutePoints;
	private final long globetrotterPoints;

	private int lastPlayerIndex;
	private int currentPlayerIndex;
	private boolean isGameOver;

	public TicketToRideState(final int numPlayers, final int aiPlayerIndex, final long numCarsPerPlayer,
			final ColorDeck colorDeck, final DestinationTicketDeck destinationTicketDeck, final Board board,
			final long longestRoutePoints, final long globetrotterPoints) {

		this.players = new Player[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			this.players[i] = new Player(numCarsPerPlayer);
		}

		this.colorDeck = colorDeck;
		this.destinationTicketDeck = destinationTicketDeck;
		this.board = board;
		this.longestRoutePoints = longestRoutePoints;
		this.globetrotterPoints = globetrotterPoints;

		this.lastPlayerIndex = -1;

		// This makes sure that the AI knows to pick destination tickets at the start of
		// the game
		this.currentPlayerIndex = aiPlayerIndex;

		this.isGameOver = false;
	}

	public void dealStartingHands(final int aiPlayerIndex, final Scanner in) {
		// Color cards
		for (final Player player : this.players) {
			this.colorDeck.dealStartingFourToPlayer(player);
		}
		this.colorDeck.dealStartingFiveFaceUp(in);

		// Destination tickets
		for (final Player player : this.players) {
			this.destinationTicketDeck.dealStartingThreeToPlayer(player);
		}

		this.resolveUnknownsForPlayerManually(aiPlayerIndex, in);
	}

	public void resolveUnknownsForPlayerManually(final int playerIndex, final Scanner in) {
		final Player player = this.players[playerIndex];

		// Color cards
		final int numUnknownColorCards = player.getNumUnknownColorCards();
		for (int i = 1; i <= numUnknownColorCards; i++) {
			System.out.print("Drawn color card " + i + ": ");
			final String color = in.next().toUpperCase();
			in.nextLine(); // consume new line

			player.convertUnknownColorCardToKnownManually(color, this.colorDeck);
		}

		// Destination tickets
		final int numUnknownDestinationTickets = player.getNumUnknownDestinationTickets();
		for (int i = 1; i <= numUnknownDestinationTickets; i++) {
			System.out.println("Ticket " + i + ": ");
			System.out.print("Start: ");
			final String start = in.nextLine().toUpperCase();
			System.out.print("End: ");
			final String end = in.nextLine().toUpperCase();
			final DestinationTicket ticket = this.destinationTicketDeck.getTicket(start, end);

			player.convertUnknownDestinationTicketToKnownManually(ticket, this.destinationTicketDeck);
		}
	}

	public void printPlayerInfo(final int playerIndex) {
		final Player player = this.players[playerIndex];

		System.out.println();
		System.out.println("Player " + playerIndex + ":");
		System.out.println("Known color cards = " + player.getKnownColorCards());
		System.out.println("Unknown color cards = " + player.getNumUnknownColorCards());
		System.out.println("Face up color cards = " + this.colorDeck.getFaceUp());
		System.out.println("Discard pile = " + this.colorDeck.getDiscard());

		System.out.println();
		final List<DestinationTicket> tickets = player.getKnownDestinationTickets();
		System.out.println("Known destination tickets = ");
		for (final DestinationTicket ticket : tickets) {
			System.out.println(ticket.getStart() + " - " + ticket.getEnd());
		}
		System.out.println();

		System.out.println("Unknown destination tickets = " + player.getNumUnknownDestinationTickets());

		System.out.println();
		final Set<DestinationTicket> ticketsDiscard = this.destinationTicketDeck.getKnownDiscards();
		System.out.println("Known destination ticket discards = ");
		for (final DestinationTicket ticket : ticketsDiscard) {
			System.out.println(ticket.getStart() + " " + ticket.getEnd());
		}
		System.out.println();

		System.out.println();
		final List<Board.Connection> connections = this.board.getConnectionsForPlayer(playerIndex);
		System.out.println("Connections = ");
		for (final Board.Connection connection : connections) {
			System.out.println(connection.getStart() + " - " + connection.getEnd());
		}
		System.out.println();

		System.out.println("Score = " + player.getScore());
		System.out.println();
	}

	public void getNumDestinationTicketsForHumanPlayers(final int aiPlayer, final Scanner in) {
		for (int i = 0; i < this.players.length; i++) {
			if (i != aiPlayer) {
				System.out.print("How many destination tickets did player " + i + " keep?: ");

				final int numTicketsKept = in.nextInt();
				in.nextLine(); // consume new line

				this.players[i].setNumUnknownDestinationTickets(numTicketsKept);
			}
		}
	}

	public int getCurrentPlayer() {
		return this.currentPlayerIndex;
	}

	public boolean isGameOver() {
		return this.isGameOver;
	}

	@Override
	public int getLastPlayer() {
		return this.lastPlayerIndex;
	}

	@Override
	public List<GameState> getNextStates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GameState getRandomNextState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getWinningPlayers() {
		final List<Integer> winningPlayers = new ArrayList<>();

		if (!this.isGameOver) {
			return winningPlayers;
		}

		// Assumes that all players' destination tickets have been revealed
		Scoring.doEndGameScoring(this.players, this.board, this.longestRoutePoints, this.globetrotterPoints);

		for (int i = 0; i < this.players.length; i++) {
			if (winningPlayers.isEmpty()) {
				winningPlayers.add(i);
			} else {
				final int indexOfBestPlayer = winningPlayers.get(0);
				final Player bestPlayer = this.players[indexOfBestPlayer];
				final Player contender = this.players[i];

				if (contender.getScore() > bestPlayer.getScore() || (contender.getScore() == bestPlayer.getScore()
						&& contender.getNumCompletedTickets() > bestPlayer.getNumCompletedTickets())) {
					winningPlayers.clear();
					winningPlayers.add(i);
				} else if (contender.getScore() == bestPlayer.getScore()
						&& contender.getNumCompletedTickets() == bestPlayer.getNumCompletedTickets()) {
					winningPlayers.add(i);
				}
			}
		}

		return winningPlayers;
	}

	public void revealHumanDestinationTickets(final int aiPlayer, final Scanner in) {
		for (int i = 0; i < this.players.length; i++) {
			if (i != aiPlayer) {
				final Player player = this.players[i];
				final int numUnknownDestinationTickets = player.getNumUnknownDestinationTickets();
				for (int j = 1; j <= numUnknownDestinationTickets; j++) {
					System.out.println("Ticket " + j + ": ");
					System.out.print("Start: ");
					final String start = in.nextLine().toUpperCase();
					System.out.print("End: ");
					final String end = in.nextLine().toUpperCase();
					final DestinationTicket ticket = this.destinationTicketDeck.getTicket(start, end);

					player.convertUnknownDestinationTicketToKnownManually(ticket, this.destinationTicketDeck);
				}
			}
		}
	}
}
