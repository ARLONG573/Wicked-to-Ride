package state;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import data.DestinationTicket;

public class Player {

	private final Map<String, Integer> colorCards;
	private final Set<DestinationTicket> destinationTickets;
	private int numCarsRemaining;
	private int score;

	public Player(final int numStartingCars) {
		this.colorCards = new HashMap<>();
		this.destinationTickets = new HashSet<>();
		this.numCarsRemaining = numStartingCars;
		this.score = 0;
	}
}
