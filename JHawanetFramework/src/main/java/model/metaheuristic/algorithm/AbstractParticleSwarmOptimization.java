/*
 * Code take of from https://github.com/jMetal/jMetal
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
package model.metaheuristic.algorithm;

import epanet.core.EpanetException;
import model.metaheuristic.solution.Solution;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractParticleSwarmOptimization<S extends Solution<?>> implements Algorithm<S> {
    private List<S> swarm;
    private int step = 0;

    public List<S> getSwarm() {
        return swarm;
    }

    public void setSwarm(List<S> swarm) {
        this.swarm = swarm;
    }

    protected abstract void initProgress();

    protected abstract void updateProgress();

    protected abstract List<S> createInitialSwarm();

    protected abstract List<S> evaluateSwarm(List<S> swarm) throws EpanetException;

    protected abstract void initializeLeader(List<S> swarm);

    protected abstract void initializeParticlesMemory(List<S> swarm);

    protected abstract void initializeVelocity(List<S> swarm);

    protected abstract void updateVelocity(List<S> swarm);

    protected abstract void updatePosition(List<S> swarm);

    protected abstract void perturbation(List<S> swarm);

    protected abstract void updateLeaders(List<S> swarm);

    protected abstract void updateParticlesMemory(List<S> swarm);

    @Override
    public abstract @NotNull List<S> getResult();

//    @Override
//    public void run() throws EpanetException {
//        swarm = createInitialSwarm();
//        swarm = evaluateSwarm(swarm);
//        initializeVelocity(swarm);
//        initializeParticlesMemory(swarm);
//        initializeLeader(swarm);
//        initProgress();
//
//        while (!isStoppingConditionReached()) {
//            updateVelocity(swarm);
//            updatePosition(swarm);
//            perturbation(swarm);
//            swarm = evaluateSwarm(swarm);
//            updateLeaders(swarm);
//            updateParticlesMemory(swarm);
//            updateProgress();
//        }
//    }

    @Override
    public void runSingleStep() throws Exception, EpanetException {

        if (step == 0) {
            swarm = createInitialSwarm();
            swarm = evaluateSwarm(swarm);
            initializeVelocity(swarm);
            initializeParticlesMemory(swarm);
            initializeLeader(swarm);
            initProgress();
        }

        if (!isStoppingConditionReached()) {
            updateVelocity(swarm);
            updatePosition(swarm);
            perturbation(swarm);
            swarm = evaluateSwarm(swarm);
            updateLeaders(swarm);
            updateParticlesMemory(swarm);
            updateProgress();
        }

        this.step++;
    }
}