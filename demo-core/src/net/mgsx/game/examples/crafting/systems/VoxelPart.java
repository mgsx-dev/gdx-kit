package net.mgsx.game.examples.crafting.systems;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ShapeCache;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class VoxelPart implements RenderableProvider
{
	private ShapeCache cache;
	private int SIZE;
	private int [] data;
	private Vector3 offset = new Vector3();
	private float cubeSize;
	public VoxelPart(int sIZE, float cubeSize) {
		super();
		SIZE = sIZE;
		this.cubeSize = cubeSize;
	}

	public void generateData(VoxelWorldSystem config, Vector3 worldPos){
		final int cubes = SIZE*SIZE*SIZE;
		
		// System.out.println(offset);
		
		data = new int[cubes];
		
		Vector3 p = new Vector3();
		Vector3 world = new Vector3();
		Vector2 p2 = new Vector2();
		
		// max 2^15-1 : 11^3 * 24
		// if(SIZE > 11) SIZE = 11;
		
		offset.set(worldPos);
		
		for(int z=0 ; z<SIZE ; z++){
			for(int x=0 ; x<SIZE ; x++){
				
				for(int y=0 ; y<SIZE ; y++)
				{
					world.set(x,y,z).scl(cubeSize).add(worldPos);
					float n2 = (config.noise.apply(p2.set(world.x, world.z).scl(config.freq2D)) - .5f) * 2 * config.planarity;
					
					float n = config.noise.apply(p.set(worldPos).scl(config.freq3D));
					
					if(n>config.lacunarity && world.y<n2){
						int index = (z * SIZE + y) * SIZE + x;
						data[index] = 1;
					}
					
				}
			}
		}
	}
	
	public void generateMesh(float cubeSize, float cubeSpace)
	{
		final int cubes = SIZE*SIZE*SIZE;
		
		int minVertex = 6 * 4 * cubes;
		int minIndices = 6 * 6 * cubes;
		if(minVertex > 32768) minVertex = 32768;
		if(minIndices > 32768) minIndices = 32768;
		
		int maxCubes = 32768 / 36;
		
		
		
		
		ShapeCache r = new ShapeCache(minVertex, minIndices, new VertexAttributes(VertexAttribute.Position(), VertexAttribute.Normal()), GL20.GL_TRIANGLES);
		MeshPartBuilder mpb = r.begin(GL20.GL_TRIANGLES);
		int cnt = 0;
		int SIZE2 = SIZE * SIZE;
		for(int z=0 ; z<SIZE ; z++)
			for(int y=0 ; y<SIZE ; y++)
				for(int x=0 ; x<SIZE ; x++){
					int index = (z * SIZE + y) * SIZE + x;
					if(data[index] > 0 && cnt < maxCubes){
						int neigh = 0;
						if(x > 0 && data[index-1]>0) neigh++;
						if(x < SIZE-1 && data[index+1]>0) neigh++;
						if(y > 0 && data[index-SIZE]>0) neigh++;
						if(y < SIZE-1 && data[index+SIZE]>0) neigh++;
						if(z > 0 && data[index-SIZE2]>0) neigh++;
						if(z < SIZE-1 && data[index+SIZE2]>0) neigh++;
						if(neigh < 6){
							BoxShapeBuilder.build(mpb, x * cubeSpace + offset.x, y* cubeSpace + offset.y, z* cubeSpace + offset.z, cubeSize, cubeSize, cubeSize);
							cnt++;
						}
					}
				}
		r.end();
		
		// System.out.println(cnt + "/" + cubes + "/" + maxCubes);
		
		cache = r;
	}

	@Override
	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		if(cache != null){
			cache.getRenderables(renderables, pool);
		}
	}

	public void reset() {
		cache = null;
	}
	
	
}
