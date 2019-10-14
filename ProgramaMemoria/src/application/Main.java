package application;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import epanet.core.EpanetAPI;
import epanet.core.EpanetException;
import javafx.application.Application;
import javafx.stage.Stage;
import model.metaheuristic.algorithm.Algorithm;
import model.metaheuristic.algorithm.GeneticAlgorithm;
import model.metaheuristic.operator.crossover.IntegerSBXCrossover;
import model.metaheuristic.operator.mutation.IntegerPolynomialMutation;
import model.metaheuristic.operator.selection.SelectionOperator;
import model.metaheuristic.operator.selection.TournamentSelection;
import model.metaheuristic.problem.CostConstructionProblem;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.solution.IntegerSolution;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 400, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		// launch(args);

		EpanetAPI epanet;
		try {
			epanet = new EpanetAPI();
			epanet.ENopen("inp/hanoi-Frankenstein.INP", "inp/hanoi.rpt", "");

			TournamentSelection<IntegerSolution> selection = new TournamentSelection<IntegerSolution>(2);
			IntegerSBXCrossover crossover = new IntegerSBXCrossover(0.5, 0.6);
			IntegerPolynomialMutation mutation = new IntegerPolynomialMutation();
			Problem<IntegerSolution> problem = new CostConstructionProblem(epanet, "inp/hanoiHW.Gama");
			GeneticAlgorithm<IntegerSolution> algorithm = new GeneticAlgorithm<IntegerSolution>(problem, 10, selection,
					crossover, mutation);
			algorithm.run();

			epanet.ENclose();
			
			System.out.println(algorithm.getResult());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EpanetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
