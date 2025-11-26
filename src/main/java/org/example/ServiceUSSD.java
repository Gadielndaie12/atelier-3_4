package org.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ServiceUSSD {

    // Nouvelle dépendance : ENCAPSULATION
    private OffreService offreService;

    // Attributs pour l'Orchestration
    private Scanner scanner;
    private Map<Integer, MenuUSSD> menus; // AJOUTÉ : Map pour stocker et naviguer entre les menus
    private MenuUSSD menuCourant;
    private final String codeUSSD = "*1414#";
    private boolean enCours;

    // Constructeur mis à jour
    public ServiceUSSD() {
        this.scanner = new Scanner(System.in);
        this.offreService = new OffreService(); // Initialise la BDD (la liste)
        this.menus = new HashMap<>(); // AJOUTÉ : Initialisation de la Map
        this.enCours = false;
        initialiserMenus();
    }

    // Setter manquant pour navigation entre menus
    public void setMenuCourant(MenuUSSD menuCourant) {
        this.menuCourant = menuCourant;
    }

    // Méthode de création des menus mise à jour
    private void initialiserMenus() {
        // --- 1. Menu Principal (ID 100) ---
        MenuUSSD menuPrincipal = new MenuUSSD(100, "BIENVENUE SUR *1414# - JUSTE POUR TOI", "", true);
        menus.put(menuPrincipal.getIdMenu(), menuPrincipal); // Enregistrement du menu principal

        // Les options pointent vers des IDs de menu pour la navigation
        menuPrincipal.ajouterOption(new OptionUSSD(1, "Forfaits Internet", "NAV_MENU", 0, "201"));
        menuPrincipal.ajouterOption(new OptionUSSD(2, "Forfaits Voix (Appels)", "NAV_MENU", 0, "202"));
        menuPrincipal.ajouterOption(new OptionUSSD(3, "--- GESTION CRUD ---", "NAV_MENU", 0, "900")); // Navigation vers menu CRUD
        menuPrincipal.ajouterOption(new OptionUSSD(4, "Info/Aide", "AIDE", 0, ""));

        // Les sous-menus réels (où les forfaits sont affichés dynamiquement)
        // Les sous-menus 201 et 202 sont uniquement utilisés pour la navigation logique
        MenuUSSD menuInternet = new MenuUSSD(201, "FORFAITS INTERNET", "Choisissez un forfait :", false);
        menus.put(menuInternet.getIdMenu(), menuInternet);
        MenuUSSD menuVoix = new MenuUSSD(202, "FORFAITS VOIX", "Choisissez un forfait :", false);
        menus.put(menuVoix.getIdMenu(), menuVoix);

        // Menu CRUD (ID 900)
        MenuUSSD menuCRUD = new MenuUSSD(900, "GESTION DES OFFRES (CRUD)", "", false);
        menus.put(menuCRUD.getIdMenu(), menuCRUD); // Enregistrement du menu CRUD

        menuCRUD.ajouterOption(new OptionUSSD(1, "1. Créer une nouvelle offre", "CREATE", 0, ""));
        menuCRUD.ajouterOption(new OptionUSSD(2, "2. Lire/Afficher toutes les offres", "READ", 0, ""));
        menuCRUD.ajouterOption(new OptionUSSD(3, "3. Modifier le prix d'une offre", "UPDATE", 0, ""));
        menuCRUD.ajouterOption(new OptionUSSD(4, "4. Supprimer une offre", "DELETE", 0, ""));
        menuCRUD.ajouterOption(new OptionUSSD(9, "9. Retour au menu principal", "BACK", 0, ""));

        this.menuCourant = menuPrincipal; // Démarrer sur le menu principal
    }

    // Méthode principale de gestion des interactions utilisateur (CORRIGÉE)
    // Dans ServiceUSSD.java, modifier traiterChoixUtilisateur
    // Dans ServiceUSSD.java, modifier traiterChoixUtilisateur
    private void traiterChoixUtilisateur() {
        String choix = scanner.nextLine();

        // 1. Gestion des commandes spéciales (0 et 9)
        if (choix.equals("0") && menuCourant.isEstMenuPrincipal()) {
            this.enCours = false;
            return;
        }

        if (choix.equals("9")) {
            if (!menuCourant.isEstMenuPrincipal()) {
                setMenuCourant(menus.get(100)); // Retour au menu principal
                return;
            }
        }

        try {
            int choixNum = Integer.parseInt(choix);
            OptionUSSD optionChoisie = menuCourant.trouverOption(choixNum);

            if (optionChoisie != null) {

                String action = optionChoisie.getDescriptionForfait();

                // Si l'utilisateur est dans le menu principal
                if (menuCourant.isEstMenuPrincipal()) {
                    switch (action) {
                        case "NAV_MENU":
                            int targetId = Integer.parseInt(optionChoisie.getDureeValidite());

                            // *** CORRECTION CLÉ : Gérer immédiatement l'affichage dynamique / l'achat ***
                            if (targetId == 201) {
                                afficherSousMenuDynamique("INTERNET");
                                setMenuCourant(menus.get(100)); // Retourne au principal après la sélection/l'annulation
                            } else if (targetId == 202) {
                                afficherSousMenuDynamique("VOIX");
                                setMenuCourant(menus.get(100)); // Retourne au principal après la sélection/l'annulation
                            } else {
                                setMenuCourant(menus.get(targetId)); // Navigation vers le menu CRUD (900)
                            }
                            break;
                        case "AIDE":
                            afficherAide();
                            break;
                    }
                }
                // Si l'utilisateur est dans le menu CRUD
                else if (menuCourant.getIdMenu() == 900) {
                    // Exécute l'action CRUD basée sur le choix
                    gererActionCRUD(action);
                }
                // *** BLOC SUPPRIMÉ : L'ancien bloc 201/202 ne doit plus exister ici. ***

            } else {
                System.out.println("\nChoix invalide. Veuillez réessayer.");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nEntrée non numérique. Veuillez entrer un numéro de l'option.");
        }
    }

    // Nouveau : Affichage dynamique des forfaits basés sur la liste Java (AJUSTÉ)
    // Cette méthode gère maintenant l'affichage ET l'achat simulé, et non pas seulement l'affichage.
    private void afficherSousMenuDynamique(String type) {
        // Rediriger la logique d'activation ici, sinon l'activation n'est jamais gérée.
        // Si l'on est dans le menu 201 ou 202, le choix de l'utilisateur est un index de forfait.

        List<Offre> offres = offreService.lireOffresParType(type);
        if (offres.isEmpty()) {
            System.out.println("\nAucune offre de type " + type + " disponible.");
            return;
        }

        System.out.println("\n--- FORFAITS " + type + " (Choisissez une offre à ACTIVER) ---");
        for (int i = 0; i < offres.size(); i++) {
            System.out.println(offres.get(i).afficherOffrePourMenu(i + 1));
        }
        System.out.println("9. Retour au menu principal");
        System.out.print("Entrez votre choix : ");

        String choix = scanner.nextLine();
        try {
            int index = Integer.parseInt(choix);
            if (index == 9) return; // Le retour est géré par le while de demarrerService

            if (index > 0 && index <= offres.size()) {
                Offre offreChoisie = offres.get(index - 1);
                // Utilisation du Polymorphisme : chaque Offre sait donner ses détails
                System.out.println("\nActivation en cours pour : " + offreChoisie.afficherDetail());
                System.out.println("L'achat simulé a réussi. Vous recevrez un SMS de confirmation.");
            } else {
                System.out.println("\nChoix de forfait invalide.");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nEntrée invalide.");
        }

    }

    // Méthode obsolète remplacée par gererActionCRUD, gardée ici vide pour ne pas casser le squelette
    private void gererCRUD() {
        // L'action CRUD n'est plus gérée ici. La navigation vers le menu CRUD (ID 900)
        // est gérée dans traiterChoixUtilisateur().
        // L'exécution des actions se fait via gererActionCRUD.
    }

    // NOUVEAU : Gestion des actions CRUD individuelles
    private void gererActionCRUD(String action) {
        System.out.println("\n--- ACTION : " + action + " ---");

        switch (action) {
            case "READ":
                // READ: Afficher toutes les offres
                if (offreService.lireToutesLesOffres().isEmpty()) {
                    System.out.println("La base de données est vide.");
                } else {
                    offreService.lireToutesLesOffres().forEach(offre -> {
                        System.out.println("ID: " + offre.getIdUnique() + " | " + offre.afficherDetail());
                    });
                }
                break;

            case "CREATE":
                // CREATE: Créer une nouvelle offre (simplifié pour la démo)
                try {
                    System.out.print("Nouvel ID unique (Ex: X-100) : ");
                    String newId = scanner.nextLine();
                    System.out.print("Nom de l'offre : ");
                    String newNom = scanner.nextLine();
                    System.out.print("Prix en Unités : ");
                    int newPrix = Integer.parseInt(scanner.nextLine());
                    System.out.print("Type d'offre (INTERNET/VOIX) : ");
                    String newType = scanner.nextLine().toUpperCase();

                    Offre nouvelleOffre = null;
                    if (newType.equals("INTERNET")) {
                        System.out.print("Volume Data (Ex: 1GB) : ");
                        String newData = scanner.nextLine();
                        nouvelleOffre = new ForfaitInternet(newId, newNom, newPrix, "30 Jours", newData);
                    } else if (newType.equals("VOIX")) {
                        System.out.print("Minutes d'appel : ");
                        int newMins = Integer.parseInt(scanner.nextLine());
                        nouvelleOffre = new ForfaitVoix(newId, newNom, newPrix, "30 Jours", newMins);
                    }

                    if (nouvelleOffre != null) {
                        offreService.creerOffre(nouvelleOffre);
                        System.out.println("Offre créée avec succès !");
                    } else {
                        System.out.println("Type d'offre non reconnu.");
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Erreur: Entrée prix/minutes invalide.");
                }
                break;

            case "UPDATE":
                // UPDATE: Modifier le prix
                try {
                    System.out.print("ID de l'offre à modifier : ");
                    String updateId = scanner.nextLine();
                    Offre offreToUpdate = offreService.getOffreById(updateId);
                    if (offreToUpdate != null) {
                        System.out.print("Nouveau prix en Unités pour " + offreToUpdate.getNom() + " (actuel: " + offreToUpdate.getPrixUnites() + ") : ");
                        int nouveauPrix = Integer.parseInt(scanner.nextLine());
                        if (offreService.modifierPrix(updateId, nouveauPrix)) {
                            System.out.println("Prix mis à jour avec succès !");
                        }
                    } else {
                        System.out.println("Erreur: Offre non trouvée.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Erreur: Entrée prix invalide.");
                }
                break;

            case "DELETE":
                // DELETE: Supprimer une offre
                System.out.print("ID de l'offre à supprimer : ");
                String deleteId = scanner.nextLine();
                if (offreService.supprimerOffre(deleteId)) {
                    System.out.println("Offre " + deleteId + " supprimée avec succès.");
                } else {
                    System.out.println("Erreur: Offre non trouvée.");
                }
                break;
        }

        System.out.println("(Appuyez sur Entrée pour revenir au menu CRUD)");
        scanner.nextLine();
    }

    // Méthode d'affichage de l'aide
    private void afficherAide() {
        System.out.println("\n--- INFO/AIDE ---");
        System.out.println("Le service *1414# vous propose les meilleures offres 'Juste pour Toi'.");
        System.out.println("Composez *1100# pour vérifier le solde de vos forfaits.");
        System.out.println("(Appuyez sur Entrée pour continuer)");
        scanner.nextLine();
    }

    public void demarrerService() {
        this.enCours = true;
        System.out.println("--- Démarrage du service USSD : " + this.codeUSSD + " ---");

        while (enCours) {
            menuCourant.afficherMenu();
            traiterChoixUtilisateur();
        }

        System.out.println("\nMerci d'avoir utilisé le service Vodacom. Au revoir.");
        scanner.close();
    }

    // Point d'entrée de l'application (main)
    public static void main(String[] args) {
        ServiceUSSD service = new ServiceUSSD();
        service.demarrerService();
    }
}