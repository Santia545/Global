package global;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Semaphore;

public class PuenteMain {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new PuenteGUI());
	}
}

class PuenteGUI extends JFrame {
	private final JLabel puenteLabel;
	private final JLabel[] norteLabels;
	private final JLabel[] surLabels;
	private final int NUM_NORTE = 5;
	private final int NUM_SUR = 5;
	private final Puente puente;

	public PuenteGUI() {
		setTitle("Puente de un solo carril");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		JPanel panelEstados = new JPanel(new GridLayout(NUM_NORTE + NUM_SUR + 1, 1));
		puenteLabel = new JLabel("Puente: Libre");
		panelEstados.add(puenteLabel);

		norteLabels = new JLabel[NUM_NORTE];
		for (int i = 0; i < NUM_NORTE; i++) {
			norteLabels[i] = new JLabel("CocheNorte-" + (i+1) + ": Esperando");
			panelEstados.add(norteLabels[i]);
		}

		surLabels = new JLabel[NUM_SUR];
		for (int i = 0; i < NUM_SUR; i++) {
			surLabels[i] = new JLabel("CocheSur-" + (i+1) + ": Esperando");
			panelEstados.add(surLabels[i]);
		}

		add(panelEstados, BorderLayout.CENTER);
		setSize(400, 400);
		setLocationRelativeTo(null);
		setVisible(true);

		puente = new Puente(puenteLabel);
		for (int i = 0; i < NUM_NORTE; i++) {
			new CocheNorte(i, puente, norteLabels[i]).start();
		}
		for (int i = 0; i < NUM_SUR; i++) {
			new CocheSur(i, puente, surLabels[i]).start();
		}
	}
}

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
