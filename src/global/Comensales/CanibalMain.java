/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package global.Comensales;

/**
 *
 * @author CESAR
 */
import javax.swing.*;
import java.awt.*;

public class CanibalMain {
    static final int NUM_CANIBALES = 5;
    static final int CAPACIDAD_OLLA =3;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new OllaGUI(NUM_CANIBALES, CAPACIDAD_OLLA);
        });
    }
}

class OllaGUI extends JFrame {
    public final JLabel[] canibalLabels;
    public final JLabel cocineroLabel;
    public final JLabel ollaLabel;
    private final Olla olla;

    public OllaGUI(int numCanibales, int capacidadOlla) {
        setTitle("Problema de la Olla - Caníbales y Cocinero");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelEstados = new JPanel(new GridLayout(numCanibales + 2, 1));
        canibalLabels = new JLabel[numCanibales];
        for (int i = 0; i < numCanibales; i++) {
            canibalLabels[i] = new JLabel("Caníbal-" + (i+1) + ": Esperando");
            panelEstados.add(canibalLabels[i]);
        }
        cocineroLabel = new JLabel("Cocinero: Durmiendo");
        panelEstados.add(cocineroLabel);
        ollaLabel = new JLabel("Olla: " + capacidadOlla + "/" + capacidadOlla + " raciones");
        panelEstados.add(ollaLabel);

        add(panelEstados, BorderLayout.CENTER);
        setSize(350, 300);
        setLocationRelativeTo(null);
        setVisible(true);

        olla = new Olla(capacidadOlla, this);
        new Cocinero(olla, cocineroLabel, ollaLabel).start();
        for (int i = 0; i < numCanibales; i++) {
            new Canibal(olla, "Caníbal-" + (i+1), canibalLabels[i]).start();
        }
    }
}
