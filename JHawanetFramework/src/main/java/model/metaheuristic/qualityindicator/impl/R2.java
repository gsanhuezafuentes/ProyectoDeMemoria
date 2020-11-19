/*
 * Code took from https://github.com/jMetal/jMetal
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
package model.metaheuristic.qualityindicator.impl;

import model.metaheuristic.qualityindicator.QualityIndicator;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.util.front.Front;
import model.metaheuristic.util.front.impl.ArrayFront;
import model.metaheuristic.util.front.util.FrontNormalizer;
import model.metaheuristic.util.front.util.FrontUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.StringTokenizer;

/**
 * TODO: Add comments here
 */
@SuppressWarnings("serial")
public class R2<Evaluate extends List<? extends Solution<?>>>
        implements QualityIndicator<Evaluate, Double> {
    private final double[][] lambda;


    private final Front referenceParetoFront;


    /**
     * Creates a new instance of the R2 indicator for a problem with
     * two objectives and 100 lambda vectors
     */
    public R2(Front referenceParetoFront) {
        // by default it creates an R2 indicator for a two dimensions problem and
        // uses only 100 weight vectors for the R2 computation
        this(100, referenceParetoFront);
    }


    /**
     * Creates a new instance of the R2 indicator for a problem with
     * two objectives and 100 lambda vectors
     */
    public R2() {
        // by default it creates an R2 indicator for a two dimensions problem and
        // uses only 100 weight vectors for the R2 computation
        this(100);
    }

    /**
     * Creates a new instance of the R2 indicator for a problem with
     * two objectives and N lambda vectors
     */
    public R2(int nVectors) {
        this(nVectors, null);
    }

    /**
     * Constructor
     * Creates a new instance of the R2 indicator for nDimensiosn
     * It loads the weight vectors from the file fileName
     * @throws java.io.IOException if there is a error reading referenceParetoFront
     */
    public R2(String file, Front referenceParetoFront) throws java.io.IOException {
        this(readWeightsFrom(file), referenceParetoFront);
    }

    /**
     * Creates a new instance of the R2 indicator for a problem with
     * two objectives and N lambda vectors
     */
    public R2(int nVectors, Front referenceParetoFront) {
        // by default it creates an R2 indicator for a two dimensions problem and
        // uses only <code>nVectors</code> weight vectors for the R2 computation
        this(generateWeights(nVectors), referenceParetoFront);
    }

    private R2(double[][] lambda, Front referenceParetoFront) {
        // by default it creates an R2 indicator for a two dimensions problem and
        // uses only <code>nVectors</code> weight vectors for the R2 computation
        this.lambda = lambda;
        this.referenceParetoFront = referenceParetoFront;
    }

    private static double[][] generateWeights(int nVectors) {
        double[][] lambda = new double[nVectors][2];
        for (int n = 0; n < nVectors; n++) {
            double a = 1.0 * n / (nVectors - 1);
            lambda[n][0] = a;
            lambda[n][1] = 1 - a;
        }
        return lambda;
    }

    private static double[][] readWeightsFrom(String file) throws java.io.IOException {
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis);
        try (BufferedReader br = new BufferedReader(isr)) {

            String line = br.readLine();
            double[][] lambda;
            if (line == null) {
                lambda = null;
            } else {
                int numberOfObjectives = (new StringTokenizer(line)).countTokens();
                int numberOfVectors = (int) br.lines().count();

                lambda = new double[numberOfVectors][numberOfObjectives];

                int index = 0;
                while (line != null) {
                    StringTokenizer st = new StringTokenizer(line);
                    for (int i = 0; i < numberOfObjectives; i++)
                        lambda[index][i] = Double.parseDouble(st.nextToken());
                    index++;
                    line = br.readLine();
                }


            }
            return lambda;
        }
    }


    /**
     * Constructor
     * Creates a new instance of the R2 indicator for nDimensiosn
     * It loads the weight vectors from the file fileName
     */
    public R2(String file) throws java.io.IOException {
        this(file, null);
    } // R2


    @Override
    public @NotNull Double evaluate(Evaluate solutionList) {
        return r2(new ArrayFront(solutionList));
    }

    @Override
    public @NotNull String getName() {
        return "R2";
    }

    public double r2(Front front) {
        if (this.referenceParetoFront != null) {
            // STEP 1. Obtain the maximum and minimum values of the Pareto front
            double[] maximumValues = FrontUtils.getMaximumValues(this.referenceParetoFront);
            double[] minimumValues = FrontUtils.getMinimumValues(this.referenceParetoFront);

            // STEP 2. Get the normalized front
            FrontNormalizer frontNormalizer = new FrontNormalizer(minimumValues, maximumValues);
            front = frontNormalizer.normalize(front);
        }

        int numberOfObjectives = front.getPoint(0).getDimension();

        // STEP 3. compute all the matrix of Tschebyscheff values if it is null
        double[][] matrix = new double[front.getNumberOfPoints()][lambda.length];
        for (int i = 0; i < front.getNumberOfPoints(); i++) {
            for (int j = 0; j < lambda.length; j++) {
                matrix[i][j] = lambda[j][0] * Math.abs(front.getPoint(i).getValue(0));
                for (int n = 1; n < numberOfObjectives; n++) {
                    matrix[i][j] = Math.max(matrix[i][j],
                            lambda[j][n] * Math.abs(front.getPoint(i).getValue(n)));
                }
            }
        }

        double sum = 0.0;
        for (int i = 0; i < lambda.length; i++) {
            double tmp = matrix[0][i];
            for (int j = 1; j < front.getNumberOfPoints(); j++) {
                tmp = Math.min(tmp, matrix[j][i]);
            }
            sum += tmp;
        }
        return sum / (double) lambda.length;
    }
}
