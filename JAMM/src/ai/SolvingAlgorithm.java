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
 * This interface describes how a solving algorithm has to be implemented.
 * It has to be able to generate a valid guess. Normaly based on
 * previous guesses.
 * And it has to be able to actually make a guess interacting with the
 * ControlInterface.
 * 
 * @see ControlInterface
 */
public interface SolvingAlgorithm {
    /**
     * This function creates a guess and does a full turn in the game.
     * 
     * @return The state of the turn() function.
     */
    public int makeGuess();
    
    /**
     * Generate a guess and return it as a Row.
     * 
     * @return The row that was guessed.
     */
    public Row generateGuess();
}
