/**
 * This package contains the annotation that must be used by the class that
 * inherit of {@link registrable.Registrable} and by the {@link model.metaheuristic.operator.Operator}
 * indicate the elements to be injected to constructor.
 * <p>
 *
 * Use with {@link registrable.Registrable}:
 * <ul>
 *    <li>{@link annotations.registrable.NewProblem}</li>
 *    <li>{@link annotations.registrable.Parameters}</li>
 *    <li>{@link annotations.registrable.OperatorInput}</li>
 *    <li>{@link annotations.registrable.OperatorOption}</li>
 *    <li>{@link annotations.NumberInput}</li>
 *    <li>{@link annotations.registrable.NumberToggleInput}</li>
 * </ul>
 * <p>
 * Use with {@link model.metaheuristic.operator.Operator}:
 *  <ul>
 *     <li>{@link annotations.operator.DefaultConstructor}</li>
 *     <li>{@link annotations.NumberInput}</li>
 *  </ul>
 */
package annotations;