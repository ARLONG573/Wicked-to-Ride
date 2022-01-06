package setup;

import java.awt.BorderLayout;

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
		// TODO
		System.out.println("There are " + numPlayersPanel.getNumPlayers() + " players");
		System.out.println("Longest Route: " + awardsPanel.isLongestRouteEnabled());
		System.out.println("Globetrotter: " + awardsPanel.isGlobetrotterEnabled());
		System.out.println("Config File: " + fileChooserPanel.getConfigFile());
	}
}
