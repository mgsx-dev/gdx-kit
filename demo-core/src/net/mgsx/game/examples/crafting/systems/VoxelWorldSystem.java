package net.mgsx.game.examples.crafting.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ShapeCache;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.examples.crafting.utils.Noise;
import net.mgsx.game.plugins.g3d.systems.G3DRendererSystem;

@EditableSystem
public class VoxelWorldSystem extends EntitySystem
{
	private ModelBatch batch;
	private GameScreen game;
	private Array<RenderableProvider> renderables = new Array<RenderableProvider>();
	
	private Noise noise;
	
	private int [] data;
	
	@Editable public int SIZE = 10;
	@Editable public float cubeSize = .9f;
	@Editable public float cubeSpace = 1f;
	@Editable public float lacunarity = .05f;
	@Editable public float planarity = .1f;
	@Editable public float freq2D = .15f;
	@Editable public float freq3D = .15f;
	
	@Editable
	public void regen(){
		noise = new Noise();
		renderables.clear();
		final int cubes = SIZE*SIZE*SIZE;
		
		data = new int[cubes];
		
		Vector3 p = new Vector3();
		Vector2 p2 = new Vector2();
		
		// max 2^15-1 : 11^3 * 24
		if(SIZE > 11) SIZE = 11;
		
		for(int z=0 ; z<SIZE ; z++){
			for(int x=0 ; x<SIZE ; x++){
				float n2 = noise.apply(p2.set(x, z).scl(freq2D)) * planarity + .5f - planarity / 2;
				for(int y=0 ; y<SIZE && y<n2*SIZE ; y++)
				{
					
					float n = noise.apply(p.set(x, y, z).scl(freq3D));
					
					if(n>lacunarity){
						int index = (z * SIZE + y) * SIZE + x;
						data[index] = 1;
					}
					
				}
			}
		}
		
		
		
		
		
		ShapeCache r = new ShapeCache(6 * 4 * cubes, 6 * 6 * cubes, new VertexAttributes(VertexAttribute.Position(), VertexAttribute.Normal()), GL20.GL_TRIANGLES);
		MeshPartBuilder mpb = r.begin(GL20.GL_TRIANGLES);
		int cnt = 0;
		int SIZE2 = SIZE * SIZE;
		for(int z=0 ; z<SIZE ; z++)
			for(int y=0 ; y<SIZE ; y++)
				for(int x=0 ; x<SIZE ; x++){
					int index = (z * SIZE + y) * SIZE + x;
					if(data[index] > 0){
						int neigh = 0;
						if(x > 0 && data[index-1]>0) neigh++;
						if(x < SIZE-1 && data[index+1]>0) neigh++;
						if(y > 0 && data[index-SIZE]>0) neigh++;
						if(y < SIZE-1 && data[index+SIZE]>0) neigh++;
						if(z > 0 && data[index-SIZE2]>0) neigh++;
						if(z < SIZE-1 && data[index+SIZE2]>0) neigh++;
						if(neigh < 6){
							BoxShapeBuilder.build(mpb, x * cubeSpace, y* cubeSpace, z* cubeSpace, cubeSize, cubeSize, cubeSize);
							cnt++;
						}
					}
				}
		r.end();
		
		System.out.println(cnt + "/" + cubes);
		
		renderables.add(r);
	}
	
	public VoxelWorldSystem(GameScreen game) {
		super(GamePipeline.RENDER);
		this.game = game;
		
		//MeshBuilder b = new MeshBuilder();
		//b.begin(VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked);
		
		regen();
		
		batch = new ModelBatch();
	}
	
	@Override
	public void update(float deltaTime) 
	{
		batch.begin(game.camera);
		
		batch.render(renderables, getEngine().getSystem(G3DRendererSystem.class).environment);
		
		batch.end();
	}
}
