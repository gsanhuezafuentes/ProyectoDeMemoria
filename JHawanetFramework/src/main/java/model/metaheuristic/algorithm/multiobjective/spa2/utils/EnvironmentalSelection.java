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
package model.metaheuristic.algorithm.multiobjective.spa2.utils;

import model.metaheuristic.operator.selection.SelectionOperator;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.util.SolutionListUtils;
import model.metaheuristic.util.solutionattribute.LocationAttribute;
import model.metaheuristic.util.solutionattribute.StrengthFitnessComparator;
import model.metaheuristic.util.solutionattribute.StrengthRawFitness;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class EnvironmentalSelection<S extends Solution<?>> implements SelectionOperator<List<S>,List<S>> {

    private final int solutionsToSelect ;
    private final StrengthRawFitness<S> strengthRawFitness ;

    public EnvironmentalSelection(int solutionsToSelect) {
        this(solutionsToSelect, 1) ;
    }

    public EnvironmentalSelection(int solutionsToSelect, int k) {
        this.solutionsToSelect = solutionsToSelect ;
        this.strengthRawFitness = new StrengthRawFitness<>(k) ;
    }

    @Override
    public List<S> execute(List<S> source2) {
        int size;
        List<S> source = new ArrayList<>(source2.size());
        source.addAll(source2);
        if (source2.size() < this.solutionsToSelect) {
            size = source.size();
        } else {
            size = this.solutionsToSelect;
        }

        List<S> aux = new ArrayList<>(source.size());

        int i = 0;
        while (i < source.size()){
            double fitness = (double) this.strengthRawFitness.getAttribute(source.get(i));
            if (fitness<1.0){
                aux.add(source.get(i));
                source.remove(i);
            } else {
                i++;
            }
        }

        if (aux.size() < size){
            StrengthFitnessComparator<S> comparator = new StrengthFitnessComparator<S>();
            Collections.sort(source,comparator);
            int remain = size - aux.size();
            for (i = 0; i < remain; i++){
                aux.add(source.get(i));
            }
            return aux;
        } else if (aux.size() == size) {
            return aux;
        }

        double [][] distance = SolutionListUtils.distanceMatrix(aux);
        List<List<Pair<Integer, Double>> > distanceList = new ArrayList<>();
        LocationAttribute<S> location = new LocationAttribute<S>(aux);
        for (int pos = 0; pos < aux.size(); pos++) {
            List<Pair<Integer, Double>> distanceNodeList = new ArrayList<>();
            for (int ref = 0; ref < aux.size(); ref++) {
                if (pos != ref) {
                    distanceNodeList.add(Pair.of(ref, distance[pos][ref]));
                }
            }
            distanceList.add(distanceNodeList);
        }

        for (List<Pair<Integer, Double>> pairs : distanceList) {
            Collections.sort(pairs, (pair1, pair2) -> {
                return pair1.getRight().compareTo(pair2.getRight());
            });
        }

        while (aux.size() > size) {
            double minDistance = Double.MAX_VALUE;
            int toRemove = 0;
            i = 0;
            for (List<Pair<Integer, Double>> dn : distanceList) {
                if (dn.get(0).getRight() < minDistance) {
                    toRemove = i;
                    minDistance = dn.get(0).getRight();
                    //i y toRemove have the same distance to the first solution
                } else if (dn.get(0).getRight().equals(minDistance)) {
                    int k = 0;
                    while ((dn.get(k).getRight().equals(
                            distanceList.get(toRemove).get(k).getRight())) &&
                            k < (distanceList.get(i).size() - 1)) {
                        k++;
                    }

                    if (dn.get(k).getRight() <
                            distanceList.get(toRemove).get(k).getRight()) {
                        toRemove = i;
                    }
                }
                i++;
            }

            int tmp =  (int) location.getAttribute(aux.get(toRemove));
            aux.remove(toRemove);
            distanceList.remove(toRemove);

            for (List<Pair<Integer, Double>> pairs : distanceList) {
                Iterator<Pair<Integer, Double>> interIterator = pairs.iterator();
                while (interIterator.hasNext()) {
                    if (interIterator.next().getLeft() == tmp) {
                        interIterator.remove();
                        continue;
                    }
                }
            }
        }
        return aux;
    }

}
