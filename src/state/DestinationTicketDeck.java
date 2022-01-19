package state;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import data.DestinationTicket;

public class DestinationTicketDeck {

	private final List<DestinationTicket> possiblyInDeck;
	private final Set<DestinationTicket> knownDiscard;
	private int numCardsInDrawPile;

	public DestinationTicketDeck() {
		this.possiblyInDeck = new ArrayList<>();
		this.knownDiscard = new HashSet<>();
		this.numCardsInDrawPile = 0;
	}

	public DestinationTicketDeck(final DestinationTicketDeck deck) {
		this.possiblyInDeck = new ArrayList<>();
		for (final DestinationTicket ticket : deck.possiblyInDeck) {
			this.possiblyInDeck.add(new DestinationTicket(ticket));
		}

		this.knownDiscard = new HashSet<>();
		for (final DestinationTicket ticket : deck.knownDiscard) {
			this.knownDiscard.add(new DestinationTicket(ticket));
		}

		this.numCardsInDrawPile = deck.numCardsInDrawPile;
	}

	public Set<DestinationTicket> getKnownDiscards() {
		return this.knownDiscard;
	}

	public void initDestinationTicket(final DestinationTicket destinationTicket) {
		this.possiblyInDeck.add(destinationTicket);
		this.numCardsInDrawPile++;
	}

	public void dealStartingThreeToPlayer(final Player player) {
		this.numCardsInDrawPile -= 3;
		player.addUnknownDestinationTickets(3);
	}

	public void removeCardFromDeckPossiblility(final DestinationTicket ticket) {
		this.possiblyInDeck.remove(ticket);
	}

	public DestinationTicket getTicket(final String start, final String end) {
		for (final DestinationTicket ticket : this.possiblyInDeck) {
			if (ticket.getStart().equals(start) && ticket.getEnd().equals(end)) {
				return ticket;
			}
		}

		return null;
	}

	public void fillUnknownsRandomlyForPlayer(final Player player) {
		final int numUnknowns = player.getNumUnknownDestinationTickets();
		for (int i = 0; i < numUnknowns; i++) {
			final int randomIndex = (int) (Math.random() * this.possiblyInDeck.size());
			player.convertUnknownDestinationTicketToKnownManually(this.possiblyInDeck.get(randomIndex), this);
		}
	}

	public void discardKnownTicket(final DestinationTicket ticket) {
		this.knownDiscard.add(ticket);
	}
}
