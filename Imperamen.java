import java.io.*;
import java.util.ArrayList;

public class Imperamem {
    private ImperaInstruction[][] grille;  // Grille de mémoire 40x40
    private int taille = 40;

    // Constructeur pour initialiser la grille
    public Imperamem() {
        grille = new ImperaInstruction[taille][taille];
    }

    // Méthode pour initialiser la grille avec des cases nulles
    public void initialiserGrille() {
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                grille[i][j] = null;
            }
        }
    }

    // Méthode pour lire les instructions depuis un fichier
    public ArrayList<ImperaInstruction> lireInstructions(String nomFichier) throws IOException {
        ArrayList<ImperaInstruction> instructions = new ArrayList<>();
        BufferedReader lecteur = new BufferedReader(new FileReader(nomFichier));

        String ligne;
        while ((ligne = lecteur.readLine()) != null) {
            String[] parties = ligne.split(" ");
            if (parties.length == 3) {
                String commande = parties[0];
                String parametreA = parties[1];
                String parametreB = parties[2];
                instructions.add(new ImperaInstruction(commande, parametreA, parametreB));
            }
        }
        lecteur.close();
        return instructions;
    }

    // Méthode pour copier les instructions d'un programme dans la grille
    public void copierInstructions(ArrayList<ImperaInstruction> instructions, int posX, int posY) {
        int i = 0;
        for (ImperaInstruction instruction : instructions) {
            grille[(posX + i) % taille][posY] = instruction;  // Position circulaire en X
            i++;
        }
    }

    // Méthode pour exécuter les instructions à une position donnée
    public void executerInstructions(int posX, int posY) {
        ImperaInstruction instruction = grille[posX][posY];
        if (instruction == null) return;

        String commande = instruction.getCommande();
        String paramA = instruction.getParametreA();
        String paramB = instruction.getParametreB();

        switch (commande) {
            case "DAT":
                grille[posX][posY] = null; // Le programme perd, donc cette case reste une donnée
                break;

            case "MOV":
                int sourceX = (posX + Integer.parseInt(paramA)) % taille;
                int destX = (posX + Integer.parseInt(paramB)) % taille;
                grille[destX][posY] = grille[sourceX][posY];
                break;

            case "ADD":
                int valA = paramA.startsWith("#") ? Integer.parseInt(paramA.substring(1)) : getValeurDepuisAdresse(posX, posY, paramA);
                int valB = getValeurDepuisAdresse(posX, posY, paramB);
                setValeurDansAdresse(posX, posY, paramB, valA + valB);
                break;

            case "JMP":
                posX = (posX + Integer.parseInt(paramA)) % taille;
                break;
        }
    }

    // Méthode pour obtenir la valeur d'une adresse donnée
    private int getValeurDepuisAdresse(int posX, int posY, String param) {
        int adresse = Integer.parseInt(param);
        if (grille[(posX + adresse) % taille][posY] != null) {
            return Integer.parseInt(grille[(posX + adresse) % taille][posY].getParametreA());
        }
        return 0;
    }

    // Méthode pour mettre à jour une valeur dans une adresse spécifique
    private void setValeurDansAdresse(int posX, int posY, String param, int valeur) {
        int adresse = Integer.parseInt(param);
        if (grille[(posX + adresse) % taille][posY] != null) {
            grille[(posX + adresse) % taille][posY].setParametreA(String.valueOf(valeur));
        }
    }

    // Méthode pour obtenir la grille (getter)
    public ImperaInstruction[][] getGrille() {
        return grille;
    }
}
