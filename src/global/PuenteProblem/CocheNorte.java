package global.PuenteProblem;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

class CocheNorte extends Thread {
	private final int id;
	private final Puente puente;
	private final JLabel cocheLabel;

	public CocheNorte(int id, Puente puente, JLabel cocheLabel) {
		this.id = id;
		this.puente = puente;
		this.cocheLabel = cocheLabel;
	}

	public void run() {
		try {
			while (true) {
				Thread.sleep((int)(Math.random() * 4000) + 1000); // Esperando
				puente.entrarNorte(id, cocheLabel);
				Thread.sleep((int)(Math.random() * 2000) + 1000); // Cruzando
				puente.salirNorte(id, cocheLabel);
			}
		} catch (InterruptedException e) {
			SwingUtilities.invokeLater(() -> cocheLabel.setText("CocheNorte-" + (id+1) + ": Interrumpido"));
		}
	}
}