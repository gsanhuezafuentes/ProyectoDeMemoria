/*
 * Base on code from https://github.com/jMetal/jMetal
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
package model.metaheuristic.utils.solutionattribute;

import model.metaheuristic.solution.Solution;
import model.metaheuristic.utils.SolutionListUtils;
import model.metaheuristic.utils.comparator.DominanceComparator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class StrengthRawFitness <S extends Solution<?>>
        extends SolutionAttribute<S, Double>{
    private static final Comparator<Solution<?>> DOMINANCE_COMPARATOR = new DominanceComparator<Solution<?>>();

    private int k ; // k-th individual

    public StrengthRawFitness(int k) {
        this.k = k ;
    }

    public StrengthRawFitness() {
        this.k = 1 ;
    }

    public void computeDensityEstimator(List<S> solutionSet) {
        double [][] distance = SolutionListUtils.distanceMatrix(solutionSet);
        double []   strength    = new double[solutionSet.size()];
        double []   rawFitness  = new double[solutionSet.size()];
        double kDistance                                          ;

        // strength(i) = |{j | j <- SolutionSet and i dominate j}|
        for (int i = 0; i < solutionSet.size(); i++) {
            for (int j = 0; j < solutionSet.size();j++) {
                if (DOMINANCE_COMPARATOR.compare(solutionSet.get(i),solutionSet.get(j))==-1) {
                    strength[i] += 1.0;
                }
            }
        }

        //Calculate the raw fitness
        // rawFitness(i) = |{sum strenght(j) | j <- SolutionSet and j dominate i}|
        for (int i = 0;i < solutionSet.size(); i++) {
            for (int j = 0; j < solutionSet.size();j++) {
                if (DOMINANCE_COMPARATOR.compare(solutionSet.get(i),solutionSet.get(j))==1) {
                    rawFitness[i] += strength[j];
                }
            }
        }

        // Add the distance to the k-th individual. In the reference paper of SPEA2,
        // k = sqrt(population.size()), but a value of k = 1 is recommended. See
        // http://www.tik.ee.ethz.ch/pisa/selectors/spea2/spea2_documentation.txt
        for (int i = 0; i < distance.length; i++) {
            Arrays.sort(distance[i]);
            kDistance = 1.0 / (distance[i][k] + 2.0);
            solutionSet.get(i).setAttribute(getAttributeIdentifier(), rawFitness[i] + kDistance);
        }
    }

    public int getK() {
        return k ;
    }
}
