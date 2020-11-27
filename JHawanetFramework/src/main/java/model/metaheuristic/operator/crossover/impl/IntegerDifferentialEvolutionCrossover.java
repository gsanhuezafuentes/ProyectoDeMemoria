/*
 * Code taken and modify from https://github.com/jMetal/jMetal
 *
 * Copyright <2017> <Antonio J. Nebro, Juan J. Durillo>
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to
 * whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. © 2019
 * GitHub, Inc.
 */
package model.metaheuristic.operator.crossover.impl;


import model.metaheuristic.operator.crossover.CrossoverOperator;
import model.metaheuristic.solution.impl.IntegerSolution;
import model.metaheuristic.util.random.BoundedRandomGenerator;
import model.metaheuristic.util.random.JavaRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Differential evolution crossover operator
 *
 * @author Antonio J. Nebro
 * <p>Comments: - The operator receives two parameters: the current individual and an array of
 * three parent individuals - The best and rand variants depends on the third parent, according
 * whether it represents the current of the "best" individual or a random one. The
 * implementation of both variants are the same, due to that the parent selection is external to
 * the crossover operator. - Implemented variants: - rand/1/bin (best/1/bin) - rand/1/exp
 * (best/1/exp) - current-to-rand/1 (current-to-best/1) - current-to-rand/1/bin
 * (current-to-best/1/bin) - current-to-rand/1/exp (current-to-best/1/exp)
 */
@SuppressWarnings("serial")
public class IntegerDifferentialEvolutionCrossover implements CrossoverOperator<IntegerSolution> {
    public enum DE_VARIANT {
        RAND_1_BIN,
        RAND_1_EXP,
        RAND_2_BIN,
        RAND_2_EXP,
        BEST_1_BIN,
        BEST_1_EXP,
        BEST_2_BIN,
        BEST_2_EXP,
        RAND_TO_BEST_1_BIN,
        RAND_TO_BEST_1_EXP,
        CURRENT_TO_RAND_1_BIN,
        CURRENT_TO_RAND_1_EXP
    }

    public enum DE_CROSSOVER_TYPE {
        BIN,
        EXP
    }

    public enum DE_MUTATION_TYPE {
        RAND,
        BEST,
        RAND_TO_BEST,
        CURRENT_TO_RAND
    }

    private static final DE_VARIANT DEFAULT_DE_VARIANT = DE_VARIANT.RAND_1_BIN;

    private static final double DEFAULT_CR = 0.5;
    private static final double DEFAULT_F = 0.5;

    private double cr;
    private double f;

    private int numberOfDifferenceVectors = 1;
    private DE_CROSSOVER_TYPE crossoverType = DE_CROSSOVER_TYPE.BIN;
    private DE_MUTATION_TYPE mutationType = DE_MUTATION_TYPE.RAND;

    private DE_VARIANT variant;

    private IntegerSolution currentSolution = null;
    private IntegerSolution bestSolution = null;

    private BoundedRandomGenerator<Integer> jRandomGenerator;
    private BoundedRandomGenerator<Double> crRandomGenerator;

    /**
     * Constructor
     */
    public IntegerDifferentialEvolutionCrossover() {
        this(DEFAULT_CR, DEFAULT_F, DEFAULT_DE_VARIANT);
    }

    /**
     * Constructor
     *
     * @param cr
     * @param f
     * @param variant
     */
    public IntegerDifferentialEvolutionCrossover(double cr, double f, DE_VARIANT variant) {
        this(
                cr,
                f,
                variant,
                (a, b) -> JavaRandom.getInstance().nextInt(a, b),
                (a, b) -> JavaRandom.getInstance().nextDouble(a, b));
    }

    /**
     * Constructor
     *
     * @param cr
     * @param f
     * @param variant
     * @param jRandomGenerator
     * @param crRandomGenerator
     */
    public IntegerDifferentialEvolutionCrossover(
            double cr,
            double f,
            DE_VARIANT variant,
            BoundedRandomGenerator<Integer> jRandomGenerator,
            BoundedRandomGenerator<Double> crRandomGenerator) {
        this.cr = cr;
        this.f = f;
        this.variant = variant;

        analyzeVariant(variant);

        this.jRandomGenerator = jRandomGenerator;
        this.crRandomGenerator = crRandomGenerator;
    }

    /**
     *
     * @param variant the type of variant
     * @throws IllegalArgumentException if the variant isn't valid
     */
    private void analyzeVariant(DE_VARIANT variant) {
        switch (variant) {
            case RAND_1_BIN:
            case RAND_1_EXP:
            case BEST_1_BIN:
            case BEST_1_EXP:
            case RAND_TO_BEST_1_BIN:
            case RAND_TO_BEST_1_EXP:
            case CURRENT_TO_RAND_1_BIN:
            case CURRENT_TO_RAND_1_EXP:
                numberOfDifferenceVectors = 1;
                break;
            case RAND_2_BIN:
            case RAND_2_EXP:
            case BEST_2_BIN:
            case BEST_2_EXP:
                numberOfDifferenceVectors = 2;
                break;
            default:
                throw new IllegalArgumentException("DE variant type invalid: " + variant);
        }

        switch (variant) {
            case RAND_1_BIN:
            case BEST_1_BIN:
            case RAND_TO_BEST_1_BIN:
            case CURRENT_TO_RAND_1_BIN:
            case RAND_2_BIN:
            case BEST_2_BIN:
                crossoverType = DE_CROSSOVER_TYPE.BIN;
                break;
            case RAND_1_EXP:
            case BEST_1_EXP:
            case RAND_TO_BEST_1_EXP:
            case CURRENT_TO_RAND_1_EXP:
            case RAND_2_EXP:
            case BEST_2_EXP:
                crossoverType = DE_CROSSOVER_TYPE.EXP;
                break;
            default:
                throw new IllegalArgumentException("DE crossover type invalid: " + variant);
        }

        switch (variant) {
            case RAND_1_BIN:
            case RAND_1_EXP:
            case RAND_2_BIN:
            case RAND_2_EXP:
                mutationType = DE_MUTATION_TYPE.RAND;
                break;
            case BEST_1_BIN:
            case BEST_1_EXP:
            case BEST_2_BIN:
            case BEST_2_EXP:
                mutationType = DE_MUTATION_TYPE.BEST;
                break;
            case CURRENT_TO_RAND_1_BIN:
            case CURRENT_TO_RAND_1_EXP:
                mutationType = DE_MUTATION_TYPE.CURRENT_TO_RAND;
                break;
            case RAND_TO_BEST_1_BIN:
            case RAND_TO_BEST_1_EXP:
                mutationType = DE_MUTATION_TYPE.RAND_TO_BEST;
                break;
            default:
                throw new IllegalArgumentException("DE mutation type invalid: " + variant);
        }
    }

    /* Getters */
    public double getCr() {
        return cr;
    }

    public double getF() {
        return f;
    }

    public DE_VARIANT getVariant() {
        return variant;
    }

    public int getNumberOfDifferenceVectors() {
        return numberOfDifferenceVectors;
    }

    public DE_CROSSOVER_TYPE getCrossoverType() {
        return crossoverType;
    }

    public DE_MUTATION_TYPE getMutationType() {
        return mutationType;
    }

    public int getNumberOfRequiredParents() {
        return 1 + numberOfDifferenceVectors * 2;
    }

    public int getNumberOfGeneratedChildren() {
        return 1;
    }

    public double getCrossoverProbability() {
        return 1.0;
    }

    /* Setters */
    public void setCurrentSolution(IntegerSolution current) {
        this.currentSolution = current;
    }

    public void setBestSolution(IntegerSolution bestSolution) {
        this.bestSolution = bestSolution;
    }

    public void setCr(double cr) {
        this.cr = cr;
    }

    public void setF(double f) {
        this.f = f;
    }

    /**
     * Execute() method
     */
    @Override
    public List<IntegerSolution> execute(List<IntegerSolution> parentSolutions) {
        IntegerSolution child = (IntegerSolution) currentSolution.copy();

        int numberOfVariables = parentSolutions.get(0).getNumberOfVariables();
        int jrand = jRandomGenerator.getRandomValue(0, numberOfVariables);

        Double[][] parent = new Double[getNumberOfRequiredParents()][];

        IntStream.range(0, getNumberOfRequiredParents())
                .forEach(
                        i -> {
                            parent[i] = new Double[numberOfVariables];
                            parentSolutions.get(i).getVariables().toArray(parent[i]);
                        });

        if (crossoverType.equals(DE_CROSSOVER_TYPE.BIN)) {
            for (int j = 0; j < numberOfVariables; j++) {
                if (crRandomGenerator.getRandomValue(0.0, 1.0) < cr || j == jrand) {
                    double value = mutate(parent, j);

                    child.setVariable(j, (int) value);
                }
            }
        } else if (crossoverType.equals(DE_CROSSOVER_TYPE.EXP)) {
            int j = jRandomGenerator.getRandomValue(0, numberOfVariables);
            int l = 0;

            do {
                double value = mutate(parent, j);

                child.setVariable(j, (int) value);

                j = (j + 1) % numberOfVariables;
                l++;
            } while ((crRandomGenerator.getRandomValue(0.0, 1.0) < cr) && (l < numberOfVariables));
        }

        repairVariableValues(child);

        List<IntegerSolution> result = new ArrayList<>(1);
        result.add(child);
        return result;
    }

    private void repairVariableValues(IntegerSolution solution) {
        IntStream.range(0, solution.getNumberOfVariables())
                .forEach(
                        i -> {
                            solution.setVariable(
                                    i,
                                    (int) repairSolutionVariableValue(
                                            solution.getVariable(i), solution.getLowerBound(i), solution.getUpperBound(i)));
                        });
    }

    private double mutate(Double[][] parent, int index) {
        double value = 0;
        if (mutationType.equals(DE_MUTATION_TYPE.RAND)) {
            value = randMutation(parent, index, numberOfDifferenceVectors);
        } else if (mutationType.equals(DE_MUTATION_TYPE.BEST)) {
            value = bestMutation(parent, index, numberOfDifferenceVectors);
        } else if (mutationType.equals(DE_MUTATION_TYPE.RAND_TO_BEST)) {
            value = bestRandToBestMutation(parent, index);
        }

        return value;
    }

    /**
     *
     * @param parent
     * @param index
     * @param numberOfDifferenceVectors
     * @return
     * @throws IllegalArgumentException if numberOfDifferenceVectors isn't 1 or 2.
     */
    private double randMutation(Double[][] parent, int index, int numberOfDifferenceVectors) {
        if (numberOfDifferenceVectors == 1) {
            return parent[2][index] + f * (parent[0][index] - parent[1][index]);
        } else if (numberOfDifferenceVectors == 2) {
            return parent[4][index]
                    + f * (parent[0][index] - parent[1][index])
                    + f * (parent[2][index] - parent[3][index]);
        } else {
            throw new IllegalArgumentException(
                    "Number of difference vectors invalid: " + numberOfDifferenceVectors);
        }
    }

    /**
     *
     * @param parent
     * @param index
     * @param numberOfDifferenceVectors a 1 or 2.
     * @return get the best mutation
     * @throws NullPointerException if bestSolution of this object is null.
     * @throws IllegalArgumentException if the numberOfDifferenceVectors isn't 1 or 2.
     */
    private double bestMutation(Double[][] parent, int index, int numberOfDifferenceVectors) {
        Objects.requireNonNull(bestSolution);
        if (numberOfDifferenceVectors == 1) {
            return bestSolution.getVariable(index) + f * (parent[0][index] - parent[1][index]);
        } else if (numberOfDifferenceVectors == 2) {
            return bestSolution.getVariable(index)
                    + f * (parent[0][index] - parent[1][index])
                    + f * (parent[2][index] - parent[3][index]);
        } else {
            throw new IllegalArgumentException(
                    "Number of difference vectors invalid: " + numberOfDifferenceVectors);
        }
    }

    /**
     *
     * @param parent
     * @param index
     * @return
     * @throws NullPointerException if bestSolution or currentSolution of this object is null when this method is called
     */
    private double bestRandToBestMutation(Double[][] parent, int index) {
        Objects.requireNonNull(bestSolution);
        Objects.requireNonNull(currentSolution);
        return currentSolution.getVariable(index)
                + f * (bestSolution.getVariable(index) - currentSolution.getVariable(index))
                + f * (parent[0][index] - parent[1][index]);
    }

    /**
     * Get the variant from string
     * @param variant the variant to search
     * @return the DE_VARIANT type
     * @throws IllegalArgumentException if the string isn't valid.
     */
    public static DE_VARIANT getVariantFromString(String variant) {
        DE_VARIANT deVariant;
        switch (variant) {
            case "RAND_1_BIN":
                deVariant = DE_VARIANT.RAND_1_BIN;
                break;
            case "RAND_2_BIN":
                deVariant = DE_VARIANT.RAND_2_BIN;
                break;
            case "BEST_1_BIN":
                deVariant = DE_VARIANT.BEST_1_BIN;
                break;
            case "BEST_1_EXP":
                deVariant = DE_VARIANT.BEST_1_EXP;
                break;
            case "BEST_2_BIN":
                deVariant = DE_VARIANT.BEST_2_BIN;
                break;
            case "BEST_2_EXP":
                deVariant = DE_VARIANT.BEST_2_EXP;
                break;
            case "RAND_TO_BEST_1_BIN":
                deVariant = DE_VARIANT.RAND_TO_BEST_1_BIN;
                break;
            case "RAND_TO_BEST_1_EXP":
                deVariant = DE_VARIANT.RAND_TO_BEST_1_EXP;
                break;
            case "CURRENT_TO_RAND_1_BIN":
                deVariant = DE_VARIANT.CURRENT_TO_RAND_1_BIN;
                break;
            case "CURRENT_TO_RAND_1_EXP":
                deVariant = DE_VARIANT.CURRENT_TO_RAND_1_EXP;
                break;
            default:
                throw new IllegalArgumentException("Invalid differential evolution variant: " + variant);
        }
        return deVariant;
    }

    /**
     * Checks if the value is between its bounds; if not, if it lower/higher than the lower/upper bound, this last
     * value is returned.
     *
     * @param value      The value to be checked
     * @param lowerBound
     * @param upperBound
     * @return The same value if it is in the limits or a repaired value otherwise
     * @throws IllegalArgumentException if lowerBound isn't less than upperBound
     */
    private double repairSolutionVariableValue(double value, double lowerBound, double upperBound) {
        if (!(lowerBound < upperBound)) {
            throw new IllegalArgumentException("The lower bound (" + lowerBound + ") is greater than the "
                    + "upper bound (" + upperBound + ")");
        }

        double result = value;
        if (value < lowerBound) {
            result = lowerBound;
        }
        if (value > upperBound) {
            result = upperBound;
        }

        return result;
    }
}
