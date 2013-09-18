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

import common.Row;
import java.io.Serializable;

/**
 * The actual game field.
 * This includes the guess rows and the result rows.
 * The active row number is stored here also.
 */
class GameField implements Serializable {
    // Object vars.
    private int activeRowNumber = 0;
    private Row[] gameField;
    private Row[] resultField;

    /**
     * Constructor which inits the game field with width and max. tries.
     *
     * @param width Number of colors per Row.
     * @param maxTries Maximum number of tries.
     */
    public GameField(int width, int maxTries) {
        gameField = new Row[maxTries];
        for (int i = 0; i < gameField.length; i++) {
            gameField[i] = new Row(width);
        }
        resultField = new Row[maxTries];
    }

    /**
     * Setter - saves the result in the active Row of the result field.
     *
     * @param result Result to set.
     */
    public void setResult(Row result) {
        resultField[activeRowNumber] = result;
    }
    
    /**
     * Getter for the active Row number.
     * 
     * @return The active Row number.
     */
    public int getActiveRowNumber() {
        return activeRowNumber;
    }

    /**
     * Getter for active (last) result Row.
     * 
     * @return The last result.
     * If there is no last result, null will be returned.
     */
    public Row getResult() {
        if (activeRowNumber > 0) {
            return resultField[activeRowNumber - 1];
        }
        return null;
    }

    /**
     * Getter for a result Row.
     *
     * @param rowNumber The number of the Row.
     * @return The specified Row.
     */
    public Row getResult(int rowNumber) {
        return resultField[rowNumber];
    }

    /**
     * Increments the active row number by one.
     */
    public void incActiveRowNumber() {
        activeRowNumber++;
    }

    /**
     * Getter for the active Row.
     *
     * @return The active Row.
     */
    public Row getRow() {
        return gameField[activeRowNumber];
    }

    /**
     * Getter for a game Row.
     *
     * @param rowNumber The number of the game Row.
     * @return The Row with the given number from the game field.
     */
    public Row getRow(int rowNumber) {
        return gameField[rowNumber];
    }

    /**
     * Setter for a game Row.
     *
     * @param rowNumber The number of the Row in the game field.
     * @param row The Row to set.
     */
    public void setRow(int rowNumber, Row row) {
        gameField[rowNumber] = row;
    }
    
     /**
     * Setter for the active Row.
     * The startup value for active Row number is 0.
     *
     * @param row The Row to set as active row.
     */
    public void setRow(Row row) {
        gameField[activeRowNumber] = row;
    }

    /**
     * Getter for the whole game field.
     *
     * @return The whole game field.
     */
    public Row[] getField() {
        return gameField;
    }

    /**
     * Setter for the whole game field.
     *
     * @param field The game field to set.
     */
    public void setField(Row[] field) {
        gameField = field;
    }
}
