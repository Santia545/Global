package global.Santa;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

class Duende extends Thread {
	private final int id;
	private final SantaClaus santa;
	private final JLabel duendeLabel;

	public Duende(int id, SantaClaus santa, JLabel duendeLabel) {
		this.id = id;
		this.santa = santa;
		this.duendeLabel = duendeLabel;
	}

	public void run() {
		try {
			while (true) {
				Thread.sleep((int)(Math.random() * 4000) + 1000); // Trabajando
				santa.duendeProblema(id, duendeLabel);
			}
		} catch (InterruptedException e) {
			SwingUtilities.invokeLater(() -> duendeLabel.setText("Duende-" + (id+1) + ": Interrumpido"));
		}
	}
}