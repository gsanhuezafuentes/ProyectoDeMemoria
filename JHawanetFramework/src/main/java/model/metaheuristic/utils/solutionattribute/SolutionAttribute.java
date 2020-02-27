package model.metaheuristic.utils.solutionattribute;

import model.metaheuristic.solution.Solution;

/**
 * Generic class for implementing {@link SolutionAttribute} classes. By default,
 * the identifier of a {@link SolutionAttribute} is the class object, but it can
 * be set to a different value when constructing an instance.<br>
 * <br>
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * 
 *         <pre>
 * Taked from https://github.com/jMetal/jMetal
 * 
 * Copyright <2017> <Antonio J. Nebro, Juan J. Durillo>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE. © 2019 GitHub, Inc.
 *         </pre>
 */
public class SolutionAttribute<S extends Solution<?>, V> {
	private Object identifier;

	/**
	 * Constructor
	 */
	public SolutionAttribute() {
		identifier = this.getClass();
	}

	/**
	 * Constructor
	 * 
	 * @param id Attribute identifier
	 */
	public SolutionAttribute(Object id) {
		this.identifier = id;
	}

	/**
	 * Get the attribute
	 * 
	 * @param solution the solution to extract the attribute
	 * @return the attribute or null if not exist
	 */
	@SuppressWarnings("unchecked")
	public V getAttribute(S solution) {
		return (V) solution.getAttribute(getAttributeIdentifier());
	}

	/**
	 * Set a attribute to solution
	 * 
	 * @param solution the solution to assign to solution
	 * @param value    the value assigned to solution
	 */
	public void setAttribute(S solution, V value) {
		solution.setAttribute(getAttributeIdentifier(), value);
	}

	/**
	 * Get the attribute identifier
	 * 
	 * @return the attribute identifier
	 */
	public Object getAttributeIdentifier() {
		return identifier;
	}
}
