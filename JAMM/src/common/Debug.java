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

package common;

/**
 * Used for debugging, i.e. with a function for debug-output.
 */
public class Debug {

    /**
     * This value enables (true) or disables (false) debugging.
     * Std is: False.
     */
    private static boolean debug = false;
    
    /**
     * This value enables (true) or disables (false) error messages.
     * Std is: True.
     */
    private static boolean error = true;

    /**
     * Print the given String if debugging is enabled.
     *
     * @param s The String to be printed.
     */
    public static void dbgPrint(String s) {
        if (debug) {
            System.out.println("Debug: " + s);
        }
    }
    
    /**
     * Print the given String if error is enabled.
     * 
     * @param s The String to be printed.
     */
    public static void errorPrint(String s) {
        if (error) {
            System.out.println("Error: " + s);
        }
    }

    /**
     * Set the debugging on or off.
     *
     * @param value True to switch debugging on,
     * false to switch debugging off.
     */
    public static void setDebug(boolean value) {
        debug = value;
    }
}
