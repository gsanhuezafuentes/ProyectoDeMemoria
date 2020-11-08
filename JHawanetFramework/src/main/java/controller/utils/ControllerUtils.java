package controller.utils;

import application.Main;
import exception.ViewLoadException;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ControllerUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerUtils.class);

    /**
     * Load the fxml view.
     * @param pathToResource the path to fxml resource.
     * @param controller the controller instance of the view (generally is the value of this).
     * @return the root pane of view.
     * @throws ViewLoadException if the fxml can't be loaded.
     */
    public static Pane loadFXML(String pathToResource, Object controller) {
        FXMLLoader fxmlLoader = new FXMLLoader(controller.getClass().getResource(pathToResource));
        LOGGER.debug("Load FXML {} from resources.", pathToResource);
        fxmlLoader.setController(controller);
        try {
            return fxmlLoader.load();
        } catch (IOException exception) {
            throw new ViewLoadException("The FXML " + pathToResource + "can't be loaded.",exception);
        }
    }
}
