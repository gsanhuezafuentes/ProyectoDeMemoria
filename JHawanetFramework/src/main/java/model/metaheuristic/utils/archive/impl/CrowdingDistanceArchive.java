package model.metaheuristic.utils.archive.impl;

import model.metaheuristic.solution.Solution;
import model.metaheuristic.utils.SolutionListUtils;
import model.metaheuristic.utils.comparator.CrowdingDistanceComparator;
import model.metaheuristic.utils.solutionattribute.CrowdingDistance;

import java.util.Collections;
import java.util.Comparator;

public class CrowdingDistanceArchive<S extends Solution<?>> extends AbstractBoundedArchive<S> {
    private Comparator<S> crowdingDistanceComparator;
    private CrowdingDistance<S> crowdingDistance ;

    public CrowdingDistanceArchive(int maxSize) {
        super(maxSize);
        crowdingDistanceComparator = new CrowdingDistanceComparator<S>() ;
        crowdingDistance = new CrowdingDistance<S>() ;
    }

    /**{@inheritDoc}*/
    @Override
    public void prune() {
        if (getSolutionList().size() > getMaxSize()) {
            computeDensityEstimator();
            S worst = SolutionListUtils.findWorstSolution(getSolutionList(), crowdingDistanceComparator) ;
            getSolutionList().remove(worst);
        }
    }

    /**{@inheritDoc}*/
    @Override
    public Comparator<S> getComparator() {
        return crowdingDistanceComparator ;
    }

    /**{@inheritDoc}*/
    @Override
    public void computeDensityEstimator() {
        crowdingDistance.computeDensityEstimator(getSolutionList());
    }

    /**{@inheritDoc}*/
    @Override
    public void sortByDensityEstimator() {
        Collections.sort(getSolutionList(), new CrowdingDistanceComparator<S>());
    }
}
