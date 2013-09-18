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

package ai;

import common.*;
import game.ControlInterface;

/**
 * A "solving algorithm" that makes random guesses.
 */
public class RandomGuesses implements SolvingAlgorithm{
    /**
     * The ControlInterface to work with.
     */
    private ControlInterface ci;
    /**
     * Width of the row.
     */
    private int width;
    /**
     * Range of colors to choose from.
     */
    private int colQuant;
    /**
     * Allowance of same colors.
     */
    private boolean doubleColors;
    /**
     * The colors that are "allowed" in the game.
     */
    Color[] allowedCols;
    /**
     * All colors available in the game.
     */
    Color[] allCols;

    /**
     * Initialize the AI with settings from the Mastermind engine.
     * 
     * @param ci A control interface the AI will use to
     * interact with a game.
     */
    public RandomGuesses(ControlInterface ci){
        this.ci = ci;
        width = ci.getSettingWidth();
        colQuant = ci.getSettingColQuant();
        doubleColors = ci.getSettingDoubleCol();
        //Prepare values to fit colorQuant-rule
        allowedCols = new Color[colQuant];
        allCols = Color.values();
        System.arraycopy(allCols, 0, allowedCols, 0, colQuant);
    }

    /**
     * Do a full guess on the Mastermind engine.
     * This includes to generate a guess, pass it to the engine
     * and do a full game turn.
     * 
     * @return -1 = Game ended and code was not broken. <br />
     * 1 = Game ended an code was broken. <br />
     * 0 = Just a normal turn or the game already ended.
     * @see ControlInterface#turn()
     */
    public int makeGuess(){
        Row tmp = generateGuess();
        ci.writeToGameField(tmp.getColors());
        return ci.turn();
    }

    /**
     * Generating a totaly random guess ignoring previous results.
     * 
     * @return Generated guess.
     */
    public Row generateGuess(){
        Row guess = new Row(width);

        int i = 0;
        while (i < width) {
            Color now = allowedCols[(int) (Math.random() * colQuant)];
            if (guess.containsColor(now) > 0) {
                if (doubleColors == true) {
                    guess.setColorAtPos(i++, now);
                }
            } else {
                guess.setColorAtPos(i++, now);
            }
        }
        return guess;
    }
}
