package test;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Imperamem jeu = new Imperamem();
            jeu.setVisible(true);
        });
    }
}