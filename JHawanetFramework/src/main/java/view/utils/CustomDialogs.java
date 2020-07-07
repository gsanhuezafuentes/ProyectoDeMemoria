package view.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Window;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This class contains static method that shows dialogs.
 *
 */
public class CustomDialogs {

	/**
	 * Show a dialog. The type of dialog can be configured used type parameter.
	 * 
	 * @param title the title
	 * @param headerText the header text
	 * @param contentText the content text
	 * @param type the type of alert
	 */
	public static void showDialog(String title, String headerText, String contentText, AlertType type) {
		showDialog(title, headerText, contentText, type, null);
	}

	/**
	 * Show a dialog. The type of dialog can be configured used type parameter.
	 * <p>
	 * If the owner window is configured as setOnTop so this dialog always is on the window.
	 * @param title the title
	 * @param headerText the header text
	 * @param contentText the content text
	 * @param type the type of alert
	 * @param owner the owner window of the dialog
	 */
	public static void showDialog(String title, String headerText, String contentText, AlertType type, Window owner) {
		Alert alert = new Alert(type);
		alert.initOwner(owner);
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		alert.show();

	}

	/**
	 * Show a dialog with the information of exception.
	 * <p>
	 * The dialog does not wait for user input.
	 * @param title the title
	 * @param headerText the header text
	 * @param contentText the content tex
	 * @param exception the exception throw
	 */
	public static void showExceptionDialog(String title, String headerText, String contentText, Throwable exception) {
		showExceptionDialog(title, headerText, contentText, exception, null);
	}

	/**
	 * Show a dialog with the information of exception.
	 * <p>
	 * If the owner window is configured as setOnTop so this dialog always is on the window.
	 * <p>
	 * The dialog does not wait for user input.
	 *
	 * @param title the title
	 * @param headerText the header text
	 * @param contentText the content tex
	 * @param exception the exception throw
	 * @param owner the owner window of the dialog
	 */
	public static void showExceptionDialog(String title, String headerText, String contentText, Throwable exception, Window owner) {
		showExceptionDialog(title, headerText, contentText, exception, owner,false);
	}

	/**
	 * Show a dialog with the information of exception.
	 * <p>
	 * If the owner window is configured as setOnTop so this dialog always is on the window.
	 *
	 * @param title the title
	 * @param headerText the header text
	 * @param contentText the content tex
	 * @param exception the exception throw
	 * @param owner the owner window of the dialog
	 * @param wait the modality of this window. if true the dialog wait for user response.
	 */
	public static void showExceptionDialog(String title, String headerText, String contentText, Throwable exception, Window owner, boolean wait) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label("The trace of exception was:");

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);
		if (wait){
			alert.showAndWait();
		}else{
			alert.show();
		}


	}
}
