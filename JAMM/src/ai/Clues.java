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
 * Check if a guess is valide or makes no sense in context
 * of previous guesses and results.
 */
public class Clues {

    /**
     * Checks if the guess is valide or makes no sense in context
     * of previous guesses and results.
     * 
     * A guess is good (feasible) if, compared to each row, it scores the same
     * result as the compared row did to the secret code.<br />
     * In other words:<br />
     * A code c is eligible or feasible if it results in the same values for 
     * Xk and Yk for all guesses k that have been played up till that stage,
     * if c was the secret code.<br />
     * X is the number of exact matches. Y is the number of guesses which are 
     * the right color but in the wrong position.
     * 
     * @param row The guess to be checked.
     * @param ci The control interface of a game which you want
     * to use to check if a guess is feasible.
     * @return True if guess is good. Otherwise false.
     */
    public static boolean isFeasible(ControlInterface ci, Row row) {
        for (int i = 0; i < ci.getActiveRowNumber(); i++) {
            int[] result = compare(row,
                    ci.getGameFieldRow(i));

            if (result[0] != ci.getResultRow(i)
                    .containsColor(Color.Black) ||
                    result[1] != ci.getResultRow(i)
                    .containsColor(Color.White)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Compare two Rows to another.
     * @param a The guess Row.
     * @param b The secret code Row.
     * @return [0] is the number of black pegs, [1] is the number of white pegs.
     */
    private static int[] compare(Row a, Row b) {
        int[] result = {0, 0};
        Color[] code = new Color[a.width()];
        Color[] secret = new Color[code.length];
        // Create real copy.
        System.arraycopy(a.getColors(), 0, code, 0, code.length);
        System.arraycopy(b.getColors(), 0, secret, 0, code.length);

        for (int i = 0; i < code.length; i++) {
            if (code[i] == secret[i]) {
                result[0]++;
                secret[i] = null;
                code[i] = null;
            }
        }
        
        outer:
        for (int i = 0; i < code.length; i++) {
            if (code[i] != null) {
                for (int j = 0; j < code.length; j++) {
                    if (code[i] == secret[j]) {
                        result[1]++;
                        secret[j] = null;
                        continue outer;
                    }
                }
            }
        }
        return result;
    }
}
