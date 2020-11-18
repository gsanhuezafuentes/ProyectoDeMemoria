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

import model.metaheuristic.experiment.util.FriedmanTest;
import model.metaheuristic.util.visualization.html.impl.htmlTable.HtmlTable;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.text.DecimalFormat;

/**
 * This class transforms the results of FriedmanTest into a {@link HtmlTable}.
 *
 * <p>It creates 5 columns: Algorithm, Ranking, p-value, Holm and Hypothesis
 *
 * @author Javier Pérez Abad
 */
public class FriedmanTestTable extends HtmlTable<String> {

  public FriedmanTestTable(
      Table table, StringColumn algorithms, StringColumn problems, boolean minimizar) {
    this.title = "Friedman ranking and Holm test";
    FriedmanTest test = new FriedmanTest(minimizar, table, algorithms, problems, "IndicatorValue");
    test.computeHolmTest();
    Table ranking = test.getResults();
    this.headersColumn = ranking.columnNames().toArray(new String[0]);
    this.data = new String[algorithms.size()][ranking.columnCount()];
    for (int i = 0; i < algorithms.size(); i++) {
      for (int j = 0; j < ranking.columnCount(); j++) {
        if (j == ranking.columnIndex("Algorithm")) {
          this.data[i][j] = ranking.stringColumn(0).get(i);
        } else if (j == ranking.columnIndex("Hypothesis")) {
          this.data[i][j] = ranking.stringColumn(j).get(i);
        } else if (j == ranking.columnIndex("p-value")) {
          DecimalFormat format = new DecimalFormat("0.###E0");
          this.data[i][j] = format.format(ranking.doubleColumn(j).get(i));
        } else {
          DecimalFormat format = new DecimalFormat("##.###");
          this.data[i][j] = format.format(ranking.doubleColumn(j).get(i));
        }
      }
    }
  }
}
