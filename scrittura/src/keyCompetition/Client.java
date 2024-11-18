package keyCompetition;

public class Client {

    public static void main(String[] args) {
        // Esegui il frame principale per avviare l'applicazione
        javax.swing.SwingUtilities.invokeLater(() -> {
            keyFrame frame = new keyFrame();
            frame.setVisible(true); // Mostra l'interfaccia grafica
        });
    }
}