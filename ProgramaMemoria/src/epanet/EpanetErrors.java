package epanet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EpanetErrors {
	/**
	 * Only-Read Map that contains errors that can be throw for EpanetToolkit.
	 */
	public static final Map<Integer, String> errors;

	static {
		// Map with errors that can be throw for EPanetToolkit.
		Map<Integer, String> temp = new HashMap<Integer, String>();
		temp.put(0, "No error ");
		temp.put(101, "Insufficient memory ");
		temp.put(102, "No network data to process ");
		temp.put(103, "Hydraulics solver not initialized ");
		temp.put(104, "No hydraulic results available ");
		temp.put(105, "Water quality solver not initialized ");
		temp.put(106, "No results to report on ");
		temp.put(110, "Cannot solve hydraulic equations ");
		temp.put(120, "Cannot solve WQ transport equations ");
		temp.put(200, "One or more errors in input file ");
		temp.put(202, "Illegal numeric value in function call ");
		temp.put(203, "Undefined node in function call ");
		temp.put(204, "Undefined link in function call ");
		temp.put(205, "Undefined time pattern in function call ");
		temp.put(207, "Attempt made to control a check valve ");
		temp.put(223, "Not enough nodes in network ");
		temp.put(224, "No tanks or reservoirs in network ");
		temp.put(240, "Undefined source in function call ");
		temp.put(241, "Undefined control statement in function call ");
		temp.put(250, "Function argument has invalid format ");
		temp.put(251, "Illegal parameter code in function call ");
		temp.put(301, "Identical file names ");
		temp.put(302, "Cannot open input file ");
		temp.put(303, "Cannot open report file ");
		temp.put(304, "Cannot open binary output file ");
		temp.put(305, "Cannot open hydraulics file ");
		temp.put(306, "Invalid hydraulics file ");
		temp.put(307, "Cannot read hydraulics file ");
		temp.put(308, "Cannot save results to file ");
		temp.put(309, "Cannot write report to file ");

		temp.put(1,
				"System hydraulically unbalanced - convergence to a hydraulic solution was not achieved in the allowed number of trials.");
		temp.put(2,
				"System may be hydraulically unstable - hydraulic convergence was only achieved after the status of all links was held fixed.");
		temp.put(3,
				"System disconnected - one or more nodes with positive demands were disconnected from all supply sources");
		temp.put(4,
				"Pumps cannot deliver enough flow or head - one or more pumps were forced to either shut down (due to insufficient head) or operate beyond the maximum rated flow.");
		temp.put(5,
				"Valves cannot deliver enough flow - one or more flow control valves could not deliver the required flow even when fully open.");
		temp.put(6,
				"System has negative pressures - negative pressures occurred at one or more junctions with positive demand.");

		errors = Collections.unmodifiableMap(temp);
	}
	

	public static void throwException(int errcode) throws EpanetException{
        String msg = errors.get(errcode);
        throw new EpanetException(msg);
	}
	
    public static void checkError( int errcode ) throws EpanetException {
        if (errcode > 100) {
            throwException(errcode);
        }
    }

    public static String checkWarning( int errcode ) {
        if (errcode > 0 && errcode < 7) {
            String msg = errors.get(errcode);
            return msg;
        }
        return null;
    }
}
