package controller.multiobjective.indicator;

import controller.util.ControllerUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.utils.CustomDialogs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
/**
 * This class show the result of indicators.
 */
public class ResultIndicatorController {
    private static Logger LOGGER = LoggerFactory.getLogger(ResultIndicatorController.class);
    private static final String htmlFolder = "html";
    private final String tempFolderPath;

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
        this.tempFolderPath = tempFolderPath;
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

        // Change the cell type of listview
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

        // Change page to select other element
        this.pagesListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    String filePath = newValue.toUri().toString();
                    LOGGER.debug("Load {}", filePath);
                    this.webView.getEngine().load(filePath);
                });

        // Prevent change page
        this.webView.getEngine().locationProperty().addListener((observableValue, oldLoc, newLoc) -> {
            if (!newLoc.contains(path.toUri().toString())) {
                Platform.runLater(() -> {
                    webView.getEngine().load(oldLoc);
                });
            }
        });

        if (this.pagesListView.getItems().size() > 0)
            this.pagesListView.getSelectionModel().select(0);

    }

    /**
     * Copy the temp folder.
     */
    public void saveResult(){
        DirectoryChooser targetDirectory = new DirectoryChooser();
        targetDirectory.setTitle("Choose the directory");
        File file = targetDirectory.showDialog(root.getScene().getWindow());

        Path path = Paths.get(this.tempFolderPath);
        try {
            copyFolder(path, file.toPath(), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.COPY_ATTRIBUTES, LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {
            LOGGER.error("Error copying of temp {} file to target directory {}", this.tempFolderPath, targetDirectory);
            CustomDialogs.showExceptionDialog("Error", "Error saving the result of indicators",
                    "Error writing the folder", e);
        }
    }

    private void copyFolder(Path source, Path target, CopyOption... options)
            throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                Files.createDirectories(target.resolve(source.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                Files.copy(file, target.resolve(source.relativize(file)), options);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Return the node of this component.
     *
     * @return the graphic node.
     */
    public Pane getNode() {
        return this.root;
    }
}
