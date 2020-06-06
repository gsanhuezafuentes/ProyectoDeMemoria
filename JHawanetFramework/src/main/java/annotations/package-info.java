/**
 * This package contains the annotation that must be used by the class that
 * inherit of {@link registrable.Registrable} and by the {@link model.metaheuristic.operator.Operator}
 * indicate the elements to be injected to constructor.
 * <p>
 *
 * Use with {@link registrable.Registrable}:
 * <ul>
 *    <li>{@link annotations.NewProblem}</li>
 *    <li>{@link annotations.Parameters}</li>
 *    <li>{@link annotations.OperatorInput}</li>
 *    <li>{@link annotations.OperatorOption}</li>
 *    <li>{@link annotations.NumberInput}</li>
 *    <li>{@link annotations.NumberToggleInput}</li>
 * </ul>
 * <p>
 * Use with {@link model.metaheuristic.operator.Operator}:
 *  <ul>
 *     <li>{@link annotations.DefaultConstructor}</li>
 *     <li>{@link annotations.NumberInput}</li>
 *  </ul>
 */
package annotations;