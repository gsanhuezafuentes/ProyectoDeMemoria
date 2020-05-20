package controller;

import exception.ApplicationException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;
import model.metaheuristic.solution.Solution;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ResultPlotController {

	@NotNull private final Pane root;
	private final int numberOfObjectives;

	@FXML
	private ScatterChart<Number, Number> resultsPlot;

	@Nullable private XYChart.Series<Number, Number> defaultSerie;

	/**
	 * Constructor of the controller of ResultPlotWindow
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @throws IllegalArgumentException if the number of objectives is distinct to
	 *                                  one or two.
	 */
	public ResultPlotController(int numberOfObjectives) {
		if (numberOfObjectives != 1 && numberOfObjectives != 2) {
			throw new IllegalArgumentException(
					"The plot only support one until two objectives, but the number of objectives received is "
							+ numberOfObjectives);
		}
		this.numberOfObjectives = numberOfObjectives;
		this.root = loadFXML();
		configurePlot();
	}

	/**
	 * Configure the plot.
	 */
	private void configurePlot() {
		this.resultsPlot.setVerticalGridLinesVisible(false);
		this.resultsPlot.setHorizontalGridLinesVisible(false);
		if (this.numberOfObjectives == 1) {
			this.resultsPlot.getXAxis().setLabel("Number of iterations");
			this.resultsPlot.getYAxis().setLabel("Objective");
		} else {
			this.resultsPlot.getXAxis().setLabel("Objective1");
			this.resultsPlot.getYAxis().setLabel("Objective2");
		}
	}

	/**
	 * Load the FXML view associated to this controller.
	 * 
	 * @return the root pane.
	 * @throws ApplicationException if there is an error in load the .fxml.
	 */
	private Pane loadFXML() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ResultPlot.fxml"));
		fxmlLoader.setController(this);
		try {
			return fxmlLoader.load();
		} catch (IOException exception) {
			throw new ApplicationException(exception);
		}
	}

	/**
	 * Add data to plot. If the number of objectives is 1, so the plot was between
	 * objectives and the number of iteration. If the number of objectives is 2, so
	 * the plot was between objective1 and objective2.
	 * 
	 * @param solutionList    the solution list
	 * @param iterationNumber a number used has the x-axis when the solution is of a
	 *                        singleobjective problem. if solution is multiobjective
	 *                        this number is ignored.
	 * @throws NullPointerException if solutionList is null
	 */
	public void addData(List<? extends Solution<?>> solutionList, int iterationNumber) {
		Objects.requireNonNull(solutionList);

		if (this.numberOfObjectives == 1) { // use objective / number of iteration
			if (this.defaultSerie == null) {
				this.defaultSerie = new XYChart.Series<>();
				this.defaultSerie.setName("Solution");
				this.resultsPlot.getData().add(this.defaultSerie);
			}
			for (Solution<?> solution : solutionList) {
				this.defaultSerie.getData().add(new XYChart.Data<>(iterationNumber, solution.getObjective(0)));
			}
		} else { // is 2. Use objective1 vs Objective2
			XYChart.Series<Number, Number> series = new XYChart.Series<>();
			series.setName("Solution");

			for (Solution<?> solution : solutionList) {
				series.getData().add(new XYChart.Data<>(solution.getObjective(0), solution.getObjective(1)));
			}
			this.resultsPlot.getData().add(series);
		}
	}

	/**
	 * Return the node view associated to this controller
	 * @return the node
	 */
	public Node getNode(){
		return this.root;
	}
}
