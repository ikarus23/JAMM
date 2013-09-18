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

import java.io.Serializable;

/**
 * This class represents a single Row with colors.
 */
public class Row implements Serializable{
    // Object vars.
    private Color[] colors;

    /**
     * Constructor for a Row with colors.
     *
     * @param colors An array of colors to initialize the Row.
     */
    public Row(Color[] colors) {
        this.colors = colors;
    }

    /**
     * Constructor for a Row with a given width.
     *
     * @param width The width of the Row.
     */
    public Row(int width) {
        colors = new Color[width];
        for (int i=0; i<width; i++) {
            colors[i] = Color.Null;
        }
    }

    /**
     * Getter for a Row.
     *
     * @return An array of colors.
     */
    public Color[] getColors() {
        return colors;
    }

    /**
     * Setter for a Row.
     *
     * @param colors An array of colors.
     */
    public void setColors(Color[] colors) {
        this.colors = colors;
    }

    /**
     * Get a color at a specific position in the Row.
     *
     * @param pos The position from where to get the color.
     * @return The color from the position. If the position was
     * out of the Row bounds "null" will be retruned.
     */
    public Color getColorAtPos(int pos) {        
        if (pos < colors.length && pos >= 0) {
            return colors[pos];
        }
        return null;
    }

    /**
     * Set a color at a specific position in the Row.
     *
     * @param pos The position where to set the color.
     * @param color The color to set on the position.
     * @return True if the position was in the Row bounds and everything went
     * well. False otherwise.
     */
    public boolean setColorAtPos(int pos, Color color) {
        if (pos < this.colors.length && pos >= 0) {
            this.colors[pos] = color;
            return true;
        }
        return false;
    }

    /**
     * Getter for the Row width.
     *
     * @return The width of a Row.
     */
    public int width() {
        return colors.length;
    }

    /**
     * Checks how often a Row contains a specific color.
     *
     * @param color The color to look for.
     * @return The quantity of matches.
     */
    public int containsColor(Color color) {
        int ret = 0;
        for (int i=0; i < this.colors.length; i++) {
            if (this.colors[i] == color) {
                ret++;
            }
        }
        return ret;
    }

    /**
     * Checks if the same Color is inside the Row more than once.
     * 
     * @return True if a Color is found more than once, false if not.
     */
    public boolean containsDoubleColor(){
        for(int i=0; i<colors.length; i++){
            for(int j=i+1; j<colors.length; j++){
                if(colors[i] == colors[j]){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Line-up all colors seperated with ",".
     *
     * @return A String with all colors lined-up, seperated by ",".
     */
    @Override
    public String toString() {
        if (colors != null) {
            String ret = "";
            for (int i=0; i<colors.length-1; i++) {
                ret += colors[i] + ", ";
            }
            return ret + colors[colors.length-1];
        }
        return "no colors";
    }
    
    /**
     * Check if the specified object equals this Row.
     * A Row is equal if it contains the same colors at the same positions.
     * @param o The object to compare with.
     * @return True if given object is equal. False otherwise.
     */
    @Override
    public boolean equals(Object o) {
        Row row;
        if (o instanceof Row) {
            row = (Row)o;
        } else {
            return false;
        }
        for (int i = 0; i < row.width(); i++) {
            if (row.getColorAtPos(i) != this.getColorAtPos(i)) {
                return false;
            }
        }
        return true;
    }
}
