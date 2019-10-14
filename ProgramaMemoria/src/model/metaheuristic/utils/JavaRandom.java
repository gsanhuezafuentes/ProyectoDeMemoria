package model.metaheuristic.utils;

import java.util.Random;

public class JavaRandom {
	private static JavaRandom instance;
	
	private Random random;
	
	private JavaRandom() {
		this.random = new Random(System.currentTimeMillis());
	}
	
	public static JavaRandom getInstance() {
		if (instance == null) {
			instance = new JavaRandom();
		}
		return instance;
	}

	/**
	 * @param seed
	 * @see java.util.Random#setSeed(long)
	 */
	public void setSeed(long seed) {
		random.setSeed(seed);
	}

	/**
	 * @return
	 * @see java.util.Random#nextInt()
	 */
	public int nextInt() {
		return random.nextInt();
	}

	/**
	 * @param bound
	 * @return
	 * @see java.util.Random#nextInt(int)
	 */
	public int nextInt(int bound) {
		return random.nextInt(bound);
	}
	
	/**
	 * Generate a random value between lowerBound and upperBound exclusive.
	 * @param lowerBound the lowerBound
	 * @param upperBound the upperBound exclusive
	 * @return int between lowerBound and upperBound
	 */
	public int nextInt(int lowerBound, int upperBound) {
		return lowerBound + random.nextInt(upperBound - lowerBound);
	}

	/**
	 * @return
	 * @see java.util.Random#nextLong()
	 */
	public long nextLong() {
		return random.nextLong();
	}

	/**
	 * @return
	 * @see java.util.Random#nextBoolean()
	 */
	public boolean nextBoolean() {
		return random.nextBoolean();
	}

	/**
	 * @return
	 * @see java.util.Random#nextFloat()
	 */
	public float nextFloat() {
		return random.nextFloat();
	}

	/**
	 * @return
	 * @see java.util.Random#nextDouble()
	 */
	public double nextDouble() {
		return random.nextDouble();
	}

	/**
	 * @return
	 * @see java.util.Random#nextGaussian()
	 */
	public double nextGaussian() {
		return random.nextGaussian();
	}
	
	
	
}
