public class ImperaInstruction {
    private String commande;
    private String parametreA;
    private String parametreB;

    // Constructeur pour initialiser les attributs
    public ImperaInstruction(String commande, String parametreA, String parametreB) {
        this.commande = commande;
        this.parametreA = parametreA;
        this.parametreB = parametreB;
    }

    // Getters
    public String getCommande() {
        return commande;
    }

    public String getParametreA() {
        return parametreA;
    }

    public String getParametreB() {
        return parametreB;
    }

    // Setters
    public void setCommande(String commande) {
        this.commande = commande;
    }

    public void setParametreA(String parametreA) {
        this.parametreA = parametreA;
    }

    public void setParametreB(String parametreB) {
        this.parametreB = parametreB;
    }
}
