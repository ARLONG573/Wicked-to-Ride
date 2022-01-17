package state;

import java.util.HashSet;
import java.util.Set;

import data.DestinationTicket;

public class DestinationTicketDeck {

	private final Set<DestinationTicket> possiblyInDeck;
	private final Set<DestinationTicket> knownDiscard;
	private int numCardsInDrawPile;

	public DestinationTicketDeck() {
		this.possiblyInDeck = new HashSet<>();
		this.knownDiscard = new HashSet<>();
		this.numCardsInDrawPile = 0;
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
}
