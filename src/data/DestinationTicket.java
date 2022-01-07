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

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + this.start.hashCode();
		result = 31 * result + this.end.hashCode();
		result = 31 * result + Integer.valueOf(this.points).hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof DestinationTicket)) {
			return false;
		}

		final DestinationTicket otherTicket = (DestinationTicket) o;

		return this.start.equals(otherTicket.start) && this.end.equals(otherTicket.end)
				&& this.points == otherTicket.points;
	}
}
