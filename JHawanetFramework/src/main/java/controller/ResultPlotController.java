package controller;

import application.ApplicationSetup;
import exception.ApplicationException;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import model.metaheuristic.solution.Solution;
import org.jetbrains.annotations.NotNull;
import view.utils.CustomDialogs;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ResultPlotController {

	private static final String[] KELLY_COLORS = {
			"#FFB300",    // Vivid Yellow
			"#803E75",    // Strong Purple
			"#FF6800",    // Vivid Orange
			"#A6BDD7",    // Very Light Blue
			"#C10020",    // Vivid Red
			"#CEA262",    // Grayish Yellow
			"#817066",    // Medium Gray
			"#007D34",    // Vivid Green
			"#F6768E",    // Strong Purplish Pink
			"#00538A",    // Strong Blue
			"#FF7A5C",    // Strong Yellowish Pink
			"#53377A",    // Strong Violet
			"#FF8E00",    // Vivid Orange Yellow
			"#B32851",    // Strong Purplish Red
			"#F4C800",    // Vivid Greenish Yellow
			"#7F180D",    // Strong Reddish Brown
			"#93AA00",    // Vivid Yellowish Green
			"#593315",    // Deep Yellowish Brown
			"#F13A13",    // Vivid Reddish Orange
			"#232C16",    // Dark Olive Green
	};

	@NotNull private final Pane root;
	@FXML
	@NotNull private Label executionStatusLabel;
	private final int numberOfObjectives;

	/*
	 * This linechart act as a scatterchart but let put lines to show pareto front. The line to other points are removed
	 * with css styles.
	 */
	@FXML
	private ScatterChart<Number, Number> resultsPlot;

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
	 * The text to show.
	 * @param text the text.
	 * @throws NullPointerException if text is null.
	 */
	public void updateExecutionStatusLabel(@NotNull String text){
		Objects.requireNonNull(text);
		this.executionStatusLabel.setText(text);
	}

	/**
	 * Configure the plot.
	 */
	private void configurePlot() {
		//this.resultsPlot.setVerticalGridLinesVisible(false);
		//this.resultsPlot.setHorizontalGridLinesVisible(false);
		if (this.numberOfObjectives == 1) {
			this.resultsPlot.getXAxis().setLabel("Number of generations");
			this.resultsPlot.getYAxis().setLabel("Objective");
		} else {
			this.resultsPlot.getXAxis().setLabel("Objective1");
			this.resultsPlot.getYAxis().setLabel("Objective2");
		}
	}


	/**
	 * Add data to plot. Only use when the problem is single objective.
	 * <p>
	 * The plot will be drawing using the generationNumber int the axis X and the objective in
	 * the axis Y.
	 * <p>
	 * For each repetition of the algorithm a different color to show the points will be used.
	 * 
	 * @param solution   the solution
	 * @param generationNumber number used in the x-axis. It indicates the number of generations in the current executed
	 *                         repetition of algorithm.
	 * @param repeatNumber This number indicate the repeat number of the algorithm.
	 * @throws NullPointerException if solution is null
	 */
	public void addData(Solution<?> solution, int generationNumber, double repeatNumber) {
		Objects.requireNonNull(solution);

		if (this.numberOfObjectives == 1) { // use objective / number of iteration
						// if there is not a serie added to this repeat number add one.
			// Get the data of chart
			ObservableList<XYChart.Series<Number, Number>> chartSeries = this.resultsPlot.getData();
			// if chart not have a series to the current repeat number of the algorithm so create one.
			if (chartSeries.size() <= repeatNumber){
				XYChart.Series<Number, Number> newSerie = new XYChart.Series<>();
				chartSeries.addAll(newSerie);
			}
			XYChart.Data<Number, Number> data = new XYChart.Data<>(generationNumber, solution.getObjective(0));

			// add the point to the last added series.
			chartSeries.get(chartSeries.size()-1).getData().add(data);

			// The data has to be resize after add this to the ScatterChart, because the scatterchart
			// Resize node size
			applyStyleToData(data);
		} else { // is 2. Use objective1 vs Objective2
			throw new IllegalStateException("You can't call this method when the problem is mono objective (number of objectives > 2).");
		}
	}

	/**
	 * Add data to plot. Only use if the number of objectives is 2.
	 * The plot was between objective1 and objective2.
	 *
	 * @param solutionList   the solution
	 * @throws NullPointerException if solutionList is null
	 * @throws IllegalStateException if call this method with the instance of this class is a single objective
	 */
	public void addData(List<? extends Solution<?>> solutionList) {
		Objects.requireNonNull(solutionList);

		if (this.numberOfObjectives == 1) {
			throw new IllegalStateException("You can't call this method when the problem is single objective.");
		} else { // is 2. Use objective1 vs Objective2
			XYChart.Series<Number, Number> serie = new XYChart.Series<>();
			serie.setName("Solution");

			solutionList.forEach(solution -> {
				XYChart.Data<Number, Number> data = new XYChart.Data<>(solution.getObjective(0), solution.getObjective(1));
				serie.getData().add(data);
			});

			this.resultsPlot.getData().add(serie);
			serie.getData().forEach(this::applyStyleToData);
		}
	}

	/**
	 * Apply the size and color to data.
	 * <p>
	 * This method has to be called after the data is added to chart.
	 *
	 * @param data the data of the chart (single point)
	 */
	public void applyStyleToData(XYChart.Data<Number, Number> data){
		// Resize node size
		StackPane stackPane =  (StackPane) data.getNode();
		stackPane.setPrefWidth(ApplicationSetup.getInstance().getChartPointSize());
		stackPane.setPrefHeight(ApplicationSetup.getInstance().getChartPointSize());
		int numberOfCssClass = stackPane.getStyleClass().size();
		// remove the last class to avoid that figure change default-color<i>
		stackPane.getStyleClass().remove(numberOfCssClass - 1);

//		System.out.println(stackPane);
		int size = this.resultsPlot.getData().size();
		// choose a color
		data.getNode().setStyle(String.format("-fx-background-color: %s", KELLY_COLORS[size % KELLY_COLORS.length]));
	}

	/**
	 * Return the node view associated to this controller
	 * @return the node
	 */
	public Node getNode(){
		return this.root;
	}

	/**
	 * Take a snapshot of chart.
	 */
	@FXML
	private void takeSnapshotOnAction(){
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
		fileChooser.setTitle("Save plot");

		File file = fileChooser.showSaveDialog(this.resultsPlot.getScene().getWindow());

		if (file == null){
			return;
		}
		WritableImage writableImage = pixelScaleAwareChartSnapshot(this.resultsPlot, 2);

		try {
			ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
		} catch (IOException exception) {
			CustomDialogs.showDialog("Error", "Error in the creation of the image",
					"The image of chart can't be saved", Alert.AlertType.ERROR);
		}

	}

	/**
	 * Take the snapshot
	 * @param chart the chart to take the snapshot
	 * @param pixelScale a scale number
	 * @return the image
	 */
	public static WritableImage pixelScaleAwareChartSnapshot(XYChart<Number, Number> chart, double pixelScale) {
		WritableImage writableImage = new WritableImage((int)Math.rint(pixelScale*chart.getWidth()), (int)Math.rint(pixelScale*chart.getHeight()));
		SnapshotParameters spa = new SnapshotParameters();
		spa.setTransform(Transform.scale(pixelScale, pixelScale));
		return chart.snapshot(spa, writableImage);
	}

//	Use to simulate the LineChart as ScatterChart
//	/**
//	 * Apply the style to only show line in pareto front
//	 * <p>
//	 * This method has to be called after the serie is added to chart.
//	 *
//	 * @param serie the serie to apply the style
//	 */
//	public void applyStyleToSerie(XYChart.Series<Number, Number> serie, boolean isParetoFront){
//		if (!isParetoFront){
//			// Resize node size
//			Path path =  (Path) serie.getNode();
//
//			// not show the line
//			path.setStyle("-fx-stroke: transparent; -fx-stroke-width: 0px;");
//		}
//	}

}
