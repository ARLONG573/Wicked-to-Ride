package mcts;

import mcts.api.GameState;
import mcts.tree.MCTree;

/**
 * This is the class that drives the tree search. Classes that wish to use this
 * project's tree search will simply call {@link MCTS#search(GameState, int)} to
 * find the suggested move.
 * 
 * @author Aaron Tetens
 */
public class MCTS {

	/**
	 * This method drives the tree search operation by initializing a search tree
	 * with the given GameState and telling the tree to execute MCTS for the given
	 * number of seconds.
	 * 
	 * @param initialState      The state from which to start the search
	 * @param seconds           The number of seconds that should be spent searching
	 * @param simulationTimeout The maximum amount of time a simulation is allowed
	 *                          to take before being considered "dead" (in a state
	 *                          where the game cannot possibly end)
	 * @return The GameState that is the result of performing the move suggested by
	 *         the search
	 */
	public static GameState search(final GameState initialState, final int seconds, final int simulationTimeout) {
		final MCTree tree = new MCTree(initialState, seconds, simulationTimeout);
		return tree.search();
	}
}
