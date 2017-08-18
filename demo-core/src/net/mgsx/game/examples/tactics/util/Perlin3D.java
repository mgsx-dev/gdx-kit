package net.mgsx.game.examples.tactics.util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;

public class Perlin3D {

	private RandomXS128 random = new RandomXS128();
	private long seed;
	
	public void setSeed(long seed) {
		this.seed = seed;
	}
	
	public float generate(float x, float y, float z)
	{
		int ix = MathUtils.floor(x);
		int iy = MathUtils.floor(y);
		int iz = MathUtils.floor(z);
		float tx = x - ix;
		float ty = y - iy;
		float tz = z - iz;
		return MathUtils.lerp(
				MathUtils.lerp(
						MathUtils.lerp(valueAt(ix, iy, iz), valueAt(ix+1, iy, iz), tx), 
						MathUtils.lerp(valueAt(ix, iy+1, iz), valueAt(ix+1, iy+1, iz), tx), 
						ty), 
				MathUtils.lerp(
						MathUtils.lerp(valueAt(ix, iy, iz+1), valueAt(ix+1, iy, iz+1), tx), 
						MathUtils.lerp(valueAt(ix, iy+1, iz+1), valueAt(ix+1, iy+1, iz+1), tx), 
						ty),
				tz);
	}
	
	private float valueAt(int ix, int iy, int iz)
	{
		random.setSeed(seed);
		random.setSeed(random.nextInt() + ix);
		random.setSeed(random.nextInt() + iy);
		random.setSeed(random.nextInt() + iz);
		return random.nextFloat();
	}
}
