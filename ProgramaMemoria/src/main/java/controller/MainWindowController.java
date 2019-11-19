package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import exception.InputException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import model.epanet.element.Network;
import model.epanet.parser.InpParser;
import view.NetworkComponent;
import view.utils.CustomDialog;

public class MainWindowController implements Initializable{
	@FXML
	private NetworkComponent networkComponent;
	@FXML
	private BorderPane root;
	@FXML
	private Pane canvasWrapper;
	
	private Window ownerWindow;
	private File inpFile;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	/**
	 * @return the ownerWindow
	 */
	public Window getOwnerWindow() {
		return ownerWindow;
	}


	/**
	 * @param ownerWindow the ownerWindow to set
	 */
	public void setOwnerWindow(Window ownerWindow) {
		this.ownerWindow = ownerWindow;
	}


	@FXML 
	private void openOnAction() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Abrir");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("INP file", "*.inp"));
		File file = fileChooser.showOpenDialog(this.ownerWindow);
		if (file != null) {
			this.inpFile = file;
			loadNetwork(this.inpFile);

			System.out.println("RAIZ " + canvasWrapper.getWidth() + " " + canvasWrapper.getHeight());
		}
	}

	/**
	 * Load the network
	 * @param file the file
	 * @return network or null if the network can't be loaded
	 */
	private void loadNetwork(File file) {
		Network net = new Network();
		InpParser parse = new InpParser();
		
		try {
			parse.parse(net, file.getAbsolutePath());
		} catch (IOException | InputException e) {
			net = null;
			CustomDialog.showExceptionDialog("A ocurrido un error", "Error al cargar la red", "La red no ha podido ser cargada", e);
		}
		
		if (net != null) {
			networkComponent.drawNetwork(net);
		}
	}

}
