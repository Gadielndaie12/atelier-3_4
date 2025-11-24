package org.example;

// Classe abstraite : ABSTRACTION
public abstract class Offre {

    // Attributs protégés : ENCAPSULATION
    protected String idUnique;
    protected String nom;
    protected int prixUnites;
    protected String dureeValidite;
    protected String typeOffre; // "INTERNET", "VOIX", "MIX"

    // Constructeur
    public Offre(String idUnique, String nom, int prixUnites, String dureeValidite, String typeOffre) {
        this.idUnique = idUnique;
        this.nom = nom;
        this.prixUnites = prixUnites;
        this.dureeValidite = dureeValidite;
        this.typeOffre = typeOffre;
    }

    // Getters et Setters (Encapsulation)
    // ... (omission des getters/setters pour la concision, mais ils DOIVENT exister) ...
    public String getIdUnique() { return idUnique; }
    public String getNom() { return nom; }
    public int getPrixUnites() { return prixUnites; }
    public String getDureeValidite() { return dureeValidite; }
    public String getTypeOffre() { return typeOffre; }
    public void setPrixUnites(int prixUnites) {
        if (prixUnites >= 0) { // Ajout d'une validation simple
            this.prixUnites = prixUnites;
        } else {
            System.out.println("Erreur: Le prix ne peut pas être négatif.");
        }
    }

    // Méthode abstraite : oblige les sous-classes à définir leur propre affichage détaillé
    public abstract String afficherDetail();

    // Méthode polymorphique
    public String afficherOffrePourMenu(int index) {
        return index + ". " + this.nom + " (" + this.dureeValidite + ") - " + this.prixUnites + "U";
    }
}