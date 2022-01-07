package state;

import java.util.HashMap;

public class ColorDeck extends HashMap<String, Long> {

	private static final long serialVersionUID = 1L;

	public void setColor(final String color, final long count) {
		super.put(color, count);
	}

}
