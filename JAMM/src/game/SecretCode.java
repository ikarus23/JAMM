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
 * This class represents the secret code the player or the AI has to guess.
 */
class SecretCode implements Serializable {
    // Object vars.
    private Row secretCode;

    /**
     * Initializes a new secret code with the given parameters.
     *
     * @param colorQuant Number of different colors.
     * @param width Width (number of pins) of the code.
     * @param doubleColors Allowance of same colors.
     */
    public SecretCode(int colorQuant, int width, boolean doubleColors) {
        generateCode(colorQuant, width, doubleColors);
    }

    /**
     * Creates a secret code.
     * 
     * @param colorQuant Number of different colors
     * @param width Width (number of pins) of the code.
     * @param doubleColors Allowance of same colors
     */
    private void generateCode(int colorQuant, int width, boolean doubleColors) {
        secretCode = new Row(width);
        //Prepare values to fit colorQuant-rule
        Color[] values = new Color[colorQuant];
        Color[] all = Color.values();
        System.arraycopy(all, 0, values, 0, colorQuant);

        //Do the actual code generation...
        int i = 0;
        while (i < width) {
            Color now = values[(int) (Math.random() * colorQuant)];
            if (secretCode.containsColor(now) > 0) {
                if (doubleColors == true) {
                    secretCode.setColorAtPos(i++, now);
                }
            } else {
                secretCode.setColorAtPos(i++, now);
            }
        }
        Debug.dbgPrint("Secret code is: " + secretCode);
    }

    /**
     * Getter for the secret code.
     *
     * @return The secret code.
     */
    public Row getCode() {
        return secretCode;
    }

    /**
     * Setter for the secret code.
     *
     * @param code The Row to set as secret code.
     * @return True if the code was set successfully. False if not.
     */
    public boolean setCode(Row code) {
        if (secretCode.width() == code.width()) {
            secretCode = code;
            return true;
        }
        return false;
    }
}
