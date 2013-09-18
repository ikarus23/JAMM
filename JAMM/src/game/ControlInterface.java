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
import common.Color;
import common.Debug;
import java.io.*;

/**
 * The main interface to control the gameflow.
 * This class provides all functions for a game.
 * This is the only public class in the game package.
 * This design is used to provide a single interface for a frontend
 * (or an AI) which guarantees a correct and save execution.
 */
public class ControlInterface {
    // Class vars.    
    // Allways create a game first.
    private Game game = new Game(new Settings());
    
    /**
     * The file extension for saving.
     */
    public final String FILE_EXTENSION = "mm";
    
    /**
     * Identifier for loaded games.
     */
    private boolean loaded = false;
    
    /**
     * Getter for the active Row number.
     * 
     * @return The active Row number.
     */
    public int getActiveRowNumber() {
        return game.gameField.getActiveRowNumber();
    }
    
    /**
     * Do a full game turn.
     * This includes to check the guess, increment the active Row number,
     * set the result pegs and return the game state.
     * 
     * @return -1 = Game ended and code was not broken. <br />
     * 1 = Game ended an code was broken. <br />
     * 0 = Just a normal turn or game already ended.
     */
    public int turn() {
        if (getGameEnded() == false) {
            return game.turn();
        }
        return 0;
    }
    
    /**
     * Set a Row of colors to the active game Row.
     * 
     * @param colors The colors to set as active game Row.
     */
    public void writeToGameField(Color[] colors) {
        game.gameField.setRow(new Row(colors));
    }
    
    /**
     * Get the newest results.
     * 
     * @return The last game result.
     * If there is no result null will be returned.
     */
    public Row getLastResultRow() {
        if (game.gameField.getActiveRowNumber() > 0) {
            return game.gameField.getResult();
        }
        return null;
    }
    
    /**
     * Get a specific result Row.
     * 
     * @param row The Row number.
     * @return The specified result Row. If the row does not exist, null will be
     * returned.
     */
    public Row getResultRow(int row) {
        if (row >= 0 && row < game.settings.getMaxTries()) {
            return game.gameField.getResult(row);
        }
        return null;
    }
    
    /**
     * Get a specific game Row.
     * 
     * @param row The Row number.
     * @return The specified game Row. If the row does not exist, null will be
     * returned.
     */
    public Row getGameFieldRow(int row) {
        if (row >= 0 && row < game.settings.getMaxTries()) {
            return game.gameField.getRow(row);
        }
        return null;
    }
    
    /**
     * Get the secret code.
     * 
     * @return The secret code.
     */
    public Row getSecretCode() {
        return game.secretCode.getCode();
    }
    
     /**
     * Set the secret code.
     * 
     * @param colors The secret code to set.
     */
    public void setSecretCode(Color[] colors) {
        game.secretCode.setCode(new Row(colors));
        common.Debug.dbgPrint("Secret code is set to: " + 
                game.secretCode.getCode());
    }
    
    /**
     * Getter for the color quantity.
     * The quantity of different colors to choose from.
     * 
     * @return The quantity of colors in the game.
     */
    public int getSettingColQuant() {
        return game.settings.getColQuant();
    }
    
    /**
     * Setter for the color quantity.
     * If there are too less colors to fill a row
     * with different colors (if double colors are not allowed)
     * nothing will be set.
     * 
     * @param quant Quantity of colors in the game. (Between 1 and 15).
     */
    public void setSettingColQuant(int quant) {
        if(quant > 0 && quant <=15 && (game.settings.getWidth() <= quant ||
                game.settings.getDoubleCol())) {
            game.settings.setColQuant(quant);    
        }
    }
    
    /**
     * Getter for the game width.
     * This equals the length of a guess/result Row.
     * 
     * @return The width/length of an guess/result Row.
     */
    public int getSettingWidth() {
        return game.settings.getWidth();
    }
    
    /**
     * Setter for the game width.
     * This equals the length of a guess/result Row.
     * If the width is too big to fill a Row
     * with different colors (if double colors are not allowed)
     * nothing will be set.
     * 
     * @param width The width of a guess Row. (Between 1 and 8).
     */
    public void setSettingWidth(int width) {
        if(width > 0 && width <=8 && (game.settings.getColQuant() >= width ||
                game.settings.getDoubleCol())) {
           game.settings.setWidth(width); 
        }
    }
    
    /**
     * Setter for max. number of tries (guesses).
     * 
     * @param max Max. number of tries / guesses.
     * (Between 1 and Integer.MAX_VALUE).
     */
    public void setSettingMaxTries(int max) {
        if (max > 0 && max <= Integer.MAX_VALUE) {
            game.settings.setMaxTries(max);
        }
    }
    
     /**
     * Getter for max. number of tries (guesses).
     * 
     * @return The max. number of tries.
     */
    public int getSettingMaxTries() {
        return game.settings.getMaxTries();
    }
    
   /**
     * Setter for double colors allowance.
     * Allowance to place same colors in one Row.
     * 
     * @param allow Allow double colors.
     */
    public void setSettingDoubleCol(boolean allow) {
        game.settings.setDoubleCol(allow);
    }

   /**
     * Getter for double colors allowance.
     *
     * @return allow True if double colors are allowed. Otherwise false.
     */
    public boolean getSettingDoubleCol() {
        return game.settings.getDoubleCol();
    }
    
    /**
     * Getter for AI mode.
     * 
     * @return True for AI = Codebreaker, human = Codemaker.<br />
     * False for AI = Codemaker, human = Codebreaker.
     */
    public boolean getSettingAiMode() {
        return game.settings.getAiMode();
    }
    
    /**
     * Setter for AI mode.
     * 
     * @param status True for AI = Codebreaker, human = Codemaker.<br />
     * False for AI = Codemaker, human = Codebreaker.
     */
    public void setSettingAiMode(boolean status) {
        game.settings.setAiMode(status);
    }
    
    /**
     * Start a new game.
     */
    public void newGame() {
        Debug.dbgPrint("New game started");
        game = new Game(game.settings);    
    }
    
    
    /**
     * Getter for loaded game state.
     * 
     * @return True if the game is a loaded game. Otherwise false.
     */
    public boolean getLoaded() {
        return loaded;
    }
    
    /**
     * Getter for game ended state.
     * 
     * @return True if the game has ended. Otherwise false.
     */
    public boolean getGameEnded(){
        return game.gameEnded;
    }
    
    
    /**
     * Save the game with settings as "savegame" file.
     *
     * @throws FileNotFoundException
     * @throws SecurityException
     * @throws IOException
     */
    public void save()
            throws FileNotFoundException, SecurityException, IOException {
        save("savegame." + FILE_EXTENSION);
    }
    
    /**
     * Save the game with settings to a specific path/filename.
     * 
     * @param fileName Path and filename.
     * @throws FileNotFoundException
     * @throws SecurityException
     * @throws IOException 
     */
    public void save(String fileName)
            throws FileNotFoundException, SecurityException, IOException{
        // Create output streams.
        FileOutputStream fos = new FileOutputStream(fileName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        // Write object.
        oos.writeObject(game);
        oos.close();
    }

    /**
     * Load a game from "savegame" file.
     *
     * @throws FileNotFoundException
     * @throws SecurityException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void load() throws FileNotFoundException, SecurityException,
            IOException, ClassNotFoundException {
        load("savegame." + FILE_EXTENSION);
    }
    
    /**
     * Load a game from a specific path/filename.
     * 
     * @param fileName Path and filename.
     * @throws FileNotFoundException
     * @throws SecurityException
     * @throws IOException
     * @throws ClassNotFoundException 
     */
        public void load(String fileName)throws FileNotFoundException,
                SecurityException, IOException, ClassNotFoundException{
         // Create input streams.
        FileInputStream fis = new FileInputStream(fileName);
        ObjectInputStream ois = new ObjectInputStream(fis);

        // Read object.
        Game mygame = (Game) ois.readObject();
        ois.close();
        game = mygame;
        loaded = true;
    }
}
