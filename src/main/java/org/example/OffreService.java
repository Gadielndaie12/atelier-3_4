package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Classe de service gérant la persistance des offres via JSON
public class OffreService {

    private static final String JSON_FILE = "offres.json"; // Nom du fichier de persistance
    private List<Offre> listeOffres;
    private final Gson gson; // Outil de (Dé)Sérialisation

    // Constructeur : initialise Gson et charge les données
    public OffreService() {
        // PrettyPrinting pour un fichier JSON lisible
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.listeOffres = new ArrayList<>();
        chargerOffresDepuisJson(); // Charger les données au démarrage
    }

    // --- Méthodes de Persistance (Lecture/Écriture) ---

    // CHARGEMENT des données depuis le fichier JSON
    private void chargerOffresDepuisJson() {
        try (FileReader reader = new FileReader(JSON_FILE)) {
            // Lecture du fichier JSON
            JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();

            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();

                // Récupération des champs communs à Offre
                String id = jsonObject.get("idUnique").getAsString();
                String nom = jsonObject.get("nom").getAsString();
                int prix = jsonObject.get("prixUnites").getAsInt();
                String duree = jsonObject.get("dureeValidite").getAsString();
                String type = jsonObject.get("typeOffre").getAsString();

                Offre nouvelleOffre;

                // Polymorphisme manuel: recréer l'objet concret en fonction du type
                if (type.equals("INTERNET")) {
                    String volume = jsonObject.get("volumeData").getAsString();
                    nouvelleOffre = new ForfaitInternet(id, nom, prix, duree, volume);
                } else if (type.equals("VOIX")) {
                    int minutes = jsonObject.get("minutesAppel").getAsInt();
                    nouvelleOffre = new ForfaitVoix(id, nom, prix, duree, minutes);
                } else {
                    System.err.println("Type d'offre inconnu lors du chargement: " + type);
                    continue;
                }

                this.listeOffres.add(nouvelleOffre);
            }

        } catch (FileNotFoundException e) {
            System.out.println("\n--- ALERTE ---");
            System.out.println("Fichier " + JSON_FILE + " non trouvé. Initialisation des données de démo.");
            System.out.println("--- ALERTE ---\n");
            initialiserDonneesSiVide(); // Crée les données de démo et les sauvegarde
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // SAUVEGARDE des données vers le fichier JSON
    private void sauvegarderOffresVersJson() {
        try (FileWriter writer = new FileWriter(JSON_FILE)) {
            // Sérialisation directe de la liste. Gson inclut les champs des sous-classes
            gson.toJson(this.listeOffres, writer);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des offres: " + e.getMessage());
        }
    }

    // Initialisation simple si le fichier n'existe pas ou est vide
    private void initialiserDonneesSiVide() {
        if (this.listeOffres.isEmpty()) {
            // CREATION des données initiales
            creerOffre(new ForfaitInternet("I-200", "500 MB (1 Jour)", 200, "1 Jour", "500 MB"));
            creerOffre(new ForfaitInternet("I-500", "2 GB (7 Jours)", 500, "7 Jours", "2 GB"));
            creerOffre(new ForfaitVoix("V-100", "10 Min (1 Jour)", 100, "1 Jour", 10));
            // Pas besoin de sauvegarder ici, creerOffre le fait
        }
    }

    // --- CRUD: CREATE ---
    public void creerOffre(Offre offre) {
        if (getOffreById(offre.getIdUnique()) == null) {
            listeOffres.add(offre);
            sauvegarderOffresVersJson(); // Sauvegarde après CREATION
            System.out.println("Offre créée et sauvegardée avec succès.");
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
            offre.setPrixUnites(nouveauPrix); // Utilisation du setter (Encapsulation)
            sauvegarderOffresVersJson(); // Sauvegarde après MODIFICATION
            return true;
        }
        return false;
    }

    // --- CRUD: DELETE ---
    public boolean supprimerOffre(String id) {
        boolean removed = listeOffres.removeIf(o -> o.getIdUnique().equals(id));
        if (removed) {
            sauvegarderOffresVersJson(); // Sauvegarde après SUPPRESSION
        }
        return removed;
    }
}