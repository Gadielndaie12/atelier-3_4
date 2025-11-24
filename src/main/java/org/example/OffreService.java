package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Classe de service gérant la persistance des offres (liste Java)
public class OffreService {

    // Liste privée : ENCAPSULATION
    private List<Offre> listeOffres;

    public OffreService() {
        this.listeOffres = new ArrayList<>();
        initialiserDonnees(); // Méthode pour pré-remplir la liste
    }

    // --- CRUD: CREATE ---
    public void creerOffre(Offre offre) {
        // Validation simple pour éviter les doublons d'ID
        if (getOffreById(offre.getIdUnique()) == null) {
            listeOffres.add(offre);
        } else {
            System.out.println("Erreur: Une offre avec cet ID existe déjà.");
        }
    }

    // --- CRUD: READ ---
    public List<Offre> lireToutesLesOffres() {
        return new ArrayList<>(listeOffres); // Retourne une copie pour l'Encapsulation
    }

    public Offre getOffreById(String id) {
        return listeOffres.stream()
                .filter(o -> o.getIdUnique().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Offre> lireOffresParType(String type) {
        // Filtrage par le type d'offre (Polymorphisme sur le typeOffre)
        return listeOffres.stream()
                .filter(o -> o.getTypeOffre().equals(type.toUpperCase()))
                .collect(Collectors.toList());
    }

    // --- CRUD: UPDATE ---
    public boolean modifierPrix(String id, int nouveauPrix) {
        Offre offre = getOffreById(id);
        if (offre != null) {
            // Utilisation du setter (Encapsulation)
            offre.setPrixUnites(nouveauPrix);
            return true;
        }
        return false;
    }

    // --- CRUD: DELETE ---
    public boolean supprimerOffre(String id) {
        return listeOffres.removeIf(o -> o.getIdUnique().equals(id));
    }

    // Initialisation (la "persistance" au démarrage)
    private void initialiserDonnees() {
        creerOffre(new ForfaitInternet("I-200", "500 MB (1 Jour)", 200, "1 Jour", "500 MB"));
        creerOffre(new ForfaitInternet("I-500", "2 GB (7 Jours)", 500, "7 Jours", "2 GB"));
        creerOffre(new ForfaitVoix("V-100", "10 Min Voda-Voda", 100, "2 Jours", 10));
        creerOffre(new ForfaitVoix("V-500", "60 Min Voda-Voda", 500, "7 Jours", 60));
        // L'offre MIX serait la plus complexe à gérer en POO simple, ici on la simule comme une offre internet/voix combinée
        creerOffre(new ForfaitInternet("M-400", "1 GB + 10 Min", 400, "1 Jour", "1 GB (Mix)"));
    }
}