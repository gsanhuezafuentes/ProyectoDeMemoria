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

import model.metaheuristic.util.visualization.plot.PlotFront;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.ScatterPlot;

import java.util.Objects;

public class Plot2D implements PlotFront {
    private final double[][] matrix;
    private final String plotTitle;

    /**
     * @param matrix
     * @param title
     */
    public Plot2D(double[][] matrix, String title) {
        Objects.requireNonNull(matrix);
        if (!(matrix.length >= 1)){
            throw new IllegalArgumentException("The data matrix is empty");
        }
        if(!(matrix[0].length == 2)){
            throw new IllegalArgumentException("The data matrix does not have two columns");
        }

        this.matrix = matrix;
        this.plotTitle = title;
    }

    public Plot2D(double[][] matrix) {
        this(matrix, "Front");
    }

    @Override
    public void plot() {
        int numberOfRows = matrix.length;
        double[] f1 = new double[numberOfRows];
        double[] f2 = new double[numberOfRows];

        for (int i = 0; i < numberOfRows; i++) {
            f1[i] = matrix[i][0];
            f2[i] = matrix[i][1];
        }

        Table table =
                Table.create("table")
                        .addColumns(DoubleColumn.create("f1", f1), DoubleColumn.create("f2", f2));

        Plot.show(ScatterPlot.create(plotTitle, table, "f1", "f2"));
    }
}
