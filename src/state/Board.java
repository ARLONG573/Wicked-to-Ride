package state;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
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
		this.connectionsFromCity = new HashMap<>();

		for (final Connection connection : board.allConnections) {
			final Connection newConnection = new Connection(connection.start, connection.end, connection.length,
					connection.color, connection.id, connection.owner);

			this.allConnections.add(newConnection);

			this.connectionsFromCity.putIfAbsent(newConnection.start, new HashSet<>());
			this.connectionsFromCity.putIfAbsent(newConnection.end, new HashSet<>());
			this.connectionsFromCity.get(newConnection.start).add(newConnection);
			this.connectionsFromCity.get(newConnection.end).add(newConnection);
		}

		this.forbiddenConnectionsForPlayer = new HashMap<>();
		for (final Integer owner : board.forbiddenConnectionsForPlayer.keySet()) {
			final Set<Connection> setCopy = new HashSet<>();
			for (final Connection connection : board.forbiddenConnectionsForPlayer.get(owner)) {
				setCopy.add(this.getMatchingConnection(connection));
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

	public boolean isReasonableConnectionForOwner(final Connection connection, final Player player, final int owner) {
		// a useful connection is one that lowers the minimum number of connections
		// required to completed all known tickets
		int total = 0;
		for (final DestinationTicket ticket : player.getKnownDestinationTickets()) {
			total += this.getMinConnectionsBetween(ticket.getStart(), ticket.getEnd(), owner);
		}

		connection.owner = owner;
		int newTotal = 0;
		for (final DestinationTicket ticket : player.getKnownDestinationTickets()) {
			newTotal += this.getMinConnectionsBetween(ticket.getStart(), ticket.getEnd(), owner);
		}

		return newTotal < total;
	}

	public int getMinConnectionsBetween(final String start, final String end, final int owner) {
		// implementation of Dijkstra's algorithm where an open connection has weight 1
		// and an owned connection has weight 0

		final Set<String> allCities = this.connectionsFromCity.keySet();

		// assign initial distance values
		final Map<String, Integer> dist = new HashMap<>();
		for (final String city : allCities) {
			if (city.equals(start)) {
				dist.put(city, 0);
			} else {
				dist.put(city, 1000);
			}
		}

		// initialize queue
		final PriorityQueue<String> queue = new PriorityQueue<>(new Comparator<>() {
			@Override
			public int compare(final String city1, String city2) {
				return dist.get(city1).compareTo(dist.get(city2));
			}
		});

		for (final String city : allCities) {
			queue.add(city);
		}

		while (!queue.isEmpty()) {
			final String current = queue.poll();
			System.out.println("current = " + current);

			for (final Connection connection : this.connectionsFromCity.get(current)) {
				final String neighbor = connection.start.equals(current) ? connection.end : connection.start;
				if (queue.contains(neighbor)) {
					int alt = dist.get(current);

					if (connection.owner == -1) {
						alt += 1;
					} else if (connection.owner != owner) {
						alt += 1000;
					}

					if (alt < dist.get(neighbor)) {
						dist.put(neighbor, alt);
						
						//re-prioritize
						queue.remove(neighbor);
						queue.add(neighbor);
					}
				}
			}

			if (current.equals(end)) {
				return dist.get(end);
			}
			
		}

		return dist.get(end);
	}

	public Set<Connection> getReasonableConnectionsForOwner(final Player player, final int owner) {
		final Set<Connection> reasonableConnections = new HashSet<>();

		for (final Connection possible : this.getPossibleConnectionsForOwner(owner)) {
			if (this.isReasonableConnectionForOwner(possible, player, owner)) {
				reasonableConnections.add(possible);
			}
		}

		return reasonableConnections;
	}

	public Set<Connection> getForbiddenConnectionsForPlayer(final int player) {
		return this.forbiddenConnectionsForPlayer.get(player);
	}

	public void giveOwnershipToPlayer(final Connection connection, final int owner, final int numPlayers) {
		connection.owner = owner;

		// forbid player from taking any other open route between the same two cities
		// if less than 4 players, forbid EVERY player from taking any other open route
		// between the same two cities
		for (final Connection otherConnection : this.allConnections) {
			if (otherConnection.owner == -1 && otherConnection.start.equals(connection.start)
					&& otherConnection.end.equals(connection.end) && otherConnection.id != connection.id) {

				if (numPlayers > 3) {
					this.forbiddenConnectionsForPlayer.get(owner).add(otherConnection);
				} else {
					for (int i = 0; i < numPlayers; i++) {
						this.forbiddenConnectionsForPlayer.get(i).add(otherConnection);
					}
				}
			}
		}
	}

	private boolean alreadyHasConnectionBetweenCities(final String start, final String end) {
		for (final Connection connection : this.allConnections) {
			if (connection.start.equals(start) && connection.end.equals(end)) {
				return true;
			}
		}

		return false;
	}

	public void addConnection(final String start, final String end, final long length, final String color) {
		Connection connection = null;
		if (this.alreadyHasConnectionBetweenCities(start, end)) {
			connection = new Connection(start, end, length, color, 2, -1);
		} else {
			connection = new Connection(start, end, length, color, 1, -1);
		}

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

	public boolean playerOwnsCity(final String city, final int owner) {
		for (final Connection connection : this.connectionsFromCity.get(city)) {
			if (connection.owner == owner) {
				return true;
			}
		}

		return false;
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
					&& otherConnection.id == connection.id && otherConnection.owner == connection.owner) {
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
		private final int id; // used to tell apart double routes with the same length and color

		private int owner;

		private Connection(final String start, final String end, final long length, final String color, final int id,
				final int owner) {
			this.start = start;
			this.end = end;
			this.length = length;
			this.color = color;
			this.id = id;
			this.owner = owner;
		}

		private Connection(final Connection connection) {
			this.start = connection.start;
			this.end = connection.end;
			this.length = connection.length;
			this.color = connection.color;
			this.id = connection.id;
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

		public int getId() {
			return this.id;
		}
	}
}
