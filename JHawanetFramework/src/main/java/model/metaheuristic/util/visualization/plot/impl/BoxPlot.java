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
package model.metaheuristic.util.visualization.plot.impl;

import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.traces.BoxTrace;

public class BoxPlot {
  public static void main(String[] args) {
    Object[] x = {
      "sheep",
      "cows",
      "fish",
      "tree sloths",
      "sheep",
      "cows",
      "fish",
      "tree sloths",
      "sheep",
      "cows",
      "fish",
      "tree sloths"
    };
    double[] y = {1, 4, 9, 16, 3, 6, 8, 8, 2, 4, 7, 11};

    BoxTrace trace = BoxTrace.builder(x, y).build();
    Plot.show(new Figure(trace));
  }
}
