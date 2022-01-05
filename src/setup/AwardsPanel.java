package setup;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

class AwardsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private final JLabel activeAwardsLabel;
	private final JCheckBox longestRouteCheckbox;
	private final JCheckBox globetrotterCheckbox;

	AwardsPanel() {
		this.activeAwardsLabel = new JLabel("Active Awards:");
		super.add(this.activeAwardsLabel);
		
		this.longestRouteCheckbox = new JCheckBox("Longest Route");
		super.add(this.longestRouteCheckbox);
		
		this.globetrotterCheckbox = new JCheckBox("Globetrotter");
		super.add(this.globetrotterCheckbox);
	}
}
