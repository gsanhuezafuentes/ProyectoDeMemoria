package view;

import java.util.Objects;

import controller.RunningDialogController;
import controller.utils.AlgorithmTask;
import javafx.concurrent.Worker.State;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.StageStyle;

public class RunningDialog extends Dialog<Void> {
	private RunningDialogController controller;
	private AlgorithmTask task;
	ProgressIndicator progressIndicator;
	private TextArea textArea;
	private ButtonType showChartButtonType;

	public RunningDialog(RunningDialogController controller, AlgorithmTask task) {
		this.controller = Objects.requireNonNull(controller);
		this.task = Objects.requireNonNull(task);
		configurateWindow();
		createContentLayout();
		AddButton();
		AddBindingAndListener();
	}

	/**
	 * Setting initial properties of dialog.
	 */
	private void configurateWindow() {
		initStyle(StageStyle.UTILITY);

		setTitle("Running Algorithm");
		setHeaderText("Please wait... ");

		this.progressIndicator = new ProgressIndicator();
		progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
		setGraphic(progressIndicator);
	}

	/**
	 * Add node elements to view
	 */
	private void createContentLayout() {
		//Create and add the text area to dialog
		this.textArea = new TextArea();
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(textArea, 0, 0);
		getDialogPane().setContent(expContent);
	}

	/**
	 * Add button to dialog
	 */
	private void AddButton() {
		//create a custom button
		showChartButtonType = new ButtonType("Show Chart", ButtonData.OTHER);
		// Add the three button, the custom, the cancel button and the close button.
		getDialogPane().getButtonTypes().addAll(showChartButtonType, ButtonType.CANCEL, ButtonType.CLOSE);
	}

	/**
	 * Add binding and listener to component of the view and the task properties associated with the view component.
	 */
	private void AddBindingAndListener() {
		// bind the textArea text with the value of message property of task
		textArea.textProperty().bind(this.task.messageProperty());

		// Add listener to detect when the task has finished and change the progressIndicator icon and the header text.
		task.runningProperty().addListener((prop, old, newv) -> {
			if (!newv) {
				setHeaderText("Execution Finished");
				progressIndicator.setProgress(1);
			}
		});
		
		Button closeButton = (Button) getDialogPane().lookupButton(ButtonType.CLOSE);
		closeButton.setOnAction(e -> controller.onCloseButtonClick());
		
		Button cancelButton = (Button) getDialogPane().lookupButton(ButtonType.CANCEL);
		cancelButton.disableProperty().bind(task.stateProperty().isNotEqualTo(State.RUNNING));
		cancelButton.setOnAction(e -> controller.onCancelButtonClick());
		
		Button showChaButton = (Button) getDialogPane().lookupButton(showChartButtonType);
		cancelButton.setOnAction(e -> controller.onShowChartButtonClick());

	}

}
