/*
 * Base on code from https://github.com/jMetal/jMetal
 *
 * Copyright <2017> <Antonio J. Nebro, Juan J. Durillo>
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to
 * whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. © 2019
 * GitHub, Inc.
 */
package model.metaheuristic.algorithm.multiobjective.smpso;

import epanet.core.EpanetException;
import model.metaheuristic.algorithm.AbstractParticleSwarmOptimization;
import model.metaheuristic.operator.mutation.MutationOperator;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.solution.impl.IntegerSolution;
import model.metaheuristic.util.archive.BoundedArchive;
import model.metaheuristic.util.evaluator.SolutionListEvaluator;
import model.metaheuristic.util.random.JavaRandom;
import model.metaheuristic.util.solutionattribute.SolutionAttribute;

import java.util.ArrayList;
import java.util.List;

public class SMPSOInteger extends AbstractParticleSwarmOptimization<IntegerSolution> {

    private final Problem<IntegerSolution> problem;
    private final int swarmSize;
    private final BoundedArchive<IntegerSolution> leaders;

    private final MutationOperator<IntegerSolution> mutation;
    private final int maxIterations;

    private final double c1Max;
    private final double c1Min;
    private final double c2Max;
    private final double c2Min;
    private final double r1Max;
    private final double r1Min;
    private final double r2Max;
    private final double r2Min;
    private final double weightMax;
    private final double weightMin;
    private final double changeVelocity1;
    private final double changeVelocity2;

    private final JavaRandom randomGenerator;
    private final SolutionListEvaluator<IntegerSolution> evaluator;

    private final SolutionAttribute<IntegerSolution, IntegerSolution> localBest;
    private final double[][] speed;

    private final double[] deltaMax;
    private final double[] deltaMin;

    private int iterations;

    public SMPSOInteger(Problem<IntegerSolution> problem, int swarmSize, BoundedArchive<IntegerSolution> leaders,
                        MutationOperator<IntegerSolution> mutationOperator, int maxIterations, double r1Min, double r1Max,
                        double r2Min, double r2Max, double c1Min, double c1Max, double c2Min, double c2Max, double weightMin,
                        double weightMax, double changeVelocity1, double changeVelocity2,
                        SolutionListEvaluator<IntegerSolution> evaluator) {

        this.problem = problem;
        this.swarmSize = swarmSize;
        this.leaders = leaders;

        this.mutation = mutationOperator;
        this.maxIterations = maxIterations;

        this.c1Max = c1Max;
        this.c1Min = c1Min;
        this.c2Max = c2Max;
        this.c2Min = c2Min;
        this.r1Max = r1Max;
        this.r1Min = r1Min;
        this.r2Max = r2Max;
        this.r2Min = r2Min;
        this.weightMax = weightMax;
        this.weightMin = weightMin;
        this.changeVelocity1 = changeVelocity1;
        this.changeVelocity2 = changeVelocity2;

        randomGenerator = JavaRandom.getInstance();
        this.evaluator = evaluator;

        localBest = new SolutionAttribute<>();
        speed = new double[swarmSize][problem.getNumberOfVariables()];

        deltaMax = new double[problem.getNumberOfVariables()];
        deltaMin = new double[problem.getNumberOfVariables()];
        for (int i = 0; i < problem.getNumberOfVariables(); i++) {
            deltaMax[i] = (problem.getUpperBound(i) - problem.getLowerBound(i)) / 2.0;
            deltaMin[i] = -deltaMax[i];
        }

    }

    @Override
    public String getName() {
        return "SMPSOInteger";
    }

    protected void updateLeadersDensityEstimator() {
        leaders.computeDensityEstimator();
    }

    @Override
    protected void initProgress() {
        iterations = 1;
        updateLeadersDensityEstimator();
    }

    @Override
    protected void updateProgress() {
        iterations += 1;
        updateLeadersDensityEstimator();
    }

    @Override
    public boolean isStoppingConditionReached() {
        return iterations >= maxIterations;
    }

    @Override
    public String getStatusOfExecution() {
        return "Number of evaluations: " + this.iterations + " / " + this.maxIterations;
    }

    @Override
    protected List<IntegerSolution> createInitialSwarm() {
        List<IntegerSolution> swarm = new ArrayList<>(swarmSize);
        IntegerSolution newSolution;
        for (int i = 0; i < swarmSize; i++) {
            newSolution = problem.createSolution();
            swarm.add(newSolution);
        }
        return swarm;
    }

    @Override
    protected List<IntegerSolution> evaluateSwarm(List<IntegerSolution> swarm) throws EpanetException {
        swarm = evaluator.evaluate(swarm, problem);
        return swarm;
    }

    @Override
    protected void initializeLeader(List<IntegerSolution> swarm) {
        for (IntegerSolution particle : swarm) {
            leaders.add(particle);
        }
    }

    @Override
    protected void initializeParticlesMemory(List<IntegerSolution> swarm) {
        for (IntegerSolution particle : swarm) {
            localBest.setAttribute(particle, (IntegerSolution) particle.copy());
        }
    }

    @Override
    protected void initializeVelocity(List<IntegerSolution> swarm) {
        for (int i = 0; i < swarm.size(); i++) {
            for (int j = 0; j < problem.getNumberOfVariables(); j++) {
                speed[i][j] = 0.0;
            }
        }
    }

    protected IntegerSolution selectGlobalBest() {
        IntegerSolution one, two;
        IntegerSolution bestGlobal;
        int pos1 = randomGenerator.nextInt(0, leaders.getSolutionList().size());
        int pos2 = randomGenerator.nextInt(0, leaders.getSolutionList().size());
        one = leaders.getSolutionList().get(pos1);
        two = leaders.getSolutionList().get(pos2);

        if (leaders.getComparator().compare(one, two) < 1) {
            bestGlobal = (IntegerSolution) one.copy();
        } else {
            bestGlobal = (IntegerSolution) two.copy();
        }

        return bestGlobal;
    }

    private double velocityConstriction(double v, double[] deltaMax, double[] deltaMin, int variableIndex) {
        double result;
        double dmax = deltaMax[variableIndex];
        double dmin = deltaMin[variableIndex];
        result = v;
        if (v > dmax) {
            result = dmax;
        }
        if (v < dmin) {
            result = dmin;
        }
        return result;
    }

    private double constrictionCoefficient(double c1, double c2) {
        double rho = c1 + c2;
        if (rho <= 4) {
            return 1.0;
        } else {
            return 2 / (2 - rho - Math.sqrt(Math.pow(rho, 2.0) - 4.0 * rho));
        }
    }

    private double inertiaWeight(int iter, int miter, double wma, double wmin) {
        return wma;
    }

    @Override
    protected void updateVelocity(List<IntegerSolution> swarm) {
        double r1, r2, c1, c2;
        double wmax, wmin;
        IntegerSolution bestGlobal;

        for (int i = 0; i < swarm.size(); i++) {
            IntegerSolution particle = (IntegerSolution) swarm.get(i).copy();
            IntegerSolution bestParticle = (IntegerSolution) localBest.getAttribute(swarm.get(i)).copy();

            bestGlobal = selectGlobalBest();

            r1 = randomGenerator.nextDouble(r1Min, r1Max);
            r2 = randomGenerator.nextDouble(r2Min, r2Max);
            c1 = randomGenerator.nextDouble(c1Min, c1Max);
            c2 = randomGenerator.nextDouble(c2Min, c2Max);
            wmax = weightMax;
            wmin = weightMin;

            for (int var = 0; var < particle.getNumberOfVariables(); var++) {
                speed[i][var] = velocityConstriction(constrictionCoefficient(c1, c2) * (
                                inertiaWeight(iterations, maxIterations, wmax, wmin) * speed[i][var] +
                                        c1 * r1 * (bestParticle.getVariable(var) - particle.getVariable(var)) +
                                        c2 * r2 * (bestGlobal.getVariable(var) - particle.getVariable(var))),
                        deltaMax, deltaMin, var);
            }
        }
    }

    @Override
    protected void updatePosition(List<IntegerSolution> swarm) {
        for (int i = 0; i < swarmSize; i++) {
            IntegerSolution particle = swarm.get(i);
            for (int j = 0; j < particle.getNumberOfVariables(); j++) {
                particle.setVariable(j, particle.getVariable(j) + (int)speed[i][j]);
                if (particle.getVariable(j) < problem.getLowerBound(j)) {
                    particle.setVariable(j, (int) problem.getLowerBound(j));
                    speed[i][j] = speed[i][j] * changeVelocity1;
                }
                if (particle.getVariable(j) > problem.getUpperBound(j)) {
                    particle.setVariable(j, (int) problem.getUpperBound(j));
                    speed[i][j] = speed[i][j] * changeVelocity2;
                }
            }
        }
    }

    @Override
    protected void perturbation(List<IntegerSolution> swarm) {
        for (int i = 0; i < swarm.size(); i++) {
            if ((i % 6) == 0) {
                mutation.execute(swarm.get(i));
            }
        }
    }

    @Override
    protected void updateLeaders(List<IntegerSolution> swarm) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void updateParticlesMemory(List<IntegerSolution> swarm) {
        for (IntegerSolution particle : swarm) {
            leaders.add((IntegerSolution)particle.copy());
        }
    }

    @Override
    public List<IntegerSolution> getResult() {
        return leaders.getSolutionList();
    }
}

