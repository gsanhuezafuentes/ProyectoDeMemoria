package controller;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import exception.ApplicationException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.metaheuristic.solution.Solution;

public class ResultWindowController {
	private Pane root;

	@FXML
	private TableView<Solution<?>> resultTable;
	@FXML
	private Button saveButton;
	@FXML
	private Button saveAsINPButton;

	private List<? extends Solution<?>> solutionList;

	/**
	 * Constructor.
	 * 
	 * @param a list with solution to show
	 * @throws NullPointerException if solutions is null
	 */
	public ResultWindowController(List<? extends Solution<?>> solutions) {
		Objects.requireNonNull(solutions);
		this.root = loadFXML();
		this.solutionList = solutions;
		configureResultTable();
		addBinding();
	}

	private void configureResultTable() {
		if (!this.solutionList.isEmpty()) {
			this.resultTable.getItems().addAll(this.solutionList);
			this.resultTable.getItems().addAll(this.solutionList);
			this.resultTable.getItems().addAll(this.solutionList);
			this.resultTable.getItems().addAll(this.solutionList);
			
			this.resultTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			
			int numberOfObjectives = this.solutionList.get(0).getNumberOfObjectives();
			int numberOfDecisionVariables = this.solutionList.get(0).getNumberOfDecisionVariables();
			
			// add column for the objetives
			for (int i = 0; i < numberOfObjectives; i++) {
				final int index = i;
				TableColumn<Solution<?>, String> column = new TableColumn<Solution<?>, String>("Objective " + (i + 1));
				resultTable.getColumns().add(column);
				column.setCellValueFactory(
						(CellDataFeatures<Solution<?>, String> solutionData) -> new ReadOnlyObjectWrapper(
								solutionData.getValue().getObjective(index)));
			}

			// add column for the decision variables
			for (int i = 0; i < numberOfDecisionVariables; i++) {
				final int index = i;
				TableColumn<Solution<?>, String> column = new TableColumn<Solution<?>, String>("X" + (i + 1));
				resultTable.getColumns().add(column);
				column.setCellValueFactory(
						(CellDataFeatures<Solution<?>, String> solutionData) -> new ReadOnlyObjectWrapper(
								solutionData.getValue().getVariable(index)));
			}
		}
	}
	
	private void addBinding() {
		saveAsINPButton.disableProperty().bind(this.resultTable.getSelectionModel().selectedItemProperty().isNull());
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
	 * Event handler when save as inp button is pressed.
	 * 
	 * @param event the event
	 */
	@SuppressWarnings("unused") // is configure from fxml file
	@FXML
	private void onSaveAsINPButtonClick(ActionEvent event) {
		
		for (Solution<?> solution : this.resultTable.getSelectionModel().getSelectedItems()) {
			System.out.println(solution);
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
		System.out.println(event);
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
