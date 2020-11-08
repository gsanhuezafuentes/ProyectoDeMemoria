package application;

import controller.MainWindowController;
import controller.utils.ProblemMenuConfiguration;
import exception.ViewLoadException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.utils.CustomDialogs;

import java.io.IOException;


/**
 * This class create the principal windows and get his controller. When the
 * controller is loaded the metaheuristics problem are readed for
 * {@link ProblemMenuConfiguration}
 * 
 *
 */
public class Main extends Application {
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	@Override
	public void start(Stage primaryStage) {
		Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
			LOGGER.error("Unexpected error. The application will be closed.", throwable);
			CustomDialogs.showExceptionDialog("Critical error", "Unhandled error"
					, "Unhandled error. The application will be closed when close this dialog."
					, throwable, primaryStage, true);
			Platform.exit();
		});
		try {
			LOGGER.debug("Loading MainWindow.fxml.");
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainWindow.fxml"));
			BorderPane root = loader.load();
			MainWindowController controller = loader.getController();
			controller.setWindow(primaryStage);
			Scene scene = new Scene(root);

			primaryStage.setTitle("JHawanetFramework");
			primaryStage.setMinWidth(800);
			primaryStage.setMinHeight(600);
			primaryStage.setScene(scene);
			primaryStage.sizeToScene();

			LOGGER.info("Show MainWindow.");

			primaryStage.show();
		} catch (IOException e) {
			throw new ViewLoadException("The FXML MainWindow.fxml can't be loaded.", e);
		}
	}

	public static void main(String[] args) throws IOException {
//		System.out.println(System.getProperty("java.class.path").replace(";","\n"));
		launch(args);
	}
}
