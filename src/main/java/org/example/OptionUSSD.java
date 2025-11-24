package org.example;

// Classe modélisant une option de service USSD (un forfait)
public class OptionUSSD {

    // Attributs (5 requis)
    private int idOption;
    private String nomOption;
    private String descriptionForfait;
    private int prixUnites; // Le coût en Unités (U)
    private String dureeValidite; // Ex: "1 Jour", "7 Jours", "30 Jours"

    // Constructeur
    public OptionUSSD(int idOption, String nomOption, String descriptionForfait, int prixUnites, String dureeValidite) {
        this.idOption = idOption;
        this.nomOption = nomOption;
        this.descriptionForfait = descriptionForfait;
        this.prixUnites = prixUnites;
        this.dureeValidite = dureeValidite;
    }

    // --- Getters et Setters ---

    public int getIdOption() {
        return idOption;
    }

    public void setIdOption(int idOption) {
        this.idOption = idOption;
    }

    public String getNomOption() {
        return nomOption;
    }

    public void setNomOption(String nomOption) {
        this.nomOption = nomOption;
    }

    public String getDescriptionForfait() {
        return descriptionForfait;
    }

    public void setDescriptionForfait(String descriptionForfait) {
        this.descriptionForfait = descriptionForfait;
    }

    public int getPrixUnites() {
        return prixUnites;
    }

    public void setPrixUnites(int prixUnites) {
        this.prixUnites = prixUnites;
    }

    public String getDureeValidite() {
        return dureeValidite;
    }

    public void setDureeValidite(String dureeValidite) {
        this.dureeValidite = dureeValidite;
    }

    // --- Méthodes ---

    // Méthode pour afficher l'option dans le menu
    public String afficherOption() {
        return this.idOption + ". " + this.nomOption + " (" + this.dureeValidite + ") - " + this.prixUnites + "U";
    }

    // Méthode pour simuler l'activation du forfait
    public void activerForfait() {
        System.out.println("Activation en cours pour : " + this.nomOption + " (" + this.descriptionForfait + ")");
        System.out.println("L'achat simulé a réussi. Vous recevrez un SMS de confirmation.");
    }
}