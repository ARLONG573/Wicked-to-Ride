package state;

import java.util.List;

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

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}
}
