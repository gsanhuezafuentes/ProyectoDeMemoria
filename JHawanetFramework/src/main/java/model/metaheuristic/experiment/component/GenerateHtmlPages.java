package model.metaheuristic.experiment.component;

import model.metaheuristic.experiment.Experiment;
import model.metaheuristic.experiment.ExperimentComponent;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.util.visualization.StudyVisualizer;

import java.io.IOException;
import java.util.List;

/**
 * This class executes a StudyVisualizer on the experiment provided.
 *
 * <p>The results are created in in the directory {@link Experiment *
 * #getExperimentBaseDirectory()}/html.
 *
 * @author Javier Pérez
 */
public class GenerateHtmlPages<Result extends List<? extends Solution<?>>>
    implements ExperimentComponent {

  private final Experiment<?, Result> experiment;
  private StudyVisualizer.TYPE_OF_FRONT_TO_SHOW defaultTypeOfFrontToShow;

  public GenerateHtmlPages(Experiment<?, Result> experimentConfiguration) {
    this(experimentConfiguration, StudyVisualizer.TYPE_OF_FRONT_TO_SHOW.BEST);
  }

  public GenerateHtmlPages(
      Experiment<?, Result> experimentConfiguration,
      StudyVisualizer.TYPE_OF_FRONT_TO_SHOW defaultTypeOfFrontToShow) {
    this.experiment = experimentConfiguration;
    this.defaultTypeOfFrontToShow = defaultTypeOfFrontToShow;
  }

  @Override
  public void run() throws IOException {
    String directory = experiment.getExperimentBaseDirectory();
    StudyVisualizer visualizer = new StudyVisualizer(directory, defaultTypeOfFrontToShow);
    visualizer.createHTMLPageForEachIndicator();
  }
}
