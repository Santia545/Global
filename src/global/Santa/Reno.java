package global.Santa;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

class Reno extends Thread {
	private final int id;
	private final SantaClaus santa;
	private final JLabel renoLabel;

	public Reno(int id, SantaClaus santa, JLabel renoLabel) {
		this.id = id;
		this.santa = santa;
		this.renoLabel = renoLabel;
	}

	public void run() {
		try {
			while (true) {
				Thread.sleep((int)(Math.random() * 5000) + 2000); // Vacaciones
				santa.renoLlega(id, renoLabel);
			}
		} catch (InterruptedException e) {
			SwingUtilities.invokeLater(() -> renoLabel.setText("Reno-" + (id+1) + ": Interrumpido"));
		}
	}
}