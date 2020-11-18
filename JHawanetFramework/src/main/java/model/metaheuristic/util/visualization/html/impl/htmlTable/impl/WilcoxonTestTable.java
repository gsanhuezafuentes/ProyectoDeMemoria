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
package model.metaheuristic.util.visualization.html.impl.htmlTable.impl;

import model.metaheuristic.util.visualization.html.impl.htmlTable.HtmlTable;
import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.util.Arrays;

/**
 * This class computes the Wilcoxon test for every pair of algorithms.
 *
 * <p>A down arrow means the algorithm in the column outperforms the algorithm in the row.
 *
 * @author Javier Pérez Abad
 */
public class WilcoxonTestTable extends HtmlTable<WilcoxonTestTable.Difference[]> {

  private static final String[] INDICATORS_TO_MAXIMIZE = {"HV"};

  public WilcoxonTestTable(
      Table table,
      String indicator,
      StringColumn algorithms,
      StringColumn problems,
      String indicatorValueColumnName) {
    this.title = "Wilcoxon Test";
    this.headersColumn = algorithms.last(algorithms.size() - 1).asObjectArray();
    this.headersRow = algorithms.asObjectArray();
    this.data = new Difference[algorithms.size() - 1][algorithms.size() - 1][problems.size()];

    for (int row = 0; row < algorithms.size() - 1; row++) {
      Table tableAlgorithmA = filterTableBy(table, algorithms.name(), algorithms.get(row));
      for (int column = 1; column < algorithms.size(); column++) {
        Table tableAlgorithmB = filterTableBy(table, algorithms.name(), algorithms.get(column));
        for (int index = 0; index < problems.size(); index++) {
          if (row == column) {
            this.data[row][column][index] = null;
          } else {
            Table tableAlgorithmAByProblem =
                filterTableBy(tableAlgorithmA, problems.name(), problems.get(index));
            Table tableAlgorithmBByProblem =
                filterTableBy(tableAlgorithmB, problems.name(), problems.get(index));
            DoubleColumn resultsAlgorithmA =
                tableAlgorithmAByProblem.doubleColumn(indicatorValueColumnName);
            DoubleColumn resultsAlgorithmB =
                tableAlgorithmBByProblem.doubleColumn(indicatorValueColumnName);
            if (Arrays.asList(INDICATORS_TO_MAXIMIZE).contains(indicator)) {
              this.data[row][column - 1][index] = compare(resultsAlgorithmA, resultsAlgorithmB);
            } else {
              this.data[row][column - 1][index] = compare(resultsAlgorithmB, resultsAlgorithmA);
            }
          }
        }
      }
    }
  }

  public static double[] convertDoubleArray(Double[] array) {
    double[] result = new double[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = array[i];
    }
    return result;
  }

  private Table filterTableBy(Table table, String columnName, String value) {
    return table.where(table.stringColumn(columnName).isEqualTo(value));
  }

  public Difference compare(DoubleColumn columnA, DoubleColumn columnB) {
    double[] a = convertDoubleArray(columnA.asObjectArray());
    double[] b = convertDoubleArray(columnB.asObjectArray());
    WilcoxonSignedRankTest wilcoxon = new WilcoxonSignedRankTest();
    if (wilcoxon.wilcoxonSignedRankTest(a, b, false) < 0.05) {
      if (columnA.median() >= columnB.median()) {
        return Difference.BETTER;
      } else {
        return Difference.WORSE;
      }
    } else {
      return Difference.NO_DIFFERENCE;
    }
  }

  protected StringBuilder createRowOfData(int index) {
    StringBuilder html = new StringBuilder();
    for (Difference[] differences : data[index]) {
      html.append("<td>");
      html.append("<div class='horizontal'>");
      for (Difference difference : differences) {
        if (difference == Difference.BETTER) {
          html.append("<i class='fas fa-arrow-alt-circle-up'></i>");
        }
        if (difference == Difference.WORSE) {
          html.append("<i class='far fa-arrow-alt-circle-down'></i>");
        }
        if (difference == Difference.NO_DIFFERENCE) {
          html.append("<i class='fas fa-equals'></i>");
        }
      }
      html.append("</div></td>");
    }
    return html;
  }

  public String getCSS() {
    StringBuilder css = new StringBuilder(super.getCSS());

    css.append(".horizontal { display: flex; justify-content: space-evenly; align-items: center;}");
    css.append(".fas { flex-shrink: 0; margin: 1px; color: #6b6b6b} ");

    return css.toString();
  }

  public enum Difference {
    BETTER,
    WORSE,
    NO_DIFFERENCE
  }
}
