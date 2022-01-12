package state;

import java.util.HashMap;
import java.util.Map;

public class ColorDeck {

	private final Map<String, Long> seen;
	private final Map<String, Long> unseen;
	private long numCardsInDrawPile;

	public ColorDeck() {
		this.seen = new HashMap<>();
		this.unseen = new HashMap<>();
		this.numCardsInDrawPile = 0;
	}

	public void initColor(final String color, final long count) {
		this.unseen.put(color, count);
		this.numCardsInDrawPile += count;
	}

}
