package epanet.jna;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Class use to load java native interface using JNA.
 *
 */
public interface IEpanetDLL {

	/**
	 * Opens the Toolkit to analyze a particular distribution system. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * If there is no need to save EPANET's binary Output file then f3 can be an
	 * empty string ("").<br>
	 * <br>
	 * 
	 * ENopen must be called before any of the other toolkit functions (except
	 * ENepanet) are used.
	 * 
	 * @param f1 name of an EPANET Input file
	 * @param f2 name of an output Report file
	 * @param f3 name of an optional binary Output file
	 * @return Returns an error code.
	 * 
	 */
	int ENopen(String f1, String f2, String f3);

	/**
	 * Writes all current network input data to a file using the format of an EPANET
	 * input file. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * The data saved reflect any changes made by calls to the ENsetxxx family of
	 * functions since EPANET data was first loaded using ENopen.
	 * 
	 * @param fname name of the file where data is saved.
	 * @return Returns an error code.
	 */
	int ENsaveinpfile(String fname);

	/**
	 * Closes down the Toolkit system (including all files being processed). <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * ENclose must be called when all processing has been completed, even if an
	 * error condition was encountered.
	 * 
	 * @return Returns an error code.
	 */
	int ENclose();

	/**
	 * Runs a complete hydraulic simulation with results for all time periods
	 * written to the binary Hydraulics file. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Use ENsolveH to generate a complete hydraulic solution which can stand alone
	 * or be used as input to a water quality analysis. It can also be followed by
	 * calls to ENsaveH and ENreport to write a report on hydraulic results to the
	 * report file. Do not use ENopenH, ENinitH, ENrunH, ENnextH, and ENcloseH in
	 * conjunction with ENsolveH.
	 * 
	 * @return Returns an error code.
	 */
	int ENsolveH();

	/**
	 * Transfers results of a hydraulic simulation from the binary Hydraulics file
	 * to the binary Output file, where results are only reported at uniform
	 * reporting intervals. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * ENsaveH is used when only a hydraulic analysis is run and results at uniform
	 * reporting intervals need to be transferred to EPANET's binary output file.
	 * Such would be the case when an output report to EPANET's report file will be
	 * written using ENreport.<br>
	 * <br>
	 * 
	 * The reporting times can be set either in the EPANET input file (in its
	 * [TIMES] section) or by using the ENsettimeparam function.
	 * 
	 * @return Returns an error code.
	 */
	int ENsaveH();

	/**
	 * Opens the hydraulics analysis system. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Call ENopenH prior to running the first hydraulic analysis using the ENinitH
	 * - ENrunH - ENnextH sequence. Multiple analyses can be made before calling
	 * ENcloseH to close the hydraulic analysis system.<br>
	 * <br>
	 * 
	 * Do not call this function if ENsolveH is being used to run a complete
	 * hydraulic analysis.
	 * 
	 * @return Returns an error code.
	 */
	int ENopenH();

	/**
	 * Initializes storage tank levels, link status and settings, and the simulation
	 * clock time prior to running a hydraulic analysis. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Call ENinitH prior to running a hydraulic analysis using ENrunH and
	 * ENnextH.<br>
	 * <br>
	 * 
	 * ENopenH must have been called prior to calling ENinitH.<br>
	 * <br>
	 * 
	 * Do not call ENinitH if a complete hydraulic analysis is being made with a
	 * call to ENsolveH.<br>
	 * <br>
	 * 
	 * Set saveflag to 1 if you will be making a subsequent water quality run, using
	 * ENreport to generate a report, or using ENsavehydfile to save the binary
	 * hydraulics file.
	 * 
	 * @param saveFlag Initializes storage tank levels, link status and settings,
	 *                 and the simulation clock time prior to running a hydraulic
	 *                 analysis.
	 * @return Returns an error code.
	 */
	int ENinitH(int saveFlag);

	/**
	 * Runs a single period hydraulic analysis, retrieving the current simulation
	 * clock time t. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Use ENrunH along with ENnextH in a do ..while loop to analyze hydraulics in
	 * each period of an extended period simulation. This process automatically
	 * updates the simulation clock time so treat t as a read-only variable.<br>
	 * <br>
	 * 
	 * ENinitH must have been called prior to running the ENrunH - ENnextH loop.<br>
	 * <br>
	 * 
	 * 
	 * @param t current simulation clock time in seconds.
	 * @return Returns an error code.
	 */
	int ENrunH(long[] t);

	/**
	 * Determines the length of time until the next hydraulic event occurs in an
	 * extended period simulation. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * This function is used in conjunction with ENrunH to perform an extended
	 * period hydraulic analysis.
	 * 
	 * The value of tstep should be treated as a read-only variable. It is
	 * automatically computed as the smaller of:
	 * <ul>
	 * <li>the time interval until the next hydraulic time step begins</li>
	 * <li>the time interval until the next reporting time step begins</li>
	 * <li>the time interval until the next change in demands occurs</li>
	 * <li>the time interval until a tank becomes full or empty</li>
	 * <li>the time interval until a control or rule fires</li>
	 * </ul>
	 * 
	 * @param tstep time (in seconds) until next hydraulic event occurs or 0 if at
	 *              the end of the simulation period.
	 * @return Returns an error code.
	 * 
	 */
	int ENnextH(long[] tstep);

	/**
	 * Closes the hydraulic analysis system, freeing all allocated memory. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Call ENcloseH after all hydraulics analyses have been made using ENinitH -
	 * ENrunH - ENnextH. Do not call this function if ENsolveH is being used.
	 * 
	 * @return Returns an error code.
	 */
	int ENcloseH();

	/**
	 * Saves the current contents of the binary hydraulics file to a file. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Use this function to save the current set of hydraulics results to a file,
	 * either for post-processing or to be used at a later time by calling the
	 * ENusehydfile function.<br>
	 * <br>
	 * 
	 * The hydraulics file contains nodal demands and heads and link flows, status,
	 * and settings for all hydraulic time steps, even intermediate ones.<br>
	 * <br>
	 * 
	 * Before calling this function hydraulic results must have been generated and
	 * saved by having called ENsolveH or the ENinitH - ENrunH - ENnextH sequence
	 * with the saveflag parameter of ENinitH set to 1.
	 * 
	 * @param fname name of the file where the hydraulics results should be saved.
	 * @return Returns an error code.
	 */
	int ENsavehydfile(String fname);

	/**
	 * Uses the contents of the specified file as the current binary hydraulics
	 * file. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Call this function to refuse a set of hydraulic analysis results saved
	 * previously. These results are checked to see if they match the following the
	 * parameters associated with the current network being analyzed: number of
	 * nodes, number of tanks and reservoirs, number of links, number of pumps,
	 * number of valves, and simulation duration.<br>
	 * <br>
	 * 
	 * Do not call this function when the hydraulics analysis system is still opened
	 * (i.e., ENopenH has been called but ENcloseH has not).
	 * 
	 * @param fname name of the file containing hydraulic analysis results for the
	 *              current network.
	 * 
	 * @return Returns an error code.
	 * 
	 */
	int ENusehydfile(String fname);

	/**
	 * Runs a complete water quality simulation with results at uniform reporting
	 * intervals written to EPANET's binary Output file. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * A hydraulic analysis must have been run and saved to the binary hydraulics
	 * file before calling ENsolveQ. It can be followed by a call to ENreport to
	 * write a report on hydraulic and water quality results to the report file. Do
	 * not use ENopenQ, ENinitQ, ENrunQ, ENnextQ, and ENcloseQ in conjunction with
	 * ENsolveQ.
	 * 
	 * @return Returns an error code.
	 */
	int ENsolveQ();

	/**
	 * Opens the water quality analysis system. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Call ENopenQ prior to running the first water quality analysis using an
	 * ENinitQ - ENrunQ - ENnextQ (or ENstepQ) sequence. Multiple water quality
	 * analyses can be made before calling ENcloseQ to close the water quality
	 * analysis system.<br>
	 * <br>
	 * 
	 * Do not call this function if a complete water quality analysis is being made
	 * using ENsolveQ.
	 * 
	 * @return Returns an error code.
	 */
	int ENopenQ();

	/**
	 * Initializes water quality and the simulation clock time prior to running a
	 * water quality analysis. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Call ENinitQ prior to running a water quality analysis using ENrunQ in
	 * conjunction with either ENnextQ or ENstepQ.<br>
	 * <br>
	 * 
	 * ENopenQ must have been called prior to calling ENinitQ.<br>
	 * <br>
	 * 
	 * Do not call ENinitQ if a complete water quality analysis is being made with a
	 * call to ENsolveQ.<br>
	 * <br>
	 * 
	 * Set saveflag to 1 if you intend to use ENreport to generate a report or wish
	 * to save computed results to the binary output file.
	 * 
	 * @param saveflag 0-1 flag indicating if analysis results should be saved to
	 *                 EPANET's binary output file at uniform reporting periods.
	 * 
	 * @return Returns an error code.
	 * 
	 * 
	 */
	int ENinitQ(int saveflag);

	/**
	 * Makes available the hydraulic and water quality results that occur at the
	 * start of the next time period of a water quality analysis, where the start of
	 * the period is returned in t. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Use ENrunQ along with ENnextQ in a do...while loop to access water quality
	 * results at the start of each hydraulic period in an extended period
	 * simulation. Or use it with ENstepQ in a do...while loop to access results at
	 * the start of each water quality time step. See each of these functions for
	 * examples of how to code such loops.<br>
	 * <br>
	 * 
	 * ENinitQ must have been called prior to running an ENrunQ - ENnextQ (or
	 * ENstepQ) loop.<br>
	 * <br>
	 * 
	 * The current time t of the simulation is determined from information saved
	 * with the hydraulic analysis that preceded the water quality analysis. Treat
	 * it as a read-only variable.
	 * 
	 * 
	 * @param t Current simulation clock time in seconds.
	 * @return Returns an error code.
	 */
	int ENrunQ(Long t);

	/**
	 * Advances the water quality simulation to the start of the next hydraulic time
	 * period. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * This function is used in a do-while loop with ENrunQ to perform an extended
	 * period water quality analysis. It allows you to access water quality results
	 * at each hydraulic period of the simulation. The water quality routing and
	 * reactions are carried out internally at a much smaller time step. Use ENstepQ
	 * instead of this function if you need to access results after each water
	 * quality time step.<br>
	 * <br>
	 * 
	 * The value of tstep is determined from information saved with the hydraulic
	 * analysis that preceded the water quality analysis. Treat it as a read-only
	 * variable.
	 * 
	 * @param tstep time (in seconds) until next hydraulic event occurs or 0 if at
	 *              the end of the simulation period.
	 * @return Returns an error code.
	 */
	int ENnextQ(Long tstep);

	/**
	 * Advances the water quality simulation one water quality time step. The time
	 * remaining in the overall simulation is returned in tleft. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * This function is used in a do-while loop with ENrunQ to perform an extended
	 * period water quality simulation. It allows you to access water quality
	 * results at each water quality time step of the simulation, rather than at the
	 * start of each hydraulic period as with ENnextQ.<br>
	 * <br>
	 * 
	 * Use the argument tleft to determine when no more calls to ENrunQ are needed
	 * because the end of the simulation period has been reached (i.e., when tleft =
	 * 0).<br>
	 * <br>
	 * 
	 * Treat tleft as a read-only variable (do not assign it a value).
	 * 
	 * @param tleft seconds remaining in the overall simulation duration.
	 * @return Returns an error code.
	 */
	int ENstepQ(Long tleft);

	/**
	 * Closes the water quality analysis system, freeing all allocated memory. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Call ENcloseQ after all water quality analyses have been made using the
	 * ENinitQ - ENrunQ - ENnextQ (or ENstepQ) sequence of function calls. Do not
	 * call this function if ENsolveQ is being used.
	 * 
	 * @return Returns an error code.
	 * 
	 */
	int ENcloseQ();

	/**
	 * Writes a line of text to the EPANET report file.
	 * 
	 * @param line text to be written to file.
	 * @return Returns an error code.
	 */
	int ENwriteline(ByteBuffer line);

	/**
	 * Writes a formatted text report on simulation results to the Report file. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Either a full hydraulic analysis or full hydraulic and water quality analysis
	 * must have been run, with results saved to file, before ENreport is called. In
	 * the former case, ENsaveH must also be called first to transfer results from
	 * the Hydraulics file to the Output file.<br>
	 * <br>
	 * 
	 * The format of the report is controlled by commands placed in the [REPORT]
	 * section of the EPANET input file or by similar commands issued with the
	 * ENsetreport function.
	 * 
	 * @return Returns an error code.
	 */
	int ENreport();

	/**
	 * Clears any report formatting commands that either appeared in the [REPORT]
	 * section of the EPANET Input file or were issued with the ENsetreport
	 * function. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * After calling this function the default reporting options are in effect.
	 * These are:
	 * <ul>
	 * <li>No status report</li>
	 * <li>No energy report</li>
	 * <li>No nodes reported on</li>
	 * <li>No links reported on</li>
	 * <li>Node variables reported to 2 decimal places</li>
	 * <li>Link variables reported to 2 decimal places (3 for friction factor)</li>
	 * <li>Link variables reported to 2 decimal places (3 for friction factor)</li>
	 * <li>Node variables reported are elevation, head, pressure, and quality</li>
	 * <li>Link variables reported are flow, velocity, and head loss</li>
	 * <ul>
	 * 
	 * @return Returns an error code.
	 */
	int ENresetreport();

	/**
	 * Issues a report formatting command. Formatting commands are the same as used
	 * in the [REPORT] section of the EPANET Input file. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Call ENresetreport to clear any previous report formatting commands that
	 * either appeared in the Input file or were issued with calls to ENsetreport or
	 * ENsetstatusreport.<br>
	 * <br>
	 * 
	 * Formatted results of a simulation can be written to the Report file using the
	 * ENreport function.
	 * 
	 * @param command text of a report formatting command.
	 * @return Returns an error code.
	 */
	int ENsetreport(String command);

	/**
	 * Retrieves the parameters of a simple control statement. The index of the
	 * control is specified in cindex and the remaining arguments return the
	 * control's parameters. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Controls are indexed starting from 1 in the order in which they were entered
	 * into the [CONTROLS] section of the EPANET input file.<br>
	 * <br>
	 * 
	 * Control type codes consist of the following:<br>
	 * 
	 * 0 (Low Level Control) applies when tank level or node pressure drops below
	 * specified level.<br>
	 * 1 (High Level Control) applies when tank level or node pressure rises above
	 * specified level.<br>
	 * 2 (Timer Control) applies at specific time into simulation.<br>
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
	 * @param cindex  control statement index
	 * @param ctype   control type code
	 * @param lindex  index of link being controlled
	 * @param setting value of the control setting
	 * @param nindex  index of controlling node
	 * @param level   value of controlling water level or pressure for level
	 *                controls or of time of control action (in seconds) for
	 *                time-based controls
	 * @return Returns an error code.
	 */
	int ENgetcontrol(int cindex, IntBuffer ctype, IntBuffer lindex, FloatBuffer setting, IntBuffer nindex,
			FloatBuffer level);

	/**
	 * Retrieves the number of network components of a specified type. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Component codes consist of the following:<br>
	 * 
	 * EN_NODECOUNT 0 Nodes<br>
	 * EN_TANKCOUNT 1 Reservoirs and tank nodes<br>
	 * EN_LINKCOUNT 2 Links<br>
	 * EN_PATCOUNT 3 Time patterns<br>
	 * EN_CURVECOUNT 4 Curves<br>
	 * EN_CONTROLCOUNT 5 Simple controls<br>
	 * <br>
	 * 
	 * The number of junctions in a network equals the number of nodes minus the
	 * number of tanks and reservoirs.br><br>
	 * 
	 * There is no facility within the Toolkit to add to or delete from the
	 * components described in the Input file.
	 * 
	 * @param countcode component code
	 * @param count     number of countcode components in the network
	 * @return Returns an error code.
	 */
	int ENgetcount(int countcode, int[] count);

	/**
	 * Retrieves the value of a particular analysis option. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Option codes consist of the following constants:<br>
	 * 
	 * EN_TRIALS 0<br>
	 * EN_ACCURACY 1<br>
	 * EN_TOLERANCE 2<br>
	 * EN_EMITEXPON 3<br>
	 * EN_DEMANDMULT 4
	 * 
	 * @param optioncode an option code
	 * @param value      an option value
	 * 
	 * @return Returns an error code.
	 */
	int ENgetoption(int optioncode, float[] value);

	/**
	 * Retrieves the value of a specific analysis time parameter. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Time parameter codes consist of the following constants:<br>
	 * 
	 * EN_DURATION 0 Simulation duration<br>
	 * EN_HYDSTEP 1 Hydraulic time step<br>
	 * EN_QUALSTEP 2 Water quality time step<br>
	 * EN_PATTERNSTEP 3 Time pattern time step<br>
	 * EN_PATTERNSTART 4 Time pattern start time<br>
	 * N_REPORTSTEP 5 Reporting time step<br>
	 * EN_REPORTSTART 6 Report starting time<br>
	 * EN_RULESTEP 7 Time step for evaluating rule-based controls<br>
	 * EN_STATISTIC 8 Type of time series post-processing used<br>
	 * EN_PERIODS 9 Number of reporting periods saved to binary output file
	 * 
	 * @param paramcode time parameter code
	 * @param timevalue value of time parameter in seconds
	 * @return Returns an error code.
	 * 
	 */
	int ENgettimeparam(int paramcode, long[] timevalue);

	/**
	 * Retrieves a code number indicating the units used to express all flow rates.
	 * <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Flow units codes are as follows:<br>
	 * 
	 * EN_CFS 0 cubic feet per second<br>
	 * EN_GPM 1 gallons per minute<br>
	 * EN_MGD 2 million gallons per day<br>
	 * EN_IMGD 3 Imperial mgd<br>
	 * EN_AFD 4 acre-feet per day<br>
	 * EN_LPS 5 liters per second<br>
	 * EN_LPM 6 liters per minute<br>
	 * EN_MLD 7 million liters per day<br>
	 * EN_CMH 8 cubic meters per hour<br>
	 * EN_CMD 9 cubic meters per day<br>
	 * 
	 * Flow units are specified in the [OPTIONS] section of the EPANET Input
	 * file.<br>
	 * <br>
	 * 
	 * Flow units in liters or cubic meters implies that metric units are used for
	 * all other quantities in addition to flow. Otherwise US units are employed.
	 * 
	 * @param unitscode value of a flow units code number
	 * @return Returns an error code.
	 */
	int ENgetflowunits(IntBuffer unitscode);

	/**
	 * Retrieves the index of a particular time pattern. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Pattern indexes are consecutive integers starting from 1.
	 * 
	 * @param id    pattern ID label
	 * @param index pattern index
	 * @return Returns an error code.
	 */
	int ENgetpatternindex(String id, int[] index);

	/**
	 * Retrieves the ID label of a particular time pattern. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * The ID label string should be sized to hold at least 32 characters.<br>
	 * 
	 * Pattern indexes are consecutive integers starting from 1.
	 * 
	 * @param index pattern index
	 * @param id    ID label of pattern
	 * @return Returns an error code.
	 */
	int ENgetpatternid(int index, ByteBuffer id);

	/**
	 * Retrieves the number of time periods in a specific time pattern. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Pattern indexes are consecutive integers starting from 1.
	 * 
	 * @param index pattern index
	 * @param len   number of time periods in the pattern
	 * @return Returns an error code.
	 */
	int ENgetpatternlen(int index, int[] len);

	/**
	 * Retrieves the multiplier factor for a specific time period in a time pattern.
	 * <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Pattern indexes and periods are consecutive integers starting from 1.
	 * 
	 * @param index  time pattern index
	 * @param period period within time pattern
	 * @param value  multiplier factor for the period
	 * @return Returns an error code.
	 */
	int ENgetpatternvalue(int index, int period, float[] value);

	/**
	 * Retrieves the type of water quality analysis called for. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Water quality analysis codes are as follows:<br>
	 * 
	 * EN_NONE 0 No quality analysis<br>
	 * EN_CHEM 1 Chemical analysis<br>
	 * EN_AGE 2 Water age analysis<br>
	 * EN_TRACE 3 Source tracing<br>
	 * <br>
	 * 
	 * The tracenode value will be 0 when qualcode is not EN_TRACE.
	 * 
	 * @param qualcode  water quality analysis code
	 * @param tracenode index of node traced in a source tracing analysis
	 * @return Returns an error code.
	 */
	int ENgetqualtype(IntBuffer qualcode, IntBuffer tracenode);

	/**
	 * Retrieves the text of the message associated with a particular error or
	 * warning code. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Error message strings should be at least 80 characters in length.
	 * 
	 * @param errcode
	 * @param errmsg
	 * @param nchar
	 * @return Returns an error code.
	 */
	int ENgeterror(int errcode, ByteBuffer errmsg, int nchar);

	/**
	 * Retrieves the index of a node with a specified ID. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Node indexes are consecutive integers starting from 1.
	 * 
	 * @param id    node ID label
	 * @param index node ID label
	 * @return Returns an error code.
	 */
	int ENgetnodeindex(String id, int[] index);

	/**
	 * Retrieves the ID label of a node with a specified index. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * The ID label string should be sized to hold at least 32 characters.<br>
	 * <br>
	 * 
	 * Node indexes are consecutive integers starting from 1.
	 * 
	 * @param index node index
	 * @param id    ID label of node
	 * @return Returns an error code.
	 */
	int ENgetnodeid(int index, ByteBuffer id);

	/**
	 * Retrieves the node-type code for a specific node. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Node indexes are consecutive integers starting from 1.<br>
	 * <br>
	 * 
	 * Node type codes consist of the following constants:<br>
	 * 
	 * EN_JUNCTION 0 Junction node<br>
	 * EN_RESERVOIR 1 Reservoir node<br>
	 * EN_TANK 2 Tank node
	 * 
	 * @param index
	 * @param typecode
	 * @return Returns an error code.
	 */
	int ENgetnodetype(int index, int[] typecode);

	/**
	 * Retrieves the value of a specific link parameter. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Node indexes are consecutive integers starting from 1.<br>
	 * <br>
	 * 
	 * Node parameter codes consist of the following constants:<br>
	 * 
	 * EN_ELEVATION 0 Elevation<br>
	 * EN_BASEDEMAND 1 Base demand<br>
	 * EN_PATTERN 2 Demand pattern index<br>
	 * EN_EMITTER 3 Emitter coeff.<br>
	 * EN_INITQUAL 4 Initial quality<br>
	 * EN_SOURCEQUAL 5 Source quality<br>
	 * EN_SOURCEPAT 6 Source pattern index<br>
	 * EN_SOURCETYPE 7 Source type (See note below) <br>
	 * EN_TANKLEVEL 8 Initial water level in tank <br>
	 * EN_DEMAND 9 Actual demand <br>
	 * EN_HEAD 10 Hydraulic head<br>
	 * EN_PRESSURE 11 Pressure<br>
	 * EN_QUALITY 12 Actual quality<br>
	 * EN_SOURCEMASS 13 Mass flow rate per minute of a chemical source<br>
	 * <br>
	 * 
	 * The following parameter codes apply only to storage tank nodes:<br>
	 * 
	 * EN_INITVOLUME 14 Initial water volume<br>
	 * EN_MIXMODEL 15 Mixing model code (see below)<br>
	 * EN_MIXZONEVOL 16 Inlet/Outlet zone volume in a 2-compartment tank<br>
	 * EN_TANKDIAM 17 Tank diameter<br>
	 * EN_MINVOLUME 18 Minimum water volume<br>
	 * EN_VOLCURVE 19 Index of volume versus depth curve (0 if none assigned)<br>
	 * EN_MINLEVEL 20 Minimum water level<br>
	 * EN_MAXLEVEL 21 Maximum water level<br>
	 * EN_MIXFRACTION 22 Fraction of total volume occupied by the inlet/outlet zone
	 * in a 2-compartment tank<br>
	 * EN_TANK_KBULK 23 Bulk reaction rate coefficient<br>
	 * <br>
	 * 
	 * Parameters 9 - 13 (EN_DEMAND through EN_SOURCEMASS) are computed values. The
	 * others are input design parameters.<br>
	 * <br>
	 * 
	 * Source types are identified with the following constants:<br>
	 * 
	 * EN_CONCEN 0<br>
	 * EN_MASS 1<br>
	 * EN_SETPOINT 2<br>
	 * EN_FLOWPACED 3<br>
	 * <br>
	 * 
	 * See [SOURCES] for a description of these source types.<br>
	 * <br>
	 * 
	 * The codes for the various tank mixing model choices are as follows:<br>
	 * 
	 * EN_MIX1 0 Single compartment, complete mix model<br>
	 * EN_MIX2 1 Two-compartment, complete mix model<br>
	 * EN_FIFO 2 Plug flow, first in, first out model<br>
	 * EN_LIFO 3 Stacked plug flow, last in, first out model<br>
	 * <br>
	 * 
	 * Values are returned in units which depend on the units used for flow rate in
	 * the EPANET input file
	 * 
	 * @param index     node index
	 * @param paramcode parameter code
	 * @param value     parameter value
	 * @return Returns an error code.
	 */
	int ENgetnodevalue(int index, int paramcode, float[] value);

	/**
	 * Retrieves the index of a link with a specified ID. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Link indexes are consecutive integers starting from 1.
	 * 
	 * @param id    link ID label
	 * @param index link index
	 * @return Returns an error code.
	 */
	int ENgetlinkindex(String id, int[] index);

	/**
	 * Retrieves the ID label of a link with a specified index. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * The ID label string should be sized to hold at least 32 characters.<br>
	 * <br>
	 * 
	 * Link indexes are consecutive integers starting from 1.
	 * 
	 * @param index link index
	 * @param id    ID label of link
	 * @return Returns an error code.
	 */
	int ENgetlinkid(int index, ByteBuffer id);

	/**
	 * Retrieves the link-type code for a specific link. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * 
	 * Link indexes are consecutive integers starting from 1.<br>
	 * <br>
	 * 
	 * Link type codes consist of the following constants:<br>
	 * 
	 * EN_CVPIPE 0 Pipe with Check Valve<br>
	 * EN_PIPE 1 Pipe<br>
	 * EN_PUMP 2 Pump<br>
	 * EN_PRV 3 Pressure Reducing Valve<br>
	 * EN_PSV 4 Pressure Sustaining Valve<br>
	 * EN_PBV 5 Pressure Breaker Valve<br>
	 * EN_FCV 6 Flow Control Valve<br>
	 * EN_TCV 7 Throttle Control Valve<br>
	 * EN_GPV 8 General Purpose Valve
	 * 
	 * @param index    link index
	 * @param typecode link-type code
	 * @return Returns an error code.
	 */
	int ENgetlinktype(int index, int[] typecode);

	/**
	 * Retrieves the indexes of the end nodes of a specified link. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Node and link indexes are consecutive integers starting from 1.<br>
	 * <br>
	 * 
	 * The From and To nodes are as defined for the link in the EPANET input file.
	 * The actual direction of flow in the link is not considered
	 * 
	 * @param index
	 * @param fromnode
	 * @param tonode
	 * @return Returns an error code.
	 */
	int ENgetlinknodes(int index, int[] fromnode, int[] tonode);

	/**
	 * Retrieves the value of a specific link parameter. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Link indexes are consecutive integers starting from 1.<br>
	 * <br>
	 * 
	 * Link parameter codes consist of the following constants:<br>
	 * 
	 * 
	 * EN_DIAMETER 0 Diameter<br>
	 * EN_LENGTH 1 Length<br>
	 * EN_ROUGHNESS 2 Roughness coeff.<br>
	 * EN_MINORLOSS 3 Minor loss coeff.<br>
	 * EN_INITSTATUS 4 Initial link status (0 = closed, 1 = open)<br>
	 * EN_INITSETTING 5 Roughness for pipes, initial speed for pumps, initial
	 * setting for valves<br>
	 * EN_KBULK 6 Bulk reaction coeff.<br>
	 * EN_KWALL 7 Wall reaction coeff.<br>
	 * EN_FLOW 8 Flow rate<br>
	 * EN_VELOCITY 9 Flow velocity<br>
	 * EN_HEADLOSS 10 Head loss<br>
	 * EN_STATUS 11 Actual link status (0 = closed, 1 = open)<br>
	 * EN_SETTING 12 Roughness for pipes, actual speed for pumps, actual setting for
	 * valves<br>
	 * EN_ENERGY 13 Energy expended in kwatts<br>
	 * <br>
	 * 
	 * Parameters 8 - 13 (EN_FLOW through EN_ENERGY) are computed values. The others
	 * are design parameters.<br>
	 * <br>
	 * 
	 * Flow rate is positive if the direction of flow is from the designated start
	 * node of the link to its designated end node, and negative otherwise.<br>
	 * <br>
	 * 
	 * Values are returned in units which depend on the units used for flow rate in
	 * the EPANET input file
	 * 
	 * @param index     link index
	 * @param paramcode parameter code
	 * @param value     parameter value
	 * @return Returns an error code.
	 */
	int ENgetlinkvalue(int index, int paramcode, float[] value);

	/**
	 * retrieves a number assigned to the most recent update of the source code.
	 * This number, set by the constant CODEVERSION found in TYPES.H, began with
	 * 20001 and increases by 1 with each new update.
	 * 
	 * @param version version number of the source code
	 * @return Returns an error code.
	 */
	int ENgetversion(int[] version);

	/**
	 * Sets the parameters of a simple control statement. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Controls are indexed starting from 1 in the order in which they were entered
	 * into the [CONTROLS] section of the EPANET input file.<br>
	 * <br>
	 * 
	 * Control type codes consist of the following:<br>
	 * 
	 * EN_LOWLEVEL 0 Control applied when tank level or node pressure drops below
	 * specified level<br>
	 * EN_HILEVEL 1 Control applied when tank level or node pressure rises above
	 * specified level<br>
	 * EN_TIMER 2 Control applied at specific time into simulation<br>
	 * EN_TIMEOFDAY 3 Control applied at specific time of day<br>
	 * <br>
	 * 
	 * For pipes, a setting of 0 means the pipe is closed and 1 means it is open.
	 * For a pump, the setting contains the pump's speed, with 0 meaning the pump is
	 * closed and 1 meaning it is open at its normal speed. For a valve, the setting
	 * refers to the valve's pressure, flow, or loss coefficient, depending on valve
	 * type.<br>
	 * <br>
	 * 
	 * For Timer or Time-of-Day controls set the nindex parameter to 0.<br>
	 * <br>
	 * 
	 * For level controls, if the controlling node nindex is a tank then the level
	 * parameter should be a water level above the tank bottom (not an elevation).
	 * Otherwise level should be a junction pressure.<br>
	 * <br>
	 * 
	 * To remove a control on a particular link, set the lindex parameter to 0.
	 * Values for the other parameters in the function will be ignored.
	 * 
	 * @param cindex  control statement index
	 * @param ctype   control type code
	 * @param lindex  index of link being controlled
	 * @param setting value of the control setting
	 * @param nindex  index of controlling node
	 * @param level   value of controlling water level or pressure for level
	 *                controls or of time of control action (in seconds) for
	 *                time-based controls
	 * @return Returns an error code.
	 */
	int ENsetcontrol(int cindex, int ctype, int lindex, float setting, int nindex, float level);

	/**
	 * Sets the value of a parameter for a specific node. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Node indexes are consecutive integers starting from 1.<br>
	 * <br>
	 * 
	 * Node parameter codes consist of the following constants:<br>
	 * 
	 * EN_ELEVATION 0 Elevation<br>
	 * EN_BASEDEMAND 1 Baseline demand<br>
	 * EN_PATTERN 2 Time pattern index<br>
	 * EN_EMITTER 3 Emitter coefficient<br>
	 * EN_INITQUAL 4 Initial quality<br>
	 * EN_SOURCEQUAL 5 Source quality<br>
	 * EN_SOURCEPAT 6 Source pattern<br>
	 * EN_SOURCETYPE 7 Source type: (See note below)<br>
	 * EN_TANKLEVEL 8 Initial water level in tank<br>
	 * <br>
	 * 
	 * 
	 * The following parameter codes apply only to storage tank nodes:<br>
	 * 
	 * EN_TANKDIAM 17 Tank diameter<br>
	 * EN_MINVOLUME 18 Minimum water volume<br>
	 * EN_MINLEVEL 20 Minimum water level<br>
	 * EN_MAXLEVEL 21 Maximum water level<br>
	 * EN_MIXMODEL 15 Mixing model code (see below)<br>
	 * EN_MIXFRACTION 22 Fraction of total volume occupied by the inlet/outlet zone
	 * in a 2-compartment tank<br>
	 * EN_TANK_KBULK 23 Bulk reaction rate coefficient<br>
	 * <br>
	 * 
	 * Source types are identified with the following constants:<br>
	 * 
	 * EN_CONCEN 0<br>
	 * EN_MASS 1<br>
	 * EN_SETPOINT 2<br>
	 * EN_FLOWPACED 3<br>
	 * <br>
	 * 
	 * See [SOURCES] for a description of these source types.<br>
	 * <br>
	 * 
	 * The codes for the various tank mixing model choices are as follows:<br>
	 * 
	 * EN_MIX1 0 Single compartment, complete mix model<br>
	 * EN_MIX2 1 Two-compartment, complete mix model<br>
	 * EN_FIFO 2 Plug flow, first in, first out model<br>
	 * EN_LIFO 3 Stacked plug flow, last in, first out model<br>
	 * <br>
	 * 
	 * Values are supplied in units which depend on the units used for flow rate in
	 * the EPANET input file.
	 * 
	 * @param index     node index
	 * @param paramcode parameter code
	 * @param value     parameter value
	 * @return Returns an error code.
	 */
	int ENsetnodevalue(int index, int paramcode, float value);

	/**
	 * Sets the value of a parameter for a specific link. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Link indexes are consecutive integers starting from 1.<br>
	 * <br>
	 * 
	 * Link parameter codes consist of the following constants:<br>
	 * EN_DIAMETER 0 Diameter<br>
	 * EN_LENGTH 1 Length<br>
	 * EN_ROUGHNESS 2 Roughness coeff.<br>
	 * EN_MINORLOSS 3 Minor loss coeff.<br>
	 * EN_INITSTATUS 4 Initial link status (0 = closed, 1 = open)<br>
	 * EN_INITSETTING 5 Roughness for pipes, initial speed for pumps, initial
	 * setting for valves<br>
	 * EN_KBULK 6 Bulk reaction coeff.<br>
	 * EN_KWALL 7 Wall reaction coeff.<br>
	 * EN_STATUS 11 Current pump or valve status (0 = closed, 1 = open)<br>
	 * EN_SETTING 12 Roughness for pipes, current speed for pumps, current setting
	 * for valves<br>
	 * 
	 * Values are supplied in units which depend on the units used for flow rate in
	 * the EPANET input file.<br>
	 * <br>
	 * 
	 * Use EN_INITSTATUS and EN_INITSETTING to set the design value for a link's
	 * status or setting that exists prior to the start of a simulation. Use
	 * EN_STATUS and EN_SETTING to change these values while a simulation is being
	 * run (within the ENrunH - ENnextH loop).<br>
	 * <br>
	 * 
	 * If a control valve has its status explicitly set to OPEN or CLOSED, then to
	 * make it active again during a simulation you must provide a new valve setting
	 * value using the EN_SETTING parameter.<br>
	 * <br>
	 * 
	 * For pipes, either EN_ROUGHNESS or EN_INITSETTING can be used to change
	 * roughness.
	 * 
	 * @param index     link index
	 * @param paramcode parameter code
	 * @param value     parameter value
	 * @return Returns an error code.
	 * 
	 */
	int ENsetlinkvalue(int index, int paramcode, float value);

	/**
	 * Adds a new time pattern to the network. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * The ID label should contain no more than 31 characters.<br>
	 * <br>
	 * 
	 * The new pattern will contain a single time period whose multiplier factor is
	 * 1.<br>
	 * <br>
	 * 
	 * Use the ENsetpattern function to populate the pattern with a specific set of
	 * multipliers after first retrieving its index with the ENgetpatternindex
	 * function.
	 * 
	 * @param id ID label of pattern
	 * @return Returns an error code.
	 */
	int ENaddpattern(String id);

	/**
	 * Sets all of the multiplier factors for a specific time pattern. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Pattern indexes are consecutive integers starting from 1.<br>
	 * <br>
	 * 
	 * Factors points to a zero-based array that contains nfactors elements.<br>
	 * <br>
	 * 
	 * Use this function to redefine (and resize) a time pattern all at once; use
	 * ENsetpatternvalue to revise pattern factors in specific time periods of a
	 * pattern.
	 * 
	 * @param index    time pattern index
	 * @param factors  multiplier factors for the entire pattern
	 * @param nfactors number of factors in the pattern
	 * @return Returns an error code.
	 */
	int ENsetpattern(int index, FloatBuffer factors, int nfactors);

	/**
	 * Sets the multiplier factor for a specific period within a time pattern. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Pattern indexes are consecutive integers starting from 1.<br>
	 * <br>
	 * 
	 * Use ENsetpattern to reset all of the factors in a time pattern.
	 * 
	 * @param index  time pattern index
	 * @param period period within time pattern
	 * @param value  multiplier factor for the period
	 * @return Returns an error code.
	 */
	int ENsetpatternvalue(int index, int period, float value);

	/**
	 * Sets the value of a time parameter. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Time parameter codes consist of the following constants:<br>
	 * 
	 * EN_DURATION 0 Simulation duration<br>
	 * EN_HYDSTEP 1 Hydraulic time step<br>
	 * EN_QUALSTEP 2 Water quality time step<br>
	 * EN_PATTERNSTEP 3 Time pattern time step<br>
	 * EN_PATTERNSTART 4 Time pattern start time<br>
	 * EN_REPORTSTEP 5 Reporting time step<br>
	 * EN_REPORTSTART 6 Report starting time<br>
	 * EN_RULESTEP 7 Time step for evaluating rule-based controls<br>
	 * EN_STATISTIC 8 Type of time series post-processing to use<br>
	 * <br>
	 * 
	 * The codes for EN_STATISTIC are:<br>
	 * 
	 * EN_NONE 0 none<br>
	 * EN_AVERAGE 1 averaged<br>
	 * EN_MINIMUM 2 minimums<br>
	 * EN_MAXIMUM 3 maximums<br>
	 * EN_RANGE 4 ranges<br>
	 * 
	 * Do not change time parameters after calling ENinitH in a hydraulic analysis
	 * or ENinitQ in a water quality analysis.
	 * 
	 * @param paramcode
	 * @param timevalue
	 * @return Returns an error code.
	 */
	int ENsettimeparam(int paramcode, Long timevalue);

	/**
	 * Sets the value of a particular analysis option. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * 
	 * Option codes consist of the following constants:<br>
	 * 
	 * EN_TRIALS 0<br>
	 * EN_ACCURACY 1<br>
	 * EN_TOLERANCE 2<br>
	 * EN_EMITEXPON 3<br>
	 * EN_DEMANDMULT 4<br>
	 * 
	 * @param optioncode an option code
	 * @param value      an option value
	 * @return Returns an error code.
	 */
	int ENsetoption(int optioncode, float value);

	/**
	 * Sets the level of hydraulic status reporting. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Status reporting writes changes in the hydraulics status of network elements
	 * to the Report file as a hydraulic simulation unfolds. There are three levels
	 * of reporting:<br>
	 * 
	 * 0 - no status reporting <br>
	 * 1 - normal reporting <br>
	 * 2 - full status reporting <br>
	 * <br>
	 * The full status report contains information on the convergence of each trial
	 * of the solution to the system hydraulic equations at each time step of a
	 * simulation. It is useful mainly for debugging purposes. <br>
	 * <br>
	 * If many hydraulic analyses will be run in the application it is recommended
	 * that status reporting be turned off (statuslevel = 0).
	 * 
	 * @param statuslevel level of status reporting
	 * @return Returns an error code.
	 */
	int ENsetstatusreport(int statuslevel);

	/**
	 * Sets the type of water quality analysis called for. <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * Water quality analysis codes are as follows: <br>
	 * EN_NONE 0 No quality analysis <br>
	 * EN_CHEM 1 Chemical analysis <br>
	 * EN_AGE 2 Water age analysis <br>
	 * EN_TRACE 3 Source tracing <br>
	 * <br>
	 * Chemical name and units can be an empty string if the analysis is not for a
	 * chemical. The same holds for the trace node if the analysis is not for source
	 * tracing.
	 * 
	 * Note that the trace node is specified by ID and not by index.
	 * 
	 * @param qualcode  water quality analysis code
	 * @param chemname  name of the chemical being analyzed
	 * @param chemunits units that the chemical is measured in
	 * @param tracenode ID of node traced in a source tracing analysis
	 * @return Returns an error code.
	 */
	int ENsetqualtype(int qualcode, ByteBuffer chemname, ByteBuffer chemunits, ByteBuffer tracenode);
}
