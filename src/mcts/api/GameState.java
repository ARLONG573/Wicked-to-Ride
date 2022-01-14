package mcts.api;

import java.util.List;

/**
 * Any game that wishes to use this MCTS implementation must implement its game
 * state in this way so that the operations necessary for the search may be
 * carried out.
 * 
 * @author Aaron Tetens
 */
public interface GameState {

	/**
	 * @return The id of the player who made the move to achieve this game state
	 */
	public int getLastPlayer();

	/**
	 * @return A list of all possible game states that can occur after one move (for
	 *         node expansion purposes)
	 */
	public List<GameState> getNextStates();

	/**
	 * @return A single possible next game state, chosen randomly (for simulation
	 *         purposes)
	 */
	public GameState getRandomNextState();

	/**
	 * @return A list of the ids of the players who have won in this position - a
	 *         position that represents an unfinished game should return an empty
	 *         list
	 */
	public List<Integer> getWinningPlayers();
}
