package global;

import global.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.*;
import java.awt.*;

public class ServidorPalindromo extends JFrame implements PalindromoService {
    private JTextArea areaTexto, areaResultados;
    private JButton btnCargar, btnEnviar, btnLimpiar;
    private JComboBox<String> comboAlgoritmo;
    private JLabel labelTotal, labelTiempo;
    private List<ResultadoCliente> resultadosParciales = new ArrayList<>();
    private long tiempoInicio;

    public ServidorPalindromo() {
        setTitle("Servidor Palíndromos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        areaTexto = new JTextArea(6, 40);
        areaResultados = new JTextArea(10, 40);
        areaResultados.setEditable(false);
        btnCargar = new JButton("Cargar texto");
        btnEnviar = new JButton("Enviar a clientes");
        btnLimpiar = new JButton("Limpiar");
        comboAlgoritmo = new JComboBox<>(new String[]{"secuencial", "concurrente", "paralelo"});
        labelTotal = new JLabel("Total palíndromas: 0");
        labelTiempo = new JLabel("Tiempo total: 0 ms");

        JPanel top = new JPanel();
        top.add(new JLabel("Texto:"));
        top.add(new JScrollPane(areaTexto));
        top.add(comboAlgoritmo);
        top.add(btnCargar);
        top.add(btnEnviar);
        top.add(btnLimpiar);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(areaResultados), BorderLayout.CENTER);
        JPanel bottom = new JPanel();
        bottom.add(labelTotal);
        bottom.add(labelTiempo);
        add(bottom, BorderLayout.SOUTH);

        btnCargar.addActionListener(e -> cargarEjemplo());
        btnLimpiar.addActionListener(e -> areaTexto.setText(""));
        btnEnviar.addActionListener(e -> enviarAClientes());

        setSize(1000, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void cargarEjemplo() {
        areaTexto.setText("Dabale arroz a la zorra el abad. Ana, la ruta natural. Luz azul. Oso. Reconocer. Salas. Somos. Otto. A mamá Roma le aviva el amor a mamá. La ruta nos aporto otro paso natural. ");
    }

    private void enviarAClientes() {
        String texto = areaTexto.getText();
        if (texto.isEmpty()) return;
        String algoritmo = (String) comboAlgoritmo.getSelectedItem();
        areaResultados.setText("");
        resultadosParciales.clear();
        labelTotal.setText("Total palíndromas: 0");
        labelTiempo.setText("Tiempo total: 0 ms");
        // Simulación: dividir texto en 2 fragmentos
        String[] palabras = texto.split("\\s+");
        int mitad = palabras.length / 2;
        String frag1 = String.join(" ", Arrays.copyOfRange(palabras, 0, mitad));
        String frag2 = String.join(" ", Arrays.copyOfRange(palabras, mitad, palabras.length));
        try {
            Registry registry = LocateRegistry.getRegistry();
            PalindromoService cliente1 = (PalindromoService) registry.lookup("Cliente1");
            PalindromoService cliente2 = (PalindromoService) registry.lookup("Cliente2");
            tiempoInicio = System.currentTimeMillis();
            new Thread(() -> {
                try {
                    ResultadoCliente r1 = cliente1.procesarFragmento(new FragmentoTexto(frag1, algoritmo));
                    ResultadoCliente r2 = cliente2.procesarFragmento(new FragmentoTexto(frag2, algoritmo));
                    SwingUtilities.invokeLater(() -> mostrarResultados(Arrays.asList(r1, r2)));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void mostrarResultados(List<ResultadoCliente> resultados) {
        resultadosParciales = resultados;
        int total = 0;
        StringBuilder sb = new StringBuilder();
        for (ResultadoCliente r : resultados) {
            total += r.getPalindromas().size();
            sb.append("Palíndromas: ").append(r.getPalindromas()).append("\n");
            sb.append("Algoritmo: ").append(r.getAlgoritmo()).append("\n");
            sb.append("Tiempo: ").append(r.getTiempoEjecucion()).append(" ms\n\n");
        }
        areaResultados.setText(sb.toString());
        labelTotal.setText("Total palíndromas: " + total);
        labelTiempo.setText("Tiempo total: " + (System.currentTimeMillis() - tiempoInicio) + " ms");
    }

    // RMI: Procesar fragmento recibido de servidor
    @Override
    public ResultadoCliente procesarFragmento(FragmentoTexto fragmento) throws RemoteException {
        // No usado en servidor
        return null;
    }

    // --- Parte 2.2: Recolección de información ---
    private final List<TextoCliente> textosClientes = Collections.synchronizedList(new ArrayList<>());
    private final Object lockAnalisis = new Object();

    @Override
    public ResultadoServidor enviarTextoCliente(TextoCliente texto) throws RemoteException {
        textosClientes.add(texto);
        // Esperar a que ambos clientes hayan enviado su texto
        if (textosClientes.size() < 2) {
            // Espera activa simple (mejorable con wait/notify)
            while (textosClientes.size() < 2) {
                try { Thread.sleep(100); } catch (InterruptedException e) { break; }
            }
        }
        // Unir textos y analizar
        StringBuilder sb = new StringBuilder();
        String algoritmo = texto.getAlgoritmo();
        for (TextoCliente t : textosClientes) {
            sb.append(t.getTexto()).append(" ");
        }
        String textoUnido = sb.toString();
        long inicio = System.currentTimeMillis();
        List<String> pals = global.PalindromoUtils.buscarPalindromas(textoUnido, algoritmo);
        long fin = System.currentTimeMillis();
        // Limpiar para la siguiente ronda
        textosClientes.clear();
        return new ResultadoServidor(pals.size(), fin-inicio);
    }

    public static void main(String[] args) {
        try {
            ServidorPalindromo obj = new ServidorPalindromo();
            PalindromoService stub = (PalindromoService) UnicastRemoteObject.exportObject(obj, 0);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("Servidor", stub);
            System.out.println("Servidor listo");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
