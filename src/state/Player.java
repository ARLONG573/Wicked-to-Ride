package state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.DestinationTicket;

public class Player {

	private final Map<String, Integer> knownColorCards;
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
			this.knownColorCards.put(color, 0);
		}
	}

	public Player(final Player player) {
		this.knownColorCards = new HashMap<>();
		for (final String color : player.knownColorCards.keySet()) {
			this.knownColorCards.put(color, Integer.valueOf(player.knownColorCards.get(color)));
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

	public Map<String, Integer> getKnownColorCards() {
		return this.knownColorCards;
	}

	public List<DestinationTicket> getKnownDestinationTickets() {
		return this.knownDestinationTickets;
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
}
