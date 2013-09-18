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
import java.util.ArrayList;

/**
 * A genetic solving algorithm.
 * This implementation is a slightly modified version of the one in the paper:
 * <a href="https://lirias.kuleuven.be/bitstream/123456789/164803/1/KBI_0806.pdf">
 * Efficient solutions for Mastermind using genetic algorithms</a>
 * <br/ >For more information on genetic algorithms see:
 * <a href="http://en.wikipedia.org/wiki/Genetic_algorithm">
 * Genetic algorithm on Wikipedia</a>
 */
public class GeneticSolver implements SolvingAlgorithm {

    /**
     * Size of the population within a generation.
     */
    private final int POPULATION_SIZE = 2000;
    /**
     * Number of generations. If no feasible code was found after all
     * generations, a new guess with new generations and populations will be
     * made.
     */
    private final int GENERATION_SIZE = 500;
    /**
     * Max. amount of feasible codes. Feasible codes are good guesses. If the
     * max. of feasible codes are found, the solver stops and take a turn with
     * one randomly chosen feasible code.
     *
     * @see #addToFeasibleCodes()
     */
    private final int FEASIBLE_CODES_MAX = 1;
    private ControlInterface ci;
    private int width;
    private int colorQuant;
    private boolean doubleColors;
    private Row[] population = new Row[POPULATION_SIZE];
    private int[] fitness = new int[POPULATION_SIZE];
    private int[] blacks;
    private int[] whites;
    private ArrayList<Row> feasibleCodes = new ArrayList<Row>();
    private int parentPos = 0;

    /**
     * Initialize the AI with settings from the Mastermind engine.
     *
     * @param ci A control interface the AI will use to
     * interact with a game.
     */
    public GeneticSolver(ControlInterface ci) {
        this.ci = ci;
        width = ci.getSettingWidth();
        colorQuant = ci.getSettingColQuant();
        doubleColors = ci.getSettingDoubleCol();
        population = new Row[POPULATION_SIZE];
        fitness = new int[POPULATION_SIZE];
        blacks = new int[ci.getSettingMaxTries()];
        whites = new int[ci.getSettingMaxTries()];
        initResults();
    }

    /**
     * Initialize the arrays "blacks" and "whites" with values from the
     * GameField. The arrays "blacks" and "whites" are needed to accelerate
     * the processing.
     */
    public void initResults() {
        for (int i = 0; i < ci.getActiveRowNumber(); i++) {
            blacks[i] = ci.getResultRow(i)
                    .containsColor(Color.Black);
            whites[i] = ci.getResultRow(i)
                    .containsColor(Color.White);
        }
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
    public int makeGuess() {
        Row guess = generateGuess();
        ci.writeToGameField(guess.getColors());
        int[] result = compare(guess, ci.getSecretCode());
        blacks[ci.getActiveRowNumber()] = result[0];
        whites[ci.getActiveRowNumber()] = result[1];
        return ci.turn();
    }

    /**
     * Create new generations until enough egible codes are found.
     *
     * @return An egible guess.
     *
     * @see #initPopulation()
     * @see #calcFitness()
     * @see #sortFeasibleByFitness(int[], common.Row[])
     * @see #evolvePopulation()
     * @see #addToFeasibleCodes()
     */
    public Row generateGuess() {
        Row guess = new Row(width);
        boolean doCalc;
        // First guess?
        if (ci.getActiveRowNumber() == 0) {
            return generateRndGuess();
        }
        do {
            int genNumber = 0;
            doCalc = true;
            initPopulation();
            calcFitness();
            sortFeasibleByFitness(fitness, population);

            while (doCalc == true && genNumber <= GENERATION_SIZE
                    && feasibleCodes.size() <= FEASIBLE_CODES_MAX) {
                parentPos = 0;
                evolvePopulation();
                calcFitness();
                sortFeasibleByFitness(fitness, population);
                doCalc = addToFeasibleCodes();
                genNumber++;
            }
            if (feasibleCodes.isEmpty() == true) {
                Debug.dbgPrint("AI: No feasible code found. "
                        + "Retry with new population");
            }
        } while (feasibleCodes.isEmpty() == true);
        // Choose guess.
        Debug.dbgPrint("AI: There are " + feasibleCodes.size() +
                " feasible code(s)");
        guess = feasibleCodes.get((int) (Math.random() * feasibleCodes.size()));
        Debug.dbgPrint("AI: guess is " + guess);
        return guess;
    }

    /**
     * Evolve the population using cross over, mutation, permutation and
     * inversion.
     * 0.5 probability for both xOver1 and xOver2
     * after the crossover
     * 0.03 chance for mutation
     * 0.03 chance for permutation
     * 0.02 chance for inversion
     *
     *<a href="http://en.wikipedia.org/wiki/Genetic_algorithm#Reproduction">
     * Reproduction</a>
     *
     * @see #xOver1(common.Row[], int, int)
     * @see #xOver2(common.Row[], int, int)
     * @see #mutation(common.Row[], int)
     * @see #permutation(common.Row[], int)
     * @see #inversion(common.Row[], int)
     */
    private void evolvePopulation() {
        Row[] newPopulation = new Row[POPULATION_SIZE];
        for (int i = 0; i < POPULATION_SIZE; i++) {
            newPopulation[i] = new Row(width);
        }
        for (int i = 0; i < POPULATION_SIZE; i += 2) {
            if ((int) (Math.random() * 2) == 0) {
                xOver1(newPopulation, i, i + 1);
            } else {
                xOver2(newPopulation, i, i + 1);
            }
        }

        for (int i = 0; i < POPULATION_SIZE; i++) {
            if ((int) (Math.random() * 100) < 3) {
                mutation(newPopulation, i);
            } else if ((int) (Math.random() * 100) < 3) {
                permutation(newPopulation, i);
            } else if ((int) (Math.random() * 100) < 2) {
                inversion(newPopulation, i);
            }
        }

        doubleToRnd(newPopulation);

        population = newPopulation;
    }

    /**
     * A code c is eligible or feasible if it results in the same values for
     * Xk and Yk for all guesses k that have been played up till that stage,
     * if c was the secret code.<br />
     * X is the number of exact matches. Y is the number of guesses which are
     * the right color but in the wrong position.
     *
     * @return False if feasibleCodes is full. Otherwise true.
     */
    private boolean addToFeasibleCodes() {
        outer:
        for (int i = 0; i < POPULATION_SIZE; i++) {
            for (int j = 0; j < ci.getActiveRowNumber(); j++) {
                int[] result = compare(population[i],
                        ci.getGameFieldRow(j));

                if (result[0] != blacks[j] || result[1] != whites[j]) {
                    continue outer;
                }
            }

            if (feasibleCodes.size() < FEASIBLE_CODES_MAX) {
                if (feasibleCodes.contains(population[i]) == false) {
                    feasibleCodes.add(population[i]);
                    if (feasibleCodes.size() < FEASIBLE_CODES_MAX) {
                        return false;
                    }
                }
            } else {
                // E is full.
                return false;
            }
        }
        return true;
    }

    /**
     * Replaces double elements in newPopulation.
     *
     * @param newPopulation The population array that will be manipulated.
     */
    private void doubleToRnd(Row[] newPopulation) {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            if (lookForSame(newPopulation, i) == true) {
                newPopulation[i] = generateRndGuess();
            }
        }
    }

    /**
     * Look for Rows that are equal to the Row at popPos in newPopulation.
     *
     * @param newPopulation The population array that will be searched.
     * @param popPos The position of the Row within the population array that
     * will be compared.
     * @return true if equal Row found. false if no equal Row found.
     */
    private boolean lookForSame(Row[] newPopulation, int popPos) {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            if (population[popPos].equals(newPopulation[popPos]) == true) {
                return true;
            }
        }
        return false;
    }

    /**
     * Mutation. Replaces the color of one randomly chosen position by a random
     * other color.
     *
     * @param newPopulation The population array that will be manipulated.
     * @param popPos The position of the Row within the population array that
     * will be changed.
     */
    private void mutation(Row[] newPopulation, int popPos) {
        newPopulation[popPos].setColorAtPos((int) (Math.random() * width),
                Color.values()[(int) (Math.random() * colorQuant)]);
    }

    /**
     * Permutation. The colors of two random positions are switched.
     *
     * @param newPopulation The population array that will be manipulated.
     * @param popPos The position of the Row within the population array that
     * will be changed.
     */
    private void permutation(Row[] newPopulation, int popPos) {
        int pos1 = (int) (Math.random() * width);
        int pos2 = (int) (Math.random() * width);
        Color tmp = newPopulation[popPos].getColorAtPos(pos1);
        newPopulation[popPos].setColorAtPos(pos1,
                newPopulation[popPos].getColorAtPos(pos2));
        newPopulation[popPos].setColorAtPos(pos2, tmp);
    }

    /**
     * Inversion. Two positions are randomly picked, and the sequence of colors
     * between these positions is inverted.
     *
     * @param newPopulation The population array that will be manipulated.
     * @param popPos The position of the Row within the population array that
     * will be changed.
     */
    private void inversion(Row[] newPopulation, int popPos) {
        int pos1 = (int) (Math.random() * width);
        int pos2 = (int) (Math.random() * width);

        if (pos2 < pos1) {
            int tmp = pos2;
            pos2 = pos1;
            pos1 = tmp;
        }

        for (int i = 0; i < (pos2 - pos1)/2; i++) {
            Color tmp = newPopulation[popPos].getColorAtPos(pos1 + i);
            newPopulation[popPos].setColorAtPos(pos1 + i,
                    newPopulation[popPos].getColorAtPos(pos2 - i));
            newPopulation[popPos].setColorAtPos(pos2 - i, tmp);
        }
    }

    /**
     * One-point crossover. A single crossover point on both parents' organism
     * strings is selected. All data beyond that point in either organism string
     * is swapped between the two parent organisms. The resulting organisms are
     * the children.
     *
     * <a href="http://en.wikipedia.org/wiki/Crossover_%28genetic_algorithm%29#One-point_crossover">
     * One-point crossover</a>
     *
     * @param newPopulation The population array that will be manipulated.
     * @param child1Pos The position of a Row within the population array that
     * will be changed.
     * @param child2Pos The position of a Row within the population array that
     * will be changed.
     */
    private void xOver1(Row[] newPopulation, int child1Pos, int child2Pos) {
        int mother = getParentPos();
        int father = getParentPos();
        int sep = ((int) (Math.random() * width)) + 1;

        for (int i = 0; i < width; i++) {
            if (i <= sep) {
                newPopulation[child1Pos].setColorAtPos(i,
                        population[mother].getColorAtPos(i));
                newPopulation[child2Pos].setColorAtPos(i,
                        population[father].getColorAtPos(i));
            } else {
                newPopulation[child1Pos].setColorAtPos(i,
                        population[father].getColorAtPos(i));
                newPopulation[child2Pos].setColorAtPos(i,
                        population[mother].getColorAtPos(i));
            }
        }
    }

    /**
     * Two-point crossover. Two points are selected on the parent organism
     * strings. Everything between the two points is swapped between the parent
     * organisms, rendering two child organisms.
     *
     * <a href="http://en.wikipedia.org/wiki/Crossover_%28genetic_algorithm%29#Two-point_crossover">
     * Two-point crossover</a>
     *
     * @param newPopulation The population array that will be manipulated.
     * @param child1Pos The position of a Row within the population array that
     * will be changed.
     * @param child2Pos The position of a Row within the population array that
     * will be changed.
     */
    private void xOver2(Row[] newPopulation, int child1Pos, int child2Pos) {
        int mother = getParentPos();
        int father = getParentPos();
        int sep1;
        int sep2;

        sep1 = ((int) (Math.random() * width)) + 1;
        sep2 = ((int) (Math.random() * width)) + 1;

        if (sep1 > sep2) {
            int temp = sep1;
            sep1 = sep2;
            sep2 = temp;
        }

        for (int i = 0; i < width; i++) {
            if (i <= sep1 || i > sep2) {
                newPopulation[child1Pos].setColorAtPos(i,
                        population[mother].getColorAtPos(i));
                newPopulation[child2Pos].setColorAtPos(i,
                        population[father].getColorAtPos(i));
            } else {
                newPopulation[child1Pos].setColorAtPos(i,
                        population[father].getColorAtPos(i));
                newPopulation[child2Pos].setColorAtPos(i,
                        population[mother].getColorAtPos(i));
            }
        }

    }

    /**
     * Getter for a good parent position in the population.
     * In this case the one of the best fifth of the population is used.
     * It is important that only parents with a good fitness value are used to
     * generate the next generation.
     *
     * @return One position in the first fifth of the population-array
     * successively increasing.
     */
    private int getParentPos() {
        parentPos += (int) (Math.random() * 7);
        if (parentPos < POPULATION_SIZE / 5) {
            return parentPos;
        } else {
            parentPos = 0;
        }
        return parentPos;
    }

    /**
     * Calculates the fitness of every Row in population.
     * A fitness-value and the corresponding element of the population array
     * both have the same index in their respective arrays.
     *
     * It should resemble the function as described in the paper:
     *<a href="https://lirias.kuleuven.be/bitstream/123456789/164803/1/KBI_0806.pdf">
     * Efficient solutions for Mastermind using genetic algorithms</a>
     * on page 6.
     */
    private void calcFitness() {
        int xtmp;
        int ytmp;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            xtmp = 0;
            ytmp = 0;
            for (int j = 0; j < ci.getActiveRowNumber(); j++) {
                int[] result = compare(
                        population[i], ci.getGameFieldRow(j));
                xtmp += Math.abs(result[0] - blacks[j]);//+width * j;
                ytmp += Math.abs(result[1] - whites[j]);//+width * j;
            }
            fitness[i] = (xtmp + ytmp);
        }
    }

    /**
     * Compares two Rows.
     * result[0] is incremented if an equal value(color) is at an equal position.
     * result[1] is incremented if an equal value is an different positions.
     * result[0] are the black pegs.
     * result[1] are the white pegs.
     *<a href="http://en.wikipedia.org/wiki/Mastermind_%28board_game%29#Gameplay_and_rules">
     * Mastermind Rules</a>
     *
     * @param a Row Guesscode.
     * @param b Row Secretcode.
     * @return [0] is the number of black pegs, [1] is the number of white pegs.
     */
    private int[] compare(Row a, Row b) {
        int[] result = {0, 0};
        Color[] code = new Color[width];
        Color[] secret = new Color[width];
        // Create real copy.
        System.arraycopy(a.getColors(), 0, code, 0, width);
        System.arraycopy(b.getColors(), 0, secret, 0, width);

        for (int i = 0; i < width; i++) {
            if (code[i] == secret[i]) {
                result[0]++;
                secret[i] = null;
                code[i] = null;
            }
        }

        outer:
        for (int i = 0; i < width; i++) {
            if (code[i] != null) {
                for (int j = 0; j < width; j++) {
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

    /**
     * Initializes the Population with random Rows.
     * feasibleCodes gets purged.
     */
    private void initPopulation() {
        // Init population with random guesses.
        int i = 0;
        feasibleCodes.clear();
        while (i < POPULATION_SIZE) {
            population[i] = generateRndGuess();
            i++;
        }
    }

    /**
     * Generates a Row with random colors.
     * Generates a Row with the current width setting. The available colors are
     * determined by the colorQuant setting. If the doubleColors setting
     * is false, only differnt colors are set.<br />
     * <b>Warning:</b> Using this function can take up to
     * several minutes, especially if you have set a high width,
     * many colors or when you run the game on a slow computer.
     * This is not a bug, just a side effect of the complex
     * algorithm the AI is using.
     *
     * @return A Row with random colors.
     */
    private Row generateRndGuess() {
        Row guess = new Row(width);
        //Prepare values to fit colorQuant-rule
        Color[] values = new Color[colorQuant];
        Color[] all = Color.values();
        System.arraycopy(all, 0, values, 0, colorQuant);
        //Do the actual code generation...
        int i = 0;
        while (i < width) {
            Color now = values[(int) (Math.random() * colorQuant)];
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

    /**
     * This is a Quicksort that sorts the fitness and pop Arrays by the criteria
     * in the fitness-array.
     *
     * @param fitness An int array.
     * @param pop An array of Rows.
     * @see #sort(int[], common.Row[], int, int)
     * @see #divide(int[], common.Row[], int, int, int)
     * @see #swap(common.Row[], int, int)
     * @see #swap(int[], int, int)
     */
    private void sortFeasibleByFitness(int[] fitness, Row[] pop) {
        sort(fitness, pop, 0, fitness.length - 1);
    }

    /**
     * Helper function for recursive sorting.
     *
     * @param fitness An int array.
     * @param pop An array of Rows.
     * @param low The lower limit.
     * @param up The upper limit.
     */
    private void sort(int[] fitness, Row[] pop, int low, int up) {
        int p = (low + up) / 2;
        if (up > low) {
            //Feld zerlegen
            int pn = divide(fitness, pop, low, up, p);
            //und sortieren
            sort(fitness, pop, low, pn - 1);
            sort(fitness, pop, pn + 1, up);
        }
    }

    /**
     * Helper function for partitioning.
     *
     * @param fitness An int array.
     * @param pop An array of Rows.
     * @param low The lower limit.
     * @param up The upper limit.
     * @param pivot The position of the Pivot element.
     * @return The new Position of the Pivot element.
     */
    private int divide(int[] fitness, Row[] pop, int low, int up, int pivot) {
        int pn = low;
        int pv = fitness[pivot];
        swap(fitness, pivot, up);
        swap(pop, pivot, up);
        for (int i = low; i < up; i++) {
            if (fitness[i] <= pv) {
                swap(fitness, pn, i);
                swap(pop, pn++, i);
            }
            swap(fitness, up, pn);
            swap(pop, up, pn);
        }
        return pn;
    }

    /**
     * Helper function to swap two elements of an int array.
     *
     * @param fitness An int array.
     * @param a Position of the first element.
     * @param b Position of the second element.
     */
    private void swap(int[] fitness, int a, int b) {
        int tmp = fitness[a];
        fitness[a] = fitness[b];
        fitness[b] = tmp;
    }

    /**
     * Helper function to swap two elements of an array of Rows.
     *
     * @param pop An array of Rows.
     * @param a Position of the first element.
     * @param b Position of the second element.
     */
    private void swap(Row[] pop, int a, int b) {
        Row tmp = pop[a];
        pop[a] = pop[b];
        pop[b] = tmp;
    }

    /**
     * Speed test.
     *
     * @param repetitions Repetitions of solving one game.
     */
    public void geneticSolverTest(int repetitions){
        long start = System.currentTimeMillis();
        long durationLongest = 0;
        long durationShortest = 4000000;
        int gamewon = 0;
        int gamelost = 0;
        double guess = 0;

        for (int i = 0; i < repetitions; i++) {
            long starti = System.currentTimeMillis();
            int game;
            ci.newGame();

            do {
                game = makeGuess();
                guess++;
            } while (game == 0);


            if (game == 1) {
                gamewon++;
            } else {
                gamelost++;
            }

            long endi = System.currentTimeMillis();
            if (endi - starti > durationLongest) {
                durationLongest = endi - starti;
            }
            if (endi - starti < durationShortest) {
                durationShortest = endi - starti;
            }
        }

        long end = System.currentTimeMillis();
        long duration = end - start;

        System.out.println("##################\nBenchmark results:");
        System.out.println("Repetitions: " + repetitions);
        System.out.println("Duration in ms: " + duration);
        System.out.println("Duration in s: " + (duration/1000f));
        System.out.println("Duration in m: "+ (duration/1000f/60));
        System.out.println("Average solving time in ms: "
                + (duration/repetitions));
        System.out.println("Average solving timein s: "
                + (duration/1000f/repetitions));
        System.out.println("Shortest in ms: "+ durationShortest);
        System.out.println("Longest in ms: "+ durationLongest);
        System.out.println("Shortest in s: "+ durationShortest/1000f);
        System.out.println("Longest in s: "+ durationLongest/1000f);
        System.out.println("Longest in m: "+ durationLongest/1000f/60);
        System.out.println("Won: "+ gamewon);
        System.out.println("Lost: "+ gamelost);
        System.out.println("Average guesses: "+ (guess/repetitions));
    }
}
