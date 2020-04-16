package model.metaheuristic.experiment;

import java.io.IOException;

/**
 * An experiment is composed of instances of this interface.
 */
public interface ExperimentComponent {
  void run() throws IOException;
}
