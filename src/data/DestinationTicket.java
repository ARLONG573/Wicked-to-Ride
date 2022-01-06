package data;

public class DestinationTicket {

	private final String start;
	private final String end;
	private final int points;

	public DestinationTicket(final String start, final String end, final int points) {
		this.start = start;
		this.end = end;
		this.points = points;
	}

	public String getStart() {
		return this.start;
	}

	public String getEnd() {
		return this.end;
	}

	public int getPoints() {
		return this.points;
	}
}
