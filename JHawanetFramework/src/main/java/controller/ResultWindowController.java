package controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import exception.ApplicationException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.epanet.element.Network;
import model.epanet.io.OutputInpWriter;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.utils.io.SolutionListOutput;
import view.utils.CustomDialogs;

/**
 * This class is the controller of result window. <br>
 * <br>
 * From this class is saved as inp the solution and saved the element has FUN
 * and VAR.
 * 
 * @author gsanh
 *
 */
public class ResultWindowController {
	private Pane root;

	@FXML
	private TableView<Solution<?>> resultTable;
	@FXML
	private Button saveButton;
	@FXML
	private Button saveAsINPButton;

	private List<? extends Solution<?>> solutionList;

	private Problem<?> problem;

	private Network network;

	/**
	 * Constructor.
	 * 
	 * @param problem   the problem that was solve by the algorithm.
	 * @param solutions a list with solution to show
	 * @param network the network object
	 * @throws NullPointerException if solutions is null or problem is null or
	 *                              network is null.
	 */
	public ResultWindowController(List<? extends Solution<?>> solutions, Problem<?> problem, Network network) {
		Objects.requireNonNull(solutions);
		Objects.requireNonNull(problem);
		Objects.requireNonNull(network);

		this.root = loadFXML();
		this.solutionList = solutions;
		this.problem = problem;
		this.network = network;

		configureResultTable();
		addBinding();
	}

	/**
	 * Load the FXML view associated to this controller.
	 * 
	 * @return the root pane.
	 * @throws ApplicationException if there is an error in load the .fxml.
	 */
	private Pane loadFXML() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ResultWindow.fxml"));
		fxmlLoader.setController(this);
		try {
			return fxmlLoader.load();
		} catch (IOException exception) {
			throw new ApplicationException(exception);
		}
	}

	/**
	 * Configure the table view and the element that will be showned.
	 */
	private void configureResultTable() {
		if (!this.solutionList.isEmpty()) {
			this.resultTable.getItems().addAll(this.solutionList);

			int numberOfObjectives = this.solutionList.get(0).getNumberOfObjectives();
			int numberOfDecisionVariables = this.solutionList.get(0).getNumberOfVariables();

			// add column for the objetives
			for (int i = 0; i < numberOfObjectives; i++) {
				final int index = i;
				TableColumn<Solution<?>, String> column = new TableColumn<Solution<?>, String>("Objective " + (i + 1));
				resultTable.getColumns().add(column);
				// tell from where get the value for the column
				column.setCellValueFactory(
						(CellDataFeatures<Solution<?>, String> solutionData) -> new ReadOnlyObjectWrapper<String>(
								Double.toString(solutionData.getValue().getObjective(index))));
			}

			// add column for the decision variables
			for (int i = 0; i < numberOfDecisionVariables; i++) {
				final int index = i;
				TableColumn<Solution<?>, String> column = new TableColumn<Solution<?>, String>("X" + (i + 1));
				resultTable.getColumns().add(column);
				// tell from where get the value for the column
				column.setCellValueFactory(
						(CellDataFeatures<Solution<?>, String> solutionData) -> new ReadOnlyObjectWrapper<String>(
								solutionData.getValue().getVariableAsString(index)));
			}
		}
	}

	/**
	 * Method to add binding to nodes
	 */
	private void addBinding() {
		saveAsINPButton.disableProperty().bind(this.resultTable.getSelectionModel().selectedItemProperty().isNull());
	}

	/**
	 * Event handler when save as inp button is pressed.
	 * 
	 * @param event the event
	 */
	@SuppressWarnings("unused") // This method is configure from fxml file
	@FXML
	private void onSaveAsINPButtonClick(ActionEvent event) {
		Solution<?> solution = this.resultTable.getSelectionModel().getSelectedItem();

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save solution as INP");

		File file = fileChooser.showSaveDialog(this.saveButton.getScene().getWindow());
		if (file != null) {
			Network netCopy = this.problem.applySolutionToNetwork(this.network.copy(), solution);
			if (netCopy != null) {
				OutputInpWriter outputInpWriter = new OutputInpWriter();
				try {
					outputInpWriter.write(netCopy, file.getAbsolutePath());
				} catch (IOException e) {
					CustomDialogs.showExceptionDialog("Error", "Error in the creation of the inp file",
							"The file can't be created", e);
				}
			} else {
				CustomDialogs.showDialog("Unsupported Operation",
						"The save as inp operation is not supported by this problem",
						"The method applySolutionToNetwork of " + problem.getName()
								+ " has returned null. To support this operation you need return a Network",
						AlertType.WARNING);
			}

		}
	}

	/**
	 * Event handler when save as inp button is pressed.
	 * 
	 * @param event the event
	 */
	@SuppressWarnings("unused") // is configure from fxml file
	@FXML
	private void onSaveButtonClick(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save table");

		File file = fileChooser.showSaveDialog(this.saveButton.getScene().getWindow());
		if (file != null) {
			SolutionListOutput output = new SolutionListOutput(this.solutionList)
					.setFunFileName(Paths.get(file.getParent(), "FUN_" + file.getName()).toString())
					.setVarFileName(Paths.get(file.getParent(), "VAR_" + file.getName()).toString());
			try {
				output.write();
			} catch (IOException e) {
				CustomDialogs.showExceptionDialog("Error", "Error in the creation of fun/var file",
						"The file can't be created", e);
			}
		}
	}

	/**
	 * Show the associated window
	 */
	public void showAssociatedWindow() {
		Stage stage = new Stage();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.sizeToScene();
		stage.show();
	}
}
