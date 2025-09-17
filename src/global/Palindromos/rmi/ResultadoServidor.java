package global.Palindromos.rmi;
import java.io.Serializable;

public class ResultadoServidor implements Serializable {
    private int totalPalindromas;
    private long tiempoEjecucion;
    public ResultadoServidor(int totalPalindromas, long tiempoEjecucion) {
        this.totalPalindromas = totalPalindromas;
        this.tiempoEjecucion = tiempoEjecucion;
    }
    public int getTotalPalindromas() { return totalPalindromas; }
    public long getTiempoEjecucion() { return tiempoEjecucion; }
}
