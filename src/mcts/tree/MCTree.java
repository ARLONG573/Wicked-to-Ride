package mcts.tree;

import java.util.List;

import mcts.api.GameState;

/**
 * This class stores the tree structure and performs the tree search.
 * 
 * @author Aaron Tetens
 */
public class MCTree {

	// exploration constant
	private static final double C = Math.sqrt(2);

	private final MCNode root;
	private final int seconds;
	private final int simulationTimeout;

	public MCTree(final GameState initialState, final int seconds, final int simulationTimeout) {
		this.root = new MCNode(initialState, null);
		this.seconds = seconds;
		this.simulationTimeout = simulationTimeout;
	}

	/**
	 * Perform MCTS from the root node. The number of iterations that are executed
	 * depends on the value given when this MCTree was created.
	 * 
	 * @return The GameState that is the result of performing the move suggested by
	 *         the search.
	 */
	public GameState search() {
		// add the first set of children to the root node
		this.root.expand();

		// perform for given amount of time
		final long startTime = System.currentTimeMillis();
		int numIterations = 0;

		while (System.currentTimeMillis() < startTime + (1000 * this.seconds)) {
			// selection + possible expansion
			final MCNode choice = this.selectNode();

			// simulation
			final List<Integer> result = choice.simulate(this.simulationTimeout);

			// backpropogation
			this.update(choice, result);

			numIterations++;
		}

		System.out.println("Performed " + numIterations + " iterations in "
				+ (System.currentTimeMillis() - startTime) / 1000.0 + " seconds");

		// choose the node with the most simulations
		return this.suggestMove();
	}

	/**
	 * This method uses the UCB1 formula to pick successive child nodes until an
	 * unvisited leaf node is reached. Expansion will happen automatically as
	 * necessary.
	 * 
	 * @return The node selected for simulation
	 */
	private MCNode selectNode() {
		// pick successive child nodes until we find a leaf node
		MCNode curr = this.root;

		while (!curr.getChildren().isEmpty()) {
			curr = this.pickChild(curr);
		}

		// if the leaf node has never been visited before, return it for simulation
		if (curr.getNumIterations() == 0) {
			return curr;
		}

		// if the leaf node has been visited before, expand it and return a random child
		// (or return it if players win at the node)
		curr.expand();

		if (curr.getChildren().isEmpty()) {
			return curr;
		}

		return curr.getRandomChild();
	}

	/**
	 * This method uses UCB1 to pick the best child node from the given parent.
	 * 
	 * @param parent The parent whose children we are picking from
	 * @return The best child node of the given parent node
	 */
	private MCNode pickChild(final MCNode parent) {
		double bestUCB1 = -Double.MAX_VALUE;
		MCNode bestChild = null;

		for (final MCNode child : parent.getChildren()) {
			// if the child has no iterations yet, pick it
			if (child.getNumIterations() == 0) {
				return child;
			}

			final double childUCB1 = this.UCB1(child);

			if (childUCB1 >= bestUCB1) {
				bestUCB1 = childUCB1;
				bestChild = child;
			}
		}

		return bestChild;
	}

	/**
	 * This method computes the UCB1 value for a given node.
	 * 
	 * @param node The node to evaluate
	 * @return The UCB1 score for the given node
	 */
	private double UCB1(final MCNode node) {
		if (node.hasImmediateLoss()) {
			return -Double.MAX_VALUE;
		}

		final double exploitation = node.getNumWins() / node.getNumIterations();
		final double exploration = C
				* Math.sqrt(Math.log(node.getParent().getNumIterations()) / node.getNumIterations());

		return exploitation + exploration;
	}

	/**
	 * This method performs backpropogation to update the simulated node, as well as
	 * all of its parents in the tree.
	 * 
	 * @param node      The node from which we ran the simulation
	 * @param simResult The players who won the simulation
	 */
	private void update(final MCNode node, final List<Integer> simResult) {
		// if node is a finished game state such that its parent is not in the winning
		// result, mark that parent as having an immediate loss so that it is never
		// visited again
		final List<Integer> nodeResult = node.getGameState().getWinningPlayers();
		if (!nodeResult.isEmpty() && !nodeResult.contains(node.getParent().getGameState().getLastPlayer())) {
			node.getParent().markImmediateLoss();
		}

		MCNode curr = node;

		while (curr != null) {
			curr.addResult(simResult);
			curr = curr.getParent();
		}
	}

	/**
	 * This method is called once MCTS is complete; it picks the move that got the
	 * most simulations.
	 * 
	 * @return The game state after the suggested move is made
	 */
	private GameState suggestMove() {
		MCNode bestChild = null;

		for (final MCNode child : this.root.getChildren()) {
			if (bestChild == null) {
				bestChild = child;
			} else {
				if (child.getNumIterations() > bestChild.getNumIterations()) {
					bestChild = child;
				}
			}
		}

		System.out.println("Expected value = " + (bestChild.getNumWins() / bestChild.getNumIterations()) * 10 + "%");

		return bestChild.getGameState();
	}
}
