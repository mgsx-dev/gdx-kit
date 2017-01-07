package net.mgsx.game.examples.crafting.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.examples.crafting.utils.Cube;
import net.mgsx.game.examples.crafting.utils.Index3;
import net.mgsx.game.examples.crafting.utils.Noise;
import net.mgsx.game.plugins.g3d.systems.G3DRendererSystem;

@EditableSystem
public class VoxelWorldSystem extends EntitySystem
{
	private ModelBatch batch;
	private GameScreen game;
	private Array<RenderableProvider> renderables = new Array<RenderableProvider>();
	
	public static class VoxelInfo{
		public boolean solid;
		public boolean full;
		public Vector3 position = new Vector3();
	}
	
	private Cube<VoxelPart> view;
	
	Noise noise;
	
	@Editable public int WORLD_SIZE = 4;
	@Editable public int VIEW_SIZE = 8;
	@Editable public float cubeSize = .9f;
	@Editable public float cubeSpace = 300f;
	@Editable public float lacunarity = .05f;
	@Editable public float planarity = 300f;
	@Editable public float freq2D = .01f;
	@Editable public float freq3D = .01f;
	@Editable public Vector3 offset = new Vector3();
	
	@Editable
	public void regen(){
		noise = new Noise();
		view.regenerate();
	}
	
	private void create(){
		view = new Cube<VoxelPart>(WORLD_SIZE, cubeSpace * 1.f) { // TODO debug separate
			@Override
			protected void generate(VoxelPart data, Index3 index, Vector3 position) 
			{
				data.reset();
//				float n2 = noise.apply(p2.set(position.x, position.z).scl(freq2D)) * planarity + .5f - planarity / 2;
//
//				if(position.y<n2*VIEW_SIZE){
					data.generateData(VoxelWorldSystem.this, position);
					data.generateMesh(cubeSize * cubeSpace / VIEW_SIZE, cubeSpace / VIEW_SIZE);
					genCount++;
					System.out.println(genCount);
//				}
			}
			
			@Override
			protected VoxelPart create() {
				VoxelPart part = new VoxelPart(VIEW_SIZE, cubeSpace / VIEW_SIZE);
				renderables.add(part);
				return part;
			}
		};
	}
	
	@Editable
	public void recreate(){
		
		renderables.clear();
		
		create();
		
		view.regenerate();
	}
	
	
	
	Vector3 p = new Vector3();
	Vector2 p2 = new Vector2();
	
	private int genCount = 0;
	
	public VoxelWorldSystem(GameScreen game) {
		super(GamePipeline.RENDER);
		this.game = game;
		noise = new Noise();
		create();
		
		batch = new ModelBatch();
	}
	
	@Override
	public void update(float deltaTime) 
	{
		
		// set cube in bounds : min
		game.camera.far = 500f;
		game.camera.update(true);
		//view.set(offset);
		BoundingBox box = new BoundingBox();
		box.set(game.camera.frustum.planePoints[0], game.camera.frustum.planePoints[0]);
		for(int i=0 ; i<game.camera.frustum.planePoints.length ; i++) box.ext(game.camera.frustum.planePoints[i]);
		
		
		
		Vector3 center = box.getCenter(new Vector3());
		view.set(offset.cpy().add(box.min));
		
		batch.begin(game.camera);
		
		batch.render(renderables, getEngine().getSystem(G3DRendererSystem.class).environment);
		
		batch.end();
	}
}
