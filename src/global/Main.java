/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package global;

/**
 *
 * @author CESAR
 */
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
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

class Olla {
    private int raciones;
    private final int capacidad;
    private final OllaGUI gui;
    private boolean cocineroDespertado = false;

    public Olla(int capacidad, OllaGUI gui) {
        this.capacidad = capacidad;
        this.raciones = capacidad;
        this.gui = gui;
    }

    public synchronized void obtenerRacion(String nombreCanibal, JLabel canibalLabel) throws InterruptedException {
        while (raciones == 0) {
            SwingUtilities.invokeLater(() -> canibalLabel.setText(nombreCanibal + ": Esperando (olla vacía)"));
            if (!cocineroDespertado) {
                cocineroDespertado = true;
                notifyAll(); // Solo un caníbal despierta al cocinero
            }
            wait();      // Espera a que el cocinero rellene la olla
        }
        raciones--;
        SwingUtilities.invokeLater(() -> canibalLabel.setText(nombreCanibal + ": Comiendo"));
        actualizarOllaLabel();
        if (raciones == 0) {
            cocineroDespertado = false; // Permite que el siguiente caníbal despierte al cocinero cuando vuelva a estar vacía
        }
    }

    public synchronized void rellenarOlla(JLabel cocineroLabel, JLabel ollaLabel) throws InterruptedException {
        while (raciones > 0) {
            SwingUtilities.invokeLater(() -> cocineroLabel.setText("Cocinero: Durmiendo"));
            wait(); // Espera a que la olla esté vacía
        }
        SwingUtilities.invokeLater(() -> cocineroLabel.setText("Cocinero: Cocinando"));
        Thread.sleep(800); // Simula el tiempo de cocinar
        raciones = capacidad;
        actualizarOllaLabel();
        SwingUtilities.invokeLater(() -> cocineroLabel.setText("Cocinero: Olla lista"));
        notifyAll(); // Despierta a los caníbales
    }

    private void actualizarOllaLabel() {
        SwingUtilities.invokeLater(() -> {
            gui.ollaLabel.setText("Olla: " + raciones + "/" + capacidad + " raciones");
        });
    }
}

class Canibal extends Thread {
    private final Olla olla;
    private final String nombre;
    private final JLabel canibalLabel;

    public Canibal(Olla olla, String nombre, JLabel canibalLabel) {
        this.olla = olla;
        this.nombre = nombre;
        this.canibalLabel = canibalLabel;
    }

    public void run() {
        try {
            while (true) {
                olla.obtenerRacion(nombre, canibalLabel);
                Thread.sleep((int)(Math.random() * 1200) + 500); // Simula el tiempo de comer
                canibalLabel.setText(nombre + ": Esperando");
            }
        } catch (InterruptedException e) {
            canibalLabel.setText(nombre + ": Interrumpido");
        }
    }
}

class Cocinero extends Thread {
    private final Olla olla;
    private final JLabel cocineroLabel;
    private final JLabel ollaLabel;

    public Cocinero(Olla olla, JLabel cocineroLabel, JLabel ollaLabel) {
        this.olla = olla;
        this.cocineroLabel = cocineroLabel;
        this.ollaLabel = ollaLabel;
    }

    public void run() {
        try {
            while (true) {
                olla.rellenarOlla(cocineroLabel, ollaLabel);
            }
        } catch (InterruptedException e) {
            cocineroLabel.setText("Cocinero: Interrumpido");
        }
    }
}
