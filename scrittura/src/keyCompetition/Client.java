/**
 *
 * @author matte
 */


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

public class Client extends JFrame {
    private List<String> paroleDaScrivere;
    private JTextArea areaTestoRiferimento;
    private JTextField campoTestoInserito;
    private JLabel etichettaParolaCorrente;
    private JLabel etichettaErrori;
    private JLabel etichettaPosizione;
    private AtomicLong startTime;
    private String nomeGiocatore;
    private int numeroErroriCorrenti;
    private int indiceParolaCorrente;

    public Client() {
        // Carica il file di parole e mostra un errore se non viene trovato
        paroleDaScrivere = caricaParoleDaFile("parole.txt");
        if (paroleDaScrivere.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Errore: il file 'parole.txt' vuoto o non esiste.", "Errore", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        // Chiede il nome del giocatore
        nomeGiocatore = JOptionPane.showInputDialog(this, "Inserisci il tuo nome:", "Nome Giocatore", JOptionPane.PLAIN_MESSAGE);
        if (nomeGiocatore == null || nomeGiocatore.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Devi inserire un nome per giocare!", "Errore", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        // Schermata di selezione del numero di parole
        int numeroParole = mostraSelezioneNumeroParole();
        if (numeroParole == -1) {
            System.exit(0);
        }

        // Estrae un numero casuale di parole dalla lista
        if (paroleDaScrivere.size() > numeroParole) {
            Collections.shuffle(paroleDaScrivere);
            paroleDaScrivere = paroleDaScrivere.subList(0, numeroParole);
        }

        // Configura la finestra principale
        setTitle("Test di Velocita  e Precisione - Giocatore: " + nomeGiocatore);
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // Area di testo di riferimento
        areaTestoRiferimento = new JTextArea(String.join(" ", paroleDaScrivere));
        areaTestoRiferimento.setEditable(false);
        areaTestoRiferimento.setLineWrap(true);
        areaTestoRiferimento.setWrapStyleWord(true);
        areaTestoRiferimento.setFont(new Font("Serif", Font.PLAIN, 16));
        areaTestoRiferimento.setBackground(new Color(230, 230, 250));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 0.3;
        add(new JScrollPane(areaTestoRiferimento), gbc);

        // Campo di testo per l'inserimento del testo
        campoTestoInserito = new JTextField();
        campoTestoInserito.setFont(new Font("Serif", Font.PLAIN, 16));
        campoTestoInserito.addKeyListener(new WordTypingListener());
        gbc.gridy = 1;
        gbc.weighty = 0.1;
        add(campoTestoInserito, gbc);

        // Etichetta per mostrare la parola corrente
        etichettaParolaCorrente = new JLabel("Parola corrente: " + paroleDaScrivere.get(0), JLabel.CENTER);
        etichettaParolaCorrente.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weighty = 0;
        gbc.gridx = 0;
        add(etichettaParolaCorrente, gbc);

        // Etichetta per mostrare il numero di errori in tempo reale
        etichettaErrori = new JLabel("Errori: 0", JLabel.CENTER);
        etichettaErrori.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        add(etichettaErrori, gbc);

        // Etichetta per mostrare la posizione finale
        etichettaPosizione = new JLabel("Posizione finale: in attesa...", JLabel.CENTER);
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(etichettaPosizione, gbc);

        startTime = new AtomicLong();
        numeroErroriCorrenti = 0;
        indiceParolaCorrente = 0;
    }

    private List<String> caricaParoleDaFile(String nomeFile) {
        List<String> parole = new ArrayList<>();
        try {
            // Modifica il percorso con il percorso assoluto completo al file parole.txt
            List<String> lines = Files.readAllLines(Paths.get("C:\\Users\\PPiC\\Desktop\\parole.txt"));
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    parole.add(line.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parole;
    }

    private int mostraSelezioneNumeroParole() {
        String[] opzioni = {"10", "20", "30"};
        int scelta = JOptionPane.showOptionDialog(this,
                "Quante parole vuoi scrivere?",
                "Seleziona Numero di Parole",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opzioni,
                opzioni[0]);

        if (scelta == -1) {
            return -1;
        }
        return Integer.parseInt(opzioni[scelta]);
    }

    // Listener per la digitazione delle parole
    private class WordTypingListener extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            if (startTime.get() == 0) {
                startTime.set(System.currentTimeMillis());
            }

            String testoInserito = campoTestoInserito.getText().trim();
            String parolaCorrente = paroleDaScrivere.get(indiceParolaCorrente);

            if (testoInserito.equals(parolaCorrente)) {
                // Passa alla parola successiva se Ã¨ corretta
                indiceParolaCorrente++;
                campoTestoInserito.setText("");
                if (indiceParolaCorrente < paroleDaScrivere.size()) {
                    etichettaParolaCorrente.setText("Parola corrente: " + paroleDaScrivere.get(indiceParolaCorrente));
                } else {
                    // Se tutte le parole sono state digitate, termina la partita
                    long tempoImpiegato = System.currentTimeMillis() - startTime.get();
                    campoTestoInserito.setEditable(false);
                    inviaDatiAlServer(nomeGiocatore, tempoImpiegato, numeroErroriCorrenti);
                }
            } else if (!parolaCorrente.startsWith(testoInserito)) {
                // Incrementa gli errori se il testo inserito non corrisponde alla parola corrente
                numeroErroriCorrenti++;
                etichettaErrori.setText("Errori: " + numeroErroriCorrenti);
            }
        }
    }

    private void inviaDatiAlServer(String nome, long tempo, int errori) {
        String host = "localhost";
        int porta = 1235;

        try (Socket socket = new Socket(host, porta);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

            // Invia i dati al server
            out.writeUTF(nome);
            out.writeLong(tempo);
            out.writeInt(errori);
            out.flush();

            // Riceve e mostra la posizione in classifica
            String posizione = in.readUTF();
            SwingUtilities.invokeLater(() -> etichettaPosizione.setText("Posizione: " + posizione));

        } catch (IOException ex) {
            SwingUtilities.invokeLater(() -> etichettaPosizione.setText("Errore di connessione al server"));
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Client client = new Client();
            client.setVisible(true);
        });
    }
}