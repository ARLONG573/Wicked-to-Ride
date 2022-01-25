package state;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import data.DestinationTicket;

public class DestinationTicketDeck {

	private final List<DestinationTicket> possiblyInDeck;
	private final Set<DestinationTicket> knownDiscard;

	private int numCardsInDrawPile;
	private int numDiscards;

	public DestinationTicketDeck() {
		this.possiblyInDeck = new ArrayList<>();
		this.knownDiscard = new HashSet<>();
		this.numCardsInDrawPile = 0;
		this.numDiscards = 0;
	}

	public DestinationTicketDeck(final DestinationTicketDeck deck) {
		this.possiblyInDeck = new ArrayList<>();
		for (final DestinationTicket ticket : deck.possiblyInDeck) {
			this.possiblyInDeck.add(ticket);
		}

		this.knownDiscard = new HashSet<>();
		for (final DestinationTicket ticket : deck.knownDiscard) {
			this.knownDiscard.add(ticket);
		}

		this.numCardsInDrawPile = deck.numCardsInDrawPile;
		this.numDiscards = deck.numDiscards;
	}

	public Set<DestinationTicket> getKnownDiscards() {
		return this.knownDiscard;
	}

	public void addDiscards(final int count) {
		this.numDiscards += count;
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

	public void fillUnknownsForPlayerSmartly(final Player player, final Board board, final int playerIndex,
			final int aiPlayerIndex) {
		if (player.getNumUnknownDestinationTickets() == 0) {
			return;
		}

		// if the player has built no connections or is an AI, assign them random
		// tickets
		if (playerIndex == aiPlayerIndex || board.getConnectionsForPlayer(playerIndex).isEmpty()) {
			final List<DestinationTicket> tickets = new ArrayList<>();
			tickets.addAll(this.possiblyInDeck);

			while (player.getNumUnknownDestinationTickets() > 0) {
				final DestinationTicket randomTicket = tickets.remove((int) (Math.random() * tickets.size()));
				player.convertUnknownDestinationTicketToKnownManually(randomTicket, this);
			}

			return;
		}

		// the closer they are to completing a ticket, the more likely they are to have
		// it in their hand
		// ties will be broken by highest score
		final Comparator<DestinationTicket> comparator = new Comparator<>() {
			@Override
			public int compare(final DestinationTicket ticket1, final DestinationTicket ticket2) {
				final int result = Integer.compare(
						board.getMinConnectionsBetween(ticket1.getStart(), ticket1.getEnd(), playerIndex),
						board.getMinConnectionsBetween(ticket2.getStart(), ticket2.getEnd(), playerIndex));

				if (result != 0) {
					return result;
				}

				return -Long.compare(ticket1.getPoints(), ticket2.getPoints());
			}
		};

		final Queue<DestinationTicket> sortedTickets = new PriorityQueue<>(comparator);

		for (final DestinationTicket ticket : this.possiblyInDeck) {
			sortedTickets.add(ticket);
		}

		while (player.getNumUnknownDestinationTickets() > 0) {
			final DestinationTicket ticket = sortedTickets.poll();
			player.convertUnknownDestinationTicketToKnownManually(ticket, this);
		}
	}

	public void discardKnownTicket(final DestinationTicket ticket) {
		this.knownDiscard.add(ticket);
		this.numDiscards++;
	}

	public boolean canDrawThreeTickets() {
		return this.numCardsInDrawPile + this.numDiscards >= 3;
	}

	public void drawThreeUnknown() {
		if (this.numCardsInDrawPile < 3) {
			this.possiblyInDeck.addAll(this.knownDiscard);
			this.knownDiscard.clear();
			this.numCardsInDrawPile += this.numDiscards;
			this.numDiscards = 0;
		}

		this.numCardsInDrawPile -= 3;
	}
}
