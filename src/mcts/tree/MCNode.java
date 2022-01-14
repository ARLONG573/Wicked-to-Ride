package mcts.tree;

import java.util.ArrayList;
import java.util.List;

import mcts.api.GameState;

/**
 * This class represents a node within the MCTree. A node stores the game state
 * that it represents, as well as the number of wins found on it (for
 * exploitation), the number of iterations run on it (for exploration), and its
 * parent (for backpropogation).
 * 
 * @author Aaron Tetens
 */
class MCNode {

	private final GameState gameState;
	private final MCNode parent;
	private final List<MCNode> children;

	private double numWins; // numWins is a double to keep track of winning ties with other players
	private int numIterations;
	private boolean hasImmediateLoss;

	MCNode(final GameState gameState, final MCNode parent) {
		this.gameState = gameState;
		this.parent = parent;
		this.children = new ArrayList<>();
		this.numWins = 0.0;
		this.numIterations = 0;
		this.hasImmediateLoss = false;
	}

	/**
	 * This method adds a child node for each possible next game state.
	 */
	void expand() throws IllegalStateException {
		if (!this.children.isEmpty()) {
			throw new IllegalStateException("Tried to expand a non-leaf node!");
		}

		final List<GameState> nextStates = this.gameState.getNextStates();

		for (final GameState nextState : nextStates) {
			this.children.add(new MCNode(nextState, this));
		}
	}

	/**
	 * @return A random child node
	 */
	MCNode getRandomChild() {
		final int randomIndex = (int) (Math.random() * this.children.size());
		return this.children.get(randomIndex);
	}

	/**
	 * @param simulationTimeout
	 *            The amount of seconds that a simulation may run before being
	 *            considered "dead" (the game is in a state where it can never be
	 *            finished)
	 * @return A list of integers representing the players who won the random
	 *         playout
	 */
	List<Integer> simulate(final int simulationTimeout) {
		GameState curr = this.gameState;

		final long startTime = System.currentTimeMillis();

		// while no one has won, pick a random next possible state
		while (curr.getWinningPlayers().isEmpty()) {
			curr = curr.getRandomNextState();

			// a timeout counts as a win for nobody
			if (System.currentTimeMillis() > startTime + (1000 * simulationTimeout)) {
				return new ArrayList<>();
			}
		}

		return curr.getWinningPlayers();
	}

	/**
	 * Adds the result of the simulation to this node. If this node is among a group
	 * of tying winners, the value of the win is split evenly.
	 * 
	 * @param result
	 *            The result of the simulation.
	 */
	void addResult(final List<Integer> result) {
		if (result.contains(this.gameState.getLastPlayer())) {
			this.numWins += (10.0 / result.size());
		}

		this.numIterations++;
	}

	/**
	 * @return Whether or not this node has any child node such that this node has
	 *         not won.
	 */
	boolean hasImmediateLoss() {
		return this.hasImmediateLoss;
	}

	/**
	 * This method marks this node has having a loss on the next move; this node
	 * will no longer be chosen during the selection phase.
	 */
	void markImmediateLoss() {
		this.hasImmediateLoss = true;
	}

	/**
	 * @return The parent of this node
	 */
	MCNode getParent() {
		return this.parent;
	}

	/**
	 * @return A list of the child nodes of this node
	 */
	List<MCNode> getChildren() {
		return this.children;
	}

	/**
	 * @return The number of wins that have passed through this node
	 */
	double getNumWins() {
		return this.numWins;
	}

	/**
	 * @return The number of iterations that have passed through this node
	 */
	int getNumIterations() {
		return this.numIterations;
	}

	/**
	 * @return The game state that this node represents
	 */
	GameState getGameState() {
		return this.gameState;
	}
}
