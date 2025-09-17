package global.PuenteProblem;

import java.util.concurrent.Semaphore;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

class Puente {
	private int cochesNorte = 0;
	private int cochesSur = 0;
	private final Semaphore puente = new Semaphore(1);
	private final Semaphore mutexN = new Semaphore(1);
	private final Semaphore mutexS = new Semaphore(1);
	private final JLabel puenteLabel;

	public Puente(JLabel puenteLabel) {
		this.puenteLabel = puenteLabel;
	}

	public void entrarNorte(int id, JLabel cocheLabel) throws InterruptedException {
		mutexN.acquire();
		if (cochesNorte == 0) puente.acquire();
		cochesNorte++;
		SwingUtilities.invokeLater(() -> {
			cocheLabel.setText("CocheNorte-" + (id+1) + ": Cruzando");
			puenteLabel.setText("Puente: Norte->Sur, " + cochesNorte + " cruzando");
		});
		mutexN.release();
	}

	public void salirNorte(int id, JLabel cocheLabel) throws InterruptedException {
		mutexN.acquire();
		cochesNorte--;
		SwingUtilities.invokeLater(() -> cocheLabel.setText("CocheNorte-" + (id+1) + ": Salió"));
		if (cochesNorte == 0) {
			SwingUtilities.invokeLater(() -> puenteLabel.setText("Puente: Libre"));
			puente.release();
		}
		mutexN.release();
	}

	public void entrarSur(int id, JLabel cocheLabel) throws InterruptedException {
		mutexS.acquire();
		if (cochesSur == 0) puente.acquire();
		cochesSur++;
		SwingUtilities.invokeLater(() -> {
			cocheLabel.setText("CocheSur-" + (id+1) + ": Cruzando");
			puenteLabel.setText("Puente: Sur->Norte, " + cochesSur + " cruzando");
		});
		mutexS.release();
	}

	public void salirSur(int id, JLabel cocheLabel) throws InterruptedException {
		mutexS.acquire();
		cochesSur--;
		SwingUtilities.invokeLater(() -> cocheLabel.setText("CocheSur-" + (id+1) + ": Salió"));
		if (cochesSur == 0) {
			SwingUtilities.invokeLater(() -> puenteLabel.setText("Puente: Libre"));
			puente.release();
		}
		mutexS.release();
	}
}