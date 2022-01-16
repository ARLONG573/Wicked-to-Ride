package state;

import java.util.List;
import java.util.Scanner;

import mcts.api.GameState;

public class TicketToRideState implements GameState {

	public TicketToRideState(final int numPlayers, final long numCarsPerPlayer, final ColorDeck colorDeck,
			final DestinationTicketDeck destinationTicketDeck, final Board board, final long longestRoutePoints,
			final long globetrotterPoints) {
		// TODO
		this.dealStartingHands();
	}

	public int getCurrentPlayer() {
		// TODO
		return 0;
	}

	/**
	 * This method is called after the AI takes its turn. There are 3 possible ways
	 * that this function resolves:
	 * 
	 * If the AI just took a connection, then no resolution is required. If the AI
	 * just took some destination tickets, then the destination tickets that were
	 * drawn need to be input by the user. If the AI just took 2 color cards from
	 * the top of the deck, then the color cards that were drawn need to be input by
	 * the user.
	 * 
	 * @param aiPlayerIndex The index of the AI in the players array
	 * @param in            The scanner used to read in color cards, if necessary
	 */
	public void resolveUnknownsForAI(final int aiPlayerIndex, final Scanner in) {
		// TODO
	}

	private void dealStartingHands() {
		// TODO
	}

	@Override
	public int getLastPlayer() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<GameState> getNextStates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GameState getRandomNextState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getWinningPlayers() {
		// TODO Auto-generated method stub
		return null;
	}
}
