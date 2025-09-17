package global.Santa;

import javax.swing.*;
import java.awt.*;

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
