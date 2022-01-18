package state;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import data.DestinationTicket;

public class Board {

	private final Set<Connection> allConnections;
	private final Map<String, Set<Connection>> connectionsFromCity;

	public Board() {
		this.allConnections = new HashSet<>();
		this.connectionsFromCity = new HashMap<>();
	}

	public void addConnection(final String start, final String end, final long length, final String color) {
		final Connection connection = new Connection(start, end, length, color);

		this.allConnections.add(connection);

		this.connectionsFromCity.putIfAbsent(start, new HashSet<>());
		this.connectionsFromCity.putIfAbsent(end, new HashSet<>());
		this.connectionsFromCity.get(start).add(connection);
		this.connectionsFromCity.get(end).add(connection);
	}

	public Set<Connection> getConnectionsForPlayer(final int owner) {
		final Set<Connection> connections = new HashSet<>();

		for (final Connection connection : this.allConnections) {
			if (connection.owner == owner) {
				connections.add(connection);
			}
		}

		return connections;
	}

	public boolean isCompleteTicket(final DestinationTicket ticket, final int owner) {
		// TODO
		return false;
	}

	public int getLongestRouteLengthForPlayer(final int owner) {
		// TODO
		return 0;
	}

	public class Connection {
		private final String start;
		private final String end;
		private final long length;
		private final String color;

		private int owner;

		private Connection(final String start, final String end, final long length, final String color) {
			this.start = start;
			this.end = end;
			this.length = length;
			this.color = color;

			this.owner = -1;
		}

		public String getStart() {
			return this.start;
		}

		public String getEnd() {
			return this.end;
		}
	}
}
