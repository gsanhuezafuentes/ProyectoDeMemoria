package model.metaheuristic.experiment.component;

import model.metaheuristic.experiment.Experiment;
import model.metaheuristic.experiment.ExperimentComponent;
import model.metaheuristic.util.visualization.StudyVisualizer;

import java.io.IOException;
import java.nio.file.Path;

/**
 * This class executes a StudyVisualizer on the experiment provided.
 *
 * <p>The results are created in in the directory {@link Experiment *
 * #getExperimentBaseDirectory()}/html.
 *
 * @author Javier Pérez
 */
public class GenerateHtmlPages
    implements ExperimentComponent {

  private final StudyVisualizer.TYPE_OF_FRONT_TO_SHOW defaultTypeOfFrontToShow;
  private final Path baseDirectory;

  public GenerateHtmlPages(Path baseDirectory) {
    this(baseDirectory, StudyVisualizer.TYPE_OF_FRONT_TO_SHOW.BEST);
  }

  public GenerateHtmlPages(
      Path baseDirectory,
      StudyVisualizer.TYPE_OF_FRONT_TO_SHOW defaultTypeOfFrontToShow) {
    this.baseDirectory = baseDirectory;
    this.defaultTypeOfFrontToShow = defaultTypeOfFrontToShow;
  }

  @Override
  public void run() throws IOException {
    String directory = this.baseDirectory.toFile().getAbsolutePath();
    StudyVisualizer visualizer = new StudyVisualizer(directory, defaultTypeOfFrontToShow);
    visualizer.createHTMLPageForEachIndicator();
  }
}
