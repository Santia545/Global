package global;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Semaphore;

public class SantaMain {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new SantaGUI());
	}
}

class SantaGUI extends JFrame {
	private final JLabel santaLabel;
	private final JLabel[] renoLabels;
	private final JLabel[] duendeLabels;
	private final SantaClaus santa;
	private final int NUM_RENOS = 9;
	private final int NUM_DUENDES = 10;

	public SantaGUI() {
		setTitle("Problema de Santa Claus");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		JPanel panelEstados = new JPanel(new GridLayout(NUM_RENOS + NUM_DUENDES + 2, 1));
		santaLabel = new JLabel("Santa: Durmiendo");
		panelEstados.add(santaLabel);

		renoLabels = new JLabel[NUM_RENOS];
		for (int i = 0; i < NUM_RENOS; i++) {
			renoLabels[i] = new JLabel("Reno-" + (i+1) + ": De vacaciones");
			panelEstados.add(renoLabels[i]);
		}

		duendeLabels = new JLabel[NUM_DUENDES];
		for (int i = 0; i < NUM_DUENDES; i++) {
			duendeLabels[i] = new JLabel("Duende-" + (i+1) + ": Trabajando");
			panelEstados.add(duendeLabels[i]);
		}

		add(panelEstados, BorderLayout.CENTER);
		setSize(400, 600);
		setLocationRelativeTo(null);
		setVisible(true);

		// Lógica de sincronización
		santa = new SantaClaus(santaLabel, renoLabels, duendeLabels);
		santa.start();
		for (int i = 0; i < NUM_RENOS; i++) {
			new Reno(i, santa, renoLabels[i]).start();
		}
		for (int i = 0; i < NUM_DUENDES; i++) {
			new Duende(i, santa, duendeLabels[i]).start();
		}
	}
}

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
