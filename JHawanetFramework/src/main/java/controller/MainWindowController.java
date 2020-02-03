package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import controller.problems.Registrable;
import controller.utils.ProblemMenuConfiguration;
import exception.InputException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Callback;
import model.epanet.element.Network;
import model.epanet.io.InpParser;
import model.metaheuristic.algorithm.Algorithm;
import model.metaheuristic.problem.Problem;
import view.ElementViewer;
import view.NetworkComponent;
import view.utils.CustomDialogs;

/**
 * The controller of the main window view.
 * 
 * From this class is opened the RunningWindow when the problem is selected in
 * menu item.
 *
 */
public class MainWindowController implements Initializable {
	@FXML
	private BorderPane root;

	/**
	 * The SplitPane where the NetworkComponent and the ElementViewer are
	 */
	@FXML
	private SplitPane splitPane;

	/**
	 * Is a pane that envolve networkComponent. Is used to resize the
	 * networkComponent automatically when screen size change
	 */
	@FXML
	private Pane canvasWrapper;

	/**
	 * Is the section where the network is painted
	 */
	@FXML
	private NetworkComponent networkComponent;

	/**
	 * It is a pane that show the element of network
	 */
	@FXML
	private ElementViewer elementViewer;

	/**
	 * Is the option of menu for the problem. It is filled using reflection through
	 * ProblemRegistrar.
	 */
	@FXML
	private Menu problemsMenu;

	private Window window;
	private File inpFile;
	private BooleanProperty isNetworkLoaded;
	private Network network;

	public MainWindowController() {
		this.isNetworkLoaded = new SimpleBooleanProperty(false);

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Register the menu item problem to problem menu and add the listener to it
		// menuitem to show a interface,
		ProblemMenuConfiguration problemRegistrar = new ProblemMenuConfiguration();
		problemRegistrar.addMonoObjectiveProblems(this.problemsMenu, this::runAlgorithm);
				
		// disable problem menu until a network is loaded
		this.problemsMenu.disableProperty().bind(isNetworkLoaded.not());
		
		networkComponent.selectedProperty().bindBidirectional(elementViewer.selectedProperty());
	}

	/**
	 * @param window the windows
	 * 
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

		}
	}

	/**
	 * Load the network
	 * 
	 * @param file the file
	 * @return network or null if the network can't be loaded
	 */
	private void loadNetwork(File file) {
		this.network = null;
		InpParser parse = new InpParser();

		try {
			this.network = new Network();
			parse.parse(this.network, file.getAbsolutePath());
			// If the network was loaded so show it
			if (!this.network.isEmpty()) {
				networkComponent.drawNetwork(this.network);
				elementViewer.setNetwork(network);
				isNetworkLoaded.set(true);
			}
		} catch (IOException | InputException e) {
			CustomDialogs.showExceptionDialog("Error", "Error loading the network", "The network can't be loaded", e);
		}

	}

	/**
	 * Run the algorithm.<br>
	 * <br>
	 * 
	 * <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * This method is called by ConfigurationDynamicWindow when the algorithm is
	 * successfully created. When this method is executed open a RunningDialog that
	 * show the progress of execution of algorithm.
	 * 
	 * @param registrableProblem the factory of algorithm for a problem
	 */
	private void runAlgorithm(Registrable registrableProblem) {
		String path = null;
		if (this.inpFile != null) {
			path = this.inpFile.getAbsolutePath();
		}
		Algorithm<?> algorithm = null;
		try {
			algorithm = registrableProblem.build(path);
		} catch (Exception e) {
			CustomDialogs.showExceptionDialog("Error", "Error in the creation of the algorithm",
					"The algorithm can't be created", e);
		}
		if (algorithm != null) {
			Problem<?> problem = registrableProblem.getProblem();
			RunningWindowController runningDialogController = new RunningWindowController(algorithm, problem,
					this.network);
			runningDialogController.showWindowAndRunAlgorithm();
		}

	}
}
