package compiler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Leitura {
    public static String lerLinhaArquivo(String nomeArquivo, int numeroLinha) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            int contador = 1;

            while ((linha = reader.readLine()) != null) {
                if (contador == numeroLinha) {
                    return linha;
                }
                contador++;
            }
        }
        return null;
    }
}