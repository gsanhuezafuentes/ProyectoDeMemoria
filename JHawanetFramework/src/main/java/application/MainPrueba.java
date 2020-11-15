package application;

import controller.multiobjective.indicator.ConfigurationIndicatorWindowController;
import controller.util.ProblemMenuConfiguration;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.utils.CustomDialogs;

import java.io.IOException;


/**
 * This class create the principal windows and get his controller. When the
 * controller is loaded the metaheuristics problem are readed for
 * {@link ProblemMenuConfiguration}
 */
public class MainPrueba extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainPrueba.class);

    @Override
    public void start(Stage primaryStage) {
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
            LOGGER.error("Unexpected error. The application will be closed.", throwable);
            CustomDialogs.showExceptionDialog("Critical error", "Unhandled error"
                    , "Unhandled error. The application will be closed when close this dialog."
                    , throwable, primaryStage, true);
            Platform.exit();
        });
        ConfigurationIndicatorWindowController configurationIndicatorWindowController = new ConfigurationIndicatorWindowController();
        configurationIndicatorWindowController.showWindow(primaryStage);

    }

    public static void main(String[] args) throws IOException {
//		System.out.println(System.getProperty("java.class.path").replace(";","\n"));
        launch(args);
    }
}
