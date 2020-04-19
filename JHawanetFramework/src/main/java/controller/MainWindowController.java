package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import controller.monoobjectives.MonoObjectiveRunningWindowController;
import controller.multiobjectives.MultiObjectiveRunningWindowController;
import controller.problems.MonoObjectiveRegistrable;
import controller.problems.MultiObjectiveRegistrable;
import controller.utils.ProblemMenuConfiguration;
import exception.InputException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import model.epanet.element.Network;
import model.epanet.io.InpParser;
import model.metaheuristic.algorithm.Algorithm;
import model.metaheuristic.experiment.Experiment;
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
	 * Is the option of menu for monoobjective problems. It is filled using reflection through
	 * ProblemRegistrar.
	 */
	@FXML
	private Menu monoobjectiveMenu;
	/**
	 * Is the option of menu for multiobjectives problem. It is filled using reflection through
	 * ProblemRegistrar.
	 */
	@FXML
	private Menu multiobjectiveMenu;

	@FXML
	private Menu runMenu;
	
	@FXML
	private MenuItem runMenuItem;
	
	private Window window;
	private File inpFile;
	private final BooleanProperty isNetworkLoaded;
	private Network network;

	public MainWindowController() {
		this.isNetworkLoaded = new SimpleBooleanProperty(false);

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Register the menu item problem to problem menu and add the listener to it
		// menuitem to show a interface,
		ProblemMenuConfiguration problemRegistrar = new ProblemMenuConfiguration();
		problemRegistrar.addMonoObjectiveProblems(this.monoobjectiveMenu, this::runAlgorithm);
		problemRegistrar.addMultiObjectiveProblems(this.multiobjectiveMenu, this::runExperiment);

		// disable problem menu until a network is loaded
		this.monoobjectiveMenu.disableProperty().bind(isNetworkLoaded.not());
		this.multiobjectiveMenu.disableProperty().bind(isNetworkLoaded.not());
		this.runMenu.disableProperty().bind(isNetworkLoaded.not());

		
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
	private void runAlgorithm(MonoObjectiveRegistrable registrableProblem) {
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
			MonoObjectiveRunningWindowController runningDialogController = new MonoObjectiveRunningWindowController(algorithm, problem,
					this.network);
			runningDialogController.showWindowAndRunAlgorithm();
		}

	}
	
	/**
	 * Run the experiment.<br>
	 * <br>
	 * 
	 * <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * This method is called by ConfigurationDynamicWindow when the experiment is
	 * successfully created. When this method is executed open a RunningDialog that
	 * show the progress of execution of algorithm.
	 * 
	 * @param registrableProblem the factory of algorithm for a problem
	 */
	private void runExperiment(MultiObjectiveRegistrable registrableProblem) {
		String path = null;
		if (this.inpFile != null) {
			path = this.inpFile.getAbsolutePath();
		}
		Experiment<?> experiment = null;
		try {
			experiment = registrableProblem.build(path);
		} catch (Exception e) {
			CustomDialogs.showExceptionDialog("Error", "Error in the creation of the experiment",
					"The algorithm can't be created", e);
		}
		if (experiment != null) {
			Problem<?> problem = registrableProblem.getProblem();
			MultiObjectiveRunningWindowController runningDialogController = new MultiObjectiveRunningWindowController(experiment, problem,
					this.network);
			runningDialogController.showWindowAndRunAlgorithm();
		}

	}
}
