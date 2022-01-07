package setup;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileSystemView;

class FileChooserPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final JLabel configFileLabel;
	private final JTextArea configFileDisplayArea;
	private final JButton browseButton;
	private final JButton startButton;

	private File configFile;

	FileChooserPanel() {
		this.configFileLabel = new JLabel("Config File:");
		super.add(this.configFileLabel);

		this.configFileDisplayArea = new JTextArea();
		this.configFileDisplayArea.setEnabled(false);
		super.add(this.configFileDisplayArea);

		this.browseButton = new JButton("Browse");
		this.browseButton.addActionListener((e) -> {
			this.selectConfigFile();
		});
		super.add(this.browseButton);

		this.startButton = new JButton("Start");
		this.startButton.addActionListener((e) -> {
			SetupFrame.getInstance().startGame();
		});
		super.add(this.startButton);

		this.configFile = null;
	}

	File getConfigFile() {
		return this.configFile;
	}

	private void selectConfigFile() {
		final JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		final int returnValue = fileChooser.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			this.configFile = fileChooser.getSelectedFile();
			this.configFileDisplayArea.setText(this.configFile.getAbsolutePath());

			final JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
			parentFrame.pack();
			parentFrame.setLocationRelativeTo(null);
		}
	}
}
