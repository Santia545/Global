package global.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PalindromoService extends Remote {
    // Parte 2.1: Cliente recibe fragmento, procesa y responde
    ResultadoCliente procesarFragmento(FragmentoTexto fragmento) throws RemoteException;
    // Parte 2.2: Cliente envía texto, servidor responde
    ResultadoServidor enviarTextoCliente(TextoCliente texto) throws RemoteException;
}
