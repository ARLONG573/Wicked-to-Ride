package setup;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class SetupFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private final NumPlayersPanel numPlayersPanel;
	private final AwardsPanel awardsPanel;
	private final FileChooserPanel fileChooserPanel;

	public SetupFrame() {
		super("Game Setup");

		this.numPlayersPanel = new NumPlayersPanel();
		super.add(this.numPlayersPanel, BorderLayout.NORTH);

		this.awardsPanel = new AwardsPanel();
		super.add(this.awardsPanel, BorderLayout.CENTER);

		this.fileChooserPanel = new FileChooserPanel();
		super.add(this.fileChooserPanel, BorderLayout.SOUTH);

		super.pack();
		super.setLocationRelativeTo(null);
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	static void startGame() {
		//TODO
		System.out.println("Starting game!");
	}
}
