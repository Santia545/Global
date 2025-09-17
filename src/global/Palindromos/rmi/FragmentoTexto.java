package global.Palindromos.rmi;
import java.io.Serializable;

public class FragmentoTexto implements Serializable {
    private String texto;
    private String algoritmo; // "secuencial", "concurrente", "paralelo"
    public FragmentoTexto(String texto, String algoritmo) {
        this.texto = texto;
        this.algoritmo = algoritmo;
    }
    public String getTexto() { return texto; }
    public String getAlgoritmo() { return algoritmo; }
}
