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
 * Color Enumeration.
 * Representing the colors used in the game engine.
 * The color value is the RGB value representing the color in the default sRGB
 * ColorModel. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green,
 * 0-7 are blue). This values are equal to the java.awt.Color.getRGB().
 */
public enum Color {

    Red(-65536),
    Green(-16711936),
    Blue(-16776961),
    Yellow(-256),
    Orange(-32768),
    Purple(-8388480),
    Pink(-60269),
    Olive(-8355840),
    LightBlue(-8716289),
    LightGreen(-8716422),
    LightRed(-34182),
    LightOrange(-20614),
    LightPurple(-5276929),
    White(-1),
    Black(-16777216),
    Null(-2696737);

    private final int rgb;

    /**
     * Std. Constructor.
     *
     * @param rgb SRGB-code of the color.
     */
    Color(int rgb) {
        this.rgb = rgb;
    }

    /**
     * Returning the SRGB-code as an int.
     * Format: SRGB
     *
     * @return SRGB-code of the color.
     */
    public int getRGB() {
        return rgb;
    }
}