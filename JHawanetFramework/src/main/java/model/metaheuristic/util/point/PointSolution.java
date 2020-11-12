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
package model.metaheuristic.util.point;

import model.metaheuristic.solution.Solution;

import java.util.*;

/**
 * Solution used to wrap a {@link Point} object. Only objectives are used.
 *
 * @author Antonio J. Nebro
 */
public class PointSolution implements Solution<Double> {
    private final int numberOfObjectives;
    private final double[] objectives;
    protected Map<Object, Object> attributes;

    /**
     * Constructor.
     *
     * @param numberOfObjectives the number of objectives.
     */
    public PointSolution(int numberOfObjectives) {
        this.numberOfObjectives = numberOfObjectives;
        objectives = new double[numberOfObjectives];
        attributes = new HashMap<>();
    }

    /**
     * Constructor.
     *
     * @param point
     */
    public PointSolution(Point point) {
        this.numberOfObjectives = point.getDimension();
        objectives = new double[numberOfObjectives];

        for (int i = 0; i < numberOfObjectives; i++) {
            this.objectives[i] = point.getValue(i);
        }
    }

    /**
     * Constructor.
     *
     * @param solution
     */
    public PointSolution(Solution<?> solution) {
        this.numberOfObjectives = solution.getNumberOfObjectives();
        objectives = new double[numberOfObjectives];

        for (int i = 0; i < numberOfObjectives; i++) {
            this.objectives[i] = solution.getObjective(i);
        }
    }

    /**
     * Copy constructor.
     *
     * @param point
     */
    public PointSolution(PointSolution point) {
        this(point.getNumberOfObjectives());

        for (int i = 0; i < numberOfObjectives; i++) {
            this.objectives[i] = point.getObjective(i);
        }
    }

    /**
     * Set the objective.
     *
     * @param index the index associated to objective
     * @param value the value of this objective
     */
    @Override
    public void setObjective(int index, double value) {
        objectives[index] = value;
    }

    /**
     * Get the objective.
     *
     * @param index the index assigned to objective value when was saved.
     * @return the value of objective
     */
    @Override
    public double getObjective(int index) {
        return objectives[index];
    }

    /**
     * Get all objectives.
     *
     * @return the objectives.
     */
    @Override
    public double[] getObjectives() {
        return objectives;
    }

    /**
     * Get all variables of the solution. This method return a empty collection.
     *
     * @return the variables.
     */
    @Override
    public List<Double> getVariables() {
        return Collections.emptyList();
    }

    /**
     * Get a specific variable. This method return null.
     *
     * @param index Index of decision variable
     * @return the value of the variable.
     */
    @Override
    public Double getVariable(int index) {
        return null;
    }

    /**
     * Set the variable. This method doesn't do anything.
     *
     * @param index the index associated to variable to be added or modified
     * @param value the value associated to decision variable
     */
    @Override
    public void setVariable(int index, Double value) {
        //This method is an intentionally-blank override.
    }

    /**
     * Get the variable as string. This method return null.
     *
     * @param index the index of variable to return.
     * @return
     */
    @Override
    public String getVariableAsString(int index) {
        return null;
    }

    /**
     * Get the number of variables. This method return allways 0.
     *
     * @return the number of variables.
     */
    @Override
    public int getNumberOfVariables() {
        return 0;
    }

    /**
     * Get the number of objectives.
     *
     * @return the number of objectives.
     */
    @Override
    public int getNumberOfObjectives() {
        return numberOfObjectives;
    }

    /**
     * Copy the object.
     *
     * @return the copy.
     */
    @Override
    public PointSolution copy() {
        return new PointSolution(this);
    }

    /**
     * Set attributes to solution.
     *
     * @param id    The key associated to value.
     * @param value The value.
     */
    @Override
    public void setAttribute(Object id, Object value) {
        attributes.put(id, value);
    }

    /**
     * Get attributes of the solution.
     *
     * @param id The element associated to attribute when was saved.
     * @return
     */
    @Override
    public Object getAttribute(Object id) {
        return attributes.get(id);
    }

    /**
     * Ask if a specific attribute exist.
     *
     * @param id the id of the attribute.
     * @return this method return always false.
     */
    @Override
    public boolean hasAttribute(Object id) {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        PointSolution that = (PointSolution) o;

        if (numberOfObjectives != that.numberOfObjectives)
            return false;
        if (!Arrays.equals(objectives, that.objectives))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(objectives);
    }

    @Override
    public String toString() {
        return Arrays.toString(objectives);
    }

    /**
     * Get all attributes.
     * @return a map with the attributes.
     */
    @Override
    public Map<Object, Object> getAttributes() {
        return attributes;
    }
}
