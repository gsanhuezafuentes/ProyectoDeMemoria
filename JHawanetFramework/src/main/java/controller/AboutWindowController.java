package controller;

import exception.ApplicationException;
import exception.ViewLoadException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import static controller.utils.ControllerUtils.loadFXML;

public class AboutWindowController {
    private @Nullable Stage window;
    private @NotNull Parent root;

    public AboutWindowController(){
        this.root = loadFXML("/view/AboutWindow.fxml", this);
    }

    /**
     * Show the associated window
     */
    public void showWindow() {
        Stage stage = new Stage();
        stage.setScene(new Scene(this.root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("About");
        this.window = stage;

        stage.setMinWidth(this.root.minWidth(-1));
        stage.setMinHeight(this.root.minHeight(-1));
        stage.setMaxWidth(this.root.maxWidth(-1));
        stage.setMaxHeight(this.root.maxHeight(-1));

        stage.show();
    }
}
