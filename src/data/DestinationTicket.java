package data;

public class DestinationTicket {

	private final String start;
	private final String end;
	private final long points;

	public DestinationTicket(final String start, final String end, final long points) {
		this.start = start;
		this.end = end;
		this.points = points;
	}

	public DestinationTicket(final DestinationTicket ticket) {
		this.start = ticket.start;
		this.end = ticket.end;
		this.points = ticket.points;
	}

	public String getStart() {
		return this.start;
	}

	public String getEnd() {
		return this.end;
	}

	public long getPoints() {
		return this.points;
	}
}
