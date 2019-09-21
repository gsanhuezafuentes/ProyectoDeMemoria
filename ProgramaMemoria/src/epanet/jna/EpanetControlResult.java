package epanet.jna;

/**
 * Class use to return result of {@link EpanetDLL#ENgetcontrol(int)}.<br>
 * <br>
 * 
 * Control type codes consist of the following:<br>
 * 
 * 0 (Low Level Control) applies when tank level or node pressure drops below
 * specified level<br>
 * 1 (High Level Control) applies when tank level or node pressure rises above
 * specified level<br>
 * 2 (Timer Control) applies at specific time into simulation<br>
 * 3 (Time-of-Day Control) applies at specific time of day<br>
 * <br>
 * 
 * For pipes, a setting of 0 means the pipe is closed and 1 means it is open.
 * For a pump, the setting contains the pump's speed, with 0 meaning the pump is
 * closed and 1 meaning it is open at its normal speed. For a valve, the setting
 * refers to the valve's pressure, flow, or loss coefficient value, depending on
 * valve type<br>
 * <br>
 * 
 * For Timer or Time-of-Day controls the nindex parameter equals 0.
 *
 * 
 */
public class EpanetControlResult {
	private final int cindex;
	private final int ctype;
	private final int lindex;
	private final float setting;
	private final int nindex;
	private final float level;

	public EpanetControlResult(int cindex, int ctype, int lindex, float setting, int nindex, float level) {
		this.cindex = cindex;
		this.ctype = ctype;
		this.lindex = lindex;
		this.setting = setting;
		this.nindex = nindex;
		this.level = level;
	}

	/**
	 * @return the cindex
	 */
	public int getCindex() {
		return cindex;
	}

	/**
	 * @return the ctype
	 */
	public EpanetControlType getCtype() {
		return EpanetControlType.convert(ctype);
	}

	/**
	 * @return the lindex
	 */
	public int getLindex() {
		return lindex;
	}

	/**
	 * @return the setting
	 */
	public float getSetting() {
		return setting;
	}

	/**
	 * @return the nindex
	 */
	public int getNindex() {
		return nindex;
	}

	/**
	 * @return the level
	 */
	public float getLevel() {
		return level;
	}

	@Override
	public String toString() {
		String txt = "cindex: " + cindex + " | ctype: " + ctype + " | lindex: " + lindex + " | setting: " + setting
				+ " | nindex: " + nindex + " | level: " + level;
		return txt;
	}

}
