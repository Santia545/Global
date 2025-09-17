package global.PuenteProblem;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

class CocheSur extends Thread {
	private final int id;
	private final Puente puente;
	private final JLabel cocheLabel;

	public CocheSur(int id, Puente puente, JLabel cocheLabel) {
		this.id = id;
		this.puente = puente;
		this.cocheLabel = cocheLabel;
	}

	public void run() {
		try {
			while (true) {
				Thread.sleep((int)(Math.random() * 4000) + 1000); // Esperando
				puente.entrarSur(id, cocheLabel);
				Thread.sleep((int)(Math.random() * 2000) + 1000); // Cruzando
				puente.salirSur(id, cocheLabel);
			}
		} catch (InterruptedException e) {
			SwingUtilities.invokeLater(() -> cocheLabel.setText("CocheSur-" + (id+1) + ": Interrumpido"));
		}
	}
}