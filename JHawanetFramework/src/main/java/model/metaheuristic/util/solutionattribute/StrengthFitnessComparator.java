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
package model.metaheuristic.util.solutionattribute;

import model.metaheuristic.solution.Solution;

import java.util.Comparator;

public class StrengthFitnessComparator<S extends Solution<?>> implements Comparator<S>{
    private final StrengthRawFitness<S> fitnessValue = new StrengthRawFitness<S>();

    @Override
    public int compare(S solution1, S solution2) {
        int result ;
        if (solution1 == null) {
            if (solution2 == null) {
                result = 0;
            } else {
                result = 1 ;
            }
        } else if (solution2 == null) {
            result = -1;
        } else {
            double strengthFitness1 = Double.MIN_VALUE ;
            double strengthFitness2 = Double.MIN_VALUE ;

            if (fitnessValue.getAttribute(solution1) != null) {
                strengthFitness1 = (double) fitnessValue.getAttribute(solution1);
            }

            if (fitnessValue.getAttribute(solution2) != null) {
                strengthFitness2 = (double) fitnessValue.getAttribute(solution2);
            }

            if (strengthFitness1 < strengthFitness2) {
                result = -1;
            } else  if (strengthFitness1 > strengthFitness2) {
                result = 1;
            } else {
                result = 0;
            }
        }
        return result;
    }

}

