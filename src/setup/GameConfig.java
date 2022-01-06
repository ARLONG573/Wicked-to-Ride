package setup;

import java.io.File;

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
		// TODO what to send to game driver?
	}
}
