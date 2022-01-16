package state;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import data.DestinationTicket;

public class Player {

	private final Map<String, Integer> knownColorCards;
	private final Set<DestinationTicket> knownDestinationTickets;
	private int numUnknownColorCards;
	private int numUnknownDestinationTickets;
	private long numCarsRemaining;
	private int score;

	public Player(final long numStartingCars) {
		this.knownColorCards = new HashMap<>();
		this.knownDestinationTickets = new HashSet<>();
		this.numUnknownColorCards = 0;
		this.numUnknownDestinationTickets = 0;
		this.numCarsRemaining = numStartingCars;
		this.score = 0;

		// initializing color mappings now will make things easier later
		for (final String color : ColorDeck.COLORS) {
			this.knownColorCards.put(color, 0);
		}
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
		
		this.numUnknownDestinationTickets --;
		this.knownDestinationTickets.add(ticket);
		deck.removeCardFromDeckPossiblility(ticket);
	}

	public int getNumUnknownColorCards() {
		return this.numUnknownColorCards;
	}

	public int getNumUnknownDestinationTickets() {
		return this.numUnknownDestinationTickets;
	}
}
