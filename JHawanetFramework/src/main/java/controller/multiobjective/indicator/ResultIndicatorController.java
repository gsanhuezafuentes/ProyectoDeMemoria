package controller.multiobjective.indicator;

import controller.util.ControllerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * This class show the result of indicators.
 */
public class ResultIndicatorController {
    private static Logger LOGGER = LoggerFactory.getLogger(ResultIndicatorController.class);
    private static final String htmlFolder = "html";

    @FXML
    private ListView<Path> pagesListView;

    @FXML
    private WebView webView;

    private final Pane root;

    /**
     * Constructor.
     *
     * @param tempFolderPath the temp folder.
     * @throws FileNotFoundException if tempFolderPath doesn't exist.
     * @throws IOException           if there is a error reading the file in tempFolderPath.
     * @throws NullPointerException  if tempFolderPath is null.
     */
    public ResultIndicatorController(String tempFolderPath) throws IOException {
        LOGGER.debug("Initializing ResultIndicatorController.");
        Objects.requireNonNull(tempFolderPath, "tempFolderPath is null.");

        this.root = ControllerUtils.loadFXML("/view/multiobjective/indicator/ResultIndicator.fxml", this);
        configureListView(tempFolderPath);
    }

    /**
     * Configure the list view with the elements of the folderPath.
     *
     * @param tempFolderPath
     * @throws IllegalArgumentException if tempFolderPath neither doesn't exist nor is a directory.
     */
    private void configureListView(String tempFolderPath) throws IOException {
        Path path = Paths.get(tempFolderPath, htmlFolder);
        LOGGER.debug(path.toString());
        if (!(Files.exists(path) && Files.isDirectory(path))) {
            throw new FileNotFoundException("The temporal folder " + path.toString() + " doesn't exist");
        }

        Files.walk(path).forEach((filePath) -> {
            if (Files.isRegularFile(filePath) && filePath.toString().endsWith(".html")) {
                this.pagesListView.getItems().add(filePath);
            }
        });

        this.pagesListView.setCellFactory((list) -> new ListCell<Path>() {
            @Override
            protected void updateItem(Path item, boolean empty) {
                super.updateItem(item, empty);

                if (!empty) {
                    setText(item.getFileName().toString().replaceFirst("[.][^.]+$", ""));
                } else {
                    setText(null);
                }
            }
        });

        this.pagesListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    String filePath = newValue.toUri().toString();
                    LOGGER.debug("Load {}", filePath);
                    this.webView.getEngine().load(filePath);
                });
        if (this.pagesListView.getItems().size() > 0)
            this.pagesListView.getSelectionModel().select(0);

    }

    /**
     * Return the node of this component.
     *
     * @return
     */
    public Pane getNode() {
        return this.root;
    }
}
