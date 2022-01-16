package state;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import data.DestinationTicket;

public class Player {

	private final Map<String, Integer> knownColorCards;
	private final Set<DestinationTicket> knownDestinationTickets;
	private int numUnknownColorCards;
	private int numUnknownDestinationTickets;
	private int numCarsRemaining;
	private int score;

	public Player(final int numStartingCars) {
		this.knownColorCards = new HashMap<>();
		this.knownDestinationTickets = new HashSet<>();
		this.numUnknownColorCards = 0;
		this.numUnknownDestinationTickets = 0;
		this.numCarsRemaining = numStartingCars;
		this.score = 0;
	}
}