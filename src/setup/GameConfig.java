package setup;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import data.DestinationTicket;
import driver.GameDriver;
import state.Board;
import state.ColorDeck;
import state.DestinationTicketDeck;

public class GameConfig {

	private final int numPlayers;
	private final boolean isLongestRouteEnabled;
	private final boolean isGlobetrotterEnabled;
	private final File configFile;

	GameConfig(final int numPlayers, final boolean isLongestRouteEnabled, final boolean isGlobetrotterEnabled,
			final File configFile) {

		this.numPlayers = numPlayers;
		this.isLongestRouteEnabled = isLongestRouteEnabled;
		this.isGlobetrotterEnabled = isGlobetrotterEnabled;
		this.configFile = configFile;
	}

	@SuppressWarnings("unchecked")
	void startDriver() {
		JSONObject obj = null;
		try {
			obj = (JSONObject) new JSONParser().parse(new FileReader(this.configFile));
		} catch (final IOException | ParseException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// number of cars per player
		final long numCarsPerPlayer = (Long) obj.get("numCarsPerPlayer");

		// color deck
		final Map<String, Long> colorsMap = (Map<String, Long>) obj.get("deckDistribution");
		final ColorDeck colorDeck = new ColorDeck();

		for (final Map.Entry<String, Long> entry : colorsMap.entrySet()) {
			colorDeck.initColor(entry.getKey(), entry.getValue());
		}

		// destination ticket deck
		final DestinationTicketDeck destinationTicketDeck = new DestinationTicketDeck();

		final JSONArray destinationTicketArray = (JSONArray) obj.get("destinationTickets");
		for (final Object destinationTicketObj : destinationTicketArray) {
			final JSONObject destinationTicketJSONObj = (JSONObject) destinationTicketObj;

			final String start = (String) destinationTicketJSONObj.get("START");
			final String end = (String) destinationTicketJSONObj.get("END");
			final long points = (Long) destinationTicketJSONObj.get("POINTS");

			final DestinationTicket destinationTicket = new DestinationTicket(start, end, points);
			destinationTicketDeck.initDestinationTicket(destinationTicket);
		}

		// board
		final Board board = new Board(numPlayers);

		final JSONArray connectionsArray = (JSONArray) obj.get("connections");
		for (final Object connectionObj : connectionsArray) {
			final JSONObject connectionJSONObj = (JSONObject) connectionObj;

			final String start = (String) connectionJSONObj.get("START");
			final String end = (String) connectionJSONObj.get("END");
			final long length = (Long) connectionJSONObj.get("LENGTH");
			final String color = (String) connectionJSONObj.get("COLOR");

			board.addConnection(start, end, length, color);
		}

		// points for awards
		final Map<String, Long> awardsMap = (Map<String, Long>) obj.get("awards");
		final long longestRoutePoints = this.isLongestRouteEnabled ? awardsMap.get("LONGEST ROUTE") : 0;
		final long globetrotterPoints = this.isGlobetrotterEnabled ? awardsMap.get("GLOBETROTTER") : 0;

		GameDriver.runGame(this.numPlayers, numCarsPerPlayer, colorDeck, destinationTicketDeck, board,
				longestRoutePoints, globetrotterPoints);
	}
}
