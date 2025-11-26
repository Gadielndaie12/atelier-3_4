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
        this.offreService = new OffreService(); // Initialise la BDD (maintenant JSON)
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

        // Les options pointent vers des menus spécifiques (ID de navigation)
        menuPrincipal.ajouterOption(new OptionUSSD(1, "Acheter Forfait Internet", "NAV_MENU", 0, "201"));
        menuPrincipal.ajouterOption(new OptionUSSD(2, "Acheter Forfait Voix", "NAV_MENU", 0, "202"));
        menuPrincipal.ajouterOption(new OptionUSSD(3, "Gérer les offres (CRUD)", "NAV_MENU", 0, "900"));
        menuPrincipal.ajouterOption(new OptionUSSD(4, "Aide et Informations", "AIDE", 0, ""));

        // --- 2. Menu pour Forfaits Internet (ID 201) - Pas d'options car dynamique ---
        MenuUSSD menuInternet = new MenuUSSD(201, "FORFAITS INTERNET", "Choisissez votre forfait Internet (9. Retour)", false);
        menus.put(menuInternet.getIdMenu(), menuInternet);

        // --- 3. Menu pour Forfaits Voix (ID 202) - Pas d'options car dynamique ---
        MenuUSSD menuVoix = new MenuUSSD(202, "FORFAITS VOIX", "Choisissez votre forfait Voix (9. Retour)", false);
        menus.put(menuVoix.getIdMenu(), menuVoix);

        // --- 4. Menu CRUD (ID 900) ---
        MenuUSSD menuCRUD = new MenuUSSD(900, "GESTION DES OFFRES (CRUD)", "Veuillez choisir l'action à effectuer:", false);
        menuCRUD.ajouterOption(new OptionUSSD(1, "Créer une nouvelle offre", "CREATE", 0, ""));
        menuCRUD.ajouterOption(new OptionUSSD(2, "Afficher toutes les offres", "READ_ALL", 0, ""));
        menuCRUD.ajouterOption(new OptionUSSD(3, "Modifier le prix d'une offre", "UPDATE_PRICE", 0, ""));
        menuCRUD.ajouterOption(new OptionUSSD(4, "Supprimer une offre", "DELETE", 0, ""));
        menus.put(menuCRUD.getIdMenu(), menuCRUD);

        // Menu de départ
        setMenuCourant(menuPrincipal);
    }

    // Méthode corrigée pour la navigation et l'action
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

                            // *** CORRECTION CLÉ : Gérer l'affichage/l'achat dynamique immédiatement ***
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

            } else {
                System.out.println("\nChoix invalide. Veuillez réessayer.");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nEntrée non numérique. Veuillez entrer un numéro de l'option.");
        }
    }

    // Méthode pour afficher les forfaits Internet ou Voix
    private void afficherSousMenuDynamique(String type) {
        System.out.println("\n--- ACHAT DE FORFAITS " + type + " ---");
        List<Offre> offres = offreService.lireOffresParType(type);

        if (offres.isEmpty()) {
            System.out.println("Aucune offre " + type + " disponible pour le moment.");
        } else {
            int i = 1;
            for (Offre offre : offres) {
                System.out.println(i + ". " + offre.afficherDetail());
                i++;
            }
            System.out.println("9. Retour au menu principal");
            System.out.print("Entrez le numéro du forfait à acheter (ou 9) : ");

            String choix = scanner.nextLine();
            if (choix.equals("9")) {
                System.out.println("Annulation. Retour au menu principal.");
                return;
            }

            try {
                int index = Integer.parseInt(choix) - 1;
                if (index >= 0 && index < offres.size()) {
                    Offre offreChoisie = offres.get(index);
                    simulerAchat(offreChoisie);
                } else {
                    System.out.println("Choix invalide.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrée invalide. Retour au menu principal.");
            }
        }
    }

    private void simulerAchat(Offre offre) {
        System.out.println("\nConfirmation: Voulez-vous acheter le " + offre.getNom() + " pour " + offre.getPrixUnites() + "U ? (O/N)");
        String confirmation = scanner.nextLine().toUpperCase();

        if (confirmation.equals("O")) {
            System.out.println("\n*** Succès ! ***");
            System.out.println("Le forfait " + offre.getNom() + " a été activé. Vous avez été débité de " + offre.getPrixUnites() + " Unités.");
        } else {
            System.out.println("\nAchat annulé. Retour au menu principal.");
        }
        System.out.println("(Appuyez sur Entrée pour continuer)");
        scanner.nextLine();
    }

    // Méthode mise à jour pour le CRUD
    private void gererActionCRUD(String action) {
        System.out.println("\n--- EXÉCUTION DE L'ACTION: " + action + " ---");

        switch (action) {
            case "CREATE":
                System.out.println("\n--- CRÉATION D'UNE NOUVELLE OFFRE ---");
                System.out.print("Type d'offre (INTERNET ou VOIX) : ");
                String type = scanner.nextLine().toUpperCase();
                System.out.print("ID Unique (Ex: I-100 ou V-300) : ");
                String id = scanner.nextLine();
                System.out.print("Nom de l'offre : ");
                String nom = scanner.nextLine();
                System.out.print("Prix en Unités : ");
                int prix = Integer.parseInt(scanner.nextLine());
                System.out.print("Durée de validité (Ex: 1 Jour, 7 Jours) : ");
                String duree = scanner.nextLine();

                Offre nouvelleOffre = null;

                if (type.equals("INTERNET")) {
                    System.out.print("Volume de données (Ex: 1 GB, 500 MB) : ");
                    String volume = scanner.nextLine();
                    nouvelleOffre = new ForfaitInternet(id, nom, prix, duree, volume);
                } else if (type.equals("VOIX")) {
                    System.out.print("Minutes d'appel incluses (nombre entier) : ");
                    int minutes = Integer.parseInt(scanner.nextLine());
                    nouvelleOffre = new ForfaitVoix(id, nom, prix, duree, minutes);
                } else {
                    System.out.println("Type d'offre non reconnu.");
                    return;
                }

                if (nouvelleOffre != null) {
                    offreService.creerOffre(nouvelleOffre);
                }
                break;

            case "READ_ALL":
                System.out.println("\n--- LISTE COMPLÈTE DES OFFRES (JSON) ---");
                List<Offre> toutesLesOffres = offreService.lireToutesLesOffres();
                if (toutesLesOffres.isEmpty()) {
                    System.out.println("Aucune offre n'est actuellement enregistrée.");
                } else {
                    toutesLesOffres.forEach(offre -> System.out.println(offre.afficherDetail()));
                }
                break;

            case "UPDATE_PRICE":
                System.out.print("ID de l'offre à modifier : ");
                String updateId = scanner.nextLine();
                System.out.print("Nouveau prix en Unités : ");
                int nouveauPrix = Integer.parseInt(scanner.nextLine());

                if (offreService.modifierPrix(updateId, nouveauPrix)) {
                    System.out.println("Prix de l'offre " + updateId + " mis à jour avec succès et sauvegardé.");
                } else {
                    System.out.println("Erreur: Offre non trouvée.");
                }
                break;

            case "DELETE":
                System.out.print("ID de l'offre à supprimer : ");
                String deleteId = scanner.nextLine();
                if (offreService.supprimerOffre(deleteId)) {
                    System.out.println("Offre " + deleteId + " supprimée avec succès et sauvegardée.");
                } else {
                    System.out.println("Erreur: Offre non trouvée.");
                }
                break;
        }

        System.out.println("\n(Appuyez sur Entrée pour revenir au menu CRUD)");
        scanner.nextLine();
    }

    // Méthode d'affichage de l'aide
    private void afficherAide() {
        System.out.println("\n--- INFO/AIDE ---\nLe service *1414# vous propose les meilleures offres 'Juste pour Toi'.\nComposez *1100# pour vérifier le solde de vos forfaits.");
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