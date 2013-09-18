/*
 * Copyright 2011 Ikarus, René Kübler, Andreas J.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package game;

import common.*;
import java.io.Serializable;

/**
 * Game-engine class.
 * Controls the gameflow.
 */
class Game implements Serializable{
    // Object vars.
    // All protectet so that the control interface can use the
    // getters and setters.
    protected boolean gameEnded = false;
    protected GameField gameField;
    protected SecretCode secretCode;
    protected Settings settings;

    /**
     * Constructor with init instructions for new game.
     * This constructor initializes the Game class with a new gameField and
     * and a new, random secretCode.
     * 
     * @param s Settings for the new game.
     */
    public Game(Settings s) {
        settings = s;
        gameField = new GameField(settings.getWidth(), settings.getMaxTries());
        secretCode = new SecretCode(settings.getColQuant(), settings.getWidth(),
                settings.getDoubleCol());
    }
    
    /**
     * Aggregates the logic for a game turn.
     * 
     * @return 1 for win; -1 for lose; 0 for normal turn
     */
    public int turn() {
        Row result = checkActiveRow();
        gameField.incActiveRowNumber();
        if (result.containsColor(Color.Black) == result.width()) {
            Debug.dbgPrint("Code was broken");
            gameEnded = true;
            return 1;
        }
        if (gameField.getActiveRowNumber() >= settings.getMaxTries()) {
            Debug.dbgPrint("Code was not broken!");
            gameEnded = true;
            return -1;
        }
        return 0;
    }
    
    /**
     * Checks the active game Row.
     * 
     * @return The result Row.
     * (Black = correct, White = exists).
     */
    private Row checkActiveRow() {
        // Create real copies.
        Color [] row = new Color[settings.getWidth()];
        System.arraycopy(gameField.getRow().getColors(), 0, row, 0,
                settings.getWidth());
        Color [] secretRow = new Color[settings.getWidth()];
        System.arraycopy(secretCode.getCode().getColors(), 0, secretRow, 0,
                settings.getWidth());
        
        Row result = new Row(row.length);
        int index = 0;
        // Check "correct"-colors first
        // for a result like "black, black, white, null".
        for (int i=0; i < row.length; i++) {
            if (row[i] == secretRow[i]) {
                // Right color at right position (correct).
                result.setColorAtPos(index++, Color.Black);
                // Mark color as checked.
                secretRow[i] = null;
                row[i] = null;
            }
        }
        // Now check "exists"-colors
        outer: for (int i=0; i < row.length; i++) {
            if (row[i] != null) {
                for (int j=0; j < row.length; j++) {
                    if (secretRow[j] != null && row[i] == secretRow[j]) {
                        // Right color but wrong position (exists).
                        result.setColorAtPos(index++, Color.White);
                        // Mark color as checked.
                        secretRow[j] = null;
                        continue outer;
                    }
                }
            }
        }
        Debug.dbgPrint("checkActiveRow: " + result);
        gameField.setResult(result);
        return result;
    }
}
