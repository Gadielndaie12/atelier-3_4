package org.example;

// Hérite de la classe abstraite Offre : HERITAGE
public class ForfaitVoix extends Offre {

    // Attributs spécifiques
    private int minutesAppel; // Ex: 10, 60

    public ForfaitVoix(String idUnique, String nom, int prixUnites, String dureeValidite, int minutesAppel) {
        super(idUnique, nom, prixUnites, dureeValidite, "VOIX");
        this.minutesAppel = minutesAppel;
    }

    // Getters/Setters spécifiques
    public int getMinutesAppel() { return minutesAppel; }
    public void setMinutesAppel(int minutesAppel) { this.minutesAppel = minutesAppel; }

    // Implémentation de la méthode abstraite : POLYMORPHISME
    @Override
    public String afficherDetail() {
        return "Forfait VOIX: " + nom + " | Minutes: " + minutesAppel + " | Prix: " + prixUnites + "U | Valable: " + dureeValidite;
    }
}