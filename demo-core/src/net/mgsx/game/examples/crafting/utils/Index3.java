package net.mgsx.game.examples.crafting.utils;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector;

public class Index3 implements Vector<Index3>{

	public int x,y,z;
	
	public Index3 set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	@Override
	public Index3 cpy() {
		return new Index3().set(this);
	}

	@Override
	public float len() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float len2() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Index3 limit(float limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Index3 limit2(float limit2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Index3 setLength(float len) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Index3 setLength2(float len2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Index3 clamp(float min, float max) {
		return clamp((int)min, (int)max);
	}

	public Index3 clamp(int min, int max) {
		x = Math.max(min, Math.min(max, x));
		y = Math.max(min, Math.min(max, y));
		z = Math.max(min, Math.min(max, z));
		return this;
	}

	@Override
	public Index3 set(Index3 v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
		return this;
	}

	@Override
	public Index3 sub(Index3 v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
		return this;
	}

	@Override
	public Index3 nor() {
		x = x < 0 ? -1 : (x > 0 ? 1 : 0);
		y = y < 0 ? -1 : (y > 0 ? 1 : 0);
		z = z < 0 ? -1 : (z > 0 ? 1 : 0);
		return this;
	}

	@Override
	public Index3 add(Index3 v) {
		x += v.x;
		y += v.y;
		z += v.z;
		return this;
	}

	@Override
	public float dot(Index3 v) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Index3 scl(float scalar) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Index3 scl(Index3 v) {
		x *= v.x;
		y *= v.y;
		z *= v.z;
		return this;
	}

	@Override
	public float dst(Index3 v) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float dst2(Index3 v) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Index3 lerp(Index3 target, float alpha) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Index3 interpolate(Index3 target, float alpha, Interpolation interpolator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Index3 setToRandomDirection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUnit(float margin) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isZero() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isZero(float margin) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOnLine(Index3 other, float epsilon) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOnLine(Index3 other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCollinear(Index3 other, float epsilon) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCollinear(Index3 other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCollinearOpposite(Index3 other, float epsilon) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCollinearOpposite(Index3 other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPerpendicular(Index3 other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPerpendicular(Index3 other, float epsilon) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasSameDirection(Index3 other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasOppositeDirection(Index3 other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean epsilonEquals(Index3 other, float epsilon) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Index3 mulAdd(Index3 v, float scalar) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Index3 mulAdd(Index3 v, Index3 mulVec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Index3 setZero() {
		// TODO Auto-generated method stub
		return null;
	}

	public Index3 abs() {
		if(x<0) x = -x;
		if(y<0) y = -y;
		if(z<0) z = -z;
		return this;
	}

	public Index3 wrap(int mod) {
		x = (x % mod + mod) % mod;
		y = (y % mod + mod) % mod;
		z = (z % mod + mod) % mod;
		return this;
	}

}
