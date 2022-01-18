package scoring;

import java.util.ArrayList;
import java.util.List;

import data.DestinationTicket;
import state.Board;
import state.Player;

public class Scoring {

	public static void doEndGameScoring(final Player[] players, final Board board, final long longestRoutePoints,
			final long globetrotterPoints) {

		// destination tickets
		for (int i = 0; i < players.length; i++) {
			final Player player = players[i];

			for (final DestinationTicket ticket : player.getKnownDestinationTickets()) {
				if (board.isCompleteTicket(ticket, i)) {
					player.addScore(ticket.getPoints());
					player.setNumCompletedTickets(player.getNumCompletedTickets() + 1);
				} else {
					player.addScore(-ticket.getPoints());
				}
			}
		}

		// longest route
		if (longestRoutePoints > 0) {
			for (int i = 0; i < players.length; i++) {
				final Player player = players[i];

				player.setLongestRouteLength(board.getLongestRouteLengthForPlayer(i));
			}
			
			final List<Player> longestRouteWinners = new ArrayList<>();

			for (final Player player : players) {
				if (longestRouteWinners.isEmpty()) {
					longestRouteWinners.add(player);
				} else {
					final Player bestPlayer = longestRouteWinners.get(0);

					if (player.getLongestRouteLength() > bestPlayer.getLongestRouteLength()) {
						longestRouteWinners.clear();
						longestRouteWinners.add(player);
					} else if (player.getLongestRouteLength() == bestPlayer.getLongestRouteLength()) {
						longestRouteWinners.add(player);
					}
				}
			}

			for (final Player player : longestRouteWinners) {
				player.addScore(longestRoutePoints);
			}
		}

		// globetrotter
		if (globetrotterPoints > 0) {
			final List<Player> globetrotterWinners = new ArrayList<>();

			for (final Player player : players) {
				if (globetrotterWinners.isEmpty()) {
					globetrotterWinners.add(player);
				} else {
					final Player bestPlayer = globetrotterWinners.get(0);

					if (player.getNumCompletedTickets() > bestPlayer.getNumCompletedTickets()) {
						globetrotterWinners.clear();
						globetrotterWinners.add(player);
					} else if (player.getNumCompletedTickets() == bestPlayer.getNumCompletedTickets()) {
						globetrotterWinners.add(player);
					}
				}
			}

			for (final Player player : globetrotterWinners) {
				player.addScore(globetrotterPoints);
			}
		}
	}
}
