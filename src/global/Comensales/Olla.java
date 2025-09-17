package global.Comensales;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

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