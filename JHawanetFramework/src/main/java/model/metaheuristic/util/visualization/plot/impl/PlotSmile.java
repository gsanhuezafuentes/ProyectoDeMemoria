package model.metaheuristic.util.visualization.plot.impl;


import model.metaheuristic.util.visualization.plot.PlotFront;
import smile.plot.PlotCanvas;
import smile.plot.ScatterPlot;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;


public class PlotSmile implements PlotFront {
  private final double[][] matrix;
  private final String plotTitle;

  public PlotSmile(double[][] matrix) {
    this(matrix, "Front") ;
  }

  /**
   *
   * @param matrix
   * @param plotTitle
   * @throws NullPointerException if matrix is null.
   * @throws IllegalArgumentException if the matrix is empty.
   */
  public PlotSmile(double[][] matrix, String plotTitle) {
    Objects.requireNonNull(matrix);
    if (!(matrix.length >= 1)){
      throw new IllegalArgumentException("The data matrix is empty");
    }
    this.matrix = matrix;
    this.plotTitle = plotTitle ;
  }

  @SuppressWarnings("serial")
  class LocalPanel extends JPanel {
    public LocalPanel(){
      super(new GridLayout(1, 1)) ;

      double[][] data = matrix ;

      PlotCanvas canvas = ScatterPlot.plot(data);
      canvas.setTitle(plotTitle);
      add(canvas);

    }
  }

  @Override
  public void plot() {
    JFrame frame = new JFrame() ;
    frame.setSize(500, 500);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    frame.getContentPane().add(new LocalPanel());
    frame.setVisible(true);
  }
}
