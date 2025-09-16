package global.rmi;
import java.io.Serializable;
import java.util.List;

public class ResultadoCliente implements Serializable {
    private List<String> palindromas;
    private long tiempoEjecucion;
    private String algoritmo;
    public ResultadoCliente(List<String> palindromas, long tiempoEjecucion, String algoritmo) {
        this.palindromas = palindromas;
        this.tiempoEjecucion = tiempoEjecucion;
        this.algoritmo = algoritmo;
    }
    public List<String> getPalindromas() { return palindromas; }
    public long getTiempoEjecucion() { return tiempoEjecucion; }
    public String getAlgoritmo() { return algoritmo; }
}
