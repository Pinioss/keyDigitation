import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static List<Giocatore> classifica = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1235);
            System.out.println("Server in ascolto...");
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                DataInputStream input = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

                // Ricezione dei dati dal client
                String nomeGiocatore = input.readUTF();
                long tempoCompleto = input.readLong();
                int numeroErrori = input.readInt();
                int numeroParole = input.readInt(); // Ricevi il numero di parole
                
                // Elaborazione dei dati (calcolo della velocità e precisione)
                double velocita = calcolaVelocita(tempoCompleto, numeroParole);
                double precisione = calcolaPrecisione(numeroErrori, numeroParole);
                
                // Aggiungi il giocatore alla classifica
                Giocatore giocatore = new Giocatore(nomeGiocatore, tempoCompleto, velocita, precisione);
                classifica.add(giocatore);
                
                // Ordinamento della classifica (per precisione)
                classifica.sort(Comparator.comparingDouble(Giocatore::getPrecisione).reversed());

                // Invio della classifica aggiornata al client
                output.writeInt(classifica.size());
                for (Giocatore g : classifica) {
                    output.writeUTF(g.getNome());
                    output.writeLong(g.getTempo());
                    output.writeDouble(g.getVelocita());
                    output.writeDouble(g.getPrecisione());
                }

                // Chiusura della connessione
                input.close();
                output.close();
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static double calcolaVelocita(long tempo, int numeroParole) {
        int numeroCaratteri = numeroParole * 6; // Supponendo una media di 6 caratteri per parola
        return (numeroCaratteri * 1000.0) / tempo; // velocità in caratteri al secondo
    }
    
    private static double calcolaPrecisione(int errori, int numeroParole) {
        int numeroCaratteri = numeroParole * 6; // Supponendo una media di 6 caratteri per parola
        return 100.0 - ((double) errori / numeroCaratteri) * 100; // percentuale di precisione
    }
}

class Giocatore {
    private String nome;
    private long tempo;
    private double velocita;
    private double precisione;

    public Giocatore(String nome, long tempo, double velocita, double precisione) {
        this.nome = nome;
        this.tempo = tempo;
        this.velocita = velocita;
        this.precisione = precisione;
    }

    public String getNome() {
        return nome;
    }

    public long getTempo() {
        return tempo;
    }

    public double getVelocita() {
        return velocita;
    }

    public double getPrecisione() {
        return precisione;
    }

    @Override
    public String toString() {
        return nome + " - Tempo: " + tempo + "ms, Velocità: " + velocita + " cps, Precisione: " + precisione + "%";
    }
}
