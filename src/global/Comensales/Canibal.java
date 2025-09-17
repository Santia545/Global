package global.Comensales;

import javax.swing.JLabel;

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