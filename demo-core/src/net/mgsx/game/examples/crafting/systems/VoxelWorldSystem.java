package net.mgsx.game.examples.crafting.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Sphere;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.examples.crafting.utils.Cube;
import net.mgsx.game.examples.crafting.utils.Index3;
import net.mgsx.game.examples.crafting.utils.Noise;
import net.mgsx.game.examples.crafting.utils.OpenSimplexNoise;
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
	OpenSimplexNoise simplex;
	
	@Editable public float time = 1f;
	@Editable public int WORLD_SIZE = 5;
	@Editable public int VIEW_SIZE = 8;
	@Editable public float cubeSize = .9f;
	@Editable public float cubeSpace = 300f;
	@Editable public float lacunarity = .57f;
	@Editable public float lacunarityTop = .05f;
	@Editable public float planarity = 400f;
	@Editable public float freq2D = 500f;
	@Editable public float freq3D = 300f;
	@Editable public float freq3D2 = 500f;
	@Editable public float freqLight = 500f;
	@Editable public Vector3 offset = new Vector3();
	@Editable public long seed = 0xdeadbeaf;
	@Editable public float farRange = 600f;
	
	public static final float SQRT2 = (float)Math.sqrt(2);
	
	Texture texture;
	
	@Editable
	public void reseed(){
		noise = new Noise(seed = MathUtils.random(0, Long.MAX_VALUE));
		simplex = new OpenSimplexNoise(seed);
		view.regenerate();
	}
	
	@Editable
	public void regen(){
		view.regenerate();
	}
	
	private void create(){
		view = new Cube<VoxelPart>(WORLD_SIZE, cubeSpace * 1.f) { // TODO debug separate
			private Vector3 v = new Vector3();
			
			private int getLOD(Vector3 position){
				int size;
				v.set(position).add(cubeSpace / 2);
				float dst = game.camera.position.dst(v);
				float dstFactor = dst / (game.camera.far + (cubeSpace / 2) * SQRT2);
				dstFactor *= dstFactor;
				if(dst < 800){
					size = 8;
//				}else if(dst < 800){
//					size = 7;
//				}else if(dst < 1000){
//					size = 6;
//				}else if(dst < 1200){
//					size = 5;
				}else if(dst < 1200){
					size = 4;
//				}else if(dst < 1600){
//					size = 3;
				}else if(dst < 1400){
					size = 2;
				}else{
					size = 1;
				}
				return size;
			}
			@Override
			protected void generate(VoxelPart data, Index3 index, Vector3 position) 
			{
				data.reset();
				int size = getLOD(position);
				data.setSize(size, cubeSpace / size);
				data.generateData(VoxelWorldSystem.this, position);
				data.generateMesh(cubeSize * cubeSpace / size, cubeSpace / size, texture);
				genCount++;
				//System.out.println(genCount);
			}
			
			@Override
			protected VoxelPart create() {
				VoxelPart part = new VoxelPart(VIEW_SIZE, cubeSpace / VIEW_SIZE);
				renderables.add(part);
				return part;
			}

			@Override
			protected void update(VoxelPart data, Index3 index, Vector3 position) {
				int size = getLOD(position);
				if(data.SIZE != size){
					generate(data, index, position);
				}
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
		this.texture = new Texture("craft.png");
		noise = new Noise(seed);
		simplex = new OpenSimplexNoise(seed);
		create();
		
		batch = new ModelBatch();
	}
	
	@Override
	public void update(float deltaTime) 
	{
//		game.camera.direction.setFromSpherical(
//				((float)Gdx.input.getY() / (float)Gdx.graphics.getHeight() -.5f) * 360 * MathUtils.degreesToRadians,
//				- * MathUtils.degreesToRadians
//				);
		
		if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)){
			game.camera.direction.set(Vector3.Z);
			game.camera.direction.rotate(Vector3.X, ((float)Gdx.input.getY() / (float)Gdx.graphics.getHeight() -.5f) * 360);
			game.camera.direction.rotate(Vector3.Y, -(float)Gdx.input.getX() / (float)Gdx.graphics.getWidth() * 2 * 360);
		}
		float camSpeed = 4f;
		Vector3 tan = new Vector3(game.camera.direction).crs(game.camera.up);
		if(Gdx.input.isKeyPressed(Input.Keys.Z)) game.camera.position.mulAdd(game.camera.direction, camSpeed);
		if(Gdx.input.isKeyPressed(Input.Keys.S)) game.camera.position.mulAdd(game.camera.direction, -camSpeed);
		if(Gdx.input.isKeyPressed(Input.Keys.Q)) game.camera.position.mulAdd(tan, -camSpeed);
		if(Gdx.input.isKeyPressed(Input.Keys.D)) game.camera.position.mulAdd(tan, camSpeed);
		 //game.camera.position.mulAdd(game.camera.direction, camSpeed);
		
		 // set cube in bounds : min
		((PerspectiveCamera)game.camera).fieldOfView = 67;
		game.camera.far = farRange;
		game.camera.update(true);
		
		// compute camera sphere (index 4 in frustrum plane points is bottom left far plane).
		float cameraSphereRadius = game.camera.frustum.planePoints[4].dst(game.camera.position);
		Sphere sphere = new Sphere(game.camera.position, cameraSphereRadius);
		
		//view.set(offset);
		BoundingBox box = new BoundingBox();
		box.set(game.camera.frustum.planePoints[0], game.camera.frustum.planePoints[0]);
		for(int i=0 ; i<game.camera.frustum.planePoints.length ; i++) box.ext(game.camera.frustum.planePoints[i]);
		
		
		
		Vector3 center = box.getCenter(new Vector3());
		view.set(offset.cpy().add(box.min));
		view.updateAll();
		
		batch.begin(game.camera);
		
		batch.render(renderables, getEngine().getSystem(G3DRendererSystem.class).environment);
		
		batch.end();
	}
}
