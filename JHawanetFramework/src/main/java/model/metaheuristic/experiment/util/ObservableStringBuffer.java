package model.metaheuristic.experiment.util;

import java.util.Observable;

public class ObservableStringBuffer extends Observable {
	private StringBuffer buffer = new StringBuffer();
	
	/**
	 * Add text to the buffer
	 * @param txt
	 */
	public synchronized void print(String txt){
		buffer.append(txt);
		setChanged();
		notifyObservers();

	}
	
	/**
	 * Add text to the buffer with a line break 
	 * @param txt
	 */
	public void println(String txt){
		buffer.append(txt).append("\n");
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Return the text in the buffer
	 * @return the text
	 */
	public String getBufferText() {
		return this.buffer.toString();
	}
	
	@Override
	public String toString() {
		return getBufferText();
	}
}
