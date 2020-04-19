package application;

import java.io.IOException;

import controller.MainWindowController;
import controller.utils.ProblemMenuConfiguration;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * This class create the principal windows and get his controller. When the
 * controller is loaded the metaheuristics problem are readed for
 * {@link ProblemMenuConfiguration}
 * 
 *
 */
public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainWindow.fxml"));
			BorderPane root = loader.load();
			MainWindowController controller = loader.getController();
			controller.setWindow(primaryStage);
			Scene scene = new Scene(root);
			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setMinWidth(800);
			primaryStage.setMinHeight(600);
			primaryStage.setScene(scene);
			primaryStage.sizeToScene();
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		launch(args);
	}
}
