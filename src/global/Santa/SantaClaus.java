package global.Santa;

import java.util.concurrent.Semaphore;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

class SantaClaus extends Thread {
	private final JLabel santaLabel;
	private final JLabel[] renoLabels;
	private final JLabel[] duendeLabels;
	private int renosListos = 0;
	private int duendesEsperando = 0;
	private final Semaphore mutex = new Semaphore(1);
	private final Semaphore santaSem = new Semaphore(0);
	private final Semaphore renosSem = new Semaphore(0);
	private final Semaphore duendesSem = new Semaphore(0);
	private final Semaphore duendesMutex = new Semaphore(1);

	public SantaClaus(JLabel santaLabel, JLabel[] renoLabels, JLabel[] duendeLabels) {
		this.santaLabel = santaLabel;
		this.renoLabels = renoLabels;
		this.duendeLabels = duendeLabels;
	}

	public void run() {
		try {
			while (true) {
				santaSem.acquire();
				mutex.acquire();
				if (renosListos == 9) {
					SwingUtilities.invokeLater(() -> santaLabel.setText("Santa: Preparando el trineo y repartiendo regalos"));
					for (int i = 0; i < 9; i++) {
						int idx = i;
						SwingUtilities.invokeLater(() -> renoLabels[idx].setText("Reno-" + (idx+1) + ": Enganchado al trineo"));
						renosSem.release();
					}
					Thread.sleep(2000);
					for (int i = 0; i < 9; i++) {
						int idx = i;
						SwingUtilities.invokeLater(() -> renoLabels[idx].setText("Reno-" + (idx+1) + ": De vacaciones"));
					}
					renosListos = 0;
				} else if (duendesEsperando == 3) {
					SwingUtilities.invokeLater(() -> santaLabel.setText("Santa: Ayudando a duendes"));
					for (int i = 0; i < 3; i++) duendesSem.release();
					Thread.sleep(1500);
					for (int i = 0, ayudados = 0; i < duendeLabels.length && ayudados < 3; i++) {
						if (duendeLabels[i].getText().contains("Esperando")) {
							int idx = i;
							SwingUtilities.invokeLater(() -> duendeLabels[idx].setText("Duende-" + (idx+1) + ": Trabajando"));
							ayudados++;
						}
					}
					duendesEsperando = 0;
					duendesMutex.release();
				}
				SwingUtilities.invokeLater(() -> santaLabel.setText("Santa: Durmiendo"));
				mutex.release();
			}
		} catch (InterruptedException e) {
			SwingUtilities.invokeLater(() -> santaLabel.setText("Santa: Interrumpido"));
		}
	}

	// Métodos para que los renos y duendes interactúen
	public void renoLlega(int id, JLabel renoLabel) throws InterruptedException {
		mutex.acquire();
		renosListos++;
		SwingUtilities.invokeLater(() -> renoLabel.setText("Reno-" + (id+1) + ": Listo"));
		if (renosListos == 9) santaSem.release();
		mutex.release();
		renosSem.acquire();
	}

	public void duendeProblema(int id, JLabel duendeLabel) throws InterruptedException {
		duendesMutex.acquire();
		mutex.acquire();
		duendesEsperando++;
		SwingUtilities.invokeLater(() -> duendeLabel.setText("Duende-" + (id+1) + ": Esperando ayuda"));
		if (duendesEsperando == 3) santaSem.release();
		else duendesMutex.release();
		mutex.release();
		duendesSem.acquire();
	}
}