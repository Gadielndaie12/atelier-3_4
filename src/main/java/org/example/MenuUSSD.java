package org.example;

import java.util.ArrayList;
import java.util.List;

// Classe modélisant un niveau de menu (Principal ou Sous-menu)
public class MenuUSSD {

    // Attributs (5 requis)
    private String titre;
    private int idMenu; // Identifiant unique du menu
    private String messageAccueil;
    private List<OptionUSSD> options; // Liste des forfaits ou des sous-menus
    private boolean estMenuPrincipal; // Indique si c'est le menu de départ

    // Constructeur
    public MenuUSSD(int idMenu, String titre, String messageAccueil, boolean estMenuPrincipal) {
        this.idMenu = idMenu;
        this.titre = titre;
        this.messageAccueil = messageAccueil;
        this.estMenuPrincipal = estMenuPrincipal;
        this.options = new ArrayList<>();
    }

    // --- Getters et Setters ---

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public int getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }

    public String getMessageAccueil() {
        return messageAccueil;
    }

    public void setMessageAccueil(String messageAccueil) {
        this.messageAccueil = messageAccueil;
    }

    public List<OptionUSSD> getOptions() {
        return options;
    }

    public void setOptions(List<OptionUSSD> options) {
        this.options = options;
    }

    public boolean isEstMenuPrincipal() {
        return estMenuPrincipal;
    }

    public void setEstMenuPrincipal(boolean estMenuPrincipal) {
        this.estMenuPrincipal = estMenuPrincipal;
    }

    // --- Méthodes ---

    // Méthode pour ajouter une option au menu
    public void ajouterOption(OptionUSSD option) {
        this.options.add(option);
    }

    // Méthode pour afficher le menu complet
    public void afficherMenu() {
        System.out.println("\n--- " + this.titre.toUpperCase() + " ---");
        if (!this.messageAccueil.isEmpty()) {
            System.out.println(this.messageAccueil);
        }

        for (OptionUSSD option : options) {
            System.out.println(option.afficherOption());
        }

        if (!estMenuPrincipal) {
            System.out.println("9. Retour au menu principal");
        } else {
            System.out.println("0. Quitter");
        }
        System.out.print("Entrez votre choix : ");
    }

    // Méthode pour trouver une option par son ID
    public OptionUSSD trouverOption(int idChoisi) {
        for (OptionUSSD option : options) {
            if (option.getIdOption() == idChoisi) {
                return option;
            }
        }
        return null;
    }
}