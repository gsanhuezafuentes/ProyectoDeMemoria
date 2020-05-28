package application;

import com.dlsc.formsfx.model.validators.IntegerRangeValidator;
import com.dlsc.preferencesfx.PreferencesFx;
import com.dlsc.preferencesfx.model.Category;
import com.dlsc.preferencesfx.model.Group;
import com.dlsc.preferencesfx.model.Setting;
import javafx.beans.property.*;

/**
 * This class contain setup and let show a window.
 *
 * The config modified with this class will persistance. This config are saved with Preference API of java.
 */
public class ApplicationSetup {
    private static ApplicationSetup instance;

    private final PreferencesFx preferencesFx;

    BooleanProperty isNetworkLegendEnable = new SimpleBooleanProperty(false);
    BooleanProperty isChartEnable = new SimpleBooleanProperty(true);
    IntegerProperty chartPointSize = new SimpleIntegerProperty(5);
    BooleanProperty isNumberOfMultiObjectiveResultLimited = new SimpleBooleanProperty(false);
    IntegerProperty numberOfResultMultiObjectiveProblem = new SimpleIntegerProperty(0);

    static {
        instance = new ApplicationSetup();
    }

    private ApplicationSetup(){
        preferencesFx = createPreference();
    }

    /**
     * Create the window of settings
     * @return a instance of the preferenceFx
     */
    PreferencesFx createPreference(){
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
     * @return true if is visible, false otherwise.
     */
    public boolean isNetworkLegendEnable() {
        return isNetworkLegendEnable.get();
    }

    /**
     * Setting to configure if the legend on the network will be visible
     * @return a property.
     */
    public ReadOnlyBooleanProperty isNetworkLegendEnableProperty() {
        return isNetworkLegendEnable;
    }

    /**
     * Setting to configure if chart will be enable. This is a config to
     * reduce increase the performance of simulation.
     * @return true if is enable, false otherwise.
     */
    public boolean isChartEnable() {
        return isChartEnable.get();
    }

    /**
     * Setting to configure if chart will be enable. This is a config to
     * reduce increase the performance of simulation.
     * @return the property.
     */
    public ReadOnlyBooleanProperty isChartEnableProperty() {
        return isChartEnable;
    }

    /**
     * Setting to configure the size of the chart point.
     * @return the size.
     */
    public int getChartPointSize() {
        return chartPointSize.get();
    }

    /**
     * Setting to configure the size of the chart point.
     * @return the property.
     */
    public ReadOnlyIntegerProperty chartPointSizeProperty() {
        return chartPointSize;
    }

    /**
     * Setting to configure is the number of element in result list to multiobjective
     * experiment will be limited.
     * @return the size.
     */
    public boolean isNumberOfMultiObjectiveResultLimited() {
        return isNumberOfMultiObjectiveResultLimited.get();
    }

    /**
     * Setting to configure is the number of element in result list to multiobjective
     * experiment will be limited.
     * @return the property.
     */
    public ReadOnlyBooleanProperty isNumberOfMultiObjectiveResultLimitedProperty() {
        return isNumberOfMultiObjectiveResultLimited;
    }

    /**
     * Setting to configure the size of result list of multiobjective problems.
     * @return the size.
     */
    public int getNumberOfResultMultiObjectiveProblem() {
        return numberOfResultMultiObjectiveProblem.get();
    }

    /**
     * Setting to configure the size of result list of multiobjective problems.
     * @return the property.
     */
    public ReadOnlyIntegerProperty numberOfResultMultiObjectiveProblemProperty() {
        return numberOfResultMultiObjectiveProblem;
    }

    /**
     * Show the window
     */
    private void showSettingDialog(){
        preferencesFx.show(true);
    }

    /**
     * Get the instance of this class
     * @return the instance
     */
    public static ApplicationSetup getInstance(){
        return instance;
    }

    /**
     * Show the setting window
     */
    public static void showSettingWindow(){
        instance.showSettingDialog();
    }

}
