package model.metaheuristic.experiment.util;

import java.util.Observable;

/**
 * This class is a buffer on a String.
 */
public class ObservableStringBuffer extends Observable {
    private StringBuffer buffer = new StringBuffer();

    /**
     * Return the text in the buffer
     *
     * @return the text
     */
    public String getBufferText() {
        return this.buffer.toString();
    }

    /**
     * Add text to the buffer
     *
     * @param txt text to added to string buffer
     */
    public void print(String txt) {
        buffer.append(txt);
        setChanged();
        notifyObservers();

    }

    /**
     * Add text to the buffer with a line break
     *
     * @param txt text to added to string buffer
     */
    public void println(String txt) {
        buffer.append(txt).append("\n");
        setChanged();
        notifyObservers();
    }

    @Override
    public String toString() {
        return getBufferText();
    }
}
