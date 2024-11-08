import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;


public class SimulationGUI extends JFrame {
    private static final int TAILLE_GRILLE = 40;
    private JButton[][] boutonsGrille;
    private Imperamem imperamem;

    public SimulationGUI() {
        imperamem = new Imperamem();
        imperamem.initialiserGrille();
        initialiserInterface();
    }

    private void initialiserInterface() {
        setTitle("Simulation de Guerre entre Programmes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel grillePanel = new JPanel();
        grillePanel.setLayout(new GridLayout(TAILLE_GRILLE, TAILLE_GRILLE));
        boutonsGrille = new JButton[TAILLE_GRILLE][TAILLE_GRILLE];

        for (int i = 0; i < TAILLE_GRILLE; i++) {
            for (int j = 0; j < TAILLE_GRILLE; j++) {
                boutonsGrille[i][j] = new JButton();
                boutonsGrille[i][j].setBackground(Color.WHITE);
                grillePanel.add(boutonsGrille[i][j]);
            }
        }

        add(grillePanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton chargerButton = new JButton("Charger Programme");
        chargerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chargerProgramme();
            }
        });

        JButton executerButton = new JButton("Exécuter Instructions");
        executerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executerInstructions();
            }
        });

        controlPanel.add(chargerButton);
        controlPanel.add(executerButton);
        add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void chargerProgramme() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File fichier = fileChooser.getSelectedFile();
            try {
                ArrayList<ImperaInstruction> instructions = imperamem.lireInstructions(fichier.getAbsolutePath());
                imperamem.copierInstructions(instructions, 0, 0);
                mettreAJourCouleurGrille();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erreur de lecture du fichier.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void executerInstructions() {
        for (int i = 0; i < TAILLE_GRILLE; i++) {
            for (int j = 0; j < TAILLE_GRILLE; j++) {
                imperamem.executerInstructions(i, j);
            }
        }
        mettreAJourCouleurGrille();
    }

    private void mettreAJourCouleurGrille() {
        for (int i = 0; i < TAILLE_GRILLE; i++) {
            for (int j = 0; j < TAILLE_GRILLE; j++) {
                if (imperamem.getGrille()[i][j] == null) {
                    boutonsGrille[i][j].setBackground(Color.WHITE);
                } else {
                    String commande = imperamem.getGrille()[i][j].getCommande();
                    switch (commande) {
                        case "DAT":
                            boutonsGrille[i][j].setBackground(Color.RED);
                            break;
                        case "MOV":
                            boutonsGrille[i][j].setBackground(Color.BLUE);
                            break;
                        case "ADD":
                            boutonsGrille[i][j].setBackground(Color.GREEN);
                            break;
                        case "JMP":
                            boutonsGrille[i][j].setBackground(Color.ORANGE);
                            break;
                        default:
                            boutonsGrille[i][j].setBackground(Color.GRAY);
                            break;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SimulationGUI());
    }
}
