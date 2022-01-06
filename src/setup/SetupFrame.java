package setup;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JFrame;

public class SetupFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final NumPlayersPanel numPlayersPanel = new NumPlayersPanel();
	private static final AwardsPanel awardsPanel = new AwardsPanel();
	private static final FileChooserPanel fileChooserPanel = new FileChooserPanel();

	public SetupFrame() {
		super("Game Setup");

		super.add(numPlayersPanel, BorderLayout.NORTH);
		super.add(awardsPanel, BorderLayout.CENTER);
		super.add(fileChooserPanel, BorderLayout.SOUTH);

		super.pack();
		super.setLocationRelativeTo(null);
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	static void startGame() {
		final int numPlayers = numPlayersPanel.getNumPlayers();
		final boolean isLongestRouteEnabled = awardsPanel.isLongestRouteEnabled();
		final boolean isGlobetrotterEnabled = awardsPanel.isGlobetrotterEnabled();
		final File configFile = fileChooserPanel.getConfigFile();

		final GameConfig gameConfig = new GameConfig(numPlayers, isLongestRouteEnabled, isGlobetrotterEnabled,
				configFile);
		gameConfig.startDriver();
	}
}
