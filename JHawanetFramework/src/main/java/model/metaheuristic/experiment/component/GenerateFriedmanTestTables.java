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
import model.metaheuristic.qualityindicator.impl.GenericIndicator;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.util.*;

/**
 * This class computes the Friedman test ranking and generates a Latex script that produces a table per
 * quality indicator containing the ranking
 * <p>
 * The results are a set of Latex files that are written in the directory
 * {@link ExperimentSet #getExperimentBaseDirectory()}/latex. Each file is called as
 * FriedmanTest[indicatorName].tex
 * <p>
 * The implementation is based on the one included in Keel:
 * J. Alcalá-Fdez, L. Sánchez, S. García, M.J. del Jesus, S. Ventura, J.M. Garrell, J. Otero, C. Romero, J. Bacardit,
 * V.M. Rivas, J.C. Fernández, F. Herrera.
 * KEEL: A Software Tool to Assess Evolutionary Algorithms to Data Mining Problems. Soft Computing 13:3 (2009) 307-318
 * Doi: 10.1007/s00500-008-0323-y
 *
 * @author Antonio J. Nebro &lt;antonio@lcc.uma.es&gt;
 */

public class GenerateFriedmanTestTables implements ExperimentComponent {
    private static final String DEFAULT_LATEX_DIRECTORY = "latex";

    private final ExperimentSet<?> experimentSet;

    private String latexDirectoryName;
    private int numberOfAlgorithms;
    private int numberOfProblems;

    public GenerateFriedmanTestTables(ExperimentSet<?> experimentConfiguration) {
        this.experimentSet = experimentConfiguration;

        numberOfAlgorithms = experimentSet.getExperimentAlgorithms().size();
        numberOfProblems = experimentSet.getExperimentProblems().size();

        experimentSet.removeDuplicatedAlgorithms();
    }

    @Override
    public void run() throws IOException {
        latexDirectoryName = experimentSet.getExperimentBaseDirectory() + "/" + DEFAULT_LATEX_DIRECTORY;

        for (GenericIndicator indicator : experimentSet.getIndicatorList()) {
            Vector<Vector<Double>> data = readData(indicator);
            double[] averageRanking = computeAverageRanking(data);
            String fileContents = prepareFileOutputContents(averageRanking);
            writeLatexFile(indicator, fileContents);
        }
    }

    private Vector<Vector<Double>> readData(GenericIndicator<?> indicator) {
        Vector<Vector<Double>> data = new Vector<Vector<Double>>();

        for (int algorithm = 0; algorithm < experimentSet.getExperimentAlgorithms().size(); algorithm++) {
            String algorithmName = experimentSet.getExperimentAlgorithms().get(algorithm).getAlgorithmTag();

            data.add(new Vector<Double>());
            String algorithmPath = experimentSet.getExperimentBaseDirectory() + "/data/"
                    + algorithmName + "/";

            for (int problem = 0; problem < experimentSet.getExperimentProblems().size(); problem++) {
                String path = algorithmPath + experimentSet.getExperimentProblems().get(problem).getTag() +
                        "/" + indicator.getName();

                readDataFromFile(path, data, algorithm);
            }
        }

        return data;
    }

    /**
     * Read the data from file.
     *
     * @param path           the path to indicator file.
     * @param data           the vector where save result.
     * @param algorithmIndex the index of algorithm.
     * @throws ApplicationException if the file can't be found or there is a error reading the file.
     */
    private void readDataFromFile(String path, Vector<Vector<Double>> data, int algorithmIndex) {
        String string = "";

        try (FileInputStream fis = new FileInputStream(path)) {

            byte[] bytes = new byte[4096];
            int readBytes = 0;

            while (readBytes != -1) {
                readBytes = fis.read(bytes);

                if (readBytes != -1) {
                    string += new String(bytes, 0, readBytes);
                }
            }

        } catch (IOException e) {
            throw new ApplicationException("Error reading the file " + path, e);
        }

        StringTokenizer lines = new StringTokenizer(string, "\n\r");

        double valor = 0.0;
        int n = 0;

        while (lines.hasMoreTokens()) {
            valor = valor + Double.parseDouble(lines.nextToken());
            n++;
        }
        if (n != 0) {
            (data.elementAt(algorithmIndex)).add(valor / n);
        } else {
            (data.elementAt(algorithmIndex)).add(valor);
        }
    }

    private double[] computeAverageRanking(Vector<Vector<Double>> data) {
        /*Compute the average performance per algorithm for each data set*/
        double[][] mean = new double[numberOfProblems][numberOfAlgorithms];

        for (int j = 0; j < numberOfAlgorithms; j++) {
            for (int i = 0; i < numberOfProblems; i++) {
                mean[i][j] = data.elementAt(j).elementAt(i);
            }
        }

        /*We use the Pair class to compute and order rankings*/
        List<List<Pair<Integer, Double>>> order = new ArrayList<List<Pair<Integer, Double>>>(numberOfProblems);

        for (int i = 0; i < numberOfProblems; i++) {
            order.add(new ArrayList<>(numberOfAlgorithms));
            for (int j = 0; j < numberOfAlgorithms; j++) {
                order.get(i).add(new ImmutablePair<>(j, mean[i][j]));
            }
            order.get(i).sort(Comparator.comparingDouble(pair -> Math.abs(pair.getValue())));
        }

        /*building of the rankings table per algorithms and data sets*/
        List<List<MutablePair<Double, Double>>> rank = new ArrayList<List<MutablePair<Double, Double>>>(numberOfProblems);

        int position = 0;
        for (int i = 0; i < numberOfProblems; i++) {
            rank.add(new ArrayList<MutablePair<Double, Double>>(numberOfAlgorithms));
            for (int j = 0; j < numberOfAlgorithms; j++) {
                boolean found = false;
                for (int k = 0; k < numberOfAlgorithms && !found; k++) {
                    if (order.get(i).get(k).getKey() == j) {
                        found = true;
                        position = k + 1;
                    }
                }
                rank.get(i).add(new MutablePair<Double, Double>((double) position, order.get(i).get(position - 1).getValue()));
            }
        }

        /*In the case of having the same performance, the rankings are equal*/
        for (int i = 0; i < numberOfProblems; i++) {
            boolean[] hasBeenVisited = new boolean[numberOfAlgorithms];
            Vector<Integer> pendingToVisit = new Vector<Integer>();

            Arrays.fill(hasBeenVisited, false);
            for (int j = 0; j < numberOfAlgorithms; j++) {
                pendingToVisit.removeAllElements();
                double sum = rank.get(i).get(j).getKey();
                hasBeenVisited[j] = true;
                int ig = 1;
                for (int k = j + 1; k < numberOfAlgorithms; k++) {
                    if (rank.get(i).get(j).getValue().equals(rank.get(i).get(k).getValue()) && !hasBeenVisited[k]) {
                        sum += rank.get(i).get(k).getKey();
                        ig++;
                        pendingToVisit.add(k);
                        hasBeenVisited[k] = true;
                    }
                }
                sum /= (double) ig;
                rank.get(i).get(j).setLeft(sum);
                for (int k = 0; k < pendingToVisit.size(); k++) {
                    rank.get(i).get(pendingToVisit.elementAt(k)).setLeft(sum);
                }
            }
        }

        /*compute the average ranking for each algorithm*/
        double[] averageRanking = new double[numberOfAlgorithms];
        for (int i = 0; i < numberOfAlgorithms; i++) {
            averageRanking[i] = 0;
            for (int j = 0; j < numberOfProblems; j++) {
                averageRanking[i] += rank.get(j).get(i).getKey() / ((double) numberOfProblems);
            }
        }

        return averageRanking;
    }

    private String prepareFileOutputContents(double[] averageRanking) {
        String fileContents = writeLatexHeader();
        fileContents = printTableHeader(fileContents);
        fileContents = printTableLines(fileContents, averageRanking);
        fileContents = printTableTail(fileContents);
        fileContents = printDocumentFooter(fileContents, averageRanking);

        return fileContents;
    }

    /**
     * Write the file contents in the output file
     *
     * @param indicator
     * @param fileContents
     * @throws ApplicationException if there is a error writing the data.
     */
    private void writeLatexFile(GenericIndicator<?> indicator, String fileContents) {
        String outputFile = latexDirectoryName + "/FriedmanTest" + indicator.getName() + ".tex";

        File latexOutput;
        latexOutput = new File(latexDirectoryName);
        if (!latexOutput.exists()) {
            latexOutput.mkdirs();
        }

        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(outputFile))) {
            dataOutputStream.writeBytes(fileContents);
        } catch (IOException e) {
            throw new ApplicationException("Error writing data ", e);
        }
    }

    private String writeLatexHeader() {

        return ("\\documentclass{article}\n" +
                "\\usepackage{graphicx}\n" +
                "\\title{Results}\n" +
                "\\author{}\n" +
                "\\date{\\today}\n" +
                "\\begin{document}\n" +
                "\\oddsidemargin 0in \\topmargin 0in" +
                "\\maketitle\n" +
                "\\\n" +
                "\\section{Tables}");
    }

    private String printTableLines(String fileContents, double[] averageRanking) {
        String output = fileContents;
        for (int i = 0; i < experimentSet.getExperimentAlgorithms().size(); i++) {
            output += "\n" + experimentSet.getExperimentAlgorithms().get(i).getAlgorithmTag() + "&" + averageRanking[i] + "\\\\";
        }

        return output;
    }

    private String printTableTail(String fileContents) {
        return fileContents + "\n" +
                "\\end{tabular}\n" +
                "\\end{table}";
    }

    private String printTableHeader(String fileContents) {
        return fileContents + "\n" + ("\\begin{table}[!htp]\n" +
                "\\centering\n" +
                "\\caption{Average ranking of the algorithms}\n" +
                "\\begin{tabular}{c|c}\n" +
                "Algorithm&Ranking\\\\\n\\hline");
    }

    private String printDocumentFooter(String fileContents, double[] averageRanking) {
        double term1 = (12 * (double) numberOfProblems) / (numberOfAlgorithms * (numberOfAlgorithms + 1));
        double term2 = numberOfAlgorithms * (numberOfAlgorithms + 1) * (numberOfAlgorithms + 1) / (4.0);
        double sum = 0;
        for (int i = 0; i < numberOfAlgorithms; i++) {
            sum += averageRanking[i] * averageRanking[i];
        }
        double friedman = (sum - term2) * term1;

        String output = fileContents + "\n" + "\n\nFriedman statistic considering reduction performance (distributed according to " +
                "chi-square with " + (numberOfAlgorithms - 1) + " degrees of freedom: " + friedman + ").\n\n";
        output = output + "\n" + "\\end{document}";

        return output;
    }
}


