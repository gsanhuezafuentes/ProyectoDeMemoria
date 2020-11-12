/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org
 * (C) HydroloGIS - www.hydrologis.com
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package model.metaheuristic.problem.evaluator;

import epanet.core.*;
import epanet.core.types.*;

import java.util.ArrayList;
import java.util.List;

public class EpatoolForJava {

    public String inInp = null;

    public String tStart = "1970-01-01 00:00:00"; //$NON-NLS-1$
    public String tCurrent = null;

    public List<Pipe> pipesList = null;
    public List<Junction> junctionsList = null;
    public List<Pump> pumpsList = null;
    public List<Valve> valvesList = null;
    public List<Tank> tanksList = null;
    public List<Reservoir> reservoirsList = null;

	private int num_tanks;
    
    //NODES INDEX
    private final ArrayList<Integer> nodeIndex = new ArrayList<>(); // todos los nodos
    private final ArrayList<Integer> nodeDemandIndex = new ArrayList<>(); // nodos con Demanda
    private final ArrayList<Integer> tankIndex = new ArrayList<>(); // tanques
    
    //LINK INDEX
    public final ArrayList<Integer> pumpIndex = new ArrayList<>();
    private final ArrayList<Integer> linkIndex = new ArrayList<>();
    
    private final ArrayList<Double> listPressureNodesDiff = new ArrayList<>();
	private final ArrayList<Double> listPressureTanksDiff = new ArrayList<>();
	private final ArrayList<Double> listFlowratePumpsDiff = new ArrayList<>();
	private final ArrayList<Double> ListInfactibilidad = new ArrayList<>();
	


	double[][] energyConsumed = null;


    public String warnings = null;
    
    public boolean doProcess = false;

    private EpanetAPI ep;


    
	/**
	 * Global formatter for joda datetime (yyyy-MM-dd HH:mm).
	 */
/*	public static String utcDateFormatterYYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
	public static DateTimeFormatter formatter = DateTimeFormat.forPattern(utcDateFormatterYYYYMMDDHHMMSS).withZone(DateTimeZone.UTC);

    private DateTime current = null;*/

    public void initProcess() {
        // activate time
        doProcess = true;
    }

    public void openINP() throws Exception {		

        ep = new EpanetAPI();
        ep.ENopen(inInp, inInp + ".rpt", "");

		int num_nodes = ep.ENgetcount(Components.EN_NODECOUNT);
		int num_links = ep.ENgetcount(Components.EN_LINKCOUNT);
        
        for (int i = 1; i <= num_nodes; i++) {
			if(ep.ENgetnodetype(i)==NodeTypes.EN_JUNCTION) {
				nodeIndex.add(i);
			}
			
			if(ep.ENgetnodetype(i)==NodeTypes.EN_JUNCTION && ep.ENgetnodevalue(i, NodeParameters.EN_BASEDEMAND)>0) {
				nodeDemandIndex.add(i);
			}
			else if (ep.ENgetnodetype(i)==NodeTypes.EN_TANK) {
				tankIndex.add(i);
			}
		}
        
        for (int i = 1; i <= num_links; i++) {
        	if(ep.ENgetlinktype(i) == LinkTypes.EN_PUMP) {
        		pumpIndex.add(i);
        	}
        	else if(ep.ENgetlinktype(i)== LinkTypes.EN_PIPE) {
        		linkIndex.add(i);
        	}
		}     
        System.out.println("Se han detectado "+ pumpIndex.size() + " bombas ");
	}



    /*Simulacion Hidraulica de una solucion*/
    public void hidraulicSimulation(int[][] solution, double timeInterval, double minPressure, double[] maxFlowrateEachPump) throws EpanetException {
    	
    	this.energyConsumed= new double[solution.length][solution[0].length];
		this.listPressureNodesDiff.clear();
		this.listPressureTanksDiff.clear();
		this.listFlowratePumpsDiff.clear();
		this.ListInfactibilidad.clear();
		
		
    	long[] tstep = {1};
		long[] t = {0};
		int hours = 0;
		double[] initialTankLevel = new double[tankIndex.size()];
		int k = 0;
		
		ep.ENopenH();
		ep.ENinitH(0);
		
		// nivel inicial de los tanques
		for(int i: tankIndex){
			initialTankLevel[k] = ep.ENgetnodevalue(i, NodeParameters.EN_TANKLEVEL);
			k++;
		}

		do {
			if(t[0]%timeInterval == 0) {
				// Cambiamos el estado de las bombas de acuerdo a la configuracion de la solucion
				for (int i=0; i < this.pumpIndex.size(); i++) {
					ep.ENsetlinkvalue(this.pumpIndex.get(i), LinkParameters.EN_STATUS, solution[i][hours]);
				}

				// Simulacion hidraulica en tiempo t
				ep.ENrunH(t);
				String w = ep.getWarningMessage();
			
				// Calculo de la energia consumida por la bombas
				for (int i=0; i < this.pumpIndex.size(); i++) {
					double value = ep.ENgetlinkvalue(this.pumpIndex.get(i), LinkParameters.EN_ENERGY)[0];
					energyConsumed[i][hours] = value;
				}
				
				// calculo de presion en los nodos con demanda
				for (int i : nodeDemandIndex) {
					double value = ep.ENgetnodevalue(i, NodeParameters.EN_PRESSURE);
					if(value < minPressure) {
						this.listPressureNodesDiff.add(Math.abs(value - minPressure));
					}
				}
				
				// Calculo de caudal en bombas. Se penaliza si existe un flujo m�ximo al establecido
				int pump =0;
				for (int i : pumpIndex) {
					double value = ep.ENgetlinkvalue(i, LinkParameters.EN_FLOW )[0];
					if (value > maxFlowrateEachPump[pump]) {
						this.listFlowratePumpsDiff.add(Math.abs(value-maxFlowrateEachPump[pump]));
					}
					pump ++;
				}
				hours++;
			}
			else {
				
				// Simulacion hidraulica en tiempo t
				ep.ENrunH(t);
				// en el caso de que la simulacion se detenga en medio una hora, se penaliza el valor de las presiones negativas
				// en los nodos con demanda superior a 0.0
				
				//ListInfactibilidad.add((double) Penalty);
			
			}

			ep.ENnextH(tstep);
			
		} while (tstep[0] > 0);
		
		k = 0;
		for(int i: tankIndex) {
			double value = ep.ENgetnodevalue(i, NodeParameters.EN_PRESSURE);
			if((initialTankLevel[k])> value){
				listPressureTanksDiff.add(Math.abs(initialTankLevel[k]-value));
			}
			k++;
		}

		
		ep.ENcloseH();
		
    }
    
	
	
	public void finish() throws EpanetException {
        ep.ENcloseH();
        ep.ENclose();
    }

	public ArrayList<Double> getListPressureNodesDiff() {
		return this.listPressureNodesDiff;
	}
	
	public ArrayList<Double> getlistPressureTanksDiff() {
		return this.listPressureTanksDiff;
	}
	
	public ArrayList<Double> getlistFlowratePumpsDiff() {
		return this.listFlowratePumpsDiff;
	}
	public double[][] getEnergyConsumed(){
		return this.energyConsumed;
	}
	
	public ArrayList<Double> getListInfactibilidad() {
		return ListInfactibilidad;
	}
	
    
}
