package state;

import java.util.HashMap;
import java.util.Map;

public class ColorDeck {

	private final Map<String, Long> possiblyInDeck;
	private final Map<String, Long> discard;
	private final Map<String, Long> faceUp;
	private long numCardsInDrawPile;

	public ColorDeck() {
		this.possiblyInDeck = new HashMap<>();
		this.discard = new HashMap<>();
		this.faceUp = new HashMap<>();
		this.numCardsInDrawPile = 0;
	}

	public void initColor(final String color, final long count) {
		this.possiblyInDeck.put(color, count);
		this.numCardsInDrawPile += count;
	}
}
