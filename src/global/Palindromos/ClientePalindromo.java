package global.Palindromos;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import global.Palindromos.rmi.*;

import java.awt.*;

public class ClientePalindromo extends JFrame implements PalindromoService {
    private JTextArea areaTexto, areaResultados;
    private JButton btnEnviar, btnLimpiar;
    private JComboBox<String> comboAlgoritmo;
    private JLabel labelPalindromas, labelTiempo, labelAlgoritmo;
    private String textoRecibido = "";
    private String algoritmoUsado = "";
    private long tiempoEjecucion = 0;
    private List<String> palindromas = new ArrayList<>();

    public ClientePalindromo(String nombre) {
        setTitle("Cliente Palíndromos - " + nombre);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        areaTexto = new JTextArea(6, 40);
        areaResultados = new JTextArea(10, 40);
        areaResultados.setEditable(false);
        btnEnviar = new JButton("Enviar texto al servidor");
        btnLimpiar = new JButton("Limpiar");
        comboAlgoritmo = new JComboBox<>(new String[] { "secuencial", "concurrente", "paralelo" });
        labelPalindromas = new JLabel("Palíndromas: 0");
        labelTiempo = new JLabel("Tiempo: 0 ms");
        labelAlgoritmo = new JLabel("Algoritmo: -");

        JPanel top = new JPanel();
        top.add(new JLabel("Texto:"));
        top.add(new JScrollPane(areaTexto));
        top.add(comboAlgoritmo);
        top.add(btnEnviar);
        top.add(btnLimpiar);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(areaResultados), BorderLayout.CENTER);
        JPanel bottom = new JPanel();
        bottom.add(labelPalindromas);
        bottom.add(labelTiempo);
        bottom.add(labelAlgoritmo);
        add(bottom, BorderLayout.SOUTH);

        btnLimpiar.addActionListener(e -> areaTexto.setText(""));
        btnEnviar.addActionListener(e -> enviarAlServidor());

        setSize(1000, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // RMI: Procesar fragmento recibido del servidor
    @Override
    public ResultadoCliente procesarFragmento(FragmentoTexto fragmento) throws RemoteException {
        this.textoRecibido = fragmento.getTexto();
        this.algoritmoUsado = fragmento.getAlgoritmo();
        long inicio = System.currentTimeMillis();
        List<String> pals = PalindromoUtils.buscarPalindromas(textoRecibido, algoritmoUsado);
        long fin = System.currentTimeMillis();
        this.palindromas = pals;
        this.tiempoEjecucion = fin - inicio;
        SwingUtilities.invokeLater(() -> mostrarResultados());
        return new ResultadoCliente(pals, tiempoEjecucion, algoritmoUsado);
    }

    // RMI: Recibir respuesta del servidor (Parte 2.2)
    @Override
    public ResultadoServidor enviarTextoCliente(TextoCliente texto) throws RemoteException {
        // No usado en cliente
        return null;
    }

    private void mostrarResultados() {
        areaResultados.setText("Texto recibido: " + textoRecibido + "\n\nPalíndromas encontradas: " + palindromas
                + "\nTiempo: " + tiempoEjecucion + " ms\nAlgoritmo: " + algoritmoUsado);
        labelPalindromas.setText("Palíndromas: " + palindromas.size());
        labelTiempo.setText("Tiempo: " + tiempoEjecucion + " ms");
        labelAlgoritmo.setText("Algoritmo: " + algoritmoUsado);
    }

    private void enviarAlServidor() {
        String texto = areaTexto.getText();
        String algoritmo = (String) comboAlgoritmo.getSelectedItem();
        if (texto.isEmpty())
            return;
        try {
            Registry registry = LocateRegistry.getRegistry();
            PalindromoService servidor = (PalindromoService) registry.lookup("Servidor");
            long inicio = System.currentTimeMillis();
            ResultadoServidor res = servidor.enviarTextoCliente(new TextoCliente(texto, algoritmo));
            long fin = System.currentTimeMillis();
            areaResultados.setText("Total palíndromas: " + res.getTotalPalindromas() + "\nTiempo: "
                    + res.getTiempoEjecucion() + " ms\n(RTT: " + (fin - inicio) + " ms)");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            String nombre = args.length > 0 ? args[0] : "Cliente2";
            ClientePalindromo obj = new ClientePalindromo(nombre);
            PalindromoService stub = (PalindromoService) UnicastRemoteObject.exportObject(obj, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(nombre, stub);
            System.out.println(nombre + " listo");
        } catch (java.rmi.ConnectException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "No se pudo conectar al servidor RMI. Asegúrate de que el servidor esté en ejecución.", "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
