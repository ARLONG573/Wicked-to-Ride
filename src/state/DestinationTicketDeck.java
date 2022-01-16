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
}
