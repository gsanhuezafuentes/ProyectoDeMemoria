package model.metaheuristic.util.visualization.plot.impl;

import model.metaheuristic.util.visualization.plot.PlotFront;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.Scatter3DPlot;

import java.util.Objects;

public class Plot3D implements PlotFront {
    private final double[][] matrix;
    private final String plotTitle;

    /**
     *
     * @param matrix
     * @param title
     * @throws NullPointerException if matrix is null.
     * @throws IllegalArgumentException if the matrix is empty or the matrix doesn't have three columns.
     */
    public Plot3D(double[][] matrix, String title) {
        Objects.requireNonNull(matrix);
        if (!(matrix.length >= 1)){
            throw new IllegalArgumentException("The data matrix is empty");
        }
        if (!(matrix[0].length == 3)){
            throw new IllegalArgumentException("The data matrix does not have three columns");
        }
        this.plotTitle = title;
        this.matrix = matrix;
    }

    public Plot3D(double[][] matrix) {
        this(matrix, "Front");
    }

    @Override
    public void plot() {
        int numberOfRows = matrix.length;
        double[] f1 = new double[numberOfRows];
        double[] f2 = new double[numberOfRows];
        double[] f3 = new double[numberOfRows];

        for (int i = 0; i < numberOfRows; i++) {
            f1[i] = matrix[i][0];
            f2[i] = matrix[i][1];
            f3[i] = matrix[i][2];
        }

        Table table =
                Table.create("table")
                        .addColumns(DoubleColumn.create("f1", f1), DoubleColumn.create("f2", f2), DoubleColumn.create("f3", f3));

        table.summary();

        Plot.show(Scatter3DPlot.create(plotTitle, table, "f1", "f2", "f3"));
    }
}
