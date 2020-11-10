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
package model.metaheuristic.util.point.impl;

import model.metaheuristic.util.point.Point;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Class representing a point (i.e, an array of double values)
 *
 * @author Antonio J. Nebro
 */
public class ArrayPoint implements Point {
  protected double[] point;

  /**
   * Default constructor
   */
  public ArrayPoint() {
    point = null ;
  }

  /**
   * Constructor
   *
   * @param dimension Dimension of the point.
   */
  public ArrayPoint(int dimension) {
    point = new double[dimension];

    Arrays.fill(point, 0);
  }

  /**
   * Copy constructor
   *
   * @param point the point to copy.
   */
  public ArrayPoint(Point point) {
    Objects.requireNonNull(point);
    this.point = new double[point.getDimension()];

    for (int i = 0; i < point.getDimension(); i++) {
      this.point[i] = point.getValue(i);
    }
  }

  /**
   * Constructor from an array of double values
   *
   * @param point
   */
  public ArrayPoint(double[] point) {
    Objects.requireNonNull(point);

    this.point = new double[point.length];
    System.arraycopy(point, 0, this.point, 0, point.length);
  }

  /**
   * Constructor reading the values from a file
   * @param fileName
   */
  public ArrayPoint(String fileName) throws IOException {
   FileInputStream fis = new FileInputStream(fileName);
   InputStreamReader isr = new InputStreamReader(fis);
   try(BufferedReader br = new BufferedReader(isr)){

    List<Double> auxiliarPoint = new ArrayList<Double>();
    String aux = br.readLine();
    while (aux != null) {
      StringTokenizer st = new StringTokenizer(aux);

      while (st.hasMoreTokens()) {
        Double value = Double.valueOf(st.nextToken());
        auxiliarPoint.add(value);
      }
      aux = br.readLine();
    }

    point = new double[auxiliarPoint.size()] ;
    for (int i = 0; i < auxiliarPoint.size(); i++) {
      point[i] = auxiliarPoint.get(i) ;
    }

   }
  }


  @Override
  public int getDimension() {
    return point.length;
  }

  @Override
  public double[] getValues() {
    return point;
  }

  /**
   * Get the value.
   * @param index the index of value.
   * @return the value.
   * @throws IllegalArgumentException if index is less than 0 or more than point length.
   */
  @Override
  public double getValue(int index) {
    if (!((index >= 0) && (index < point.length))){
      throw new IndexOutOfBoundsException("Index value invalid: " + index +
              ". The point length is: " + point.length);
    }

    return point[index] ;
  }

  /**
   * Get the value.
   * @param index the index where save the value.
   * @param value the value.
   * @throws IndexOutOfBoundsException if index is less than 0 or more than point length.
   */
  @Override
  public void setValue(int index, double value) {
    if (!((index >= 0) && (index < point.length))){
      throw new IndexOutOfBoundsException("Index value invalid: " + index +
              ". The point length is: " + point.length);
    }

    point[index] = value ;
  }

  /**
   * Update all value poing with the values of received array.
   * @param point the array of new points.
   */
  @Override
  public void update(double[] point) {
    this.set(point);
  }

  /**
   * Set all point in this object with the values in received array.
   * @param point the array with the new values.
   * @throws IllegalArgumentException if the dimension of this object is different from the point length.
   */
  @Override
  public void set(double[] point) {
    if (!(point.length == this.point.length)){
      throw new IllegalArgumentException("The point to be update have a dimension of " + point.length + " "
              + "while the parameter point has a dimension of " + point.length);
    }

    for (int i = 0; i < point.length; i++) {
      this.point[i] = point[i] ;
    }
  }

  /**
   * Return a string representation of this object.
   * @return
   */
  @Override
  public String toString() {
    String result = "";
    for (double anObjectives_ : point) {
      result += anObjectives_ + " ";
    }

    return result;
  }

  /**
   * Compare this object with received object.
   * @param o the object to compare.
   * @return if the objects are of the same class and as the same value return true; otherwise return false.
   */
  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    ArrayPoint that = (ArrayPoint) o;

    if (!Arrays.equals(point, that.point))
      return false;

    return true;
  }

  /**
   * Return the hash code.
   * @return the hash code.
   */
  @Override public int hashCode() {
    return Arrays.hashCode(point);
  }
}
