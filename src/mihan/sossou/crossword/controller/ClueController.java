package mihan.sossou.crossword.controller;

import mihan.sossou.crossword.model.Clue;
import mihan.sossou.crossword.model.Crossword;

import java.util.Iterator;

public class ClueController {

    /**
     * Met à jour les Clues (vertical et horizontal) et le square après
     * clic sur un clue
     * @param clue
     * @param crossword
     */
    public static void updateCurrentCluesAndSquare(Clue clue, Crossword crossword) {

        int currentSquareRow = 0;
        int currentSquareColumn = 0;

        if (clue.isHorizontal()) {

            crossword.setHorizontalClueCurrent(clue);
            currentSquareRow = clue.getRow();
            currentSquareColumn = clue.getColumn();

            Iterator<Clue> iterator = crossword.getVerticalClues().iterator();

            while (iterator.hasNext()) {

                Clue verticalClue = iterator.next();

                if (verticalClue.getRow() == currentSquareRow &&
                        verticalClue.getColumn() == currentSquareColumn) {

                    crossword.setVerticalClueCurrent(verticalClue);
                    break;
                }
            }
        }else {
            crossword.setVerticalClueCurrent(clue);
            currentSquareRow = clue.getRow();
            currentSquareColumn = clue.getColumn();

            Iterator<Clue> iterator = crossword.getHorizontalClues().iterator();

            while (iterator.hasNext()) {

                Clue horizontalClue = iterator.next();

                if (horizontalClue.getRow() == currentSquareRow &&
                        horizontalClue.getColumn() == currentSquareColumn) {

                    crossword.setHorizontalClueCurrent(horizontalClue);
                    break;
                }
            }
        }

        crossword.setCurrentSquare(crossword.getCell(
                currentSquareRow,
                currentSquareColumn
        ));
    }
}
