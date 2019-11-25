package view;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import annotations.DefaultConstructor;
import annotations.NumberInput;
import annotations.OperatorInput;
import annotations.OperatorOption;
import annotations.Parameters;
import exception.ApplicationException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
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
			configureOperator(operator.displayName(), comboBox.getSelectionModel().getSelectedItem());
		});
		
		this.operatorLayout.addRow(operatorGridRowCount, label, comboBox, configButton); 
		operatorGridRowCount++;
	}
	
	private void configureOperator(String name, Class<?> selectedItem) {
		Dialog<List<Number>> dialog = new Dialog<>();
		dialog.setTitle("Configuración " + name);
		dialog.setContentText("Ingrese los valores");
		ButtonType okButtonType = new ButtonType("Guardar", ButtonData.OK_DONE);
		ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
		
		GridPane grid = new GridPane();
		grid.setHgap(defaultSpace);
		grid.setVgap(defaultSpace);
		grid.setPadding(new Insets(defaultSpace));
		
		Node okButton = dialog.getDialogPane().lookupButton(okButtonType);
		okButton.setDisable(true);
		
		// Add a textfild for each parameter in the constructor
		Constructor<?> constructor = ReflectionUtils.getDefaultConstructor(selectedItem);
		ArrayList<TextField> textFieldOfParameters = new ArrayList<>(constructor.getParameterCount());
		if (constructor != null) {
			DefaultConstructor annotation = constructor.getAnnotation(DefaultConstructor.class);
			Class<?>[] parameters  = constructor.getParameterTypes();
			for (int i = 0; i < parameters.length; i++) {
				Label label = new Label(annotation.value()[i]);
				TextField textfield = new TextField();
				textfield.setPromptText("Ingrese el valor");
				
				textfield.textProperty().addListener((observable, oldValue, newValue) -> {
					okButton.setDisable(newValue.trim().isEmpty());
				});
				
				textFieldOfParameters.add(textfield);
				grid.addRow(i, label, textfield);
			}
		}
		

		dialog.getDialogPane().setContent(grid);
		
		dialog.setResultConverter(dialogButton -> {
		    if (dialogButton == okButtonType) {
		    	NumberStringConverter converter = new NumberStringConverter();
		    	List<Number> results = textFieldOfParameters.stream().map(textField -> converter.fromString(textField.getText())).collect(Collectors.toList());
		        return new ArrayList<Number>(results);
		    }
		    return null;
		});

		Optional<List<Number>> result = dialog.showAndWait();
		
		result.ifPresent(results -> {
			results.forEach(System.out::println);
		});
	}

	/**
	 * Configure the number section. It section has textfield to write the numbers.
	 * @param operator
	 */
	private void numberSection(NumberInput operator) {
		Label label = new Label(operator.displayName());
		TextField text = new TextField();
		text.setPromptText("Ingrese el valor");
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
