package view;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import annotations.NumberInput;
import annotations.OperatorInput;
import annotations.OperatorOption;
import annotations.Parameters;
import exception.ApplicationException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import model.metaheuristic.algorithm.Algorithm;
import view.problems.AlgorithmCreationNotification;
import view.problems.Registrable;
import view.utils.ReflectionUtils;

public class ConfigurationDynamicWindow extends VBox {

	private AlgorithmCreationNotification algorithmEvent;
	private Registrable problem;
	private GridPane operatorLayout;
	private GridPane primitiveLayout;
	private int operatorGridRowCount;
	private int numberGridRowCount;
	private double defaultSpace = 5;
	
	public ConfigurationDynamicWindow(Registrable registrableProblem) {
		this.operatorGridRowCount = 0;
		this.numberGridRowCount = 0;
		this.problem = registrableProblem;
		this.operatorLayout = new GridPane();
		this.primitiveLayout = new GridPane();

		initialize();
		make();
	}
	
	private void initialize() {
		this.operatorLayout.setHgap(defaultSpace);
		this.operatorLayout.setVgap(defaultSpace);
		
		this.primitiveLayout.setHgap(defaultSpace);
		this.primitiveLayout.setVgap(defaultSpace);
		
		setSpacing(defaultSpace);
		setPadding(new Insets(defaultSpace));
		
		Button run = new Button("Ejecutar");
		Button cancel = new Button("Cancelar");
		
		HBox hbox = new HBox(run, cancel);
		hbox.setSpacing(defaultSpace);
		hbox.setAlignment(Pos.CENTER_RIGHT);
		hbox.setPadding(new Insets(defaultSpace));
		
		getChildren().addAll(primitiveLayout, operatorLayout, hbox);
		
	}

	/**
	 * Read the problem using reflection and build the interface
	 */
	private void make() {
		Method method = ReflectionUtils.getInjectableMethod(problem.getClass());
		Parameters parameters = method.getAnnotation(Parameters.class);
		if (parameters != null) {
			for (OperatorInput operator : parameters.operators()) {
				operatorSection(operator);
			}
			for (NumberInput operator: parameters.numbers()) {
				numberSection(operator);
			}
		}
	}
	
	/**
	 * Configure the operator section. It section has comboBox to choose the operator
	 * @param operator the operator annotation that contains the option of combobox
	 */
	private void operatorSection(OperatorInput operator) {
		Map<Class<?>, String> operatorMap = new LinkedHashMap<>(operator.value().length);
		for (OperatorOption option: operator.value()) {
			operatorMap.put(option.value(), option.displayName());
		}
		
		Label label = new Label(operator.displayName());
		ComboBox<Class<?>> comboBox = new ComboBox<>();
		comboBox.setMaxWidth(Double.MAX_VALUE);
		comboBox.getItems().addAll(operatorMap.keySet());
		comboBox.setConverter(new StringConverter<Class<?>>() {
			
			@Override
			public String toString(Class<?> object) {
				return operatorMap.get(object);
			}
			
			@Override
			public Class<?> fromString(String string) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		Button configButton = new Button("Configurar");
		configButton.disableProperty().bind(comboBox.getSelectionModel().selectedItemProperty().isNull());
		
		configButton.setOnAction((evt) ->{
			configureOperator(comboBox.getSelectionModel().getSelectedItem());
		});
		
		this.operatorLayout.addRow(operatorGridRowCount, label, comboBox, configButton); 
		operatorGridRowCount++;
	}
	
	private void configureOperator(Class<?> selectedItem) {
		
	}

	/**
	 * Configure the number section. It section has textfield to write the numbers.
	 * @param operator
	 */
	private void numberSection(NumberInput operator) {
		Label label = new Label(operator.displayName());
		TextField text = new TextField();
		text.setMaxWidth(Double.MAX_VALUE);
		
		this.primitiveLayout.addRow(numberGridRowCount, label, text); 
		numberGridRowCount++;
	}

	/**
	 * Add a callback to notify when the algorithm is created.
	 * 
	 * @param algorithmEvent
	 */
	public void setAlgorithmCreationNotification(AlgorithmCreationNotification algorithmEvent) {
		this.algorithmEvent = algorithmEvent;
	}

	/**
	 * Is a class to fire the notification when the algorithm is ready.
	 * 
	 * @param algorithm the algorithm
	 * @throws ApplicationException if there isn't register the notification
	 *                              callback
	 */
	private void notifyAlgorithmCreation(Algorithm<?> algorithm) throws ApplicationException {
		if (algorithmEvent == null) {
			throw new ApplicationException("There isn't register this notify function in " + this.getClass().getName());
		}
		algorithmEvent.notify(algorithm);
	}
}
