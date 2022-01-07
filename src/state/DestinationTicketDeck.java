package state;

import java.util.ArrayList;

import data.DestinationTicket;

public class DestinationTicketDeck extends ArrayList<DestinationTicket> {

	private static final long serialVersionUID = 1L;

	public void addDestinationTicket(final DestinationTicket destinationTicket) {
		super.add(destinationTicket);
	}
}
