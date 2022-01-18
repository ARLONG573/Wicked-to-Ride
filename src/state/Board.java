package state;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Board {

	private final Map<String, Set<Connection>> connections;

	public Board() {
		this.connections = new HashMap<>();
	}

	public void addConnection(final String start, final String end, final long length, final String color) {
		final Connection connection = new Connection(start, end, length, color);

		this.connections.putIfAbsent(start, new HashSet<>());
		this.connections.putIfAbsent(end, new HashSet<>());

		this.connections.get(start).add(connection);
		this.connections.get(end).add(connection);
	}

	public Set<Connection> getConnectionsForPlayer(final int owner) {
		final Set<String> visited = new HashSet<>();
		final Set<Connection> playerConnections = new HashSet<>();

		for (final String start : this.connections.keySet()) {
			visited.add(start);

			for (final Connection connection : this.connections.get(start)) {
				if (connection.owner == owner) {
					final String other = (connection.start.equals(start)) ? connection.end : connection.start;

					if (!visited.contains(other)) {
						playerConnections.add(connection);
					}
				}
			}
		}

		return playerConnections;
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
