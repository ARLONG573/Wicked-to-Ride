package state;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ColorDeck {

	public static final String[] COLORS = { "RED", "ORANGE", "YELLOW", "GREEN", "BLUE", "WHITE", "BLACK", "WILD" };

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
}
