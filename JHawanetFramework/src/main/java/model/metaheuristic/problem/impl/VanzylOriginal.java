package model.metaheuristic.problem.impl;

import epanet.core.EpanetException;
import model.epanet.element.Network;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.problem.evaluator.EpatoolForJava;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.solution.impl.IntegerSolution;
import model.metaheuristic.utils.solutionattribute.NumberOfViolatedConstraints;
import model.metaheuristic.utils.solutionattribute.OverallConstraintViolation;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class VanzylOriginal implements Problem<IntegerSolution> {


    private static final long serialVersionUID = 1L;
    public OverallConstraintViolation<IntegerSolution> overallConstraintViolationDegree;
    public NumberOfViolatedConstraints<IntegerSolution> numberOfViolatedConstraints;
    private InputPse inputPse;

    protected int numPumps;
    protected int numInterval;
    protected int[][] combination;
    protected int totalOptimizationTime;
    protected int intervalOptimizationTime;
    protected double[] energyCostPerTime;
    protected double maintenanceCost;
    protected int minNodePressure;
    protected double[] minTank;
    protected double[] maxTank;
    protected double[] maxFlowrateEachPump;
    static EpatoolForJava epatool;

    private int numberOfObjectives;
    private int numberOfConstrains;
    private int numberOfVariables;
    private String problemName;
    private List<Integer> lowerLimmit;
    private List<Integer> upperLimit;

    public VanzylOriginal(String psePath, String inpPath) {

        this.inputPse = new InputPse();
        this.inputPse.loadFile(psePath);

        this.numPumps = this.inputPse.getNumPumps();
        this.totalOptimizationTime = this.inputPse.getTotalOptimizationTime();
        this.intervalOptimizationTime = this.inputPse.getIntervalOptimizationTime();
        this.energyCostPerTime = this.inputPse.getEnergyCostPerTime();
        this.maintenanceCost = this.inputPse.getMaintenanceCost();
        this.minNodePressure = this.inputPse.getMinNodePressure();
        int numConstraints = this.inputPse.getNumConstraints();
        this.maxFlowrateEachPump = this.inputPse.getMaxFlowrateEachPump();

        // REVISAR
        this.minTank = this.inputPse.getMinTank();
        this.maxTank = this.inputPse.getMaxTank();

        this.numInterval = (int) (totalOptimizationTime / intervalOptimizationTime);

        problemConfiguration(numConstraints); // Configuracion del problema

        this.epatool = new EpatoolForJava();
        epatool.inInp = inpPath;

        try {
            epatool.openINP();
        } catch (Exception e) {
            System.out.println("Error al abrir INP");
        }

        generateCombinations(numPumps); // Genera una lista de posibles configuraciones del funcionamiento de las bombas

    }

    public VanzylOriginal(int numPumps, int totalOptimizationTime, int intervalOptimizationTime,
                          double[] energyCostPerTime, double maintenanceCost, int minNodePressure, int numConstraints,
                          double[] minTank, double[] maxTank, double[] maxFlowrateEachPump, String inpPath) {

        this.numPumps = numPumps;
        this.totalOptimizationTime = totalOptimizationTime;
        this.intervalOptimizationTime = intervalOptimizationTime;
        this.energyCostPerTime = energyCostPerTime;
        this.maintenanceCost = maintenanceCost;
        this.minNodePressure = minNodePressure;
        this.minTank = minTank;
        this.maxTank = maxTank;
        this.numInterval = (int) (totalOptimizationTime / intervalOptimizationTime);
        this.maxFlowrateEachPump = maxFlowrateEachPump;

        problemConfiguration(numConstraints); // Configuracion del problema

        this.epatool = new EpatoolForJava();
        epatool.inInp = inpPath;

        try {
            epatool.openINP();
        } catch (Exception e) {
            System.out.println("Error al abrir INP");
        }

        generateCombinations(numPumps); // Genera una lista de posibles configuraciones del funcionamiento de las bombas

    }

    @Override
    public void evaluate(IntegerSolution solution) {
//		System.out.println(solution);

        int[][] binaryMatrixSolution;
        binaryMatrixSolution = generateBinaryMatrix(solution);

//		int[][]  binaryMatrixSolution = {{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//										 {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//										 {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}}; 
//				
        try {
            epatool.hidraulicSimulation(binaryMatrixSolution, this.intervalOptimizationTime, this.minNodePressure,
                    this.maxFlowrateEachPump);
        } catch (EpanetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        double fitness1 = 0;
        double fitness2 = 0;

        fitness1 = energyCost(binaryMatrixSolution);
        fitness2 = maintenanceCost(binaryMatrixSolution);
//		System.out.println("");

//		System.out.println("F1: " + fitness1 + " F2:" + fitness2);

        solution.setObjective(0, fitness1);
        solution.setObjective(1, fitness2);

        double overallConstraintViolation = 0.0;
        int violatedConstraints = 0;

        ArrayList<Double> listNodeViolated;
        listNodeViolated = epatool.getListPressureNodesDiff();

        ArrayList<Double> listPumpsViolated;
        listPumpsViolated = epatool.getlistFlowratePumpsDiff();

        ArrayList<Double> listTanksViolated;
        listTanksViolated = epatool.getlistPressureTanksDiff();

        ArrayList<Double> listInfactibilidad;
        listInfactibilidad = epatool.getListInfactibilidad();

        for (Double violation : listNodeViolated) {
            overallConstraintViolation -= violation;
        }

        for (Double violation : listPumpsViolated) {
            overallConstraintViolation -= violation;
        }

        for (Double violation : listTanksViolated) {
            overallConstraintViolation -= violation;
        }

        for (Double violation : listInfactibilidad) {
            overallConstraintViolation -= violation;
        }

        overallConstraintViolation = Math.round(overallConstraintViolation * 100.0) / 100.0;
        violatedConstraints = listNodeViolated.size() + listPumpsViolated.size() + listTanksViolated.size();

//		System.out.println(
//				"Total de violaciones: " + overallConstraintViolation + "Num Violaciones " + violatedConstraints);
//		System.out.println("Violacion en nodos: " + listNodeViolated.size());
//		System.out.println("Violacion en bombas: " + listPumpsViolated.size());
//		System.out.println("Violacion en tanques: " + listTanksViolated.size());
//		System.out.println("");

        overallConstraintViolationDegree.setAttribute(solution, overallConstraintViolation);
        numberOfViolatedConstraints.setAttribute(solution, violatedConstraints);
    }

    /*
     * Calculo del costo energetico de las bombas (Makaremi, Y., Haghighi, A., &
     * Ghafouri, H. R. (2017). Optimization of pump scheduling program in water
     * supply systems using a self-adaptive NSGA-II; a review of theory to real
     * application. Water resources management, 31(4), 1283-1304.) donde CE = CD +
     * CC, sin embargo para el caso de la red Vanzyl CD (demand charge = 0)
     */
    private double energyCost(int[][] BinaryMatrix) {
        double Cc = 0;
        double[][] Ec = null;
        double[] Pc;
        int[][] S;

        Pc = energyCostPerTime;
        Ec = epatool.getEnergyConsumed();

        S = BinaryMatrix;

        for (int n = 0; n < numPumps; n++) {
            for (int t = 0; t < numInterval; t++) {
                Cc += (Pc[t] * Ec[n][t] * S[n][t]);
            }
        }

        return Math.round(Cc * 100.0) / 100.0;
    }

    /*
     * Calcula el costo de mantencion de las bombas
     */
    private double maintenanceCost(int[][] binaryMatrixSolution) {
        double maintenanceCost = 0;

        for (int i = 0; i < binaryMatrixSolution.length; i++) {
            for (int j = 1; j < binaryMatrixSolution[0].length; j++) {
                // if(binaryMatrixSolution[i][j-1] != binaryMatrixSolution[i][j]) {
                if (binaryMatrixSolution[i][j - 1] == 0 && binaryMatrixSolution[i][j] == 1) {
                    maintenanceCost += this.maintenanceCost;
                }
            }
        }

        return maintenanceCost;
    }

    /*
     * Transforma la una solucion "comprimida" a la version real en matrices para
     * PSMO
     */
    private int[][] generateBinaryMatrix(IntegerSolution solution) {

        int[][] matrix;
        int numCombinations;
        int numVariables;
        numVariables = getNumberOfVariables();
        // int paperSolution[] =
        // {5,5,5,5,13,13,13,13,13,13,13,47,63,47,47,47,47,47,47,47,5,5,5,5};
        matrix = new int[this.numPumps][numVariables];

        for (int i = 0; i < numVariables; i++) {

            numCombinations = solution.getVariable(i);
            // numCombinations = paperSolution[i];
            for (int j = 0; j < numPumps; j++) {
                int a = combination[numCombinations][j];
                matrix[j][i] = a;
            }
        }

//		for (int i = 0; i < matrix.length; i++) {
//			for (int j = 0; j < matrix[0].length; j++) {
//				System.out.print(matrix[i][j]+ " ");
//			}
//			System.out.println("");
//		}
//        System.out.println("-------------------------------");
        return matrix;
    }

    /**
     * Crea una matrix con las combinaciones posibles.
     * <p>
     * Example of a network of 3 pumps.
     * <p>
     * 0 0 0
     * 1 0 0
     * 0 1 0
     * 1 1 0
     * 0 0 1
     * 1 0 1
     * 0 1 1
     * 1 1 1
     */
    private void generateCombinations(int numPumps) {
        combination = new int[(int) Math.pow(2, numPumps)][numPumps];
        int spin = 0;
        int spin_1 = 0;
        int spin_2 = 0;

        for (int k = 0; k < numPumps; k++) {
            spin = 1;
            spin_1 = (int) Math.pow(2, k);

            for (int l = 0; l < (int) Math.pow(2, numPumps); l++) {
                if (spin <= spin_1) {
                    combination[l][k] = 0;
                    spin++;
                    spin_2 = 1;
                } else {
                    combination[l][k] = 1;
                    spin_2++;
                    if (spin_2 > spin_1) {
                        spin = 1;
                    }
                }
            }
        }

       /* for (int i = 0; i < combination.length; i++) {
            for (int j = 0; j <
                    combination[0].length; j++) {
                System.out.print(combination[i][j] + " ");
            }
            System.out.println("");
        }

        System.out.println("");*/

    }


    protected void setLowerLimit(List<Integer> lowerLimit) {
        // TODO Auto-generated method stub
        this.lowerLimmit = lowerLimit;
    }

    protected void setUpperLimit(List<Integer> upperLimit) {
        // TODO Auto-generated method stub
        this.upperLimit = upperLimit;

    }

    private void problemConfiguration(int numConstraints) {

        overallConstraintViolationDegree = new OverallConstraintViolation<IntegerSolution>();
        numberOfViolatedConstraints = new NumberOfViolatedConstraints<IntegerSolution>();
        setNumberOfObjectives(2);
        setNumberOfVariables(24);
        setName("VanzylOriginal");
        setNumberOfConstraints(numConstraints);

        List<Integer> up = new ArrayList<Integer>();
        List<Integer> low = new ArrayList<Integer>();

        for (int i = 0; i < 24; i++) {
            up.add((int) (Math.pow(2, this.numPumps) - 1));
            low.add(0);
        }

        setLowerLimit(low);
        setUpperLimit(up);

    }

    public static class InputPse {

        private HashMap<String, ArrayList<String>> labelValues;

        /*
         * Constructor: Se inicializa HashMap "labelValues" con: keys: igual a
         * etiquetas, ej: [PumpEnergy] values: igual a un arreglo vacio. El contenido de
         * dichos arreglos será los valores
         */
        public InputPse() {
            this.labelValues = new HashMap<String, ArrayList<String>>();
            for (VariablePse label : VariablePse.values()) {
                this.labelValues.put(label.getLabel(), new ArrayList<String>());
            }
        }

        public void loadFile(String filePath) {
            List<String> list = new ArrayList<String>();
            try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
                list = br.lines().collect(Collectors.toList());
                System.out.println(list);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ArrayList<String> arraySelected = new ArrayList<String>();
            for (String line : list) {
                line = line.trim();
                if (!line.isEmpty()) {
                    if (this.labelValues.containsKey(line)) // linea: es igual a una etiqueta (label)
                        arraySelected = this.labelValues.get(line);// se selecciona el arreglo de valores según
                        // etiqueta
                    else // linea: es igual a un valor
                        arraySelected.add(getValueFrom(line));// se agrega 1 valor al arreglo que se encuentre
                    // seleccionado
                }
            }
        }

        private List<String> getValuesFrom(VariablePse variable) {
            return this.labelValues.get(variable.getLabel());
        }

        private String getValueFrom(String line) {
            String[] specialChars = {" ", "\t"}; // son los carácteres que pueden separar una linea, ej: "Pump1
            // 595.00"
            for (String specialChar : specialChars) {
                if (line.contains(specialChar))
                    return line.split(specialChar)[1];
            }
            return "";
        }

        public int getNumPumps() {
            return this.getValuesFrom(VariablePse.PUMPENERGY).size();
        }

        public int getTotalOptimizationTime() {
            String totalOptimizationTimeString = this.getValuesFrom(VariablePse.TOTALOPTIMIZATIONTIME).get(0);
            return Integer.parseInt(totalOptimizationTimeString);
        }

        public int getIntervalOptimizationTime() {
            String intervalOptimizationTimeString = this.getValuesFrom(VariablePse.INTERVALOPTIMIZATIONTIME).get(0);
            return Integer.parseInt(intervalOptimizationTimeString);
        }

        public double[] getEnergyCostPerTime() {
            List<String> energyCostPerTimeList = this.getValuesFrom(VariablePse.COSTENERGY);
            double[] energyCostPerTime = new double[energyCostPerTimeList.size()];
            for (int i = 0; i < energyCostPerTimeList.size(); i++) {
                double costEnergy = Double.parseDouble(energyCostPerTimeList.get(i));
                energyCostPerTime[i] = costEnergy;
            }
            return energyCostPerTime;
        }

        public double getMaintenanceCost() {
            String maintenanceCostString = this.getValuesFrom(VariablePse.MAINTENANCECOST).get(0);
            return Double.parseDouble(maintenanceCostString);
        }

        public int getMinNodePressure() {
            String minNodePressureString = this.getValuesFrom(VariablePse.INTERVALNODEPRESSURE).get(0);
            return Integer.parseInt(minNodePressureString);
        }

        public int getNumConstraints() {
            String numConstraintsString = this.getValuesFrom(VariablePse.MAINTENANCECOST).get(0);
            return Integer.parseInt(numConstraintsString);
        }

        public double[] getMaxFlowrateEachPump() {
            List<String> maxFlowrateEachPumpList = this.getValuesFrom(VariablePse.MAXFLOWRATEEACHPUMP);
            double[] maxFlowrateEachPump = new double[maxFlowrateEachPumpList.size()];
            for (int i = 0; i < maxFlowrateEachPumpList.size(); i++) {
                double maxFlowrate = Double.parseDouble(maxFlowrateEachPumpList.get(i));
                maxFlowrateEachPump[i] = maxFlowrate;
            }
            return maxFlowrateEachPump;
        }

        public double[] getMinTank() {
            String minTankString = this.getValuesFrom(VariablePse.INTERVALTANKPRESSURE).get(0);
            return new double[]{Integer.parseInt(minTankString), 0};
        }

        public double[] getMaxTank() {
            String maxTankString = this.getValuesFrom(VariablePse.INTERVALTANKPRESSURE).get(1);
            return new double[]{Integer.parseInt(maxTankString), 5};
        }
    }

    private enum VariablePse {
        PUMPENERGY("[PumpEnergy]"), COSTENERGY("[CostEnergy]"), ADITIONALCOST("[AditionalCost]"),
        MAXIMCONSUMPTIONPERDAY("[MaximConsumptionPerDay]"), MAXIMCONSUMPTIONPERHOUR("[MaximConsumptionPerHour]"),
        MAINTENANCECOST("[MaintenanceCost]"), INTERVALOPTIMIZATIONTIME("[IntervalOptimizationTime]"),
        TOTALOPTIMIZATIONTIME("[TotalOptimizationTime]"), INTERVALTANKPRESSURE("[IntervalTankPressure]"),
        INTERVALNODEPRESSURE("[IntervalNodePressure]"), NUMCONSTRAINTS("[NumConstraints]"),
        MAXFLOWRATEEACHPUMP("[MaxFlowrateEachPump]");

        private final String label;

        VariablePse(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    @Override
    public int getNumberOfVariables() {
        // TODO Auto-generated method stub
        return this.numberOfVariables;
    }

    private void setNumberOfVariables(int i) {
        this.numberOfVariables = i;
    }

    @Override
    public int getNumberOfObjectives() {
        // TODO Auto-generated method stub
        return this.numberOfObjectives;
    }

    private void setNumberOfObjectives(int i) {
        this.numberOfObjectives = i;
    }

    @Override
    public int getNumberOfConstraints() {
        // TODO Auto-generated method stub
        return this.numberOfConstrains;
    }

    private void setNumberOfConstraints(int numConstraints) {
        this.numberOfConstrains = numConstraints;
    }

    @Override
    public IntegerSolution createSolution() {
        IntegerSolution solution = new IntegerSolution(this);

        return solution;
    }

    @Override
    public double getLowerBound(int index) {
        return this.lowerLimmit.get(index);
    }

    @Override
    public double getUpperBound(int index) {
        // TODO Auto-generated method stub
        return this.upperLimit.get(index);
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return this.problemName;
    }

    private void setName(String string) {
        this.problemName = string;
    }

}
