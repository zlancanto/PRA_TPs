package mihan.sossou.tp3.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.istic.l3miage.morpion.*;

import static org.junit.jupiter.api.Assertions.*;

public class TicTacToeTest {
    AbstractTicTacToe morpions;
    public static final int TAILLE = AbstractTicTacToe.BOARD_WIDTH;
    public static final int NB_CASES = AbstractTicTacToe.BOARD_WIDTH * AbstractTicTacToe.BOARD_HEIGHT;

    @BeforeEach
    public void setUp() {
//		morpions = new TicTacToeV5();
//		morpions = new TicTacToeV10();
        morpions = new TicTacToeV10();
    }

    @Test
    public void testInit() {
        assertEquals(morpions.getTurn(), Owner.FIRST, "Le premier doit jouer");
        testInvariant();
        assertTrue(allCaseAreNONE(), "Toutes les cases sont vides");
        assertFalse(morpions.gameOver(), "Le jeu n'a pas encore commencé");
        assertEquals(Owner.NONE, morpions.getWinner(), "Il n'y a pas encore de gagnant");
    }

    @Test
    public void testRestart() {
        // On joue dans les cases
        morpions.play(0, 0);
        testInvariant();
        morpions.nextPlayer();

        morpions.play(2, 2);
        testInvariant();
        morpions.nextPlayer();

        morpions.play(0, 2);
        testInvariant();
        morpions.nextPlayer();

        morpions.play(0, 1);
        testInvariant();
        morpions.nextPlayer();

        morpions.play(1, 1);
        testInvariant();
        morpions.nextPlayer();

        morpions.restart();
        testInit();
    }

    /**
     * scénarios vérifiant le bon fonctionnement de getTurn()
     */
	@Test
	public void testGetJoueur() {
        morpions.restart();

        // Joueur courant = FIRST
		assertEquals(Owner.FIRST, morpions.getTurn(), "Le joueur courant devrait être FIRST" );

        morpions.play(1, 0);
		//testInvariant();
		morpions.nextPlayer();
        // Joueur courant = SECOND
		assertEquals(Owner.SECOND, morpions.getTurn(), "Le joueur courant devrait être SECOND");

        morpions.play(1, 2);
        testInvariant();
        morpions.nextPlayer();
        // Joueur courant = FIRST
        assertEquals(Owner.FIRST, morpions.getTurn(), "Le joueur courant devrait être FIRST" );

        morpions.play(2, 1);
        testInvariant();
        morpions.nextPlayer();
        // Joueur courant = SECOND
        assertEquals(Owner.SECOND, morpions.getTurn(), "Le joueur courant devrait être SECOND");

        morpions.restart();
        /*
         * 111
         * 22X
         * X2X
         */
        playWithTwo(0, 0, 1, 1, morpions);
        playWithTwo(0, 1, 1, 0, morpions);
        morpions.play(0, 2);
        testInvariant();
        assertTrue(morpions.gameOver(), "Le jeu devrait être terminé");
        assertEquals(Owner.NONE, morpions.getTurn(), "Le joueur courant devrait être NONE après la fin du jeu");
	}

    /**
     * scénarios vérifiant le bon fonctionnement de getWinner() et ainsi de suite
     * pour numberOfRounds(), \dots , jusqu’à play()
     */
    @Test
    public void testGetVainqueur() {
        morpions.restart();

        /*
         * 111
         * 22X
         * X2X
         */
        playWithTwo(0, 0, 1, 1, morpions);
        playWithTwo(0, 1, 1, 0, morpions);
        playWithOne(0, 2, morpions);
        assertEquals(Owner.FIRST, morpions.getWinner(), "Le joueur1 est gagnant");
        morpions.restart();

        /*
         * 22X
         * 111
         * XX2
         */
        playWithTwo(1, 0, 0, 0, morpions);
        playWithTwo(1, 1, 0, 1, morpions);
        playWithOne(1, 2, morpions);
        assertEquals(Owner.FIRST, morpions.getWinner(), "Le joueur1 est gagnant");
        morpions.restart();

        /*
         * 22X
         * X2X
         * 111
         */
        playWithTwo(2, 0, 0, 0, morpions);
        playWithTwo(2, 1, 0, 1, morpions);
        playWithOne(2, 2, morpions);
        assertEquals(Owner.FIRST, morpions.getWinner(), "Le joueur1 est gagnant");
        morpions.restart();

        /*
         * 1X2
         * 1X2
         * 12X
         */
        playWithTwo(0, 0, 0, 2, morpions);
        playWithTwo(1, 0, 1, 2, morpions);
        playWithOne(2, 0, morpions);
        assertEquals(Owner.FIRST, morpions.getWinner(), "Le joueur1 est gagnant");
        morpions.restart();

        /*
         * 21X
         * 21X
         * X12
         */
        playWithTwo(0, 1, 0, 0, morpions);
        playWithTwo(1, 1, 1, 0, morpions);
        playWithOne(2, 1, morpions);
        assertEquals(Owner.FIRST, morpions.getWinner(), "Le joueur1 est gagnant");
        morpions.restart();

        /*
         * 2X1
         * 2X1
         * X21
         */
        playWithTwo(0, 2, 0, 0, morpions);
        playWithTwo(1, 2, 1, 0, morpions);
        playWithOne(2, 2, morpions);
        assertEquals(Owner.FIRST, morpions.getWinner(), "Le joueur1 est gagnant");
        morpions.restart();

        /*
         * 1XX
         * 21X
         * 221
         */
        playWithTwo(0, 0, 1, 0, morpions);
        playWithTwo(1, 1, 2, 0, morpions);
        playWithOne(2, 2, morpions);
        assertEquals(Owner.FIRST, morpions.getWinner(), "Le joueur1 est gagnant");
        morpions.restart();

        /*
         * 221
         * 21X
         * 1XX
         */
        playWithTwo(0, 2, 0, 0, morpions);
        playWithTwo(1, 1, 0, 1, morpions);
        playWithOne(2, 0, morpions);
        assertEquals(Owner.FIRST, morpions.getWinner(), "Le joueur1 est gagnant");
    }

    /**
     * Scénario vérifiant le premier coup joué, notamment : - position correcte ou
     * non - non-fin de partie - identité du premier joueur
     */
    @Test
    public void testPremierCoup() {
    }

    /**
     * Scénario explorant les situations de non-fin de partie, avec vérification
     * systématique de gameOver() == false
     */
    @Test
    public void testPartiePasFinie() {
    }

    /**
     * Scénario tentant divers coups non autorisés, avec vérification systématique
     * de legalMove()
     */
    @Test
    public void testControle() {
        morpions.restart();

    }

    /**
     * Scénarios explorant les situations de fin de partie, avec vérification
     * systématique de gameOver() et de getWinner() : - alignement horizontal
     * (grille pleine ou non) - alignement vertical (grille pleine ou non) -
     * alignement diagonal (grille pleine ou non) - et ainsi de suite...
     */
    @Test
    public void testFinPartie() {
        morpions.restart();
        /**
         * 111
         * xxx
         * 22x
         */
        playWithTwo(0, 0, 2, 0, morpions);
        playWithTwo(0, 1, 2, 1, morpions);
        playWithOne(0, 2, morpions);
        assertTrue(morpions.gameOver(), "Le joueur1 est gagnant");

        /**
         * 2x1
         * 121
         * xx2
         */
        playWithTwo(0, 2, 1, 1, morpions);
        playWithTwo(1, 2, 2, 2, morpions);
        playWithTwo(1, 0, 0, 0, morpions);
        assertTrue(morpions.gameOver(), "Le joueur2 est gagnant");

        /**
         * 211
         * 112
         * 121
         */
        playWithTwo(1, 1, 0, 2, morpions);
        playWithTwo(2, 2, 0, 0, morpions);
        playWithTwo(0, 1, 2, 1, morpions);
        playWithTwo(1, 0, 1, 2, morpions);
        playWithOne(2, 0, morpions);
        assertTrue(morpions.gameOver(), "Partie nulle");
    }

    private void playWithTwo(int firstRow, int firstCol, int secondRow, int secondCol, AbstractTicTacToe morpions) {
        morpions.play(firstRow, firstCol);
        testInvariant();
        morpions.nextPlayer();
        morpions.play(secondRow, secondCol);
        testInvariant();
        morpions.nextPlayer();
    }

    private void playWithOne(int row, int col, AbstractTicTacToe morpions) {
        morpions.play(row, col);
        testInvariant();
        morpions.nextPlayer();
    }

    /**
     * @return true si toutes les cases sont vides
     */
    private boolean allCaseAreNONE() {
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
        assertTrue(morpions.getWinner() == Owner.FIRST || morpions.getWinner() == Owner.SECOND
                           || morpions.getWinner() == Owner.NONE, "Le vainqueur doit soit être FIRST, soit SECOND, soit NONE");
        assertTrue(morpions.getTurn() == Owner.FIRST || morpions.getTurn() == Owner.SECOND
                           || morpions.getTurn() == Owner.NONE, "Le joureur courant doit soit être FIRST, soit SECOND, soit NONE");
        // ----------------------
        // SÉQUENCE À COMPLÉTER
        // ----------------------
    }

}