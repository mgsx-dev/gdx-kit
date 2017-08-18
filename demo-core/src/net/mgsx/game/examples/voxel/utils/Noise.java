package net.mgsx.game.examples.voxel.utils;

import java.util.Random;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Noise {

	private Random random;
	
	private float [] randomTable = new float [2048];
	
	private int xoffset, yoffset, zoffset;

	public Noise(long seed) {
		random = new RandomXS128(seed);
		for(int i=0 ; i<randomTable.length ; i++)
			randomTable[i] = random.nextFloat();
		xoffset = random.nextInt(randomTable.length);
		yoffset = random.nextInt(randomTable.length);
		zoffset = random.nextInt(randomTable.length);
	}
	
	private float random(int i){
		return randomTable[(i % randomTable.length + randomTable.length) % randomTable.length];
	}
	
	public float apply(Vector3 position)
	{
		int ix = MathUtils.floor(position.x);
		float rx = position.x - ix;
		int iy = MathUtils.floor(position.y);
		float ry = position.y - iy;
		int iz = MathUtils.floor(position.z);
		float rz = position.z - iz;
		
		int ix1 = (ix + xoffset);
		int ix2 = (ix + xoffset + 1) ;
		
		int iy1 = (iy + yoffset) + randomTable.length / 3 ;
		int iy2 = (iy + yoffset + 1) ;
		
		int iz1 = (iz + zoffset) + 2 * randomTable.length / 3 ;
		int iz2 = (iz + zoffset + 1);
		
		float n1 = MathUtils.lerp(
				random(ix1 + iy1 + iz1),
				random(ix2 + iy1 + iz1), 
				rx);
		float n2 = MathUtils.lerp(
				random(ix1 + iy2 + iz1), 
				random(ix2 + iy2 + iz1), 
				rx);
		float n3 = MathUtils.lerp(
				random(ix1 + iy1 + iz2), 
				random(ix2 + iy1 + iz2), 
				rx);
		float n4 = MathUtils.lerp(
				random(ix1 + iy2 + iz2), 
				random(ix2 + iy2 + iz2), 
				rx);
		
		float n5 = MathUtils.lerp(n1, n2, ry);
		float n6 = MathUtils.lerp(n3, n4, ry);
		
		return MathUtils.lerp(n5, n6, rz);
	}
	

	public float apply(Vector2 position)
	{
		int ix = MathUtils.floor(position.x);
		float rx = position.x - ix;
		int iy = MathUtils.floor(position.y);
		float ry = position.y - iy;
		
		int ix1 = (ix + xoffset);
		int ix2 = (ix + xoffset + 1) ;
		
		int iy1 = (iy + yoffset) + randomTable.length / 3;
		int iy2 = (iy + yoffset + 1) ;
		
		
		float n1 = MathUtils.lerp(
				random(ix1 + iy1), 
				random(ix2 + iy1), 
				rx);
		float n2 = MathUtils.lerp(
				random(ix1 + iy2), 
				random(ix2 + iy2), 
				rx);
		
		return MathUtils.lerp(n1, n2, ry);
	}
	public float apply(float position)
	{
		int ix = MathUtils.floor(position);
		float rx = position - ix;
		
		int ix1 = (ix + xoffset);
		int ix2 = (ix + xoffset + 1) ;
		
		
		return MathUtils.lerp(
				random(ix1), 
				random(ix2), 
				rx);
		
	}
}
