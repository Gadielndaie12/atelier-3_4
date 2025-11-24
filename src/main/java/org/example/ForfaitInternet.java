package org.example;

// Hérite de la classe abstraite Offre : HERITAGE
public class ForfaitInternet extends Offre {

    // Attributs spécifiques
    private String volumeData; // Ex: "500 MB", "2 GB"

    public ForfaitInternet(String idUnique, String nom, int prixUnites, String dureeValidite, String volumeData) {
        super(idUnique, nom, prixUnites, dureeValidite, "INTERNET");
        this.volumeData = volumeData;
    }

    // Getters/Setters spécifiques
    public String getVolumeData() { return volumeData; }
    public void setVolumeData(String volumeData) { this.volumeData = volumeData; }

    // Implémentation de la méthode abstraite : POLYMORPHISME
    @Override
    public String afficherDetail() {
        return "Forfait INTERNET: " + nom + " | Volume: " + volumeData + " | Prix: " + prixUnites + "U | Valable: " + dureeValidite;
    }
}