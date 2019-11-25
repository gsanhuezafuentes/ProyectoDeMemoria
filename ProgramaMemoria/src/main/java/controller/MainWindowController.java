package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import epanet.core.EpanetException;
import exception.InputException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import model.epanet.element.Network;
import model.epanet.parser.InpParser;
import model.metaheuristic.algorithm.Algorithm;
import view.NetworkComponent;
import view.problems.ProblemRegistrar;
import view.utils.CustomDialogs;
import view.utils.ReflectionUtils;

public class MainWindowController implements Initializable{
	/**
	 * Is the section where the network is painted
	 */
	@FXML
	private NetworkComponent networkComponent;
	@FXML
	private BorderPane root;
	/**
	 * Is a pane that envolve networkComponent. Is used to resize the networkComponent automatically when screen size change
	 */
	@FXML
	private Pane canvasWrapper;
	
	/**
	 * Is the option of menu for the problem. It is filled using reflection through ProblemRegistrar.
	 */
	@FXML
	private Menu problemsMenu;
	
	private Window window;
	private File inpFile;
	private BooleanProperty isNetworkLoaded;
	
	public MainWindowController() {
		this.isNetworkLoaded = new SimpleBooleanProperty(false);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ProblemRegistrar.getInstance().register(this.problemsMenu, window, this::runAlgorithm);
		// disable problem menu until a network is loaded
//		this.problemsMenu.disableProperty().bind(isNetworkLoaded.not());
	}
	
	/**
	 * @return the ownerWindow
	 * 
	public Window getWindow() {
		return ownerWindow;
	}


	/**
	 * @param ownerWindow the ownerWindow to set
	 */
	public void setWindow(Window window) {
		this.window = window;
	}


	@FXML 
	private void openOnAction() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Abrir");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("INP file", "*.inp"));
		File file = fileChooser.showOpenDialog(this.window);
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
			CustomDialogs.showExceptionDialog("Error", "Error al cargar la red", "La red no ha podido ser cargada", e);
		}
		
		if (net != null) {
			networkComponent.drawNetwork(net);
			isNetworkLoaded.set(true);
		}
	}

	/**
	 * Run the algorithm
	 * @param algorithm algorithm to be executed
	 */
	private void runAlgorithm(Algorithm<?> algorithm) {
		try {
			algorithm.run();
		} catch (EpanetException e) {
			CustomDialogs.showExceptionDialog("Error", "Error al ejecutar el algoritmo", "A ocurrido un error durante la validación de las soluciones", e);
		}
	}
}
