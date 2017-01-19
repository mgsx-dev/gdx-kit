package net.mgsx.game.core.math;

import java.util.Random;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;

public class ClassicNoise 
{
	private Random random = new RandomXS128();
	private float [] randomTable;
	private Interpolation interpolation = Interpolation.sine;
	
	public ClassicNoise() {
		setSeed(0);
	}

	private void setSeed(long seed) 
	{
		random = new RandomXS128(seed);
		randomTable = new float[2048];
		for(int i=0 ; i<randomTable.length ; i++){
			randomTable[i] = random.nextFloat();
		}
	}
	
	public float apply(float x)
	{
		int ix = MathUtils.floor(x);
		float rx = x - ix;
		float nx1 = randomTable[(ix%randomTable.length + randomTable.length) % randomTable.length];
		float nx2 = randomTable[((ix+1)%randomTable.length + randomTable.length) % randomTable.length];
		return interpolation.apply(nx1, nx2, rx);
	}
}
