/*
 * Code taken and modify from https://github.com/jMetal/jMetal
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
package model.metaheuristic.experiment.component;

import exception.ApplicationException;
import model.metaheuristic.experiment.ExperimentComponent;
import model.metaheuristic.experiment.ExperimentSet;
import model.metaheuristic.experiment.util.FriedmanTest;
import model.metaheuristic.qualityindicator.impl.GenericIndicator;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;

/**
 * This class computes the Friedman test ranking and generates a Latex script that produces a table
 * per quality indicator containing the ranking and the result of the Holm post hoc test.
 *
 * <p>The results are a set of Latex files that are written in the directory {@link ExperimentSet
 * #getExperimentBaseDirectory()}/latex. Each file is called as FriedmanTestWitHolm[indicatorName].tex
 *
 * <p>The implementation is based on the one included in Keel: J. Alcalá-Fdez, L. Sánchez, S.
 * García, M.J. del Jesus, S. Ventura, J.M. Garrell, J. Otero, C. Romero, J. Bacardit, V.M. Rivas,
 * J.C. Fernández, F. Herrera. KEEL: A Software Tool to Assess Evolutionary Algorithms to Data
 * Mining Problems. Soft Computing 13:3 (2009) 307-318 Doi: 10.1007/s00500-008-0323-y
 *
 * @author Antonio J. Nebro
 * @author Javier Pérez Abad
 */
public class GenerateFriedmanHolmTestTables implements ExperimentComponent {
  private static final String DEFAULT_LATEX_DIRECTORY = "latex";
  private static final String INDICATOR_SUMMARY_CSV = "QualityIndicatorSummary.csv";
  // NAMES OF CSV COLUMNS
  private static final String ALGORITHM = "Algorithm";
  private static final String PROBLEM = "Problem";
  private static final String INDICATOR_NAME = "IndicatorName";

  private final ExperimentSet<?> experiment;

  private String latexDirectoryName;
  private int numberOfAlgorithms;
  private int numberOfProblems;

  public GenerateFriedmanHolmTestTables(ExperimentSet<?> experimentConfiguration) {
    this.experiment = experimentConfiguration;

    numberOfAlgorithms = experiment.getExperimentAlgorithms().size();
    numberOfProblems = experiment.getExperimentProblems().size();

    experiment.removeDuplicatedAlgorithms();
  }

  @Override
  public void run() throws IOException {
    latexDirectoryName = experiment.getExperimentBaseDirectory() + "/" + DEFAULT_LATEX_DIRECTORY;

    String path = experiment.getExperimentBaseDirectory();
    Table table = Table.read().csv(path + "/" + INDICATOR_SUMMARY_CSV);
    boolean minimizar = true;

    for (GenericIndicator indicator : experiment.getIndicatorList()) {
      Table tableFilteredByIndicator = filterTableByIndicator(table, indicator.getName());
      if (indicator.getName().equals("HV")) minimizar = false;
      Table results = computeFriedmanAndHolmTests(tableFilteredByIndicator, minimizar);
      createLatexFile(results, indicator);
    }
  }

  private Table computeFriedmanAndHolmTests(Table table, boolean minimizar) {
    StringColumn algorithms = getUniquesValuesOfStringColumn(table, ALGORITHM);
    StringColumn problems = getUniquesValuesOfStringColumn(table, PROBLEM);
    FriedmanTest test = new FriedmanTest(minimizar, table, algorithms, problems, "IndicatorValue");
    test.computeHolmTest();
    return test.getResults();
  }

  /**
   *
   * @param results the result.
   * @param indicator the indicator to write,
   * @throws ApplicationException if there is a error writing the data.
   */
  private void createLatexFile(Table results, GenericIndicator<?> indicator) {
    String outputFile = latexDirectoryName + "/FriedmanTestWithHolm" + indicator.getName() + ".tex";

    File latexOutput;
    latexOutput = new File(latexDirectoryName);
    if (!latexOutput.exists()) {
      latexOutput.mkdirs();
    }

    String fileContents = prepareFileOutputContents(results);

    try (DataOutputStream dataOutputStream =
        new DataOutputStream(new FileOutputStream(outputFile))) {
      dataOutputStream.writeBytes(fileContents);
    } catch (IOException e) {
      throw new ApplicationException("Error writing data ", e);
    }
  }

  private String prepareFileOutputContents(Table results) {
    String fileContents = writeLatexHeader();
    fileContents = printTableHeader(fileContents);
    fileContents = printTableLines(fileContents, results);
    fileContents = printTableTail(fileContents);
    fileContents =
        printDocumentFooter(fileContents, results.doubleColumn("Ranking").asDoubleArray());
    return fileContents;
  }

  private String writeLatexHeader() {

    return ("\\documentclass{article}\n"
        + "\\usepackage{graphicx}\n"
        + "\\title{Results}\n"
        + "\\author{}\n"
        + "\\date{\\today}\n"
        + "\\begin{document}\n"
        + "\\oddsidemargin 0in \\topmargin 0in"
        + "\\maketitle\n"
        + "\n"
        + "\\section{Tables}");
  }

  private String printTableHeader(String fileContents) {
    return fileContents
        + "\n"
        + ("\\begin{table}[!htp]\n"
            + "\\centering\n"
            + "\\begin{tabular}{c|c|c|c|c}\n"
            + "Algorithm&Ranking&p-value&Holm&Hypothesis\\\\\n\\hline");
  }

  private String printTableLines(String fileContents, Table results) {
    StringBuilder sb = new StringBuilder(fileContents);
    for (int i = 0; i < results.rowCount(); i++) {
      sb.append("\n");
      for (int j = 0; j < results.columnCount(); j++) {
        if (j == results.columnIndex("Algorithm")) {
          sb.append(results.stringColumn(0).get(i));
        } else if (j == results.columnIndex("Hypothesis")) {
          sb.append(results.stringColumn(j).get(i));
        } else if (j == results.columnIndex("p-value")) {
          DecimalFormat format = new DecimalFormat("0.###E0");
          sb.append(format.format(results.doubleColumn(j).get(i)));
        } else {
          DecimalFormat format = new DecimalFormat("##.###");
          sb.append(format.format(results.doubleColumn(j).get(i)));
        }
        if (j < results.columnCount() - 1) {
          sb.append(" & ");
        }
      }
      sb.append("\\\\");
    }
    return sb.toString();
  }

  private String printTableTail(String fileContents) {
    return fileContents
        + "\n"
        + "\\end{tabular}\n"
        + "\\caption{Average ranking of the algorithms}\n"
        + "\\end{table}";
  }

  private String printDocumentFooter(String fileContents, double[] averageRanking) {
    double term1 =
        (12 * (double) numberOfProblems) / (numberOfAlgorithms * (numberOfAlgorithms + 1));
    double term2 = numberOfAlgorithms * (numberOfAlgorithms + 1) * (numberOfAlgorithms + 1) / (4.0);
    double sum = 0;
    for (int i = 0; i < numberOfAlgorithms; i++) {
      sum += averageRanking[i] * averageRanking[i];
    }
    double friedman = (sum - term2) * term1;

    String output =
        fileContents
            + "\n"
            + "\n\nFriedman statistic considering reduction performance (distributed according to "
            + "chi-square with "
            + (numberOfAlgorithms - 1)
            + " degrees of freedom: "
            + friedman
            + ").\n\n";
    output = output + "\n" + "\\end{document}";

    return output;
  }

  private StringColumn getUniquesValuesOfStringColumn(Table table, String columnName) {
    return dropDuplicateRowsInColumn(table.stringColumn(columnName));
  }

  public StringColumn dropDuplicateRowsInColumn(StringColumn column) {
    LinkedList<String> result = new LinkedList<String>();
    for (int row = 0; row < column.size(); row++) {
      if (!result.contains(column.get(row))) {
        result.add(column.get(row));
      }
    }
    return StringColumn.create(column.name(), result);
  }

  private Table filterTableByIndicator(Table table, String indicator) {
    return table.where(table.stringColumn(INDICATOR_NAME).isEqualTo(indicator));
  }
}
