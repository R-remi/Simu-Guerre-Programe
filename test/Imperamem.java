package test;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.Timer;

public class Imperamem extends JFrame {
    private static int tailleTableau = 7;
    private static final int TAILLE_MEMOIRE = tailleTableau*tailleTableau;
    private ImperaInstruction[] memoire;
    private int[] positionsCourantes;
    private int[] couleurs;
    private List<List<ImperaInstruction>> programmes;
    private GrilleMemoire grilleMemoire;
    private JButton btnCharger, btnPas, btnDemarrer, btnArreter;
    private Timer timer;
    private boolean enExecution = false;
    private JTextArea console;

    public Imperamem() {
        super("test.Imperamem - Simulateur de Combat de Programmes");
        memoire = new ImperaInstruction[TAILLE_MEMOIRE];
        programmes = new ArrayList<>();
        couleurs = new int[TAILLE_MEMOIRE];
        initialiserGUI();
        initialiserMemoire();
    }

    private void initialiserGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Panneau de contrôle
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        btnCharger = new JButton("Charger Programme");
        btnPas = new JButton("Pas à pas");
        btnDemarrer = new JButton("Démarrer");
        btnArreter = new JButton("Arrêter");
        btnArreter.setEnabled(false);

        controlPanel.add(btnCharger);
        controlPanel.add(btnPas);
        controlPanel.add(btnDemarrer);
        controlPanel.add(btnArreter);

        // Grille mémoire
        grilleMemoire = new GrilleMemoire(TAILLE_MEMOIRE);
        JScrollPane scrollPane = new JScrollPane(grilleMemoire);
        scrollPane.setPreferredSize(new Dimension(800, 100));

        // Console de log
        console = new JTextArea(10, 50);
        console.setEditable(false);
        JScrollPane consoleScroll = new JScrollPane(console);

        // Layout
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(consoleScroll, BorderLayout.SOUTH);

        add(controlPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // Actions des boutons
        btnCharger.addActionListener(e -> chargerProgramme());
        btnPas.addActionListener(e -> executerTour());
        btnDemarrer.addActionListener(e -> demarrerExecution());
        btnArreter.addActionListener(e -> arreterExecution());

        timer = new Timer(500, e -> executerTour());

        pack();
        setLocationRelativeTo(null);
    }

private void initialiserMemoire() {
    memoire = new ImperaInstruction[TAILLE_MEMOIRE];
    couleurs = new int[TAILLE_MEMOIRE];
    positionsCourantes = new int[TAILLE_MEMOIRE];
    for (int i = 0; i < TAILLE_MEMOIRE; i++) {
        memoire[i] = new ImperaInstruction("DAT", "#0", "#0");
    }
    mettreAJourAffichage();
}

    public void lireProgramme(String fichier) {
        List<ImperaInstruction> programme = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                ligne = ligne.trim();
                if (!ligne.isEmpty() && !ligne.startsWith("//")) {
                    String[] parts = ligne.split("\\s+");
                    if (parts.length == 3) {
                        programme.add(new ImperaInstruction(parts[0], parts[1], parts[2]));
                    }
                }
            }
            programmes.add(programme);
            log("Programme chargé: " + fichier + " (" + programme.size() + " instructions)");
        } catch (IOException e) {
            log("Erreur lors de la lecture du fichier: " + e.getMessage());
        }
    }

    private void copierProgrammesDansMemoire() {
        positionsCourantes = new int[programmes.size()];
        Arrays.fill(couleurs, 0);

        int position = 0;
        for (int i = 0; i < programmes.size(); i++) {
            List<ImperaInstruction> prog = programmes.get(i);
            positionsCourantes[i] = position;

            for (ImperaInstruction inst : prog) {
                memoire[position] = inst;
                couleurs[position] = i + 1;
                position++;
            }
            position += 5; // Espace entre les programmes
        }
        mettreAJourAffichage();
    }

    private void executerInstruction(int programmeIndex) {
        int pos = positionsCourantes[programmeIndex];
        if (pos < 0 || pos >= TAILLE_MEMOIRE) return;

        ImperaInstruction inst = memoire[pos];
        if (inst == null) return;

        switch (inst.getCommande().toUpperCase()) {
            case "DAT":
                log("Programme " + (programmeIndex + 1) + " a perdu (instruction DAT)");
                positionsCourantes[programmeIndex] = -1;
                break;

            case "MOV":
                int source = pos + parseParametre(inst.getParametreA());
                int dest = pos + parseParametre(inst.getParametreB());
                if (estValide(source) && estValide(dest)) {
                    memoire[dest] = new ImperaInstruction(
                            memoire[source].getCommande(),
                            memoire[source].getParametreA(),
                            memoire[source].getParametreB()
                    );
                    positionsCourantes[programmeIndex] = (pos + 1) % TAILLE_MEMOIRE;
                    log("MOV: Programme " + (programmeIndex + 1) + " copie de " + source + " vers " + dest);
                }
                break;

            case "ADD":
                traiterAdd(programmeIndex, pos, inst);
                break;

            case "JMP":
                int saut = parseParametre(inst.getParametreA());
                int nouvelle = (pos + saut) % TAILLE_MEMOIRE;
                positionsCourantes[programmeIndex] = nouvelle;
                log("JMP: Programme " + (programmeIndex + 1) + " saute à " + nouvelle);
                break;
        }
        mettreAJourAffichage();
    }

    private void traiterAdd(int programmeIndex, int pos, ImperaInstruction inst) {
        try {
            int valeurA;
            if (inst.getParametreA().startsWith("#")) {
                valeurA = Integer.parseInt(inst.getParametreA().substring(1));
            } else {
                int adresseA = pos + parseParametre(inst.getParametreA());
                if (!estValide(adresseA)) return;
                valeurA = Integer.parseInt(memoire[adresseA].getParametreA().substring(1));
            }

            int adresseB = pos + parseParametre(inst.getParametreB());
            if (!estValide(adresseB)) return;

            ImperaInstruction destInst = memoire[adresseB];
            int valeurB = Integer.parseInt(destInst.getParametreB().substring(1));
            destInst.setParametreB("#" + (valeurA + valeurB));

            positionsCourantes[programmeIndex] = (pos + 1) % TAILLE_MEMOIRE;
            log("ADD: Programme " + (programmeIndex + 1) + " ajoute " + valeurA + " à la position " + adresseB);
        } catch (NumberFormatException e) {
            log("Erreur dans ADD: format de nombre invalide");
        }
    }

    private int parseParametre(String param) {
        return Integer.parseInt(param.startsWith("#") ? param.substring(1) : param);
    }

    private boolean estValide(int position) {
        return position >= 0 && position < TAILLE_MEMOIRE;
    }

    private void executerTour() {
        for (int i = 0; i < positionsCourantes.length; i++) {
            if (positionsCourantes[i] >= 0) {
                executerInstruction(i);
            }
        }

        // Vérifier si la partie est terminée
        int programmesActifs = 0;
        int dernierProgrammeActif = -1;
        for (int i = 0; i < positionsCourantes.length; i++) {
            if (positionsCourantes[i] >= 0) {
                programmesActifs++;
                dernierProgrammeActif = i;
            }
        }

        if (programmesActifs <= 1) {
            if (dernierProgrammeActif >= 0) {
                log("Programme " + (dernierProgrammeActif + 1) + " a gagné!");
            } else {
                log("Match nul - tous les programmes sont morts");
            }
            arreterExecution();
        }
    }

    private void chargerProgramme() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            lireProgramme(fileChooser.getSelectedFile().getPath());
            copierProgrammesDansMemoire();
        }
    }

    private void demarrerExecution() {
        enExecution = true;
        btnCharger.setEnabled(false);
        btnPas.setEnabled(false);
        btnDemarrer.setEnabled(false);
        btnArreter.setEnabled(true);
        timer.start();
        log("Exécution automatique démarrée");
    }

    private void arreterExecution() {
        enExecution = false;
        timer.stop();
        btnCharger.setEnabled(true);
        btnPas.setEnabled(true);
        btnDemarrer.setEnabled(true);
        btnArreter.setEnabled(false);
        log("Exécution arrêtée");
    }

    private void mettreAJourAffichage() {
        grilleMemoire.setDonnees(memoire, couleurs, positionsCourantes);
    }

    private void log(String message) {
        console.append(message + "\n");
        console.setCaretPosition(console.getDocument().getLength());
    }
}
