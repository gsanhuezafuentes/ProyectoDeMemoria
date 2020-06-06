package controller;

import application.ApplicationSetup;
import controller.multiobjectives.MultiObjectiveRunningWindowController;
import controller.singleobjectives.SingleObjectiveRunningWindowController;
import controller.utils.ProblemMenuConfiguration;
import epanet.core.EpanetException;
import exception.InputException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import model.epanet.element.Network;
import model.epanet.element.utils.HydraulicSimulation;
import model.epanet.io.InpParser;
import model.metaheuristic.experiment.Experiment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import registrable.MultiObjectiveRegistrable;
import registrable.SingleObjectiveRegistrable;
import view.ElementViewer;
import view.NetworkComponent;
import view.utils.CustomDialogs;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The controller of the main window view.
 * <p>
 * From this class is opened the RunningWindow when the problem is selected in
 * menu item.
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
     * Is the option of menu for singleobjectives problems. It is filled using reflection through
     * ProblemRegistrar.
     */
    @FXML
    private Menu singleobjectiveMenu;
    /**
     * Is the option of menu for multiobjectives problem. It is filled using reflection through
     * ProblemRegistrar.
     */
    @FXML
    private Menu multiobjectiveMenu;
    /**
     * Run a simulation with default network configuration
     */
    @FXML
    private Button runButton;

    /**
     * Open a window to show the result of the simulation with default network configuration
     */
    @FXML
    private Button resultReportButton;

    /**
     * The tabpane
     */
    @FXML
    private TabPane tabPane;
    /**
     * The button to export the table as excel.
     */
    @FXML
    private Button saveTableAsExcelButton;
    /**
     * Save the result tab
     */
    @FXML
    private Button saveTableButton;
    /**
     * Save as inp the selected items in result tab.
     */
    @FXML
    private Button saveSelectedAsINPButton;
    /**
     * The network tab. This is the tab by default and you must not close it.
     */
    @FXML
    private Tab networkTab;

    @Nullable
    private Window window;
    @Nullable
    private File inpFile;
    @NotNull
    private final BooleanProperty isNetworkLoaded;
    @NotNull
    private final ObjectProperty<Network> network;
    @NotNull
    private ObjectProperty<HydraulicSimulation> hydraulicSimulation;

    public MainWindowController() {
        this.isNetworkLoaded = new SimpleBooleanProperty(false);
        this.network = new SimpleObjectProperty<Network>();
        this.hydraulicSimulation = new SimpleObjectProperty<HydraulicSimulation>();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Register the menu item problem to problem menu and add the listener to it
        // menuitem to show a interface,
        ProblemMenuConfiguration problemRegistrar = new ProblemMenuConfiguration();
        problemRegistrar.addSingleObjectiveProblems(this.singleobjectiveMenu, this::runSingleObjectiveExperiment);
        problemRegistrar.addMultiObjectiveProblems(this.multiobjectiveMenu, this::runMultiObjectiveExperiment);

        isNetworkLoaded.bind(network.isNotNull());

        // disable problem menu and the run button until a network is loaded
        this.singleobjectiveMenu.disableProperty().bind(isNetworkLoaded.not());
        this.multiobjectiveMenu.disableProperty().bind(isNetworkLoaded.not());
        this.runButton.disableProperty().bind(isNetworkLoaded.not());

        networkComponent.networkProperty().bind(network);
        elementViewer.networkProperty().bind(network);
        networkComponent.selectedProperty().bindBidirectional(elementViewer.selectedProperty());

        // Configure tabpane behaviour when the default network tab is selected
        this.networkTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // clean bind and disable button when default tab "network" is selected
                this.saveTableButton.setDisable(true);
                this.saveTableAsExcelButton.setDisable(true);
                this.saveSelectedAsINPButton.disableProperty().unbind();
                this.saveSelectedAsINPButton.setDisable(true);
                this.saveSelectedAsINPButton.setOnAction(null);
                this.saveTableButton.setOnAction(null);
            }
        });

        //disable the button to show the hydraulic simulation until that a hydraulic simulation are be executed.
        this.resultReportButton.disableProperty().bind(this.hydraulicSimulation.isNull());
    }

    /**
     * @param window the windows
     */
    public void setWindow(@Nullable Window window) {
        this.window = window;
    }

    /**
     * Handler from event generated by FXML file
     */
    @FXML
    private void openOnAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open");
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
    private void loadNetwork(@NotNull File file) {
        this.network.setValue(null);
        this.hydraulicSimulation.setValue(null);
        InpParser parse = new InpParser();

        try {
            Network networkO = new Network();
            parse.parse(networkO, file.getAbsolutePath());
            // when network is loaded set the property that as a listener to draw it
            if (!networkO.isEmpty()) {
                this.network.set(networkO);
            }
        } catch (@NotNull IOException | InputException e) {
            CustomDialogs.showExceptionDialog("Error", "Error loading the network", "The network can't be loaded", e);
        }

    }

    /**
     * Show the setting window
     */
    public void settingOnAction() {
        ApplicationSetup.showSettingWindow();
    }

    /**
     * Run the single objective experiment.<br>
     * <br>
     *
     * <br>
     * <br>
     * <strong>Notes:</strong> <br>
     * This method is called by ConfigurationDynamicWindow when the experiment is
     * successfully created. When this method is executed open a RunningDialog that
     * show the progress of execution of experiment.
     *
     * @param registrableProblem the factory of experiment for a problem
     */
    private void runSingleObjectiveExperiment(@NotNull SingleObjectiveRegistrable registrableProblem) {
        String path = null;
        if (this.inpFile != null) {
            path = this.inpFile.getAbsolutePath();
        }
        Experiment<?> experiment = null;
        try {
            experiment = Objects.requireNonNull(registrableProblem.build(path));
        } catch (Exception e) {
            CustomDialogs.showExceptionDialog("Error", "Error in the creation of the experiment",
                    "The experiment can't be created", e);
        }
        if (experiment != null) {
            Map<String, String> parameters = registrableProblem.getParameters();
            SingleObjectiveRunningWindowController runningDialogController = new SingleObjectiveRunningWindowController(experiment, parameters,
                    this.network.get(), this::createResultTab);
            runningDialogController.showWindowAndRunExperiment();
        }

    }

    /**
     * Run the multi objective experiment.<br>
     * <br>
     *
     * <br>
     * <br>
     * <strong>Notes:</strong> <br>
     * This method is called by ConfigurationDynamicWindow when the experiment is
     * successfully created. When this method is executed open a RunningDialog that
     * show the progress of execution of experiment.
     *
     * @param registrableProblem the factory of experiment for a problem
     */
    private void runMultiObjectiveExperiment(@NotNull MultiObjectiveRegistrable registrableProblem) {
        String path = null;
        if (this.inpFile != null) {
            path = this.inpFile.getAbsolutePath();
        }
        Experiment<?> experiment = null;
        try {
            experiment = Objects.requireNonNull(registrableProblem.build(path));
        } catch (Exception e) {
            CustomDialogs.showExceptionDialog("Error", "Error in the creation of the experiment",
                    "The experiment can't be created", e);
        }
        if (experiment != null) {
            Map<String, String> parameters = registrableProblem.getParameters();
            MultiObjectiveRunningWindowController runningDialogController = new MultiObjectiveRunningWindowController(experiment, parameters,
                    this.network.get(), this::createResultTab);
            runningDialogController.showWindowAndRunExperiment();
        }
    }

    private int resultCount = 0;

    /**
     * Create a new result tab
     *
     * @param resultController the result controller
     */
    private void createResultTab(@NotNull ResultController resultController) {
        Tab tab = new Tab(resultCount++
                + " - " + resultController.getProblemName()
                + "-" + inpFile.getName().substring(0, inpFile.getName().lastIndexOf('.'))
                + "-" + LocalDate.now()
                , resultController.getNode());

        // add bind when select the tab
        tab.setOnSelectionChanged(event -> {
            if (tab.selectedProperty().getValue()) {

                //to save as fun and var
                this.saveTableButton.setDisable(false);
                this.saveTableButton.setOnAction(event1 -> resultController.saveTable());

                // to save the excel
                this.saveTableAsExcelButton.setDisable(false);
                this.saveTableAsExcelButton.setOnAction(event1 -> resultController.saveTableAsExcel());

                //to save as inp
                this.saveSelectedAsINPButton.disableProperty().bind(resultController.hasSelectedItemProperty().not());
                this.saveSelectedAsINPButton.setOnAction(event1 -> resultController.saveSelectedItemAsINP());
            }
        });

        tab.setOnClosed(event -> { // remove the bind when switch tab
            tab.setOnSelectionChanged(null);
            tab.setOnClosed(null);
        });
        this.tabPane.getTabs().addAll(tab);
        this.tabPane.getSelectionModel().select(tab);
    }

    /**
     * Event action when run button is clicked. This run the simulation.
     *
     * @param actionEvent the info of event
     */
    public void runOnAction(ActionEvent actionEvent) {
        try {
            assert inpFile != null;
            this.hydraulicSimulation.setValue(HydraulicSimulation.run(inpFile.getAbsolutePath()));
        } catch (EpanetException e) {
            CustomDialogs.showExceptionDialog("Error", "Error in the simulation.",
                    "An error has occurred during the simulation of the network.", e);
        }

    }

    /**
     * The event action when the report button is clicked. This open a window
     * to set the result that was to see.
     *
     * @param actionEvent the info of event
     */
    public void resultReportOnAction(ActionEvent actionEvent) {
        HydraulicSimulationResultController controller = new HydraulicSimulationResultController(this.hydraulicSimulation.getValue(), networkComponent.selectedProperty());
        controller.showWindow();
    }
}
