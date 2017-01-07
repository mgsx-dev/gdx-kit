package net.mgsx.game.examples.crafting.utils;

import java.util.Random;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Noise {

	private Random random = new RandomXS128();
	
	private float [] randomTable = new float [2048];
	
	private int xoffset, yoffset, zoffset;
	
	public Noise() {
		for(int i=0 ; i<randomTable.length ; i++)
			randomTable[i] = random.nextFloat();
		xoffset = random.nextInt(randomTable.length);
		yoffset = random.nextInt(randomTable.length);
		zoffset = random.nextInt(randomTable.length);
	}
	
	public float apply(Vector3 position)
	{
		int ix = (int)(position.x);
		float rx = position.x - ix;
		int iy = (int)(position.y);
		float ry = position.y - iy;
		int iz = (int)(position.z);
		float rz = position.z - iz;
		
		int ix1 = (ix + xoffset);
		int ix2 = (ix + xoffset + 1) ;
		
		int iy1 = (ix + yoffset) ;
		int iy2 = (ix + yoffset + 1) ;
		
		int iz1 = (ix + zoffset) ;
		int iz2 = (ix + zoffset + 1);
		
		float n1 = MathUtils.lerp(
				randomTable[(ix1 + iy1 + iz1) % randomTable.length], 
				randomTable[(ix2 + iy1 + iz1) % randomTable.length], 
				rx);
		float n2 = MathUtils.lerp(
				randomTable[(ix1 + iy2 + iz1) % randomTable.length], 
				randomTable[(ix2 + iy2 + iz1) % randomTable.length], 
				rx);
		float n3 = MathUtils.lerp(
				randomTable[(ix1 + iy1 + iz2) % randomTable.length], 
				randomTable[(ix2 + iy1 + iz2) % randomTable.length], 
				rx);
		float n4 = MathUtils.lerp(
				randomTable[(ix1 + iy2 + iz2) % randomTable.length], 
				randomTable[(ix2 + iy2 + iz2) % randomTable.length], 
				rx);
		
		float n5 = MathUtils.lerp(n1, n2, ry);
		float n6 = MathUtils.lerp(n3, n4, ry);
		
		return MathUtils.lerp(n5, n6, rz);
	}
	

	public float apply(Vector2 position)
	{
		int ix = (int)(position.x);
		float rx = position.x - ix;
		int iy = (int)(position.y);
		float ry = position.y - iy;
		
		int ix1 = (ix + xoffset);
		int ix2 = (ix + xoffset + 1) ;
		
		int iy1 = (ix + yoffset) ;
		int iy2 = (ix + yoffset + 1) ;
		
		
		float n1 = MathUtils.lerp(
				randomTable[(ix1 + iy1) % randomTable.length], 
				randomTable[(ix2 + iy1) % randomTable.length], 
				rx);
		float n2 = MathUtils.lerp(
				randomTable[(ix1 + iy2) % randomTable.length], 
				randomTable[(ix2 + iy2) % randomTable.length], 
				rx);
		
		return MathUtils.lerp(n1, n2, ry);
	}
	public float apply(float position)
	{
		int ix = (int)(position);
		float rx = position - ix;
		
		int ix1 = (ix + xoffset);
		int ix2 = (ix + xoffset + 1) ;
		
		
		return MathUtils.lerp(
				randomTable[(ix1) % randomTable.length], 
				randomTable[(ix2) % randomTable.length], 
				rx);
		
	}
}
