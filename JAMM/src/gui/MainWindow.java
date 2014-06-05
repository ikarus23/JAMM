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
 * GNU General Public License for more deta
ils.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package gui;

// Our packages.
import common.*;
import game.ControlInterface;
import ai.*;

// Java packages.
import java.io.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.AWTEvent;
import java.awt.Toolkit;


/**
 * Provides a GUI for the Mastermind game engine.
 *
 * The class contains all GUI elements and functions that a user
 * needs to interact with the Mastermind game engine (package game).
 * The Mastermind engine is self-contained, so that any UI
 * (GUI, console, etc. ) only has to use the ControlInterface of the
 * game package. This design makes the GUI and engine nearly independent
 * form each other. Logical changes in the Mastermind engine don't
 * affect the GUI. Of course graphical changes in the GUI don't affect the
 * Mastermind engine neither.
 *
 * @see game.ControlInterface
 */
public class MainWindow extends javax.swing.JFrame {
    // Object vars. used by various functions.
    /**
     * The delay between two guesses (if the makeGuess()-Function of the
     * AI was fast enouth).
     */
    private final int AI_GUESS_DELAY = 500;
    private static ControlInterface ci = new ControlInterface();
    private JDialog aboutDialog;
    private JButton chosenColorButton;
    private JSlider colorsSlider;
    private JCheckBox doubleColorsCheckBox;
    private JFileChooser fileChooser;
    private JMenuBar gameMenuBar;
    private JSlider gameWidthSlider;
    private JSpinner maxTriesSpinner;
    private JDialog settingsDialog;
    private JComboBox<String> gameModeComboBox;
    private JButton colorButtons[];
    private JScrollPane colorScrollPane;
    private JButton secretCodeButtons[];
    private JPanel secretCodePanel;
    private JButton gameButtons[][];
    private JPanel gamePanel;
    private JScrollPane gameScrollPane;
    private JLabel gamePlaceholder;
    private JLabel gameState;
    private Timer aiTimer;

    /**
     * Creates new form MainWindow and initializes all components.
     */
    public MainWindow() {

        initKeyListener();
        initComponents();
        initNewGame();

    }


    // <editor-fold defaultstate="collapsed" desc="initComponents">

    /**
     * Init all GUI components (Constructor subfunction,
     * called only once).
     *
     */
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        aboutDialog = new JDialog();
        fileChooser = new JFileChooser();
        settingsDialog = new JDialog();
        colorsSlider = new JSlider();
        gameWidthSlider = new JSlider();
        maxTriesSpinner = new JSpinner();
        doubleColorsCheckBox = new JCheckBox();
        gameScrollPane = new JScrollPane();
        secretCodePanel = new JPanel();
        chosenColorButton = new JButton();
        colorScrollPane = new JScrollPane();
        gameMenuBar = new JMenuBar();
        gamePanel = new JPanel();
        gamePlaceholder = new JLabel();
        gameState = new JLabel();
        gameModeComboBox = new JComboBox<String>();

        // ### About Dialog. ###################################################
        aboutDialog.setTitle("About");
        aboutDialog.setResizable(false);
        JButton closeButton = new JButton("Close");
        JLabel aboutLabel = new JLabel(
                "<html><b>JAMM</b><br /><b>J</b>ust <b>A</b>nother "
                + "<b>M</b>aster<b>M</b>ind<br />"
                + "Version: 0.4 (06.2014)<br />License: GPL v3"
                + "<br /><br /><b>Developer Team:</b><br />Rene Kuebler<br />"
                + "Andreas J.<br />Ikarus<br />");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        // About Dialog Layout (Taken from designer).
        javax.swing.GroupLayout aboutDialogLayout = new javax.swing.GroupLayout(
                aboutDialog.getContentPane());
        aboutDialog.getContentPane().setLayout(aboutDialogLayout);
        aboutDialogLayout.setHorizontalGroup(
            aboutDialogLayout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aboutDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(aboutLabel,
                javax.swing.GroupLayout.PREFERRED_SIZE,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(85, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                aboutDialogLayout.createSequentialGroup()
                .addContainerGap(178, Short.MAX_VALUE)
                .addComponent(closeButton)
                .addContainerGap())
        );
        aboutDialogLayout.setVerticalGroup(
            aboutDialogLayout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aboutDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(aboutLabel,
                javax.swing.GroupLayout.PREFERRED_SIZE,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(
                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(closeButton)
                .addContainerGap())
        );

        // ### File Chooser. ###################################################
        fileChooser.setCurrentDirectory(
                new File(System.getProperty("user.dir")));
        // Init fileChooser filter.
        fileChooser.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                "Mastermind-Savegames (*." + ci.FILE_EXTENSION +
                ")", ci.FILE_EXTENSION));
        fileChooser.setAcceptAllFileFilterUsed(false);

        // ### Settings Dialog. ################################################
        settingsDialog.setTitle("Settings");
        settingsDialog.setResizable(false);

        JLabel colorsLabel = new JLabel("Colors:");

        colorsSlider.setMaximum(Color.values().length -1);
        colorsSlider.setMinimum(1);
        colorsSlider.setMinorTickSpacing(1);
        colorsSlider.setMajorTickSpacing(2);
        colorsSlider.setPaintLabels(true);
        colorsSlider.setPaintTicks(true);
        colorsSlider.setSnapToTicks(true);

        JLabel gameWidthLabel = new JLabel("Game Width:");

        gameWidthSlider.setMaximum(8);
        gameWidthSlider.setMinimum(1);
        gameWidthSlider.setMajorTickSpacing(1);
        gameWidthSlider.setPaintLabels(true);
        gameWidthSlider.setPaintTicks(true);
        gameWidthSlider.setSnapToTicks(true);

        JLabel maxTriesLabel = new JLabel("Max. Tries:");
        maxTriesSpinner.setModel(new javax.swing.SpinnerNumberModel(
                ci.getSettingMaxTries(), Integer.valueOf(1),
                null, Integer.valueOf(1)));
        maxTriesSpinner.setPreferredSize(new java.awt.Dimension(60, 28));

        doubleColorsCheckBox.setText("Use Double Colors");

        JLabel gameModeLabel = new JLabel(
                "<html>Game Mode:<br />(<b>H</b>uman - " +
                "<b>A</b>rtificial <b>I</b>ntelligence)");

        DefaultComboBoxModel<String> dcbm = new DefaultComboBoxModel<String>();
        dcbm.addElement("H: Codebreaker - AI: Codemaker");
        dcbm.addElement("H: Codemaker - AI: Codebreaker");
        gameModeComboBox.setModel(dcbm);

        JButton saveButton = new JButton("Apply (New Game)");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        // Settings Dialog Layout (Taken from designer).
        javax.swing.GroupLayout settingsDialogLayout =
                new javax.swing.GroupLayout(
                    settingsDialog.getContentPane());
        settingsDialog.getContentPane().setLayout(settingsDialogLayout);
        settingsDialogLayout.setHorizontalGroup(
            settingsDialogLayout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(settingsDialogLayout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(colorsLabel)
                    .addComponent(colorsSlider,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(gameWidthLabel)
                    .addComponent(cancelButton)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                        settingsDialogLayout.createSequentialGroup()
                        .addComponent(saveButton))
                    .addComponent(maxTriesLabel)
                    .addComponent(maxTriesSpinner,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(gameWidthSlider,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(doubleColorsCheckBox)
                    .addComponent(gameModeLabel)
                    .addComponent(gameModeComboBox,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    )
                .addContainerGap())
        );
        settingsDialogLayout.setVerticalGroup(
            settingsDialogLayout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(colorsLabel)
                .addPreferredGap(
                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(colorsSlider,
                javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(
                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gameWidthLabel)
                .addPreferredGap(
                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gameWidthSlider,
                javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(
                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(maxTriesLabel)
                .addPreferredGap(
                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(maxTriesSpinner,
                javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(
                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(doubleColorsCheckBox)
                .addPreferredGap(
                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gameModeLabel)
                .addPreferredGap(
                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gameModeComboBox)
                .addGap(30)
                .addPreferredGap(
                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                    javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                 .addGroup(settingsDialogLayout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        // ### Main Window. ####################################################
        setTitle("JAMM - Just Another MasterMind");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        JLabel guessLabel = new JLabel("Your Guess");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        getContentPane().add(guessLabel, gridBagConstraints);

        JLabel resultLabel = new JLabel("Result");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(0, 10, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(resultLabel, gridBagConstraints);

        gameScrollPane.setPreferredSize(new Dimension(400, 400));
        gamePanel.addComponentListener(
                new ComponentListener() {
                    public void componentResized(ComponentEvent e) {
                        // Scroll down if gamePanel was resized.
                        gameScrollPane.getVerticalScrollBar().setValue(
                            gameScrollPane.getVerticalScrollBar().getMaximum());
                    }

                    public void componentMoved(ComponentEvent e) {}
                    public void componentShown(ComponentEvent e) {}
                    public void componentHidden(ComponentEvent e) {}
                });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(gameScrollPane, gridBagConstraints);

        // Secret Code.
        JLabel secretCodeLabel = new JLabel("Secret Code");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        getContentPane().add(secretCodeLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(0, 10, 0, 0);
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(secretCodePanel, gridBagConstraints);

        // Choose Color
        JLabel colorLabel = new JLabel("Chosen Color");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints.insets = new Insets(0, 10, 0, 10);
        getContentPane().add(colorLabel, gridBagConstraints);

        chosenColorButton.setHorizontalAlignment(
                javax.swing.SwingConstants.CENTER);
        chosenColorButton.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(0, 10, 0, 10);
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(chosenColorButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weighty = 0.9;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(colorScrollPane, gridBagConstraints);

        JButton showHintButton = new JButton("Show Hint");
        // Remove binding to space key.
        showHintButton.getInputMap().put(
                KeyStroke.getKeyStroke("SPACE"), "none" );
        showHintButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Use the equivalent menu action.
                showHintMenuItemActionPerformed(evt);
            }
        });
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weighty = 0.05;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(showHintButton, gridBagConstraints);

        JButton validateGuessButton = new JButton("Validate Guess");
        // Remove binding to space key.
        validateGuessButton.getInputMap().put(
                KeyStroke.getKeyStroke("SPACE"), "none" );
        validateGuessButton.addActionListener(
                new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Use the equivalent menu action.
                validateGuessMenuItemActionPerformed(evt);
            }
        });
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weighty = 0.05;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(validateGuessButton, gridBagConstraints);
        // More code now in initColorTable.

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(gameState, gridBagConstraints);


        // ### Main Window Menus (Taken from designer).
        JMenu gameMenu = new JMenu("Game");

        JMenuItem newGameMenuItem = new JMenuItem("New Game");
        newGameMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_N,
                java.awt.event.InputEvent.CTRL_MASK));
        newGameMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newGameMenuItemActionPerformed(evt);
            }
        });
        gameMenu.add(newGameMenuItem);
        gameMenu.add(new JPopupMenu.Separator());

        JMenuItem showHintMenuItem = new JMenuItem("Show Hint");
        showHintMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_H,
                java.awt.event.InputEvent.CTRL_MASK));
        showHintMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showHintMenuItemActionPerformed(evt);
            }
        });
        gameMenu.add(showHintMenuItem);

        JMenuItem validateGuessMenuItem = new JMenuItem("Validate Guess");
        validateGuessMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_V,
                java.awt.event.InputEvent.CTRL_MASK));
        validateGuessMenuItem.addActionListener(
                new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validateGuessMenuItemActionPerformed(evt);
            }
        });
        gameMenu.add(validateGuessMenuItem);

        JMenuItem setLastGuessMenuItem = new JMenuItem("Set Last Guess");
        setLastGuessMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_L,
                java.awt.event.InputEvent.CTRL_MASK));
        setLastGuessMenuItem.addActionListener(
                new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setLastGuessMenuItemMenuItemActionPerformed(evt);
            }
        });
        gameMenu.add(setLastGuessMenuItem);

        gameMenu.add(new JPopupMenu.Separator());

        JMenuItem loadMenuItem = new JMenuItem("Load...");
        loadMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_O,
                java.awt.event.InputEvent.CTRL_MASK));
        loadMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMenuItemActionPerformed(evt);
            }
        });
        gameMenu.add(loadMenuItem);

        JMenuItem saveAsMenuItem = new JMenuItem("Save...");
        saveAsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_S,
                java.awt.event.InputEvent.CTRL_MASK));
        saveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsMenuItemActionPerformed(evt);
            }
        });
        gameMenu.add(saveAsMenuItem);
        gameMenu.add(new JPopupMenu.Separator());

        JMenuItem quitMenuItem = new JMenuItem("Quit Game");
        quitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_Q,
                java.awt.event.InputEvent.CTRL_MASK));
        quitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitMenuItemActionPerformed(evt);
            }
        });
        gameMenu.add(quitMenuItem);

        gameMenuBar.add(gameMenu);

        JMenu settingsMenu = new JMenu("Settings");

        JMenuItem editSettingsMenuItem = new JMenuItem("Edit Settings...");
        editSettingsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_E,
                java.awt.event.InputEvent.CTRL_MASK));
        editSettingsMenuItem.addActionListener(
                new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editSettingsMenuItemActionPerformed(evt);
            }
        });
        settingsMenu.add(editSettingsMenuItem);

        gameMenuBar.add(settingsMenu);

        JMenu helpMenu = new JMenu("Help");

        JMenuItem howToMenuItem = new JMenuItem("How To Play...");
        howToMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                howToMenuItemActionPerformed(evt);
            }
        });

        helpMenu.add(howToMenuItem);

        JMenuItem aboutMenuItem = new JMenuItem("About JAMM...");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });

        helpMenu.add(aboutMenuItem);
        gameMenuBar.add(helpMenu);

        setJMenuBar(gameMenuBar);

        setMinimumSize(new Dimension(300, 250));
        pack();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Init functions">

    /**
     * Initialize all GUI components for a new game.
     */
    private void initNewGame() {
        this.requestFocus();

        // Stop AI guess timer.
        if (aiTimer != null) {
            aiTimer.stop();
        }

        // Set state.
        if (ci.getGameEnded() == false &&
                ci.getSettingAiMode() == false) {
            gameState.setText("Choose a color (Click or key a, b, c,..) " +
                    "and place it (Click or key 1, 2, 3,...).");
        }
        else if (ci.getGameEnded() == true){
            gameState.setText("Game is finished.");
        } else {
            gameState.setText("Set the secret code and watch the AI.");
        }

        // Init components that may be affected by changed settings.
        initGameTable();
        initSecretCode();
        initColorTable();
    }

    /**
     * Initialize the color table with the available colors.
     * The available colors depends on the color quantity setting.
     */
    private void initColorTable() {
        Color[] all = Color.values();
        colorButtons = new JButton[ci.getSettingColQuant()];
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));

        // Initialize the now chosen color button.
        chosenColorButton.setBackground(new java.awt.Color(255, 0, 0));

        for (int i = 0; i < ci.getSettingColQuant(); i++) {
            // Initialize buttons with char.
            byte[] b = new byte[1];
            b[0] = (byte)(65 + i);
            colorButtons[i] = new JButton(new String(b));
            // Remove binding to space key.
            colorButtons[i].getInputMap().put(
                    KeyStroke.getKeyStroke("SPACE"), "none" );
            // Add listener.
            colorButtons[i].addActionListener(
                    new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    colorButtonsActionPerformed(evt);
                }
            });
            // Set color.
            java.awt.Color color = new java.awt.Color(all[i].getRGB());
            colorButtons[i].setBackground(color);
            // Set size.
            colorButtons[i].setMinimumSize(new Dimension(100, 28));
            colorButtons[i].setMaximumSize(new Dimension(100, 28));
            colorButtons[i].setPreferredSize(new Dimension(100, 28));
            // Add to panel.
            colorButtons[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            buttons.add(colorButtons[i]);

        }
        colorScrollPane.setViewportView(buttons);
    }


    /**
     * Initialize the game table (game field).
     *
     * Creates a panel in table layout with result and guess pins.
     * If a game was loaded the table will be filled with the
     * loaded game. Otherwise a new game with one empty Row will
     * be initialized.
     *
     * @see #showLoadedGameTable()
     */
    private void initGameTable() {
        int width = ci.getSettingWidth();
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gamePanel.removeAll();
        gameButtons = new JButton[0][0];
        gamePanel.setLayout(layout);

        // Add key usage labels.
        gbc.gridy = 0;
        gbc.weightx = 0.9;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        for (int i = 0; i < width; i++) {
            JLabel label = new JLabel("" + (i + 1), SwingConstants.CENTER);
            gbc.gridx = width + i + 1;
            gamePanel.add(label, gbc);

        }

        // Add empty row.
        addRow();
        if (ci.getLoaded() == true) {
            // Show the loaded game.
            showLoadedGameTable();
        }
        gameScrollPane.setViewportView(gamePanel);
        gameScrollPane.revalidate();
    }

    /**
     * Draw the game table by using loaded game data.
     */
    private void showLoadedGameTable() {
        int i = 0;
        int arn = ci.getActiveRowNumber();
        while (i < arn) {
            // Add result.
            showResultRow(i);
            // Add guess.
            showGameRow(i);
            if (ci.getGameEnded() == false || i < arn - 1) {
                // Add new row at the end if game not ended.
                addRow();
            }
            i++;
        }
    }

    /**
     * Initialize the secret code row.
     * This function only initializes the buttons and reveals the code if the
     * game already ended.
     * A secret code is already generated by the ControlInterface.
     *
     * @see game.ControlInterface
     * @see game.SecretCode
     */
    private void initSecretCode() {
        int width = ci.getSettingWidth();
        secretCodePanel.removeAll();
        secretCodeButtons = new JButton[width];
        secretCodePanel.setLayout(new GridLayout(1, width));
        for (int i = 0; i < width; i++) {
            // Init.
            secretCodeButtons[i] = new JButton();
            // Remove binding to space key.
            secretCodeButtons[i].getInputMap().put(
                    KeyStroke.getKeyStroke("SPACE"), "none" );
            // Add listener.
            secretCodeButtons[i].addActionListener(
                    new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    secretCodeButtonsActionPerformed(evt);
                }
            });
            // Set size.
            secretCodeButtons[i].setPreferredSize(new Dimension(20, 28));
            secretCodeButtons[i].setMinimumSize(new Dimension(20, 28));
            // Add to panel.
            secretCodePanel.add(secretCodeButtons[i]);
        }
        // Reveal code only if game ended (loaded game).
        if (ci.getGameEnded() == true) {
            revealSecretCode();
        }
        secretCodePanel.revalidate();
    }

    /**
     * Initialize a global Key Listener.
     * The listener provides the game handling by keyboard.
     * In practice this means you can choose a color via key a, b, c, etc.
     * and place it via 1, 2, 3, etc. The guess can be checked by pressing the
     * space bar.
     *
     * @see #keyTyped(java.awt.event.KeyEvent)
     */
    private void initKeyListener(){
        AWTEventListener ael = new AWTEventListener() {
            public void eventDispatched(AWTEvent event) {
                // Only react to key typed events.
                if (event.getID() == KeyEvent.KEY_TYPED) {
                    keyTyped((KeyEvent) event);
                }
            }
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(
                ael, AWTEvent.KEY_EVENT_MASK);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="aboutDialog">

    /**
     * Show the about dialog.
     *
     * @param evt The triggered event. Not used.
     */
    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        // Show about dialog:
        aboutDialog.pack();
        aboutDialog.setVisible(true);
    }

    /**
     * Hide the about dialog.
     *
     * @param evt The triggered event. Not used.
     */
    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Hide about dialog:
        aboutDialog.setVisible(false);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="settingsDialog">

    /**
     * Show the settings dialog.
     * Before the dialog is set to visible, all elements will be
     * initialized with the current settings.
     *
     * @param evt The triggered event. Not used.
     */
    private void editSettingsMenuItemActionPerformed(
            java.awt.event.ActionEvent evt) {
        // Init dialog with settings.
        doubleColorsCheckBox.setSelected(
                ci.getSettingDoubleCol());
        gameWidthSlider.setValue(ci.getSettingWidth());
        colorsSlider.setValue(ci.getSettingColQuant());
        maxTriesSpinner.setValue(ci.getSettingMaxTries());
        gameModeComboBox.setSelectedIndex(
                ci.getSettingAiMode() == true ? 1 : 0);
        // Show settings dialog:
        settingsDialog.pack();
        settingsDialog.setVisible(true);
    }

    /**
     * Hide the settings dialog without saving.
     *
     * @param evt The triggered event. Not used.
     */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Hide settings dialog:
        settingsDialog.setVisible(false);
    }

    /**
     * Save, init new game (with new settings) and hide dialog.
     * If the settings are not valid an error message will be displayed.
     *
     * @param evt The triggered event. Not used.
     */
    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {
        boolean dc = doubleColorsCheckBox.isSelected();
        int colQuant = colorsSlider.getValue();
        int width = gameWidthSlider.getValue();
        // Check settings.
        if(width <= colQuant || dc) {
            // Set settings.
            ci.setSettingDoubleCol(dc);
            ci.setSettingColQuant(colQuant);
            ci.setSettingWidth(width);
            ci.setSettingMaxTries(
                    Integer.parseInt(maxTriesSpinner.getValue().toString()));
            ci.setSettingAiMode(
                gameModeComboBox.getSelectedIndex() == 1 ? true : false);
            // Init new game.
            ci.newGame();
            initNewGame();
        } else {
            // Too less colors (without double colors) for the game width.
            JOptionPane.showMessageDialog(null,
                    "Too few colors or too wide game width.\n" +
                    "Use double colors or change color quantity" +
                    " / game width settings.",
                    "Error:", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Hide dialog.
        settingsDialog.setVisible(false);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Load / Save">

    /**
     * Show file browser dialog and load chosen game.
     *
     * @param evt The triggered event. Not used.
     */
    private void loadMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        fileChooser.setDialogTitle("Load Game");
        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION ||
                fileChooser.getSelectedFile() == null ||
                fileChooser.getSelectedFile().getAbsolutePath() == null) {
            return;
        }
        try {
            Debug.dbgPrint(fileChooser.getSelectedFile().getAbsolutePath());
            // Load game.
            ci.load(
                    fileChooser.getSelectedFile().getAbsolutePath());
        }
        catch (Exception e) {
            Debug.errorPrint(e.toString());
            return;
        }
        // Init loaded game.
        initNewGame();
    }

    /**
     * Show file browser dialog and save game to the given path and name.
     *
     * @param evt The triggered event. Not used.
     */
    private void saveAsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        fileChooser.setDialogTitle("Save Game");
        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION ||
                fileChooser.getSelectedFile() == null ||
                fileChooser.getSelectedFile().getAbsolutePath() == null) {
            return;
        }
        try {
            String file = fileChooser.getSelectedFile().getAbsolutePath();
            // Add file extension if not given by the user.
            if (file.endsWith("." + ci.FILE_EXTENSION) == false) {
                file += "." + ci.FILE_EXTENSION;
            }
            Debug.dbgPrint("Save to " + file);
            // Save game.
            ci.save(file);
        }
        catch (Exception e) {
            Debug.errorPrint(e.toString());
            return;
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Sub functions">

    /**
     * Checks if the complete active Row in the game table is set
     * with a color.
     *
     * @return true if all fields in the active Row are set. False otherwise.
     */
    private boolean rowIsSet(){
        int width = ci.getSettingWidth();
        for (int i = width; i < width * 2; i++){
            JButton button = (JButton)
                    gameButtons[ci.getActiveRowNumber()][i];
            // If empty (gray) cell was found return false
            if (button.getBackground().getRGB() == Color.Null.getRGB()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Cycles through the secret code (GUI) and sets it in the game engine.
     *
     * @see #translateColor(java.awt.Color)
     */
    private void writeSecrectCode() {
        int width = ci.getSettingWidth();
        Color[] colors = new Color[width];
        for (int i = 0; i < width; i++) {
            colors[i] = translateColor(secretCodeButtons[i].getBackground());
        }
        ci.setSecretCode(colors);
    }

    /**
     * Cycles through the active game Row (GUI) and sets it in the game engine.
     *
     * @see #translateColor(java.awt.Color)
     */
    private void writeToGameField() {
        int width = ci.getSettingWidth();
        Color[] colors = new Color[width];
        for (int i = width; i < width * 2; i++) {
            colors[-width+i] = translateColor(
                    gameButtons[ci.getActiveRowNumber()]
                    [i].getBackground());
        }
        ci.writeToGameField(colors);
    }

    /**
     * Translate the java.awt.Color to a common.Color.
     *
     * @param color A color in the java.awt.Color format.
     * @return A color in the common.Color format.
     * @see common.Color
     * @see java.awt.Color
     */
    private Color translateColor (java.awt.Color color) {
        Color ret = null;
        switch (color.getRGB()) {
                case -65536:
                    ret = Color.Red;
                    break;
                case -16711936:
                    ret = Color.Green;
                    break;
                case -16776961:
                    ret = Color.Blue;
                    break;
                case -256:
                    ret = Color.Yellow;
                    break;
                case -32768:
                    ret = Color.Orange;
                    break;
                case -8388480:
                    ret = Color.Purple;
                    break;
                case -60269:
                    ret = Color.Pink;
                    break;
                case -8355840:
                    ret = Color.Olive;
                    break;
                case -1:
                    ret = Color.White;
                    break;
                case -16777216:
                    ret = Color.Black;
                    break;
                case -8716289:
                    ret = Color.LightBlue;
                    break;
                case -8716422:
                    ret = Color.LightGreen;
                    break;
                case -34182:
                    ret = Color.LightRed;
                    break;
                case -20614:
                    ret = Color.LightOrange;
                    break;
                case -5276929:
                    ret = Color.LightPurple;
                    break;
            }
        return ret;
    }

    /**
     * Reveals the secret code by showing it in the GUI.
     * (Gets the secret code from the game engine.)
     */
    private void revealSecretCode() {
        Color[] secret = ci.getSecretCode().getColors();
        for (int i = 0; i <  ci.getSettingWidth(); i++){
            secretCodeButtons[i].setBackground(
                    new java.awt.Color(secret[i].getRGB()));
        }
    }

    /**
     * Show the guess result of a Row.
     * (Gets the guess result from the game engine.)
     *
     * @param row The row in which the result is displayed.
     */
    private void showResultRow(int row) {
        Color[] result = ci.getResultRow(row).getColors();
        for (int i = 0; i < ci.getSettingWidth(); i++) {
            JButton button = (JButton) gameButtons[row][i];
            java.awt.Color c = new java.awt.Color(result[i].getRGB());
            if (c.getRGB() == Color.Null.getRGB()) {
                // Use color "null" as the default color.
                c = null;
            }
            button.setBackground(c);
        }
    }

    /**
     * Show the guess row.
     * Function is used to display a loaded or AI game.
     * (Gets the guess row from the game engine.)
     * @param row The row in which the guess is displayed.
     */
    private void showGameRow(int row) {
        int width = ci.getSettingWidth();
        Color[] colors = ci.getGameFieldRow(row).getColors();
        for (int i = width; i < width * 2; i++) {
            gameButtons[row][i].setBackground(
                    new java.awt.Color(colors[i-width].getRGB()));
        }
    }

    /**
     * Parse the return state of the ControlInterface.turn() function.
     * For state == 1 show "you won" message; for state == -1 show
     * "you lose" message. Otherwise add a new Row.
     *
     * @param state A state integer like the ControlInterface.turn() function
     * returns it.
     * @see game.ControlInterface
     * @see #addRow()
     */
    private void parseGameState(int state) {
        if (state == 1) {
            revealSecretCode();
            this.setEnabled(false);
            if (ci.getSettingAiMode() == true) {
                gameState.setText("AI: I got the code!");
                JOptionPane.showMessageDialog(
                        null, "AI: I got the code!", "Info:",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                gameState.setText("You won!");
                JOptionPane.showMessageDialog(null, "You Won!", "Info:",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            this.setEnabled(true);
        } else if (state == -1) {
            revealSecretCode();
            this.setEnabled(false);
            if (ci.getSettingAiMode() == true) {
                gameState.setText("AI: I coudn't crack the code!");
                JOptionPane.showMessageDialog(
                        null, "AI: I coudn't crack the code!",
                        "Info:", JOptionPane.INFORMATION_MESSAGE);
            } else {
                gameState.setText("You lose!");
                JOptionPane.showMessageDialog(null, "You lose!", "Info:",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            this.setEnabled(true);
        } else {
            // Only add a new row if the game continues.
            addRow();
            if (ci.getSettingAiMode() == false) {
                gameState.setText("Choose a color and place it.");
            }
        }
    }

    /**
     * Add a new Row to the game table.
     */
    private void addRow() {
        int width = ci.getSettingWidth();
        int i;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel label;

        // Add new row.
        JButton[][] temp = new JButton[gameButtons.length + 1][width * 2];
        // Get ref. from old game table.
        System.arraycopy(gameButtons, 0, temp, 0, gameButtons.length);
        i = gameButtons.length;
        gameButtons = temp;

        label = new JLabel("" + (i + 1), SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = i + 1;
        gbc.weightx = 0.05;
        gamePanel.add(label, gbc);

        // Results.
        gbc.weightx = 0.1;
        for (int j = 0; j < width; j++) {
            gameButtons[i][j] = new JButton();
            gameButtons[i][j].getInputMap().put(
                    KeyStroke.getKeyStroke("SPACE"), "none" );
            gameButtons[i][j].setPreferredSize(new Dimension(20, 28));
            gameButtons[i][j].addActionListener(
                    new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    gameButtonResultActionPerformed(evt);
                }
            });
            gbc.gridx = j + 1; // +1 because of guess nr. label.
            gbc.gridy = i + 1; // +1 because of key usage label.
            gamePanel.add(gameButtons[i][j], gbc);
        }
        // Guesses.
        gbc.weightx = 0.9;
        for (int j = width; j < width * 2; j++) {
            gameButtons[i][j] = new JButton();
            // Add ToolTipText.
            // This tag is used to find the buttons position in the array.
            // By this effecitve way the button knows its own position. with out
            // crawling through the button array.
            gameButtons[i][j].setToolTipText(
                    "Button in row: " + (i + 1) +
                    ", column: " + ((j - width) + 1));
            gameButtons[i][j].getInputMap().put(
                    KeyStroke.getKeyStroke("SPACE"), "none" );
            gameButtons[i][j].setPreferredSize(new Dimension(35, 28));
            gameButtons[i][j].addActionListener(
                    new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    gameButtonPinActionPerformed(evt);
                }
            });
            gbc.gridx = j + 1;
            gbc.gridy = i + 1;
            gamePanel.add(gameButtons[i][j], gbc);
        }

        // Placeholder dummy.
        gbc.gridy = i + 2;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gamePanel.remove(gamePlaceholder);
        gamePanel.add(gamePlaceholder, gbc);
        gamePanel.revalidate();
    }

    /**
     * Parse the typed key.
     * Choose color for keys: a, b, c, etc.
     * Place color for keys: 1, 2, 3, etc.
     * Check guess result for key: space bar.
     *
     * @param k The key event that should be parsed.
     */
    private void keyTyped(KeyEvent k)
    {
        if (ci.getGameEnded() == false && this.isFocused()) {
            int key = (int)k.getKeyChar();
            // If it is a color choose key (a, b, c, ...).
            if (key-97 >= 0 && key-97 < ci.getSettingColQuant()) {
                colorButtons[key-97].doClick();
            }
            // If it is a color place key (1, 2, 3, ...).
            else if (key-49 >= 0 &&
                    key-49 < ci.getSettingWidth()) {
                if (ci.getSettingAiMode() == false) {
                    gameButtons[ci.getActiveRowNumber()]
                            [(key - 49) +
                            ci.getSettingWidth()].doClick();
                } else {
                    secretCodeButtons[key - 49].doClick();
                }
            }
            // If it is the check result key (SPACE).
            else if (key == 32) {
                if (ci.getSettingAiMode() == false) {
                    gameButtons[ci.getActiveRowNumber()]
                            [0].doClick();
                }
            }
        }
    }

    /**
     * Run an AI game until the game is solved.
     * While the AI is guessing the GUI is locked.
     * Every "AI_GUESS_DELAY"ms the timer makes a guess. This provides the GUI
     * enouth time between two guesses to repaint.
     * (In most cases the AI can guess very fast. Only if the game width is
     * large and the color quantity is high it will take
     * longer in later guesses.)
     * If a guess creation takes more than "AI_GUESS_DELAY"ms the timer
     * waits until the guess is computed.
     *
     * @see #AI_GUESS_DELAY
     */
    private void doAIGame() {
        final GeneticSolver gs = new GeneticSolver(ci);
        gs.initResults();
        gameState.setText("AI is guessing. Please wait...");
        setEnabled(false);

        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                // Do a guess every AI_GUESS_DELAY ms.
                if (ci.getGameEnded() == false) {
                    int state = gs.makeGuess();
                    showGameRow(ci.getActiveRowNumber()-1);
                    showResultRow(ci.getActiveRowNumber()-1);
                    parseGameState(state);
                } else {
                    aiTimer.stop();
                    setEnabled(true);
                }
            }
        };

        aiTimer = new Timer(AI_GUESS_DELAY, taskPerformer);
        aiTimer.start();
    }

    /**
     * Subfunction to parse the arguments from the command line.
     *
     * @param args Command line arguments from the main function.
     * @see #main(java.lang.String[])
     */
    private static void parseArgs(String args[]) {
        // -d = debug ?
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-d") == true) {
                // Arguments contain "-d" Debung mode on.
                System.out.println("Debug mode on");
                Debug.setDebug(true);
            }
        }
        // -b = benchmark?
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-b") == true) {
                try {
                    if ((args.length - 1 - i) >= 5) {
                        // There are at least 5 following args for -b.
                        int gameRepetitions = Integer.parseInt(args[i+1]);
                        int maxTries = Integer.parseInt(args[i+2]);
                        int gameWidth = Integer.parseInt(args[i+3]);
                        int colorQuant = Integer.parseInt(args[i+4]);
                        boolean doubleColors = Boolean.parseBoolean(args[i+5]);

                        // Check input.
                        if (gameRepetitions < 1) {
                            throw new Exception("There has to be at " +
                                    "least one repetition");
                        }
                        if (maxTries < 1) {
                            throw new Exception("There has to be at " +
                                    "least one try to solve a game");
                        }
                        if (gameWidth > colorQuant && doubleColors == false) {
                            throw new Exception("Too few colors or too wide " +
                                    "game width. Use double colors or " +
                                    "change color quantity / game width " +
                                    "settings.");
                        }
                        // Set settings.
                        ci.setSettingMaxTries(maxTries);
                        ci.setSettingWidth(gameWidth);
                        ci.setSettingColQuant(colorQuant);
                        ci.setSettingDoubleCol(doubleColors);
                        // Start banchmark.
                        System.out.println("Starting benchmark...");
                        GeneticSolver solve = new GeneticSolver(ci);
                        solve.geneticSolverTest(gameRepetitions);
                        System.exit(0);
                    } else {
                        throw new Exception("Not enough benchmark arguemtns");
                    }

                } catch (Exception e) {
                    Debug.errorPrint("Benchmark arguments error");
                    System.out.println(""+e.toString());
                    System.exit(1);
                }
            }
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Menu actions (by click)">

    /**
     * Quit the whole game.
     *
     * @param evt The triggered event. Not used.
     */
    private void quitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        System.exit(0);
    }

    /**
     * Initialize a new game.
     *
     * @param evt The triggered event. Not used.
     */
    private void newGameMenuItemActionPerformed(
            java.awt.event.ActionEvent evt) {
        ci.newGame();
        initNewGame();
    }

    /**
     * Fill out the current game table Row with a guess.
     * The logic for a valid guess is managed in the AI.
     *
     * @param evt The triggered event. Not used.
     * @see ai.GeneticSolver
     */
    private void showHintMenuItemActionPerformed(
            java.awt.event.ActionEvent evt) {
        if (ci.getGameEnded() == false && ci.getSettingAiMode() == false) {
            int i = ci.getSettingWidth();
            GeneticSolver hint = new GeneticSolver(ci);
            hint.initResults();
            Row r = hint.generateGuess();
            for (Color color : r.getColors()) {
                gameButtons[ci.getActiveRowNumber()]
                        [i++].setBackground(new java.awt.Color(color.getRGB()));
            }
        }
    }

    /**
     * Checks if the guess is valide or makes no sense in context
     * of previous guesses and results.
     *
     * @param evt The triggered event. Not used.
     * @see ai.Clues
     */
    private void validateGuessMenuItemActionPerformed(
            java.awt.event.ActionEvent evt) {
        if (ci.getGameEnded() == false && ci.getSettingAiMode() == false) {
            if (rowIsSet() == true) {
                Color[] c = new Color[ci.getSettingWidth()];
                for (int i = 0; i < c.length; i++) {
                    c[i] = translateColor(
                            gameButtons[ci.getActiveRowNumber()]
                            [ci.getSettingWidth()
                            + i].getBackground());
                }
                Row r = new Row(c);
                if (Clues.isFeasible(ci, r) == true) {
                    JOptionPane.showMessageDialog(null, "Good guess!", "Info:",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(
                            null, "Not a good idea...", "Info:",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Row is not set", "Info:",
                            JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * Set the last guess in the active row.
     * This feature becomes handy if you want to change only a few colors
     * in your next guess.
     *
     * @param evt The triggered event. Not used.
     */
    private void setLastGuessMenuItemMenuItemActionPerformed(
            java.awt.event.ActionEvent evt) {
        int i = ci.getSettingWidth();
        if (ci.getGameEnded() == false && ci.getSettingAiMode() == false
                && ci.getActiveRowNumber() > 0) {
            for (Color c : ci.getGameFieldRow(
                    ci.getActiveRowNumber()-1).getColors()) {
                gameButtons[ci.getActiveRowNumber()][i++].setBackground(
                        new java.awt.Color(c.getRGB()));
            }
        }
    }

     /**
     * Show the how-to.pdf in the system std. viewer.
     * The how-to.pdf will be exported to the systems std. temp. directory.
     * Then the file will be opened (if supported) with the system std.
     * PDF viewer.
     *
     * @param evt The triggered event. Not used.
     * @see java.awt.Desktop
     */
    private void howToMenuItemActionPerformed(
            java.awt.event.ActionEvent evt) {
        String tempPath = System.getProperty("java.io.tmpdir") + "/how-to.pdf";
        try {
            // Export the PDF from the jar to std. temp folder.
            InputStream in = MainWindow.class.getResourceAsStream(
                    "/gui/how-to.pdf");
            File f = new File(tempPath);
            OutputStream out = new FileOutputStream(f);
            byte buf[]=new byte[1024];
            int len;
            while((len=in.read(buf))>0) {
                out.write(buf,0,len);
            }
            out.close();
            in.close();

            // Open PDF if export went well.
            if (f.exists() == true) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(f);
                } else {
                    Debug.errorPrint("Awt Desktop is not supported!");
                    JOptionPane.showMessageDialog(null,
                            "Awt Desktop is not supported!", "Error:",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                Debug.errorPrint(tempPath + " dosen't exist!");
                JOptionPane.showMessageDialog(null,
                        tempPath + " dosen't exist!", "Error:",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            Debug.errorPrint(ex.toString());
            JOptionPane.showMessageDialog(null,
                        "Couldn't open " + tempPath, "Error:",
                        JOptionPane.ERROR_MESSAGE);
	}
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Button actions (by click)">

    /**
     * Set the chosen color.
     *
     * @param evt The triggered event.
     * The souce object color will be used to set the chosen color.
     */
    private void colorButtonsActionPerformed(java.awt.event.ActionEvent evt) {
        JButton button = (JButton) evt.getSource() ;
        chosenColorButton.setBackground(button.getBackground());
    }

    /**
     * Set the secret code color.
     *
     * @param evt The triggered event.
     * The souce object color will be used to set the secret code color.
     */
    private void secretCodeButtonsActionPerformed(
            java.awt.event.ActionEvent evt) {
        if (ci.getSettingAiMode() &&
                ci.getGameEnded() == false) {
            if (ci.getSettingDoubleCol() == false) {
                // Check for double colors.
                for (int i = 0; i< secretCodeButtons.length; i++){
                    java.awt.Color c = secretCodeButtons[i].getBackground();
                    if (c.getRGB() != Color.Null.getRGB() &&
                        c.getRGB() ==
                            chosenColorButton.getBackground().getRGB()) {
                        gameState.setText("Double colors are not allowed.");
                        return;
                    }
                }
            }
            // Set color.
            JButton button = (JButton) evt.getSource();
            button.setBackground(chosenColorButton.getBackground());
            gameState.setText("Set the secret code and watch the AI.");
            // Check if secret code is set.
            for (int i = 0; i< secretCodeButtons.length; i++){
                if (secretCodeButtons[i].getBackground().getRGB()
                        == Color.Null.getRGB()) {
                    return;
                }
            }
            // Secret code is set. Start AI game.
            writeSecrectCode();
            doAIGame();
        }
    }

    /**
     * Check the active game Row and display the result.
     *
     * @param evt The triggered event. Not used.
     * @see #writeToGameField()
     * @see #parseGameState(int)
     * @see #showResultRow(int)
     */
    private void gameButtonResultActionPerformed(
            java.awt.event.ActionEvent evt) {
        if (ci.getSettingAiMode() == false &&
                ci.getGameEnded() == false &&
                rowIsSet() == true) {
            // Check for guess result.
            // Write the colors (form the GUI) to the real game.
            writeToGameField();
            // Check colors.
            parseGameState(ci.turn());
            // Show the guess result in the GUI.
            showResultRow(ci.getActiveRowNumber() - 1);
        }
    }

    /**
     * Place the chosen color to the game table pin.
     *
     * @param evt The triggered event.
     * The souce object color will be set to the chosen color.
     */
    private void gameButtonPinActionPerformed(
            java.awt.event.ActionEvent evt) {
        if (ci.getSettingAiMode() == false) {
            JButton button = (JButton) evt.getSource();
            // Get buttons array pos. by parsing the ToolTipText.
            int row = Integer.parseInt(button.getToolTipText().substring(
                    button.getToolTipText().indexOf(": ") + 2,
                    button.getToolTipText().indexOf(","))) - 1;
            int col = Integer.parseInt(button.getToolTipText().substring(
                    button.getToolTipText().lastIndexOf(": ")+2)) - 1;
            int width = ci.getSettingWidth();

            if (row == ci.getActiveRowNumber()) {
                if (ci.getSettingDoubleCol() == false) {
                    // Check for double colors.
                    for (int i = width; i < width * 2; i++) {
                        java.awt.Color c = gameButtons[row][i].getBackground();
                        if (c.getRGB() != Color.Null.getRGB() &&
                                i != col + width &&
                                c.getRGB() ==
                                chosenColorButton.getBackground().getRGB()) {
                            gameState.setText(
                            "Double colors are not allowed.");
                            return;
                        }
                    }
                }
                button.setBackground(chosenColorButton.getBackground());
                if (ci.getGameEnded() == false &&
                        rowIsSet() == true) {
                    gameState.setText(
                            "Click on one result button (or press SPACE) " +
                            "to check your guess.");
                } else {
                    gameState.setText(
                            "Choose a color (Click or key a, b, c,..) " +
                            "and place it (Click or key 1, 2, 3,...).");
                }
            }
        }
    }

    // </editor-fold>

    /**
     * Parse command line arguments, change to "Nimbus" Look & Feel
     * (if installed) and initialize the main window with a new standard game.
     *
     * @param args The command line arguments. <br />
     * -d = Show debug messages.<br />
     * -b &lt;repetitions&gt; &lt;max tries&gt; &lt;game width&gt;
     * &lt;color quantity&gt; &lt;double colors&gt; = AI benchmark<br />
     * Example: -b 100 10 4 6 True
     */
    public static void main(String args[]) {

        parseArgs(args);

        try {
            for (UIManager.LookAndFeelInfo info :
                    UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            Debug.errorPrint("Nimbus Look & Feel not found. Fallback.");
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }
}
