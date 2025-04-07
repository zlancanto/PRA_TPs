/**
 * Définit le module pour l'application Mots Croisés.
 * Requiert les modules JavaFX nécessaires, SQL, et potentiellement d'autres modules projet (morpion, junit).
 * Ouvre les packages spécifiques pour l'accès par réflexion requis par JavaFX.
 */
module PRA.TPs { // Assurez-vous que c'est le bon nom de module

    // == Modules requis ==
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;    // Pour les propriétés JavaFX dans le modèle
    requires java.sql;       // Pour la classe Database

    // Dépendances spécifiques à votre projet (à conserver si correctes)
    requires morpion;
    requires junit;
    requires org.junit.jupiter.api;


    // == Ouverture des packages pour JavaFX (Réflexion) ==
    // Ouvre le package controller à javafx.fxml pour que FXMLLoader puisse y accéder
    opens mihan.sossou.crossword.controller to javafx.fxml;

    // Ouvre le package model à javafx.base si FXML utilise des liaisons directes aux propriétés
    opens mihan.sossou.crossword.model to javafx.base;

    // Ouvre le package app à javafx.graphics pour le lancement et à javafx.fxml (si FXML y accède)
    opens mihan.sossou.crossword.app to javafx.graphics, javafx.fxml;


    // == Exports (uniquement si d'autres modules *externes* ont besoin d'accéder à ces packages) ==
    // Conservez ceci si votre module 'morpion' ou autre a besoin d'accéder à ce package
    exports mihan.sossou.morpion to javafx.graphics; // À vérifier : est-ce vraiment nécessaire ?


    // == Ouverture pour les tests (à conserver) ==
    opens mihan.sossou.tp4.test to junit;

    // !! Les lignes suivantes ont été supprimées car redondantes/incorrectes avec 'opens' !!
    // exports mihan.sossou.crossword.controller to javafx.fxml; // Supprimé
    // exports mihan.sossou.crossword.app to javafx.graphics;    // Supprimé
}
