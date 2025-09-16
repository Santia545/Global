package global.rmi;
import java.io.Serializable;

public class TextoCliente implements Serializable {
    private String texto;
    private String algoritmo;
    public TextoCliente(String texto, String algoritmo) {
        this.texto = texto;
        this.algoritmo = algoritmo;
    }
    public String getTexto() { return texto; }
    public String getAlgoritmo() { return algoritmo; }
}
