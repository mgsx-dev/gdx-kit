package net.mgsx.game.plugins.procedural.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;

public class ClassicalPerlinNoise 
{
	private RandomXS128 random = new RandomXS128();
	private long seed;
	
	public void seed(long seed){
		this.seed = seed;
	}
	
	public float get(float x, float y)
	{
		int ix = MathUtils.floor(x);
		int iy = MathUtils.floor(y);
		
		float rx = x - ix;
		float ry = y - iy;
		
		float v00 = value(ix, iy);
		float v10 = value(ix+1, iy);
		float v01 = value(ix, iy+1);
		float v11 = value(ix+1, iy+1);
		
		return MathUtils.lerp(
				MathUtils.lerp(v00, v10, rx), 
				MathUtils.lerp(v01, v11, rx), ry);

	}

	private float value(int ix, int iy) 
	{
		random.setSeed(seed + ix);
		random.setSeed(random.nextInt() + iy);
		return random.nextFloat();
	}
}
