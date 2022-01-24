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

	public void fillUnknownsForPlayerSmartly(final Player player, final Board board, final int playerIndex) {
		if (player.getNumUnknownDestinationTickets() == 0) {
			return;
		}

		if (player.getNumUnknownDestinationTickets() > this.possiblyInDeck.size()) {
			this.possiblyInDeck.addAll(this.knownDiscard);
			this.knownDiscard.clear();
			this.numCardsInDrawPile += this.numDiscards;
			this.numDiscards = 0;
		}

		// the closer they are to completing a ticket, the more likely they are to have
		// it in their hand
		final List<DestinationTicket> alreadyCompleted = new ArrayList<>();
		final List<DestinationTicket> bothCitiesClaimed = new ArrayList<>();
		final List<DestinationTicket> oneCityClaimed = new ArrayList<>();
		final List<DestinationTicket> noCitiesClaimed = new ArrayList<>();

		for (final DestinationTicket ticket : this.possiblyInDeck) {
			if (board.isCompleteTicket(ticket, playerIndex)) {
				alreadyCompleted.add(ticket);
			} else {
				final boolean startClaimed = board.playerOwnsCity(ticket.getStart(), playerIndex);
				final boolean endClaimed = board.playerOwnsCity(ticket.getEnd(), playerIndex);

				if (startClaimed && endClaimed) {
					bothCitiesClaimed.add(ticket);
				} else if (startClaimed || endClaimed) {
					oneCityClaimed.add(ticket);
				} else {
					noCitiesClaimed.add(ticket);
				}
			}
		}

		while (player.getNumUnknownDestinationTickets() > 0) {
			if (!alreadyCompleted.isEmpty()) {
				final int randomIndex = (int) (Math.random() * alreadyCompleted.size());
				final DestinationTicket ticket = alreadyCompleted.remove(randomIndex);
				player.convertUnknownDestinationTicketToKnownManually(ticket, this);
				continue;
			}

			if (!bothCitiesClaimed.isEmpty()) {
				final int randomIndex = (int) (Math.random() * bothCitiesClaimed.size());
				final DestinationTicket ticket = bothCitiesClaimed.remove(randomIndex);
				player.convertUnknownDestinationTicketToKnownManually(ticket, this);
				continue;
			}

			if (!oneCityClaimed.isEmpty()) {
				final int randomIndex = (int) (Math.random() * oneCityClaimed.size());
				final DestinationTicket ticket = oneCityClaimed.remove(randomIndex);
				player.convertUnknownDestinationTicketToKnownManually(ticket, this);
				continue;
			}

			final int randomIndex = (int) (Math.random() * noCitiesClaimed.size());
			final DestinationTicket ticket = noCitiesClaimed.remove(randomIndex);
			player.convertUnknownDestinationTicketToKnownManually(ticket, this);
			continue;
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
