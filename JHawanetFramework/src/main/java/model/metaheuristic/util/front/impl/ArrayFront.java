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
package model.metaheuristic.util.front.impl;

import exception.InvalidConditionException;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.util.front.Front;
import model.metaheuristic.util.point.Point;
import model.metaheuristic.util.point.impl.ArrayPoint;

import java.io.*;
import java.util.*;

/**
 * This class implements the {@link Front} interface by using an array of {@link Point} objects
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class ArrayFront implements Front {
    protected Point[] points;
    protected int numberOfPoints;
    private int pointDimensions;

    /**
     * Constructor
     */
    public ArrayFront() {
        points = null;
        numberOfPoints = 0;
        pointDimensions = 0;
    }

    /**
     * Constructor
     *
     * @throws NullPointerException if solutionList is null.
     * @throws if                   the size of solutionList is 0.
     */
    public ArrayFront(List<? extends Solution<?>> solutionList) {
        Objects.requireNonNull(solutionList, "The list of solutions is null");
        if (solutionList.size() == 0) {
            throw new IllegalArgumentException("The list of solutions is empty");
        }

        numberOfPoints = solutionList.size();
        pointDimensions = solutionList.get(0).getNumberOfObjectives();
        points = new Point[numberOfPoints];

        points = new Point[numberOfPoints];
        for (int i = 0; i < numberOfPoints; i++) {
            Point point = new ArrayPoint(pointDimensions);
            for (int j = 0; j < pointDimensions; j++) {
                point.setValue(j, solutionList.get(i).getObjective(j));
            }
            points[i] = point;
        }
    }

    /**
     * Copy Constructor
     *
     * @throws NullPointerException     if front is null-
     * @throws IllegalArgumentException if the number of points in front is 0.
     */
    public ArrayFront(Front front) {
        Objects.requireNonNull(front);
        if (front.getNumberOfPoints() == 0) {
            throw new IllegalArgumentException("The front is empty");
        }

        numberOfPoints = front.getNumberOfPoints();
        pointDimensions = front.getPoint(0).getDimension();
        points = new Point[numberOfPoints];

        points = new Point[numberOfPoints];
        for (int i = 0; i < numberOfPoints; i++) {
            points[i] = new ArrayPoint(front.getPoint(i));
        }
    }

    /**
     * Constructor
     */
    public ArrayFront(int numberOfPoints, int dimensions) {
        this.numberOfPoints = numberOfPoints;
        pointDimensions = dimensions;
        points = new Point[this.numberOfPoints];

        for (int i = 0; i < this.numberOfPoints; i++) {
            Point point = new ArrayPoint(pointDimensions);
            for (int j = 0; j < pointDimensions; j++) {
                point.setValue(j, 0.0);
            }
            points[i] = point;
        }
    }

    /**
     * Constructor
     *
     * @param fileName  the file name with front values
     * @param separator the separator used
     * @throws FileNotFoundException     if the file can't be found.
     * @throws IOException               error reading file.
     * @throws NumberFormatException     error in format of values in reading file.
     * @throws InvalidConditionException if each line in file hasn't the same number of values.
     */
    public ArrayFront(String fileName, String separator) throws IOException, FileNotFoundException {
        this();

        InputStream inputStream = createInputStream(fileName);
        InputStreamReader isr = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(isr);

        List<Point> list = new ArrayList<>();
        int numberOfObjectives = 0;
        String line;

        line = br.readLine();

        while (line != null) {
            String[] stringValues = line.split(separator);
            double[] values = new double[stringValues.length];
            for (int i = 0; i < stringValues.length; i++) {
                values[i] = Double.parseDouble(stringValues[i]);
            }

            if (numberOfObjectives == 0) {
                numberOfObjectives = stringValues.length;
            } else if (!(numberOfObjectives == stringValues.length)) {
                throw new InvalidConditionException("Invalid number of points read. Expected: " + numberOfObjectives
                        + ", received: " + values.length);
            }

            Point point = new ArrayPoint(values);
            list.add(point);
            line = br.readLine();
        }
        br.close();

        numberOfPoints = list.size();
        points = new Point[list.size()];
        points = list.toArray(points);
        if (numberOfPoints == 0) {
            pointDimensions = 0;
        } else {
            pointDimensions = points[0].getDimension();
        }
        for (int i = 0; i < numberOfPoints; i++) {
            points[i] = list.get(i);
        }
    }

    /**
     * Constructor
     *
     * @param fileName File containing the data. Each line of the file is a list of objective values
     * @throws FileNotFoundException if the sile can't be found.
     * @throws IOException error reading file.
     */
    public ArrayFront(String fileName) throws IOException, FileNotFoundException {
        //this(fileName, "\\s+");
        this(fileName, ",");
    }

    public InputStream createInputStream(String fileName) throws FileNotFoundException {
        InputStream inputStream = getClass().getResourceAsStream(fileName);
        if (inputStream == null) {
            inputStream = new FileInputStream(fileName);
        }

        return inputStream;
    }

    @Override
    public int getNumberOfPoints() {
        return numberOfPoints;
    }

    @Override
    public int getPointDimensions() {
        return pointDimensions;
    }

    /**
     * Get the point.
     *
     * @param index the index of point to get.
     * @return the point.
     * @throws IndexOutOfBoundsException if the index is less than 0 or the index is greater than number of points.
     */
    @Override
    public Point getPoint(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("The index value is negative");
        } else if (index >= numberOfPoints) {
            throw new IndexOutOfBoundsException(
                    "The index value ("
                            + index
                            + ") is greater than the number of "
                            + "points ("
                            + numberOfPoints
                            + ")");
        }

        return points[index];
    }

    /**
     * Set a point in a specific index.
     *
     * @param index the index value.
     * @param point the point to save.
     * @throws NullPointerException      if point is null.
     * @throws IndexOutOfBoundsException if index is less than 0 or greater than number of points.
     */
    @Override
    public void setPoint(int index, Point point) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("The index value is negative");
        } else if (index >= numberOfPoints) {
            throw new IndexOutOfBoundsException(
                    "The index value ("
                            + index
                            + ") is greater than the number of "
                            + "points ("
                            + numberOfPoints
                            + ")");
        }
        Objects.requireNonNull(point, "The point is null.");
        points[index] = point;
    }

    /**
     * Sort the points using the comparator.
     *
     * @param comparator
     */
    @Override
    public void sort(Comparator<Point> comparator) {
        // Arrays.sort(points, comparator);
        Arrays.sort(points, 0, numberOfPoints, comparator);
    }

    /**
     * Compare this object with the received.
     *
     * @param o the object to compare.
     * @return true if are the same object or has the same type and values. False in other case.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArrayFront that = (ArrayFront) o;

        if (numberOfPoints != that.numberOfPoints) return false;
        if (pointDimensions != that.pointDimensions) return false;
        if (!Arrays.equals(points, that.points)) return false;

        return true;
    }

    /**
     * Get the hash code.
     *
     * @return the hash code.
     */
    @Override
    public int hashCode() {
        int result = Arrays.hashCode(points);
        result = 31 * result + numberOfPoints;
        result = 31 * result + pointDimensions;
        return result;
    }

    /**
     * Get the string representation of this object.
     *
     * @return the string representation.
     */
    @Override
    public String toString() {
        return Arrays.toString(points);
    }

    /**
     * Get all points has a matrix. Each row is a point and each column is the value of the point.
     *
     * @return
     */
    @Override
    public double[][] getMatrix() {
        double[][] matrix = new double[getNumberOfPoints()][];
        for (int i = 0; i < getNumberOfPoints(); i++) {
            matrix[i] = points[i].getValues();
        }

        return matrix;
    }
}
