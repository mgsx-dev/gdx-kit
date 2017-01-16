package net.mgsx.game.examples.crafting.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

abstract public class Cube<T> 
{
	/** chunk size */
	private float chunkSize = 1;
	
	/** number of chunk for all 3 dims */
	private int chunkCount = 4;
	
	private Object [] data;
	
	private final Index3 index = new Index3();
	private final Index3 index2 = new Index3();
	private final Index3 current = new Index3();
	
	public Cube(int chunkCount, float chunkSize) {
		this.chunkCount = chunkCount;
		this.chunkSize = chunkSize;
		data = new Object[chunkCount*chunkCount*chunkCount];
		generate();
	}
	
	private void generate() 
	{
		// generate init state (all chunks)
		for(int z=0 ; z<chunkCount ; z++)
			for(int y=0 ; y<chunkCount ; y++)
				for(int x=0 ; x<chunkCount ; x++){
					T chunk = create();
					data[index(x,y,z)] = chunk;
				}
	}
	public void regenerate() 
	{
		Index3 wrapped = new Index3();
		Vector3 pos = new Vector3();
		for(int z=0 ; z<chunkCount ; z++)
			for(int y=0 ; y<chunkCount ; y++)
				for(int x=0 ; x<chunkCount ; x++){
					index.set(x,y,z).add(current);
					generate((T)data[index(wrapped.set(index).wrap(chunkCount))], index, position(pos, index));
				}
	}

	private int index(int x, int y, int z) {
		return (z * chunkCount + y) * chunkCount + x;
	}

	private int index(Index3 i) {
		return index(i.x, i.y, i.z);
	}

	public void set(Vector3 position){
		
		set(index(index, position));
	}
	
	public void set(Index3 position){
		
		// invalidate some cube part and call regeneration
		Index3 delta = position.cpy().sub(current);
		
		dir.set(delta).nor();
		delta.abs();
		base.set(position);
		
		if(delta.x >= chunkCount || delta.y >= chunkCount || delta.z >= chunkCount){
			current.set(position);
			regenerate();
			return;
		}
		
		for(int z=0 ; z<delta.z ; z++){
			for(int y=0 ; y<chunkCount-delta.y ; y++)
				for(int x=0 ; x<chunkCount-delta.x ; x++){
					update(x,y, dir.z>0 ? chunkCount-z-1 : z);
				}
		}
		for(int x=0 ; x<delta.x ; x++){
			for(int y=0 ; y<chunkCount-delta.y ; y++)
				for(int z=0 ; z<chunkCount-delta.z ; z++){
					update(dir.x>0 ? chunkCount-x-1 : x,y,z);
				}
		}
		for(int y=0 ; y<delta.y ; y++){
			for(int x=0 ; x<chunkCount-delta.x ; x++)
				for(int z=0 ; z<chunkCount-delta.z ; z++){
					update(x,dir.y>0 ? chunkCount-y-1 : y,z);
				}
		}
		
		current.set(position);
	}
	Index3 wrapped = new Index3();
	Index3 dir = new Index3();
	Index3 base = new Index3();
	Vector3 pos = new Vector3();
	
	private void update(int x, int y, int z){
		index2.set(x,y,z).add(base);
		wrapped.set(index2).wrap(chunkCount);
		generate((T)data[index(wrapped)], index2, position(pos, index2));
	}
	

	private Vector3 position(Vector3 pos, Index3 i) {
		pos.x = i.x * chunkSize;
		pos.y = i.y * chunkSize;
		pos.z = i.z * chunkSize;
		return pos;
	}
	private Index3 index(Index3 i, Vector3 pos) {
		i.x = MathUtils.floor(pos.x / chunkSize);
		i.y = MathUtils.floor(pos.y / chunkSize);
		i.z = MathUtils.floor(pos.z / chunkSize);
		return i;
	}

	abstract protected T create();
	abstract protected void  generate(T data, Index3 index, Vector3 position);
	abstract protected void  update(T data, Index3 index, Vector3 position);

	public T get(Index3 i) {
		return (T)data[index(index.set(i).wrap(chunkCount))];
	}
	public T get(Vector3 v) {
		return get(index(index, v));
	}

	public void updateAll() {
		for(int z=0 ; z<chunkCount ; z++)
			for(int y=0 ; y<chunkCount ; y++)
				for(int x=0 ; x<chunkCount ; x++){
					index.set(x,y,z).add(current);
					update((T)data[index(wrapped.set(index).wrap(chunkCount))], index, position(pos, index));
				}
	}

}
