/*
 * Base on code from https://github.com/jMetal/jMetal
 *
 * Copyright <2017> <Antonio J. Nebro, Juan J. Durillo>
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to
 * whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. © 2019
 * GitHub, Inc.
 */
package model.metaheuristic.util.archive.impl;


import model.metaheuristic.solution.Solution;
import model.metaheuristic.util.archive.Archive;
import model.metaheuristic.util.archive.BoundedArchive;

import java.util.List;

/**
 * This class implement some of the method of the BoundedArchive. And limit remove solution of
 * archive when the size of archive is over the maxSize
 * @param <S> the type of solution
 */
public abstract class AbstractBoundedArchive<S extends Solution<?>> implements BoundedArchive<S> {
	protected NonDominatedSolutionListArchive<S> archive;
	protected int maxSize;

	public AbstractBoundedArchive(int maxSize) {
		this.maxSize = maxSize;
		this.archive = new NonDominatedSolutionListArchive<S>();
	}

	/**{@inheritDoc}*/
	@Override
	public boolean add(S solution) {
		boolean success = archive.add(solution);
		if (success) {
			prune();
		}

		return success;
	}

	/**{@inheritDoc}*/
	@Override
	public S get(int index) {
		return getSolutionList().get(index);
	}

	/**{@inheritDoc}*/
	@Override
	public List<S> getSolutionList() {
		return archive.getSolutionList();
	}

	/**{@inheritDoc}*/
	@Override
	public int size() {
		return archive.size();
	}

	/**{@inheritDoc}*/
	@Override
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * Removes items from the file when it exceeds the maximum size
	 */
	public abstract void prune();

	/**
	 * Join two archive. i.e. add the solution of the received archive to this archive
	 * @param archive the archive
	 * @return the joined archive
	 */
	public Archive<S> join(Archive<S> archive) {
		for (S solution : archive.getSolutionList()) {
			this.add(solution) ;
		}

		return archive ;
	}
}
