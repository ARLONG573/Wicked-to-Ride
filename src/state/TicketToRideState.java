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
	private boolean haveInitialTicketsBeenChosen;
	private boolean haveAlreadyTakenColorCard;
	private boolean haveAlreadyDrawnTickets;

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
		this.haveInitialTicketsBeenChosen = false;
		this.haveAlreadyTakenColorCard = false;
		this.haveAlreadyDrawnTickets = false;
	}

	public TicketToRideState(final TicketToRideState state) {
		this.players = new Player[state.players.length];
		for (int i = 0; i < this.players.length; i++) {
			this.players[i] = new Player(state.players[i]);
		}

		this.colorDeck = new ColorDeck(state.colorDeck);
		this.destinationTicketDeck = new DestinationTicketDeck(state.destinationTicketDeck);
		this.board = new Board(state.board);
		this.longestRoutePoints = state.longestRoutePoints;
		this.globetrotterPoints = state.globetrotterPoints;
		this.lastPlayerIndex = state.lastPlayerIndex;
		this.currentPlayerIndex = state.currentPlayerIndex;
		this.isGameOver = state.isGameOver;
		this.haveInitialTicketsBeenChosen = state.haveInitialTicketsBeenChosen;
		this.haveAlreadyTakenColorCard = state.haveAlreadyTakenColorCard;
		this.haveAlreadyDrawnTickets = state.haveAlreadyDrawnTickets;
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
		System.out.println("Cars remaining: " + player.getNumCarsRemaining());
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
			System.out.println(ticket.getStart() + " - " + ticket.getEnd());
		}
		System.out.println();

		System.out.println();
		final List<Board.Connection> connections = this.board.getConnectionsForPlayer(playerIndex);
		System.out.println("Connections = ");
		for (final Board.Connection connection : connections) {
			System.out
					.println(connection.getStart() + " - " + connection.getEnd() + " (" + connection.getColor() + ")");
		}
		System.out.println();
		
		System.out.println();
		final Set<Board.Connection> forbidden = this.board.getForbiddenConnectionsForPlayer(playerIndex);
		System.out.println("Forbidden = ");
		for(final Board.Connection connection : forbidden) {
			System.out.println(connection.getStart() + " - " + connection.getEnd() + " (" + connection.getColor() + ")");
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
				this.destinationTicketDeck.addDiscards(3 - numTicketsKept);
			}
		}
	}

	public int getCurrentPlayer() {
		return this.currentPlayerIndex;
	}

	public boolean isGameOver() {
		return this.isGameOver;
	}

	public void replenishFaceUp(final Scanner in) {
		this.colorDeck.replenishFaceUp(in);
	}

	public int getNextPlayer() {
		return (this.currentPlayerIndex + 1 < this.players.length) ? this.currentPlayerIndex + 1 : 0;
	}

	@Override
	public int getLastPlayer() {
		return this.lastPlayerIndex;
	}

	@Override
	public List<GameState> getNextStates() {
		final List<GameState> nextStates = new ArrayList<>();

		// cannot expand this node if the game is over
		if (this.isGameOver) {
			return nextStates;
		}

		// cannot expand this node if the current player is not the AI (due to
		// incomplete information)
		if (this.players[this.currentPlayerIndex].getNumUnknownDestinationTickets() > 0) {
			return nextStates;
		}

		// this method assumes that all of the AI's unknown information has been filled
		// already

		// if initial tickets have not been chosen yet, the only choice is to pick a
		// combination of 2 or 3 tickets
		if (!this.haveInitialTicketsBeenChosen) {
			final TicketToRideState copy1 = new TicketToRideState(this);
			final TicketToRideState copy2 = new TicketToRideState(this);
			final TicketToRideState copy3 = new TicketToRideState(this);
			final TicketToRideState copy4 = new TicketToRideState(this);

			copy1.players[copy1.currentPlayerIndex].discardKnownTicketAtIndex(0, copy1.destinationTicketDeck);
			copy2.players[copy2.currentPlayerIndex].discardKnownTicketAtIndex(1, copy2.destinationTicketDeck);
			copy3.players[copy3.currentPlayerIndex].discardKnownTicketAtIndex(2, copy3.destinationTicketDeck);

			copy1.lastPlayerIndex = copy1.currentPlayerIndex;
			copy1.currentPlayerIndex = 0;
			copy1.haveInitialTicketsBeenChosen = true;

			copy2.lastPlayerIndex = copy2.currentPlayerIndex;
			copy2.currentPlayerIndex = 0;
			copy2.haveInitialTicketsBeenChosen = true;

			copy3.lastPlayerIndex = copy3.currentPlayerIndex;
			copy3.currentPlayerIndex = 0;
			copy3.haveInitialTicketsBeenChosen = true;

			copy4.lastPlayerIndex = copy4.currentPlayerIndex;
			copy4.currentPlayerIndex = 0;
			copy4.haveInitialTicketsBeenChosen = true;

			nextStates.add(copy1);
			nextStates.add(copy2);
			nextStates.add(copy3);
			nextStates.add(copy4);

			return nextStates;
		}
		// if this is a second turn of a color-drawing turn, create a state for each
		// possible card to take (no face up wild allowed)
		else if (this.haveAlreadyTakenColorCard) {

			if (this.colorDeck.canDrawFromTop()) {
				final TicketToRideState takeFromDrawPileCopy = new TicketToRideState(this);
				takeFromDrawPileCopy.players[takeFromDrawPileCopy.currentPlayerIndex]
						.drawUnknownColorCardFromDeck(takeFromDrawPileCopy.colorDeck);
				nextStates.add(takeFromDrawPileCopy);
			}

			for (final String color : this.colorDeck.getFaceUp().keySet()) {
				if (!color.equals("WILD") && this.colorDeck.getFaceUp().get(color) > 0) {
					final TicketToRideState state = new TicketToRideState(this);
					state.players[state.currentPlayerIndex].drawFaceUp(color, state.colorDeck);
					nextStates.add(state);
				}
			}

			for (final GameState state : nextStates) {
				final TicketToRideState ticketToRideState = (TicketToRideState) state;

				ticketToRideState.haveAlreadyTakenColorCard = false;
				ticketToRideState.lastPlayerIndex = ticketToRideState.currentPlayerIndex;
				ticketToRideState.currentPlayerIndex = ticketToRideState.getNextPlayer();

				// if we are at less than 3 trains, then we must have been the one to get there
				// first, so the game is over
				if (this.players[this.currentPlayerIndex].getNumCarsRemaining() < 3) {
					ticketToRideState.isGameOver = true;
				}
			}

			// if we can't do anything, just go to the next player
			if (nextStates.isEmpty()) {
				final TicketToRideState copy = new TicketToRideState(this);
				copy.haveAlreadyTakenColorCard = false;
				copy.lastPlayerIndex = copy.currentPlayerIndex;
				copy.currentPlayerIndex = copy.getNextPlayer();

				// if we are at less than 3 trains, then we must have been the one to get there
				// first, so the game is over
				if (this.players[this.currentPlayerIndex].getNumCarsRemaining() < 3) {
					copy.isGameOver = true;
				}

				nextStates.add(copy);
			}

			return nextStates;
		}
		// if this is a second turn of a ticket-drawing turn, create a state for each
		// possible combination of tickets to take
		else if (this.haveAlreadyDrawnTickets) {
			final TicketToRideState copy1 = new TicketToRideState(this);
			final TicketToRideState copy2 = new TicketToRideState(this);
			final TicketToRideState copy3 = new TicketToRideState(this);
			final TicketToRideState copy4 = new TicketToRideState(this);
			final TicketToRideState copy5 = new TicketToRideState(this);
			final TicketToRideState copy6 = new TicketToRideState(this);
			final TicketToRideState copy7 = new TicketToRideState(this);

			// keep left
			final Player copy1Player = copy1.players[copy1.currentPlayerIndex];
			copy1Player.discardKnownTicketAtIndex(copy1Player.getNumKnownDestinationTickets() - 1,
					copy1.destinationTicketDeck);
			copy1Player.discardKnownTicketAtIndex(copy1Player.getNumKnownDestinationTickets() - 1,
					copy1.destinationTicketDeck);

			// keep middle
			final Player copy2Player = copy2.players[copy2.currentPlayerIndex];
			copy2Player.discardKnownTicketAtIndex(copy2Player.getNumKnownDestinationTickets() - 1,
					copy2.destinationTicketDeck);
			copy2Player.discardKnownTicketAtIndex(copy2Player.getNumKnownDestinationTickets() - 2,
					copy2.destinationTicketDeck);

			// keep right
			final Player copy3Player = copy3.players[copy3.currentPlayerIndex];
			copy3Player.discardKnownTicketAtIndex(copy3Player.getNumKnownDestinationTickets() - 2,
					copy3.destinationTicketDeck);
			copy3Player.discardKnownTicketAtIndex(copy3Player.getNumKnownDestinationTickets() - 2,
					copy3.destinationTicketDeck);

			// keep left and middle
			final Player copy4Player = copy4.players[copy4.currentPlayerIndex];
			copy4Player.discardKnownTicketAtIndex(copy4Player.getNumKnownDestinationTickets() - 1,
					copy4.destinationTicketDeck);

			// keep left and right
			final Player copy5Player = copy5.players[copy5.currentPlayerIndex];
			copy5Player.discardKnownTicketAtIndex(copy5Player.getNumKnownDestinationTickets() - 2,
					copy5.destinationTicketDeck);

			// keep middle and right
			final Player copy6Player = copy6.players[copy6.currentPlayerIndex];
			copy6Player.discardKnownTicketAtIndex(copy6Player.getNumKnownDestinationTickets() - 3,
					copy6.destinationTicketDeck);

			nextStates.add(copy1);
			nextStates.add(copy2);
			nextStates.add(copy3);
			nextStates.add(copy4);
			nextStates.add(copy5);
			nextStates.add(copy6);
			nextStates.add(copy7);

			for (final GameState state : nextStates) {
				final TicketToRideState ticketToRideState = (TicketToRideState) state;

				ticketToRideState.haveAlreadyDrawnTickets = false;
				ticketToRideState.lastPlayerIndex = ticketToRideState.currentPlayerIndex;
				ticketToRideState.currentPlayerIndex = ticketToRideState.getNextPlayer();

				// if we are at less than 3 trains, then we must have been the one to get there
				// first, so the game is over
				if (this.players[this.currentPlayerIndex].getNumCarsRemaining() < 3) {
					ticketToRideState.isGameOver = true;
				}
			}

			return nextStates;
		}

		// if this is a first turn, make a state for each possible train placement, each
		// possible color card, and drawing tickets

		// train placements
		final Set<Board.Connection> possibleConnectionsForPlayer = this.board
				.getPossibleConnectionsForOwner(this.currentPlayerIndex);

		final boolean isLastTurn = this.players[this.currentPlayerIndex].getNumCarsRemaining() < 3;

		for (final Board.Connection connection : possibleConnectionsForPlayer) {
			if (this.players[this.currentPlayerIndex].canAffordConnection(connection)) {

				if (!connection.getColor().equals("GRAY")) {
					final TicketToRideState copy = new TicketToRideState(this);
					copy.players[copy.currentPlayerIndex].buildConnection(copy.board.getMatchingConnection(connection),
							copy.board, copy.colorDeck, copy.currentPlayerIndex, copy.players.length);
					copy.lastPlayerIndex = copy.currentPlayerIndex;
					copy.currentPlayerIndex = copy.getNextPlayer();
					copy.isGameOver = isLastTurn;
					nextStates.add(copy);
				} else {
					for (final String color : ColorDeck.COLORS) {
						if (!color.equals("WILD")) {
							if (this.players[this.currentPlayerIndex].canAffordGrayWithColor(connection.getLength(),
									color)) {
								final TicketToRideState copy = new TicketToRideState(this);
								copy.players[copy.currentPlayerIndex].buildGrayConnectionWithColor(
										copy.board.getMatchingConnection(connection), copy.board, color, copy.colorDeck,
										copy.currentPlayerIndex, copy.players.length);
								copy.lastPlayerIndex = copy.currentPlayerIndex;
								copy.currentPlayerIndex = copy.getNextPlayer();
								copy.isGameOver = isLastTurn;
								nextStates.add(copy);
							}
						}
					}
				}

			}
		}

		// color card choices
		if (this.colorDeck.canDrawFromTop()) {
			final TicketToRideState takeFromDrawPileCopy = new TicketToRideState(this);
			takeFromDrawPileCopy.players[takeFromDrawPileCopy.currentPlayerIndex]
					.drawUnknownColorCardFromDeck(takeFromDrawPileCopy.colorDeck);
			takeFromDrawPileCopy.haveAlreadyTakenColorCard = true;
			nextStates.add(takeFromDrawPileCopy);
		}

		for (final String color : this.colorDeck.getFaceUp().keySet()) {
			if (this.colorDeck.getFaceUp().get(color) > 0) {
				final TicketToRideState state = new TicketToRideState(this);
				state.players[state.currentPlayerIndex].drawFaceUp(color, state.colorDeck);
				state.haveAlreadyTakenColorCard = !color.equals("WILD");
				nextStates.add(state);
			}
		}

		// tickets
		if (this.destinationTicketDeck.canDrawThreeTickets()) {
			final TicketToRideState copy = new TicketToRideState(this);
			copy.players[copy.currentPlayerIndex].drawThreeTickets(copy.destinationTicketDeck);
			copy.haveAlreadyDrawnTickets = true;
			nextStates.add(copy);
		}

		// if next states is somehow still empty, just end the game since there are no
		// legal moves
		if (nextStates.isEmpty()) {
			final TicketToRideState copy = new TicketToRideState(this);
			copy.isGameOver = true;
			nextStates.add(copy);
		}

		return nextStates;
	}

	@Override
	public GameState getRandomNextState() {
		// remember that it is guaranteed that the game has not ended
		// it is also guaranteed that initial tickets have been chosen already

		// make a copy of the current state to spawn new states off of
		final TicketToRideState temp = new TicketToRideState(this);

		// randomly fill in all unknown cards
		for (final Player player : temp.players) {
			temp.colorDeck.fillUnknownsRandomlyForPlayer(player);
			temp.destinationTicketDeck.fillUnknownsRandomlyForPlayer(player);
		}

		// replenish the face up with a random card if needed and possible
		temp.colorDeck.replenishFaceUpRandomly();

		// generate all of the possible next states
		final List<GameState> nextStates = new ArrayList<>();

		// if this is a second turn of a color-drawing turn, create a state for each
		// possible card to take (no face up wild allowed)
		if (temp.haveAlreadyTakenColorCard) {

			if (temp.colorDeck.canDrawFromTop()) {
				final TicketToRideState takeFromDrawPileCopy = new TicketToRideState(temp);
				takeFromDrawPileCopy.players[takeFromDrawPileCopy.currentPlayerIndex]
						.drawUnknownColorCardFromDeck(takeFromDrawPileCopy.colorDeck);
				nextStates.add(takeFromDrawPileCopy);
			}

			for (final String color : temp.colorDeck.getFaceUp().keySet()) {
				if (!color.equals("WILD") && temp.colorDeck.getFaceUp().get(color) > 0) {
					final TicketToRideState state = new TicketToRideState(temp);
					state.players[state.currentPlayerIndex].drawFaceUp(color, state.colorDeck);
					nextStates.add(state);
				}
			}

			for (final GameState state : nextStates) {
				final TicketToRideState ticketToRideState = (TicketToRideState) state;

				ticketToRideState.haveAlreadyTakenColorCard = false;
				ticketToRideState.lastPlayerIndex = ticketToRideState.currentPlayerIndex;
				ticketToRideState.currentPlayerIndex = ticketToRideState.getNextPlayer();

				// if we are at less than 3 trains, then we must have been the one to get there
				// first, so the game is over
				if (temp.players[temp.currentPlayerIndex].getNumCarsRemaining() < 3) {
					ticketToRideState.isGameOver = true;
				}
			}

			// if we can't do anything, just go to the next player
			if (nextStates.isEmpty()) {
				final TicketToRideState copy = new TicketToRideState(temp);
				copy.haveAlreadyTakenColorCard = false;
				copy.lastPlayerIndex = copy.currentPlayerIndex;
				copy.currentPlayerIndex = copy.getNextPlayer();

				// if we are at less than 3 trains, then we must have been the one to get there
				// first, so the game is over
				if (temp.players[temp.currentPlayerIndex].getNumCarsRemaining() < 3) {
					copy.isGameOver = true;
				}

				nextStates.add(copy);
			}

			// pick one at random
			final int randomIndex = (int) (Math.random() * nextStates.size());
			return nextStates.get(randomIndex);
		}
		// if this is a second turn of a ticket-drawing turn, create a state for each
		// possible combination of tickets to take
		else if (temp.haveAlreadyDrawnTickets) {
			final TicketToRideState copy1 = new TicketToRideState(temp);
			final TicketToRideState copy2 = new TicketToRideState(temp);
			final TicketToRideState copy3 = new TicketToRideState(temp);
			final TicketToRideState copy4 = new TicketToRideState(temp);
			final TicketToRideState copy5 = new TicketToRideState(temp);
			final TicketToRideState copy6 = new TicketToRideState(temp);
			final TicketToRideState copy7 = new TicketToRideState(temp);

			// keep left
			final Player copy1Player = copy1.players[copy1.currentPlayerIndex];
			copy1Player.discardKnownTicketAtIndex(copy1Player.getNumKnownDestinationTickets() - 1,
					copy1.destinationTicketDeck);
			copy1Player.discardKnownTicketAtIndex(copy1Player.getNumKnownDestinationTickets() - 1,
					copy1.destinationTicketDeck);

			// keep middle
			final Player copy2Player = copy2.players[copy2.currentPlayerIndex];
			copy2Player.discardKnownTicketAtIndex(copy2Player.getNumKnownDestinationTickets() - 1,
					copy2.destinationTicketDeck);
			copy2Player.discardKnownTicketAtIndex(copy2Player.getNumKnownDestinationTickets() - 2,
					copy2.destinationTicketDeck);

			// keep right
			final Player copy3Player = copy3.players[copy3.currentPlayerIndex];
			copy3Player.discardKnownTicketAtIndex(copy3Player.getNumKnownDestinationTickets() - 2,
					copy3.destinationTicketDeck);
			copy3Player.discardKnownTicketAtIndex(copy3Player.getNumKnownDestinationTickets() - 2,
					copy3.destinationTicketDeck);

			// keep left and middle
			final Player copy4Player = copy4.players[copy4.currentPlayerIndex];
			copy4Player.discardKnownTicketAtIndex(copy4Player.getNumKnownDestinationTickets() - 1,
					copy4.destinationTicketDeck);

			// keep left and right
			final Player copy5Player = copy5.players[copy5.currentPlayerIndex];
			copy5Player.discardKnownTicketAtIndex(copy5Player.getNumKnownDestinationTickets() - 2,
					copy5.destinationTicketDeck);

			// keep middle and right
			final Player copy6Player = copy6.players[copy6.currentPlayerIndex];
			copy6Player.discardKnownTicketAtIndex(copy6Player.getNumKnownDestinationTickets() - 3,
					copy6.destinationTicketDeck);

			nextStates.add(copy1);
			nextStates.add(copy2);
			nextStates.add(copy3);
			nextStates.add(copy4);
			nextStates.add(copy5);
			nextStates.add(copy6);
			nextStates.add(copy7);

			for (final GameState state : nextStates) {
				final TicketToRideState ticketToRideState = (TicketToRideState) state;

				ticketToRideState.haveAlreadyDrawnTickets = false;
				ticketToRideState.lastPlayerIndex = ticketToRideState.currentPlayerIndex;
				ticketToRideState.currentPlayerIndex = ticketToRideState.getNextPlayer();

				// if we are at less than 3 trains, then we must have been the one to get there
				// first, so the game is over
				if (temp.players[temp.currentPlayerIndex].getNumCarsRemaining() < 3) {
					ticketToRideState.isGameOver = true;
				}
			}

			// pick one at random
			final int randomIndex = (int) (Math.random() * nextStates.size());
			return nextStates.get(randomIndex);
		}

		// if this is a first turn, make a state for each possible train placement, each
		// possible color card, and drawing tickets

		// train placements
		final Set<Board.Connection> possibleConnectionsForPlayer = temp.board
				.getPossibleConnectionsForOwner(temp.currentPlayerIndex);

		final boolean isLastTurn = temp.players[temp.currentPlayerIndex].getNumCarsRemaining() < 3;

		for (final Board.Connection connection : possibleConnectionsForPlayer) {
			if (temp.players[temp.currentPlayerIndex].canAffordConnection(connection)) {

				if (!connection.getColor().equals("GRAY")) {
					final TicketToRideState copy = new TicketToRideState(temp);
					copy.players[copy.currentPlayerIndex].buildConnection(copy.board.getMatchingConnection(connection),
							copy.board, copy.colorDeck, copy.currentPlayerIndex, copy.players.length);
					copy.lastPlayerIndex = copy.currentPlayerIndex;
					copy.currentPlayerIndex = copy.getNextPlayer();
					copy.isGameOver = isLastTurn;
					nextStates.add(copy);
				} else {
					for (final String color : ColorDeck.COLORS) {
						if (!color.equals("WILD")) {
							if (temp.players[temp.currentPlayerIndex].canAffordGrayWithColor(connection.getLength(),
									color)) {
								final TicketToRideState copy = new TicketToRideState(temp);
								copy.players[copy.currentPlayerIndex].buildGrayConnectionWithColor(
										copy.board.getMatchingConnection(connection), copy.board, color, copy.colorDeck,
										copy.currentPlayerIndex, copy.players.length);
								copy.lastPlayerIndex = copy.currentPlayerIndex;
								copy.currentPlayerIndex = copy.getNextPlayer();
								copy.isGameOver = isLastTurn;
								nextStates.add(copy);
							}
						}
					}
				}
			}
		}

		// color card choices
		if (temp.colorDeck.canDrawFromTop()) {
			final TicketToRideState takeFromDrawPileCopy = new TicketToRideState(temp);
			takeFromDrawPileCopy.players[takeFromDrawPileCopy.currentPlayerIndex]
					.drawUnknownColorCardFromDeck(takeFromDrawPileCopy.colorDeck);
			takeFromDrawPileCopy.haveAlreadyTakenColorCard = true;
			nextStates.add(takeFromDrawPileCopy);
		}

		for (final String color : temp.colorDeck.getFaceUp().keySet()) {
			if (temp.colorDeck.getFaceUp().get(color) > 0) {
				final TicketToRideState state = new TicketToRideState(temp);
				state.players[state.currentPlayerIndex].drawFaceUp(color, state.colorDeck);
				state.haveAlreadyTakenColorCard = !color.equals("WILD");
				nextStates.add(state);
			}
		}

		// tickets
		if (temp.destinationTicketDeck.canDrawThreeTickets()) {
			final TicketToRideState copy = new TicketToRideState(temp);
			copy.players[copy.currentPlayerIndex].drawThreeTickets(copy.destinationTicketDeck);
			copy.haveAlreadyDrawnTickets = true;
			nextStates.add(copy);
		}

		// if next states is somehow still empty, just end the game since there are no
		// legal moves
		if (nextStates.isEmpty()) {
			final TicketToRideState copy = new TicketToRideState(temp);
			copy.isGameOver = true;
			nextStates.add(copy);
		}

		// pick one at random
		final int randomIndex = (int) (Math.random() * nextStates.size());
		return nextStates.get(randomIndex);
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
				System.out.println("Revealing player " + i + "'s destination tickets:");
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
	
	public void setCurrentPlayer(final int player) {
		this.currentPlayerIndex = player;
	}
	
	public void setLastPlayer(final int player) {
		this.lastPlayerIndex = player;
	}
}
