package state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.DestinationTicket;

public class Player {

	private final Map<String, Long> knownColorCards;
	private final List<DestinationTicket> knownDestinationTickets;
	private int numUnknownColorCards;
	private int numUnknownDestinationTickets;
	private long numCarsRemaining;
	private int score;

	// these are only calculated at the end
	private int numCompletedTickets;
	private int longestRouteLength;

	public Player(final long numStartingCars) {
		this.knownColorCards = new HashMap<>();
		this.knownDestinationTickets = new ArrayList<>();
		this.numUnknownColorCards = 0;
		this.numUnknownDestinationTickets = 0;
		this.numCarsRemaining = numStartingCars;
		this.score = 0;
		this.numCompletedTickets = 0;
		this.longestRouteLength = 0;

		// initializing color mappings now will make things easier later
		for (final String color : ColorDeck.COLORS) {
			this.knownColorCards.put(color, 0L);
		}
	}

	public Player(final Player player) {
		this.knownColorCards = new HashMap<>();
		for (final String color : player.knownColorCards.keySet()) {
			this.knownColorCards.put(color, Long.valueOf(player.knownColorCards.get(color)));
		}

		this.knownDestinationTickets = new ArrayList<>();
		for (final DestinationTicket ticket : player.knownDestinationTickets) {
			this.knownDestinationTickets.add(new DestinationTicket(ticket));
		}

		this.numUnknownColorCards = player.numUnknownColorCards;
		this.numUnknownDestinationTickets = player.numUnknownDestinationTickets;
		this.numCarsRemaining = player.numCarsRemaining;
		this.score = player.score;
		this.numCompletedTickets = player.numCompletedTickets;
		this.longestRouteLength = player.longestRouteLength;
	}

	public long getNumCarsRemaining() {
		return this.numCarsRemaining;
	}

	public void addUnknownColorCards(final int numCards) {
		this.numUnknownColorCards += numCards;
	}

	public void addUnknownDestinationTickets(final int numTickets) {
		this.numUnknownDestinationTickets += numTickets;
	}

	public void convertUnknownColorCardToKnownManually(final String color, final ColorDeck deck) {
		this.numUnknownColorCards--;
		this.knownColorCards.put(color, this.knownColorCards.get(color) + 1);
		deck.removeCardFromDeckPossibility(color);
	}

	public void convertUnknownDestinationTicketToKnownManually(final DestinationTicket ticket,
			final DestinationTicketDeck deck) {

		this.numUnknownDestinationTickets--;
		this.knownDestinationTickets.add(ticket);
		deck.removeCardFromDeckPossiblility(ticket);
	}

	public void setNumUnknownDestinationTickets(final int numTickets) {
		this.numUnknownDestinationTickets = numTickets;
	}

	public int getNumUnknownColorCards() {
		return this.numUnknownColorCards;
	}

	public int getNumUnknownDestinationTickets() {
		return this.numUnknownDestinationTickets;
	}

	public Map<String, Long> getKnownColorCards() {
		return this.knownColorCards;
	}

	public List<DestinationTicket> getKnownDestinationTickets() {
		return this.knownDestinationTickets;
	}

	public int getNumKnownDestinationTickets() {
		return this.knownDestinationTickets.size();
	}

	public int getScore() {
		return this.score;
	}

	public int getNumCompletedTickets() {
		return this.numCompletedTickets;
	}

	public void setNumCompletedTickets(final int num) {
		this.numCompletedTickets = num;
	}

	public int getLongestRouteLength() {
		return this.longestRouteLength;
	}

	public void setLongestRouteLength(final int num) {
		this.longestRouteLength = num;
	}

	public void addScore(final long amount) {
		this.score += amount;
	}

	public void discardKnownTicketAtIndex(final int index, final DestinationTicketDeck deck) {
		final DestinationTicket discardedTicket = this.knownDestinationTickets.remove(index);
		deck.discardKnownTicket(discardedTicket);
	}

	public void drawUnknownColorCardFromDeck(final ColorDeck deck) {
		this.numUnknownColorCards++;
		deck.drawUnknownFromDeck();
	}

	public void drawFaceUp(final String color, final ColorDeck deck) {
		this.knownColorCards.put(color, this.knownColorCards.get(color) + 1);
		deck.drawFaceUp(color);
	}

	public boolean canAffordConnection(final Board.Connection connection) {
		if (this.numCarsRemaining < connection.getLength()) {
			return false;
		}

		if (!connection.getColor().equals("GRAY")) {
			return this.knownColorCards.get(connection.getColor()) + this.knownColorCards.get("WILD") >= connection
					.getLength();
		}

		long maxGrayLength = 0;
		for (final String color : this.knownColorCards.keySet()) {
			if (!color.equals("WILD")) {
				maxGrayLength = Math.max(maxGrayLength,
						this.knownColorCards.get(color) + this.knownColorCards.get("WILD"));
			}
		}

		return maxGrayLength >= connection.getLength();
	}

	public boolean canAffordGrayWithColor(final long length, final String color) {
		// technically it isn't a requirement to have at least one of the color if you
		// have sufficient wilds
		// this just stops it from investigating plays that are very likely to be bad
		return this.numCarsRemaining >= length && this.knownColorCards.get(color) > 0
				&& this.knownColorCards.get(color) + this.knownColorCards.get("WILD") >= length;
	}

	public void buildConnection(final Board.Connection connection, final Board board, final ColorDeck deck,
			final int currentPlayer, final int numPlayers) {
		// pay cars
		this.numCarsRemaining -= connection.getLength();

		// pay cards and send to discard pile
		final long numColor = this.knownColorCards.get(connection.getColor());

		if (connection.getLength() <= numColor) {
			this.knownColorCards.put(connection.getColor(),
					this.knownColorCards.get(connection.getColor()) - connection.getLength());

			deck.sendKnownToDiscard(connection.getLength(), connection.getColor());
		} else {
			this.knownColorCards.put(connection.getColor(), 0L);
			this.knownColorCards.put("WILD", this.knownColorCards.get("WILD") - (connection.getLength() - numColor));

			deck.sendKnownToDiscard(numColor, connection.getColor());
			deck.sendKnownToDiscard(connection.getLength() - numColor, "WILD");
		}

		// add score
		if (connection.getLength() == 1) {
			this.score += 1;
		} else if (connection.getLength() == 2) {
			this.score += 2;
		} else if (connection.getLength() == 3) {
			this.score += 4;
		} else if (connection.getLength() == 4) {
			this.score += 7;
		} else if (connection.getLength() == 5) {
			this.score += 10;
		} else
			this.score += 15;

		// adjust board
		board.giveOwnershipToPlayer(connection, currentPlayer, numPlayers);
	}

	public void buildGrayConnectionWithColor(final Board.Connection connection, final Board board, final String color,
			final ColorDeck deck, final int currentPlayer, final int numPlayers) {
		// pay cars
		this.numCarsRemaining -= connection.getLength();

		// pay cards and send to discard pile
		final long numColor = this.knownColorCards.get(color);

		if (connection.getLength() <= numColor) {
			this.knownColorCards.put(color, this.knownColorCards.get(color) - connection.getLength());

			deck.sendKnownToDiscard(connection.getLength(), color);
		} else {
			this.knownColorCards.put(color, 0L);
			this.knownColorCards.put("WILD", this.knownColorCards.get("WILD") - (connection.getLength() - numColor));

			deck.sendKnownToDiscard(numColor, color);
			deck.sendKnownToDiscard(connection.getLength() - numColor, "WILD");
		}

		// add score
		if (connection.getLength() == 1) {
			this.score += 1;
		} else if (connection.getLength() == 2) {
			this.score += 2;
		} else if (connection.getLength() == 3) {
			this.score += 4;
		} else if (connection.getLength() == 4) {
			this.score += 7;
		} else if (connection.getLength() == 5) {
			this.score += 10;
		} else
			this.score += 15;

		// adjust board
		board.giveOwnershipToPlayer(connection, currentPlayer, numPlayers);
	}

	public void drawThreeTickets(final DestinationTicketDeck deck) {
		this.numUnknownDestinationTickets += 3;
		deck.drawThreeUnknown();
	}
}
