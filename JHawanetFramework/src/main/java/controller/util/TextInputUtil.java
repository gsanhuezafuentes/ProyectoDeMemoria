package controller.util;

import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;
import org.jetbrains.annotations.NotNull;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * This class has some utility element using with text field.
 */
public class TextInputUtil {
    // This avoid the creation of a instance.
    private TextInputUtil() {
    }

    /**
     * Create a DecimalFormater. It when is attached a textfield only let valid
     * values for a decimal
     *
     * @return the formatter
     */
    public static @NotNull TextFormatter<Double> createDecimalTextFormatter(double defaultValue) {
        Pattern validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?");

        UnaryOperator<TextFormatter.Change> filter = c -> {
            String text = c.getControlNewText();
            if (validEditingState.matcher(text).matches()) {
                return c;
            } else {
                return null;
            }
        };

        StringConverter<Double> converter = createDoubleConverter();

        return new TextFormatter<>(converter, defaultValue, filter);
    }

    /**
     * Create a DecimalFormater. It when is attached a textfield only let valid
     * values for a decimal. A call to this method is the same that call
     * {@link #createDecimalTextFormatter(double)} with 0 as value.
     *
     * @return the formatter
     */
    public static @NotNull TextFormatter<Double> createDecimalTextFormatter() {
        return createDecimalTextFormatter(0);
    }

    /**
     * Create a WholeFormatter. It when is attached a textfield only let valid
     * values for a whole number
     *
     * @return the formatter
     */
    public static @NotNull TextFormatter<Integer> createWholeTextFormatter(int defaultValue) {
        Pattern validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?");

        UnaryOperator<TextFormatter.Change> filter = c -> {
            String text = c.getControlNewText();
            if (validEditingState.matcher(text).matches()) {
                return c;
            } else {
                return null;
            }
        };

        StringConverter<Integer> converter = createIntegerConverter();

        return new TextFormatter<>(converter, defaultValue, filter);
    }

    /**
     * Create a WholeFormatter. It when is attached a textfield only let valid
     * values for a whole number.A call to this method is the same that call
     * {@link #createDecimalTextFormatter(double)} with 0 as value.
     *
     * @return the formatter
     */
    public static @NotNull TextFormatter<Integer> createWholeTextFormatter() {
        return createWholeTextFormatter(0);
    }

    /**
     * Create a String converter for integer.
     * @return the converter.
     */
    public static @NotNull StringConverter<Integer> createIntegerConverter(){
        StringConverter<Integer> converter = new StringConverter<Integer>() {

            @Override
            public Integer fromString(String s) {
                if (s.isEmpty() || "-".equals(s)) {
                    return 0;
                } else {
                    return Integer.valueOf(s);
                }
            }

            @Override
            public String toString(Integer i) {
                return i.toString();
            }
        };
        return converter;
    }

    /**
     * Create a String converted for double.
     * @return the converter.
     */
    public static @NotNull StringConverter<Double> createDoubleConverter(){
        StringConverter<Double> converter = new StringConverter<Double>() {

            @Override
            public Double fromString(String s) {
                if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
                    return 0.0;
                } else {
                    return Double.valueOf(s);
                }
            }

            @Override
            public String toString(Double d) {
                return d.toString();
            }
        };
        return converter;
    }


}
