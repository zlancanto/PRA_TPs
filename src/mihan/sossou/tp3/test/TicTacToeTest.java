package mihan.sossou.tp3.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.istic.l3miage.morpion.*;

import static org.junit.jupiter.api.Assertions.*;

public class TicTacToeTest
{
    AbstractTicTacToe morpions;
    public static final int TAILLE = AbstractTicTacToe.BOARD_WIDTH;
    public static final int NB_CASES = AbstractTicTacToe.BOARD_WIDTH * AbstractTicTacToe.BOARD_HEIGHT;

    @BeforeEach
    public void setUp() {
//		morpions = new TicTacToeV1();
//		morpions = new TicTacToeV2();
//		morpions = new TicTacToeV3();
//		morpions = new TicTacToeV4();
//		morpions = new TicTacToeV5();
//		morpions = new TicTacToeV6();
        morpions = new TicTacToeV7();
    }

    @Test
    public void testInit() {
        assertEquals(morpions.getTurn(), Owner.FIRST, "Le premier doit jouer");
        testInvariant();
        assertTrue(allCaseAreNONE(), "Toutes les cases sont vides");
        assertFalse(morpions.gameOver(), "Le jeu n'a pas encore commencé");
        assertSame(Owner.NONE, morpions.getWinner(), "Il n'y a pas encore de gagnant");
        // ----------------------
        // SÉQUENCE À COMPLÉTER
        // ----------------------
    }

    @Test
    public void testRestart() {

    }

    /**
     * scénarios vérifiant le bon fonctionnement de getTurn()
     */
    @Test
    public void testGetJoueur() {}

    /**
     * scénarios vérifiant le bon fonctionnement de getWinner()
     * et ainsi de suite pour numberOfRounds(), \dots ,
     * jusqu’à play()
     */
    @Test
    public void testGetVainqueur() {}

    /**
     * Scénario vérifiant le premier coup joué, notamment :
     * - position correcte ou non
     * - non-fin de partie
     * - identité du premier joueur
     */
    @Test
    public void testPremierCoup() {}

    /**
     * Scénario explorant les situations de non-fin de partie,
     * avec vérification systématique de gameOver() == false
     */
    @Test
    public void testPartiePasFinie() {}

    /**
     * Scénario tentant divers coups non autorisés,
     * avec vérification systématique de legalMove()
     */
    @Test
    public void testControle() {}

    /**
     * Scénarios explorant les situations de fin de partie,
     * avec vérification systématique de gameOver() et de
     * getWinner() :
     * - alignement horizontal (grille pleine ou non)
     * - alignement vertical (grille pleine ou non)
     * - alignement diagonal (grille pleine ou non)
     * - et ainsi de suite...
     */
    @Test
    public void testFinPartie() {}

    /**
     * @return true si toutes les cases sont vides
     */
    private boolean allCaseAreNONE () {
        for (int i = 0; i < TAILLE; i++) {
            for (int j = 0; j < TAILLE; j++) {
                if (morpions.getSquare(i, j) != Owner.NONE) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Fonction à utiliser après chaque action, pour tester les conditions qui
     * doivent toujours être vraies
     */
    private void testInvariant() {
        assertTrue(morpions.numberOfRounds() >= 0, "Nombre de coups >= 0");
        assertTrue(morpions.numberOfRounds() <= NB_CASES, "Nombre de coups <= " + NB_CASES);
        assertTrue(morpions.getWinner() == Owner.FIRST ||
                           morpions.getWinner() == Owner.SECOND ||
                           morpions.getWinner() == Owner.NONE,
                "Le premier doit jouer"
        );
        assertTrue(morpions.getTurn() == Owner.FIRST ||
                           morpions.getTurn() == Owner.SECOND ||
                           morpions.getTurn() == Owner.NONE,
                "Le premier doit jouer"
        );
        // ----------------------
        // SÉQUENCE À COMPLÉTER
        // ----------------------
    }

}