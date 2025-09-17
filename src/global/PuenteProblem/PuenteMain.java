package global.PuenteProblem;

import javax.swing.*;
import java.awt.*;

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
