package application;

import com.dlsc.formsfx.model.validators.IntegerRangeValidator;
import com.dlsc.preferencesfx.PreferencesFx;
import com.dlsc.preferencesfx.model.Category;
import com.dlsc.preferencesfx.model.Group;
import com.dlsc.preferencesfx.model.Setting;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contain setup and let show a window.
 * <p>
 * The config modified with this class will persistance. This config are saved with Preference API of java.
 */
public class ApplicationSetup {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationSetup.class);

    private static ApplicationSetup instance;

    private final PreferencesFx preferencesFx;

    BooleanProperty isNetworkLegendEnable = new SimpleBooleanProperty(this,"isNetworkLegendEnable",false);
    BooleanProperty isChartEnable = new SimpleBooleanProperty(this, "isChartEnable",true);
    IntegerProperty chartPointSize = new SimpleIntegerProperty(this, "chartPointSize", 5);
    BooleanProperty isNumberOfMultiObjectiveResultLimited = new SimpleBooleanProperty(this, "isNumberOfMultiObjectiveResultLimited", false);
    IntegerProperty numberOfResultMultiObjectiveProblem = new SimpleIntegerProperty(this, "numberOfResultMultiObjectiveProblem", 0);

    static {
        instance = new ApplicationSetup();
    }

    private ApplicationSetup() {

        preferencesFx = createPreference();

        ChangeListener<Object> listener = (prop, oldV, newV) -> {
            ReadOnlyProperty<Object> property = (ReadOnlyProperty<Object>) prop;
            LOGGER.debug("The value of property in {} of {} has changed to {}.", property.getBean().getClass().getName(), property.getName()
                    , newV);
        };

        this.isNetworkLegendEnable.addListener(listener);
        this.isChartEnable.addListener(listener);
        this.chartPointSize.addListener(listener);
        this.isNumberOfMultiObjectiveResultLimited.addListener(listener);
        this.numberOfResultMultiObjectiveProblem.addListener(listener);
    }

    /**
     * Create the window of settings
     *
     * @return a instance of the preferenceFx
     */
    PreferencesFx createPreference() {
        return PreferencesFx.of(ApplicationSetup.class, Category.of("General",
                Group.of("Network Setting",
                        Setting.of("Enable network legend", isNetworkLegendEnable)
                ),
                Group.of("Chart Setting",
                        Setting.of("Enable chart", isChartEnable),
                        Setting.of("Chart point size (px)", chartPointSize)
                                .validate(IntegerRangeValidator
                                        .atLeast(1, "Number of results needs to be at least 1")
                                )
                ),
                Group.of("Result Setting",
                        Setting.of("Limit number of result in multiobjective problem", isNumberOfMultiObjectiveResultLimited),
                        Setting.of("Number of result multiobjective problem", numberOfResultMultiObjectiveProblem)
                                .validate(IntegerRangeValidator
                                        .atLeast(1, "Number of results needs to be at least 1")
                                )
                )
        )).persistWindowState(false).saveSettings(true).debugHistoryMode(false).buttonsVisibility(true);
    }

    /**
     * Setting to configure if the legend on the network will be visible
     *
     * @return true if is visible, false otherwise.
     * @throws IllegalStateException if call to method is realized other than FX Application Thread.
     */
    public boolean isNetworkLegendEnable() {
        checkThread();
        return isNetworkLegendEnable.get();
    }

    /**
     * Setting to configure if the legend on the network will be visible
     *
     * @return a property.
     * @throws IllegalStateException if call to method is realized other than FX Application Thread.
     */
    public ReadOnlyBooleanProperty isNetworkLegendEnableProperty() {
        checkThread();
        return isNetworkLegendEnable;
    }

    /**
     * Setting to configure if chart will be enable. This is a config to
     * reduce increase the performance of simulation.
     *
     * @return true if is enable, false otherwise.
     * @throws IllegalStateException if call to method is realized other than FX Application Thread.
     */
    public boolean isChartEnabled() {
        checkThread();
        return isChartEnable.get();
    }

    /**
     * Setting to configure if chart will be enable. This is a config to
     * reduce increase the performance of simulation.
     *
     * @return the property.
     * @throws IllegalStateException if call to method is realized other than FX Application Thread.
     */
    public ReadOnlyBooleanProperty isChartEnableProperty() {
        checkThread();
        return isChartEnable;
    }

    /**
     * Setting to configure the size of the chart point.
     *
     * @return the size.
     * @throws IllegalStateException if call to method is realized other than FX Application Thread.
     */
    public int getChartPointSize() {
        checkThread();
        return chartPointSize.get();
    }

    /**
     * Setting to configure the size of the chart point.
     *
     * @return the property.
     * @throws IllegalStateException if call to method is realized other than FX Application Thread.
     */
    public ReadOnlyIntegerProperty chartPointSizeProperty() {
        checkThread();
        return chartPointSize;
    }

    /**
     * Setting to configure is the number of element in result list to multiobjective
     * experiment will be limited.
     *
     * @return the size.
     * @throws IllegalStateException if call to method is realized other than FX Application Thread.
     */
    public boolean isNumberOfMultiObjectiveResultLimited() {
        checkThread();
        return isNumberOfMultiObjectiveResultLimited.get();
    }

    /**
     * Setting to configure is the number of element in result list to multiobjective
     * experiment will be limited.
     *
     * @return the property.
     * @throws IllegalStateException if call to method is realized other than FX Application Thread.
     */
    public ReadOnlyBooleanProperty isNumberOfMultiObjectiveResultLimitedProperty() {
        checkThread();
        return isNumberOfMultiObjectiveResultLimited;
    }

    /**
     * Setting to configure the size of result list of multiobjective problems.
     *
     * @return the size.
     * @throws IllegalStateException if call to method is realized other than FX Application Thread.
     */
    public int getNumberOfResultMultiObjectiveProblem() {
        checkThread();
        return numberOfResultMultiObjectiveProblem.get();
    }

    /**
     * Setting to configure the size of result list of multiobjective problems.
     *
     * @return the property.
     * @throws IllegalStateException if call to method is realized other than FX Application Thread.
     */
    public ReadOnlyIntegerProperty numberOfResultMultiObjectiveProblemProperty() {
        checkThread();
        return numberOfResultMultiObjectiveProblem;
    }

    /**
     * Show the window
     */
    private void showSettingDialog() {
        preferencesFx.show(true);
    }

    /**
     * Get the instance of this class
     *
     * @return the instance
     * @throws IllegalStateException if call to method is realized other than FX Application Thread.
     */
    public static ApplicationSetup getInstance() {
        checkThread();
        return instance;
    }

    /**
     * Show the setting window
     *
     * @throws IllegalStateException if call to method is realized other than FX Application Thread.
     */
    public static void showSettingWindow() {
        checkThread();
        instance.showSettingDialog();
    }

    /**
     * Check if the call is realized from the FX Application Thread.
     *
     * @throws IllegalStateException if call to method is realized other than FX Application Thread.
     */
    private static void checkThread() {
        if (!Platform.isFxApplicationThread()) {
            throw new IllegalStateException("Task must only be used from the FX Application Thread");
        }
    }
}
