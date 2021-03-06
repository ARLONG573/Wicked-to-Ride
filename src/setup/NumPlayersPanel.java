package setup;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

class NumPlayersPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final JLabel numPlayersLabel;

	private final JRadioButton twoButton;
	private final JRadioButton threeButton;
	private final JRadioButton fourButton;
	private final JRadioButton fiveButton;

	private int numPlayers;

	NumPlayersPanel() {
		this.numPlayersLabel = new JLabel("Number of Players:");
		super.add(this.numPlayersLabel);

		this.twoButton = new JRadioButton("2");
		this.twoButton.addActionListener((e) -> {
			this.numPlayers = 2;
		});

		this.threeButton = new JRadioButton("3");
		this.threeButton.addActionListener((e) -> {
			this.numPlayers = 3;
		});

		this.fourButton = new JRadioButton("4");
		this.fourButton.addActionListener((e) -> {
			this.numPlayers = 4;
		});

		this.fiveButton = new JRadioButton("5");
		this.fiveButton.addActionListener((e) -> {
			this.numPlayers = 5;
		});

		final ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(this.twoButton);
		buttonGroup.add(this.threeButton);
		buttonGroup.add(this.fourButton);
		buttonGroup.add(this.fiveButton);

		super.add(this.twoButton);
		super.add(this.threeButton);
		super.add(this.fourButton);
		super.add(this.fiveButton);

		this.twoButton.doClick();
	}

	int getNumPlayers() {
		return this.numPlayers;
	}
}
