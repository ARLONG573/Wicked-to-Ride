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

	private class Connection {
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
	}
}
