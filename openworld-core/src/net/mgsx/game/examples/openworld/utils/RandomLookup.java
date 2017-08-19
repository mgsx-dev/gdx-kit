package net.mgsx.game.examples.openworld.utils;

import java.util.Random;

// TODO rename 2D
public class RandomLookup {
	
	private int width, height;
	private float [] values;
	
	public RandomLookup(Random random, int width, int height) {
		super();
		this.width = width;
		this.height = height;
		values = new float[width * height];
		for(int y=0 ; y<height ; y++) {
			for(int x=0 ; x<width ; x++) {
				values[y*width+x] = random.nextFloat();
			}
		}
	}

	public float get(int x, int y) {
		
		int ix = (x % width + width) % width;
		int iy = (y % height + height) % height;
		
		return values[iy * width + ix];
	}
	public float getSigned(int x, int y) {
		return get(x, y) * 2 - 1;
	}
	
}
