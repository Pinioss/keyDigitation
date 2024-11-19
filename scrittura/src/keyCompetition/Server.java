package keyCompetition;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Server {
    private static List<Partecipante> partecipanti = new ArrayList<>();

    public static void main(String[] args) {
        int porta = 1235;

        try (ServerSocket serverSocket = new ServerSocket(porta)) {
            System.out.println("Server in attesa di connessioni...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuovo partecipante connesso.");
                gestisciClient(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void gestisciClient(Socket clientSocket) {
        try (DataInputStream in = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {

            // Riceve i dati dal client
            String nome = in.readUTF();
            long tempo = in.readLong();
            int errori = in.readInt();
            Partecipante partecipante = new Partecipante(nome, tempo, errori);
            partecipanti.add(partecipante);
            System.out.println("Dati ricevuti da " + nome);

            // Ordina la classifica per tempo e numero di errori
            Collections.sort(partecipanti, Comparator.comparingLong(Partecipante::getTempoCompletamento)
                    .thenComparingInt(Partecipante::getErrori));

            // Calcola la posizione del partecipante appena aggiunto
            int posizione = partecipanti.indexOf(partecipante) + 1;

            // Invia al client solo la propria posizione
            out.writeUTF("La tua posizione in classifica è: " + posizione);
            out.flush();

            // Visualizza la classifica aggiornata nella console del server
            System.out.println("Classifica aggiornata:");
            for (int i = 0; i < partecipanti.size(); i++) {
                System.out.println((i + 1) + ". " + partecipanti.get(i));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Classe interna Partecipante
    private static class Partecipante {
        private String nome;
        private long tempoCompletamento;
        private int errori;

        public Partecipante(String nome, long tempoCompletamento, int errori) {
            this.nome = nome;
            this.tempoCompletamento = tempoCompletamento;
            this.errori = errori;
        }

        public String getNome() {
            return nome;
        }

        public long getTempoCompletamento() {
            return tempoCompletamento;
        }

        public int getErrori() {
            return errori;
        }

        @Override
        public String toString() {
            return "Nome: " + nome + ", Tempo: " + tempoCompletamento + " ms, Errori: " + errori;
        }
    }
}