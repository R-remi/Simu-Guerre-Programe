package test;
// test.GrilleMemoire.java
import javax.swing.*;
import java.awt.*;

public class GrilleMemoire extends JPanel {
    private static final int TAILLE_CELLULE = 60;
    private ImperaInstruction[] memoire;
    private int[] couleurs;
    private int[] positionsCourantes;

    public GrilleMemoire(int taille) {
        setPreferredSize(new Dimension(taille * TAILLE_CELLULE, TAILLE_CELLULE));
        setBackground(Color.WHITE);
    }


    public void setDonnees(ImperaInstruction[] memoire, int[] couleurs, int[] positionsCourantes) {
        this.memoire = memoire;
        this.couleurs = couleurs;
        this.positionsCourantes = positionsCourantes;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (memoire == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        for (int i = 0; i < memoire.length; i++) {
            int x = i * TAILLE_CELLULE;
            int y = 0;

            // Couleur de fond selon le programme
            Color[] couleursProgamme = {
                    new Color(255, 200, 200),  // Rouge clair
                    new Color(200, 255, 200),  // Vert clair
                    new Color(200, 200, 255)   // Bleu clair
            };

            g2d.setColor(couleurs[i] > 0 ? couleursProgamme[couleurs[i] - 1] : Color.WHITE);
            g2d.fillRect(x, y, TAILLE_CELLULE, TAILLE_CELLULE);

            // Bordure de la cellule
            g2d.setColor(Color.GRAY);
            g2d.drawRect(x, y, TAILLE_CELLULE, TAILLE_CELLULE);

            // Marquage position courante
            for (int pos : positionsCourantes) {
                if (pos == i) {
                    g2d.setColor(Color.RED);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRect(x + 2, y + 2, TAILLE_CELLULE - 4, TAILLE_CELLULE - 4);
                }
            }

            // Affichage de l'instruction
            if (memoire[i] != null) {
                g2d.setColor(Color.BLACK);
                String text = memoire[i].toString();
                FontMetrics fm = g2d.getFontMetrics();
                int textX = x + (TAILLE_CELLULE - fm.stringWidth(text)) / 2;
                int textY = y + (TAILLE_CELLULE + fm.getAscent()) / 2;
                g2d.drawString(text, textX, textY);
            }
        }
    }
}