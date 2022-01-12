package state;

import java.util.HashSet;
import java.util.Set;

import data.DestinationTicket;

public class DestinationTicketDeck {

	private final Set<DestinationTicket> seen;
	private final Set<DestinationTicket> unseen;
	private int numCardsInDrawPile;

	public DestinationTicketDeck() {
		this.seen = new HashSet<>();
		this.unseen = new HashSet<>();
		this.numCardsInDrawPile = 0;
	}

	public void initDestinationTicket(final DestinationTicket destinationTicket) {
		this.unseen.add(destinationTicket);
		this.numCardsInDrawPile++;
	}
}
