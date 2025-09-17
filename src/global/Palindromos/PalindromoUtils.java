package global.Palindromos;

import java.util.*;
import java.util.concurrent.*;

public class PalindromoUtils {
    // Algoritmo principal
    public static List<String> buscarPalindromas(String texto, String algoritmo) {
        String[] palabras = texto.split("\\W+");
        switch (algoritmo) {
            case "secuencial":
                return buscarSecuencial(palabras);
            case "concurrente":
                return buscarConcurrente(palabras);
            case "paralelo":
                return buscarParalelo(palabras);
            default:
                return buscarSecuencial(palabras);
        }
    }
    // Secuencial
    public static List<String> buscarSecuencial(String[] palabras) {
        List<String> res = new ArrayList<>();
        for (String p : palabras) if (esPalindroma(p)) res.add(p);
        return res;
    }
    // Concurrente (ExecutorService)
    public static List<String> buscarConcurrente(String[] palabras) {
        List<String> res = Collections.synchronizedList(new ArrayList<>());
        ExecutorService exec = Executors.newFixedThreadPool(4);
        for (String p : palabras) {
            exec.submit(() -> { if (esPalindroma(p)) res.add(p); });
        }
        exec.shutdown();
        try { exec.awaitTermination(2, TimeUnit.SECONDS); } catch (InterruptedException e) {}
        return res;
    }
    // Paralelo (ForkJoin)
    public static List<String> buscarParalelo(String[] palabras) {
        ForkJoinPool pool = new ForkJoinPool();
        return pool.invoke(new PalindromoTask(palabras, 0, palabras.length));
    }
    // Tarea ForkJoin
    static class PalindromoTask extends RecursiveTask<List<String>> {
        String[] palabras;
        int ini, fin;
        static final int THRESHOLD = 20;
        PalindromoTask(String[] palabras, int ini, int fin) {
            this.palabras = palabras; this.ini = ini; this.fin = fin;
        }
        @Override
        protected List<String> compute() {
            if (fin - ini <= THRESHOLD) {
                List<String> res = new ArrayList<>();
                for (int i = ini; i < fin; i++) if (esPalindroma(palabras[i])) res.add(palabras[i]);
                return res;
            } else {
                int mid = (ini + fin) / 2;
                PalindromoTask left = new PalindromoTask(palabras, ini, mid);
                PalindromoTask right = new PalindromoTask(palabras, mid, fin);
                left.fork();
                List<String> rightRes = right.compute();
                List<String> leftRes = left.join();
                leftRes.addAll(rightRes);
                return leftRes;
            }
        }
    }
    // Verifica si una palabra es palíndroma
    public static boolean esPalindroma(String palabra) {
        if (palabra == null || palabra.length() < 2) return false;
        String p = palabra.toLowerCase();
        int i = 0, j = p.length() - 1;
        while (i < j) {
            if (p.charAt(i) != p.charAt(j)) return false;
            i++; j--;
        }
        return true;
    }
}
