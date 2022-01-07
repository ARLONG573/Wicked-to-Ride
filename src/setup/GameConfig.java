package setup;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import driver.GameDriver;

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

	void startDriver() {
		JSONObject obj = null;
		try {
			obj = (JSONObject) new JSONParser().parse(new FileReader(this.configFile));
		} catch (final IOException | ParseException e) {
			e.printStackTrace();
			System.exit(1);
		}

		final long numCarsPerPlayer = (Long) obj.get("numCarsPerPlayer");

		// color deck

		// destination ticket deck

		// board

		final Map<String, Long> awardsMap = (Map<String, Long>) obj.get("awards");
		final long longestRoutePoints = this.isLongestRouteEnabled ? awardsMap.get("LONGEST ROUTE") : 0;
		final long globetrotterPoints = this.isGlobetrotterEnabled ? awardsMap.get("GLOBETROTTER") : 0;

		GameDriver.runGame(this.numPlayers, numCarsPerPlayer, null, null, null, longestRoutePoints, globetrotterPoints);
	}
}
