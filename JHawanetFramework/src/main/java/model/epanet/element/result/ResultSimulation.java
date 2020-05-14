package model.epanet.element.result;

public abstract class ResultSimulation {
    private final long timeInSeconds;
    private final String timeString;

    /**
     * Constructor
     * the simulation time has to be in a range of 24 hours, i.e. in the range of [0, 86399].
     * @param timeInSeconds the simulation time in seconds
     * @throws IllegalArgumentException if timeInSeconds is negative or out of range of 24 hours.
     */
    ResultSimulation(long timeInSeconds){
        if (timeInSeconds < 0){
            throw new IllegalArgumentException("the parameter timeInSeconds has to be a positive integer but it was " + timeInSeconds);
        }
        if (timeInSeconds >= 86400){
            throw new IllegalArgumentException("the parameter timeInSeconds has to be in a range of 24 hours. i.e. 0 to 86399 but it was " + timeInSeconds);
        }
        this.timeInSeconds = timeInSeconds;

        long hour = timeInSeconds / (3600);
        long minute = (timeInSeconds - (3600 * hour)) / 60;
        long second = timeInSeconds - ((3600 * hour) + (minute * 60));

        this.timeString = String.format("%02.0f:%02.0f:%02d", Math.floor(hour), Math.floor(minute), second);
    }

    /**
     * Get the simulation time of simulation in seconds
     * @return the simulation time
     */
    public long getTimeInSeconds() {
        return timeInSeconds;
    }

    /**
     * Get the simulation time of simulation in format HH:mm:ss. For example: 13:30:00.
     * @return
     */
    public String getTimeString() {
        return timeString;
    }
}
