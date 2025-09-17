package global.Comensales;

import javax.swing.JLabel;

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