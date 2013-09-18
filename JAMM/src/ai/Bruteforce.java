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
 * A solving algorithm using the brutefore technic.
 * See <a href="http://en.wikipedia.org/wiki/Brute-force_search">
 * Brute-force search</a> on Wikipedia.
 */
public class Bruteforce implements SolvingAlgorithm {
    /**
     * The ControlInterface to work with.
     */
    private ControlInterface ci;
    /**
      * Holding the guess
      */
    Row guess;
    /**
     * Width of the Row
     */
    private int width;
    /**
     * Range of colors to choose from
     */
    private int colQuant;
    /**
     * Allowance of same colors
     */
    private boolean doubleColors;
    /**
     * Arrays of colors holding all colors (allCols)
     * and the colors used in the running game (allowedCols).
     */
    Color[] allowedCols;
    Color[] allCols;

    /**
     * Initialize the AI with settings from the Mastermind engine.
     */
    public Bruteforce(ControlInterface ci){
        width = ci.getSettingWidth();
        colQuant = ci.getSettingColQuant();
        doubleColors = ci.getSettingDoubleCol();
        guess = new Row(width);
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
     * 0 = Just a normal turn or game already ended.
     * @see ControlInterface#turn() 
     */
    public int makeGuess(){
        Row tmp = generateGuess();
        Debug.dbgPrint("Code generiert.");
        ci.writeToGameField(tmp.getColors());
        return ci.turn();
    }

    /**
     * Generate a guess by "incrementing" the previous guess.
     * If no previous guess available generate "lowest" possible guess.
     * (Every color is given a value what makes it possible to generate
     * a "lowest" guess and to "increment" a guess.)
     * @return The generated guess.
     */
    public Row generateGuess(){
        // First try?
        int col=0;
        if(ci.getActiveRowNumber() == 0){
            Debug.dbgPrint("First try.");
            //fill guess with the first Color(s)
            for(int i=0; i < width; i++){
                guess.setColorAtPos(i, allowedCols[col]);
                col = doubleColors?0:col++;
            }
            return guess;
        }
        Debug.dbgPrint("Not first try.");
        // Later try?
        boolean carry = true;
        Color[] tmp = guess.getColors();
        int index;
        for(int i=tmp.length-1; i>=0; i--){
            if(!carry){
                break;
            }
            index = findColorIndex(allowedCols,tmp[i]);
            if(++index < allowedCols.length){
                tmp[i] = allowedCols[index];
                carry = false;
            }
            else{
                tmp[i] = allowedCols[0];
                carry = true;
            }
        }
        if(!doubleColors && guess.containsDoubleColor()){
            return generateGuess();
        }
        return guess;
    }
    
    /**
     * Search for a Color in an array and return its index.
     * @param ca The array within we want to do a search.
     * @param c The Color to be found.
     * @return Index of the Color, "-1" if not found.
     */
    private int findColorIndex(Color[] ca, Color c){
        int i=0;
        while(i<ca.length){
            if(ca[i]==c){
                return i;
            }
            i++;
        }
        return -1;
    }
}
