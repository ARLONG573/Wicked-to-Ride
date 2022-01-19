package state;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ColorDeck {

	public static final String[] COLORS = { "RED", "ORANGE", "YELLOW", "GREEN", "BLUE", "PINK", "WHITE", "BLACK",
			"WILD" };

	private final Map<String, Long> possiblyInDeck;
	private final Map<String, Long> discard;
	private final Map<String, Long> faceUp;
	private long numCardsInDrawPile;

	public ColorDeck() {
		this.possiblyInDeck = new HashMap<>();
		this.discard = new HashMap<>();
		this.faceUp = new HashMap<>();
		this.numCardsInDrawPile = 0;

		// initializing color mappings now will make things easier later
		for (final String color : COLORS) {
			this.discard.put(color, 0L);
			this.faceUp.put(color, 0L);
		}
	}

	public ColorDeck(final ColorDeck deck) {
		this.possiblyInDeck = new HashMap<>();
		for (final String color : deck.possiblyInDeck.keySet()) {
			this.possiblyInDeck.put(color, Long.valueOf(deck.possiblyInDeck.get(color)));
		}

		this.discard = new HashMap<>();
		for (final String color : deck.discard.keySet()) {
			this.discard.put(color, Long.valueOf(deck.discard.get(color)));
		}

		this.faceUp = new HashMap<>();
		for (final String color : deck.faceUp.keySet()) {
			this.faceUp.put(color, Long.valueOf(deck.faceUp.get(color)));
		}

		this.numCardsInDrawPile = deck.numCardsInDrawPile;
	}

	public Map<String, Long> getFaceUp() {
		return this.faceUp;
	}

	public Map<String, Long> getDiscard() {
		return this.discard;
	}

	public void initColor(final String color, final long count) {
		this.possiblyInDeck.put(color, count);
		this.numCardsInDrawPile += count;
	}

	public void dealStartingFourToPlayer(final Player player) {
		this.numCardsInDrawPile -= 4;
		player.addUnknownColorCards(4);
	}

	public void dealStartingFiveFaceUp(final Scanner in) {
		boolean tryAgain = true;
		while (tryAgain) {
			System.out.println("Drawing 5 face up cards...");

			// sweep face up to discard pile
			// no need to worry about deck re-shuffle since this method is only called at
			// the start of the game
			for (final String color : this.faceUp.keySet()) {
				this.discard.put(color, this.discard.get(color) + this.faceUp.get(color));
				this.faceUp.put(color, 0L);
			}

			// draw 5 from the top of the deck
			for (int i = 1; i <= 5; i++) {
				System.out.print("Face up card " + i + ": ");
				final String color = in.next().toUpperCase();

				this.removeCardFromDeckPossibility(color);
				this.faceUp.put(color, this.faceUp.get(color) + 1);
				this.numCardsInDrawPile--;
			}

			tryAgain = (this.faceUp.get("WILD") >= 3);
		}
	}

	public void removeCardFromDeckPossibility(final String color) {
		this.possiblyInDeck.put(color, this.possiblyInDeck.get(color) - 1);
	}

	public void fillUnknownsRandomlyForPlayer(final Player player) {
		final int numUnknowns = player.getNumUnknownColorCards();
		for (int i = 0; i < numUnknowns; i++) {
			// find total possible cards it could be
			long total = 0;

			for (final String color : this.possiblyInDeck.keySet()) {
				total += this.possiblyInDeck.get(color);
			}

			// pick a random number from 1 to total
			final long n = ((long) (Math.random() * total)) + 1;

			// iterate through the map until we find the nth card
			long current = 0;
			String colorToGive = null;

			for (final String color : this.possiblyInDeck.keySet()) {
				current += this.possiblyInDeck.get(color);

				if (current >= n) {
					colorToGive = color;
					break;
				}
			}

			player.convertUnknownColorCardToKnownManually(colorToGive, this);
		}
	}

	public long getNumFaceUp() {
		long total = 0;

		for (final String color : this.faceUp.keySet()) {
			total += this.faceUp.get(color);
		}

		return total;
	}

	public long getNumDiscard() {
		long total = 0;

		for (final String color : this.discard.keySet()) {
			total += this.discard.get(color);
		}

		return total;
	}

	public void replenishFaceUp(final Scanner in) {
		if (this.getNumFaceUp() == 5 && this.faceUp.get("WILD") < 3) {
			return;
		}

		int numSweeps = 0;
		boolean keepGoing = true;
		while (keepGoing) {
			// draw a card from the deck if possible, otherwise we cannot keep going
			if (this.getNumFaceUp() < 5) {
				if (this.numCardsInDrawPile > 0) {
					System.out.print("New face up card: ");
					final String color = in.next().toUpperCase();
					in.nextLine(); // consume new line

					this.removeCardFromDeckPossibility(color);
					this.faceUp.put(color, this.faceUp.get(color) + 1);
					this.numCardsInDrawPile--;
				} else {
					if (this.getNumDiscard() > 0) {
						this.convertDiscardToDraw();
					} else {
						keepGoing = false;
					}
				}
			}
			// if already at 5 cards, sweep if necessary, up to 3 times
			else {
				if (this.faceUp.get("WILD") >= 3) {
					for (final String color : this.faceUp.keySet()) {
						this.discard.put(color, this.discard.get(color) + this.faceUp.get(color));
						this.faceUp.put(color, 0L);
					}
					numSweeps++;

					if (numSweeps == 3) {
						keepGoing = false;
					}
				} else {
					keepGoing = false;
				}
			}
		}
	}

	private void convertDiscardToDraw() {
		this.numCardsInDrawPile = 0;

		for (final String color : this.discard.keySet()) {
			this.possiblyInDeck.put(color, this.discard.get(color));
			this.numCardsInDrawPile += this.discard.get(color);

			this.discard.put(color, 0L);
		}
	}
}
