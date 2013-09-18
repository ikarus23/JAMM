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

import java.io.Serializable;

/**
 * Contains all settings of a game.
 */
class Settings implements Serializable {
    // Object vars. representing the settings.
    // Init with standard values.
    /**
     * Maximum number of Tries.
     * Std is: 8.
     */
    private int maxTries = 8;
    
    /**
     * Width of the row.
     * Std is: 4.
     */
    private int width = 4;
    
    /**
     * Range of colors to choose from.
     * Std is: 6.
     */
    private int colQuant = 6;
    
    /**
     * Allowance of same colors.
     * Std is: True.
     */
    private boolean doubleColors = true;
    
    /**
     * Game mode.
     * True = AI: Codebreaker, Human: Codemaker.
     * False = AI: Codemaker, Human: Codebreaker.
     * Std is: False.
     */
    private boolean aiMode = false;
    
    /**
     * Getter for game mode.
     *
     * @return True = AI: Codebreaker, Human: Codemaker.<br />
     * False = AI: Codemaker, Human: Codebreaker.
     */
    public boolean getAiMode() {
        return aiMode;
    }

    /**
     * Setter for game mode.
     *
     * @param status True = AI: Codebreaker, Human: Codemaker.<br />
     * False = AI: Codemaker, Human: Codebreaker.
     */
    public void setAiMode(boolean status) {
        aiMode = status;
    }
    
    /**
     * Getter for max. tries (guesses).
     *
     * @return Max. number of tries.
     */
    public int getMaxTries() {
        return maxTries;
    }

    /**
     * Setter for max. tries (guesses).
     *
     * @param tries The max. number  of tries.
     */
    public void setMaxTries(int tries) {
        maxTries = tries;
    }

    /**
     * Getter for the game width.
     * 
     * @return The width of a Row.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Setter for the game width.
     * 
     * @param width The width for a Row.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Getter for the quantity of colors used in the game.
     * 
     * @return The number of colors used in the game.
     */
    public int getColQuant() {
        return colQuant;
    }

    /**
     * Setter for the quantity of colors used in the game.
     * 
     * @param colQuant The number of colors used in the game.
     */
    public void setColQuant(int colQuant) {
        this.colQuant = colQuant;
    }

    /**
     * Getter for double color allowance.
     * 
     * @return True if double colors are allowed. False otherwise.
     */
    public boolean getDoubleCol() {
        return doubleColors;
    }

    /**
     * Setter for double color allowance.
     * 
     * @param doubleColors True for the allowance of double colors.
     * False otherwise.
     */
    public void setDoubleCol(boolean doubleColors) {
        this.doubleColors = doubleColors;
    }
}
