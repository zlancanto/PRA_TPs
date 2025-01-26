package mihan.sossou.tp1.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mihan.sossou.tp1.Crossword;

public class TestCrossword {
	private Crossword mc;
	private int hauteur;
	private int largeur;
	private boolean[][] noire;
	private Character[][] solution;
	private Character[][] proposition;
	private String[][] horizontal;
	private String[][] vertical;

	@BeforeEach
	public void setUp() throws Exception {
		hauteur = 321;
		largeur = 654;
		mc = new Crossword(hauteur, largeur);
		System.out.println("Test de " + mc.getClass().getName());
		noire = new boolean[hauteur][largeur];
		solution = new Character[hauteur][largeur];
		proposition = new Character[hauteur][largeur];
		horizontal = new String[hauteur][largeur];
		vertical = new String[hauteur][largeur];

		// Mise en place des cases noires
		// Première étape : "mise à blanc" de toutes les cases
		for (int lig = 0; lig < mc.getHeight(); lig++) {
			for (int col = 0; col < mc.getWidth(); col++) {
				noire[lig][col] = false;
				mc.setBlackSquare(lig, col, false);
			}
		}

		// Deuxième étape : noircir toutes les cases d'une diagonale (i,i)
		// exceptée pour (2,2)
		for (int i = 0; i < mc.getHeight(); i++) {
			if (i != 2) {
				noire[i][i] = true;
				mc.setBlackSquare(i, i, true);
			}
		}

		// Mise en place des solutions, des propositions et des définitions
		fill();

		// Création d'une 2ème instance pour tester d'ventuels effets de bord
		Crossword mc2 = new Crossword(3, 4);
		for (int lig = 0; lig < mc2.getHeight(); lig++) {
			for (int col = 0; col < mc2.getWidth(); col++) {
				mc2.setBlackSquare(lig, col, false);
			}
		}

		mc2.setBlackSquare(1, 1, true);
		mc2.setSolution(2, 3, 'Z');
		mc2.setProposition(2, 3, 'Y');
		mc2.setDefinition(2, 1, true, "WWWW");
		mc2.setDefinition(1, 3, false, "XXXX");
	}

	private void fill() {
		char lettre = 'A';
		for (int lig = 0; lig < mc.getHeight(); lig++) {
			for (int col = 0; col < mc.getWidth(); col++) {
				if (!mc.isBlackSquare(lig, col)) {
					solution[lig][col] = lettre;
					mc.setSolution(lig, col, lettre);
					String def = "(" + lig + "," + col + ") " + lettre;
					if ((lig + col) % 2 == 0) {
						horizontal[lig][col] = def;
						mc.setDefinition(lig, col, true, def);
					}
					if ((lig + col) % 3 == 0) {
						vertical[lig][col] = def;
						mc.setDefinition(lig, col, false, def);
					}
					lettre++;
					proposition[lig][col] = lettre;
					mc.setProposition(lig, col, lettre);
					lettre++;
				}
			}
		}
	}

	public void afficherReference(Object[][] tab, String libelle) {
		System.out.println("Référence " + libelle + " :");
		for (int lig = 0; lig < hauteur; lig++) {
			for (int col = 0; col < largeur; col++) {
				System.out.print("" + tab[lig][col] + "-");
			}
			System.out.println("");
		}
		System.out.println(" ");
	}

	@Test
	public void testCasesNoires() {
		for (int lig = 0; lig < mc.getHeight(); lig++)
			for (int col = 0; col < mc.getWidth(); col++) {
				if (noire[lig][col]) {
					assertTrue(mc.isBlackSquare(lig, col), "La case (" + lig + "," + col + ") doit être noire");
				} else {
					assertFalse(mc.isBlackSquare(lig, col), "La case (" + lig + "," + col + ") ne doit pas être noire");
				}
			}
	}

	@Test
	public void testSolutions() {
		testGrille(solution, "solution");
	}

	@Test
	public void testPropositions() {
		testGrille(proposition, "proposition");
	}

	@Test
	public void testHorizontal() {
		testGrille(horizontal, "définition horizontale");
	}

	@Test
	public void testVertical() {
		testGrille(vertical, "définition verticale");
	}

	@Test
	public void testToutesAssertions() {
		String probleme = "";
		for (int meth = 1; meth <= NB_METHODES; meth++) {
			probleme += testMethode(meth);
		}
		if (probleme.length() > 0) {
			fail("Ces appels de fonction devraient d�clencher une RuntimeException : " + probleme.toString());
		}
	}

	private static final int NB_METHODES = 18;

	private String testMethode(int meth) {
		String[] probleme = { "isBlackSquare(0, largeur)", "isBlackSquare(hauteur, -1)",
				"isBlackSquare(hauteur+1, largeur)", "isBlackSquare(hauteur, 9999)", "setBlackSquare(hauteur, 0, true)",
				"setBlackSquare(hauteur, largeur+1, true)", "getSolution(0, 1)", "setSolution(1, 0, 'A')",
				"getProposition(hauteur+1, 1)", "setProposition(1, largeur+1, 'A')",
				"getDefinition(hauteur+1, 1, true)", "setDefinition(1, largeur+1, false, \"bla bla\")",
				"case noire : getSolution(1, 1)", "case noire : setSolution(3, 3, 'B')",
				"case noire : getProposition(4, 4)", "case noire : setProposition(1, 1, 'C')",
				"case noire : getDefinition(3, 3, true", "case noire : setDefinition(4, 4, false, \"bla bla\")" };
		try {
			switch (meth) {
			case 1:
				mc.isBlackSquare(0, largeur);
				break;
			case 2:
				mc.isBlackSquare(hauteur, -1);
				break;
			case 3:
				mc.isBlackSquare(hauteur + 1, largeur);
				break;
			case 4:
				mc.isBlackSquare(hauteur, 9999);
				break;
			case 5:
				mc.setBlackSquare(hauteur, 0, true);
				break;
			case 6:
				mc.setBlackSquare(hauteur, largeur + 1, true);
				break;
			case 7:
				mc.getSolution(-1, 1);
				break;
			case 8:
				mc.setSolution(1, -1, 'A');
				break;
			case 9:
				mc.getProposition(hauteur, 1);
				break;
			case 10:
				mc.setProposition(1, largeur, 'A');
				break;
			case 11:
				mc.getDefinition(hauteur, 1, true);
				break;
			case 12:
				mc.setDefinition(1, largeur, false, "bla bla");
				break;
			case 13:
				mc.getSolution(1, 1);
				break;
			case 14:
				mc.setSolution(3, 3, 'B');
				break;
			case 15:
				mc.getProposition(4, 4);
				break;
			case 16:
				mc.setProposition(1, 1, 'C');
				break;
			case 17:
				mc.getDefinition(3, 3, true);
				break;
			case 18:
				mc.setDefinition(4, 4, false, "bla bla");
				break;
			default:
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			return "ArrayIndexOutOfBoundsException";
		} catch (RuntimeException e) {
			return "";
		}
		return probleme[meth - 1] + " / ";
	}

	private void testGrille(Object[][] grille, String libelle) {
		Object attendu;
		Object observe = null;
		for (int lig = 0; lig < mc.getHeight(); lig++)
			for (int col = 0; col < mc.getWidth(); col++) {
				if (!noire[lig][col]) {
					attendu = grille[lig][col];
					switch (libelle) {
					case "solution":
						observe = mc.getSolution(lig, col);
						break;
					case "proposition":
						observe = mc.getProposition(lig, col);
						break;
					case "définition horizontale":
						observe = mc.getDefinition(lig, col, true);
						break;
					case "définition verticale":
						observe = mc.getDefinition(lig, col, false);
						break;
					default:
					}
					assertEquals(attendu, observe,
							"La case (" + lig + "," + col + ") de " + libelle + " doit contenir \"" + attendu + "\".");
				}
			}
	}

}
