package state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data.DestinationTicket;

public class Board {

	private final List<Connection> allConnections;

	// used for path calculation (e.g. completed tickets, longest route)
	private final Map<String, Set<Connection>> connectionsFromCity;

	// used for connections that are technically open, but not claimable by a player
	// (e.g. specific double-route rules)
	private final Map<Integer, Set<Connection>> forbiddenConnectionsForPlayer;

	public Board(final int numPlayers) {
		this.allConnections = new ArrayList<>();
		this.connectionsFromCity = new HashMap<>();

		this.forbiddenConnectionsForPlayer = new HashMap<>();
		for (int i = 0; i < numPlayers; i++) {
			this.forbiddenConnectionsForPlayer.put(i, new HashSet<>());
		}
	}

	public Board(final Board board) {
		this.allConnections = new ArrayList<>();
		for (final Connection connection : board.allConnections) {
			this.allConnections.add(new Connection(connection));
		}

		this.connectionsFromCity = new HashMap<>();
		for (final String city : board.connectionsFromCity.keySet()) {
			final Set<Connection> setCopy = new HashSet<>();
			for (final Connection connection : board.connectionsFromCity.get(city)) {
				setCopy.add(new Connection(connection));
			}

			this.connectionsFromCity.put(city, setCopy);
		}

		this.forbiddenConnectionsForPlayer = new HashMap<>();
		for (final Integer owner : board.forbiddenConnectionsForPlayer.keySet()) {
			final Set<Connection> setCopy = new HashSet<>();
			for (final Connection connection : board.forbiddenConnectionsForPlayer.get(owner)) {
				setCopy.add(new Connection(connection));
			}

			this.forbiddenConnectionsForPlayer.put(owner, setCopy);
		}
	}

	public Set<Connection> getPossibleConnectionsForOwner(final int owner) {
		final Set<Connection> connections = new HashSet<>();

		for (final Connection connection : this.allConnections) {
			if (connection.owner == -1 && !this.forbiddenConnectionsForPlayer.get(owner).contains(connection)) {
				connections.add(connection);
			}
		}

		return connections;
	}

	public void giveOwnershipToPlayer(final Connection connection, final int owner, final int numPlayers) {
		connection.owner = owner;

		// forbid player from taking any other open route between the same two cities
		// if less than 4 players, forbid EVERY player from taking any other open route
		// between the same two cities
		for (final Connection otherConnection : this.allConnections) {
			if (!otherConnection.equals(connection) && otherConnection.owner == -1
					&& otherConnection.start.equals(connection.start) && otherConnection.end.equals(connection.end)) {

				if (numPlayers > 3) {
					this.forbiddenConnectionsForPlayer.get(owner).add(connection);
				} else {
					for (int i = 0; i < numPlayers; i++) {
						this.forbiddenConnectionsForPlayer.get(i).add(connection);
					}
				}
			}
		}
	}

	public void addConnection(final String start, final String end, final long length, final String color) {
		final Connection connection = new Connection(start, end, length, color);

		this.allConnections.add(connection);

		this.connectionsFromCity.putIfAbsent(start, new HashSet<>());
		this.connectionsFromCity.putIfAbsent(end, new HashSet<>());
		this.connectionsFromCity.get(start).add(connection);
		this.connectionsFromCity.get(end).add(connection);
	}

	public List<Connection> getConnectionsForPlayer(final int owner) {
		final List<Connection> connections = new ArrayList<>();

		for (final Connection connection : this.allConnections) {
			if (connection.owner == owner) {
				connections.add(connection);
			}
		}

		return connections;
	}

	public boolean isCompleteTicket(final DestinationTicket ticket, final int owner) {
		final Set<String> visited = new HashSet<>();
		final List<String> queue = new ArrayList<>();

		queue.add(ticket.getStart());

		while (!queue.isEmpty()) {
			final String city = queue.remove(0);

			if (city.equals(ticket.getEnd())) {
				return true;
			}

			visited.add(city);

			for (final Connection connection : this.connectionsFromCity.get(city)) {
				if (connection.owner == owner) {
					final String otherCity = connection.start.equals(city) ? connection.end : connection.start;

					if (!visited.contains(otherCity)) {
						queue.add(otherCity);
					}
				}
			}
		}

		return false;
	}

	public Connection getMatchingConnection(final Connection connection) {
		for (final Connection otherConnection : this.allConnections) {
			if (otherConnection.start.equals(connection.start) && otherConnection.end.equals(connection.end)
					&& otherConnection.length == connection.length && otherConnection.color.equals(connection.color)
					&& otherConnection.owner == connection.owner) {
				return otherConnection;
			}
		}

		return null;
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

		private Connection(final Connection connection) {
			this.start = connection.start;
			this.end = connection.end;
			this.length = connection.length;
			this.color = connection.color;
			this.owner = connection.owner;
		}

		public String getStart() {
			return this.start;
		}

		public String getEnd() {
			return this.end;
		}

		public long getLength() {
			return this.length;
		}

		public String getColor() {
			return this.color;
		}

		public int getOwner() {
			return this.owner;
		}
	}
}
