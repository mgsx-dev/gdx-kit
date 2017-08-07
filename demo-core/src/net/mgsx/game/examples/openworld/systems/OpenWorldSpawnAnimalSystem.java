package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.PostInitializationListener;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.examples.openworld.components.SpawnAnimalComponent;
import net.mgsx.game.examples.openworld.components.SpawnAnimalComponent.State;
import net.mgsx.game.examples.openworld.model.OpenWorldElement;
import net.mgsx.game.examples.openworld.model.OpenWorldPathBuilder;
import net.mgsx.game.examples.openworld.model.SpawnGenerator;
import net.mgsx.game.examples.openworld.utils.VirtualGrid;
import net.mgsx.game.plugins.g3d.components.G3DModel;
import net.mgsx.game.plugins.spline.components.PathComponent;
import net.mgsx.game.plugins.spline.components.SplineDebugComponent;

/**
 * Logic system responsible of spawning element/entities and
 * manage their lifecycle.
 * 
 * @author mgsx
 *
 */
public class OpenWorldSpawnAnimalSystem extends IteratingSystem implements PostInitializationListener
{
	public static class SpawnAnimalChunk {
		public Array<Entity> entities = new Array<Entity>();
		public Array<OpenWorldElement> elements = new Array<OpenWorldElement>();
		public Rectangle zone = new Rectangle();
	}
	
	@Inject OpenWorldCameraSystem cameraSystem;
	@Inject UserObjectSystem userObject;
	@Inject OpenWorldGeneratorSystem generatorSystem;
	@Inject OpenWorldEnvSystem envSystem;
	
	private VirtualGrid<SpawnAnimalChunk> spawnGrid;

	private int spawnGridSize = 3; // 3 active chunks forming a minimal 3x3=9 chunks
	private int spawnGridMargin = 2; // extra 2 chunks forming a maximal 5x5=25 chunks
	private float spawnGridScale = 100; // 100m chunk size forming a 300m X 300m living grid, 500m X 500m memory
	
	private SpawnGenerator spawnGenerator;
	private AssetManager assets;
	
	private OpenWorldPathBuilder pathBuilder = new OpenWorldPathBuilder();

	public OpenWorldSpawnAnimalSystem(AssetManager assets) {
		super(Family.all(SpawnAnimalComponent.class, G3DModel.class).get(), GamePipeline.LOGIC);
		this.assets = assets;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		engine.addEntityListener(Family.all(SpawnAnimalComponent.class).get(), new EntityListener() {
			@Override
			public void entityRemoved(Entity entity) {
				SpawnAnimalComponent spawn = SpawnAnimalComponent.components.get(entity);
				spawn.chunk.entities.removeValue(entity, true);
				// we remove linked element only if active. That is not
				// removed because of exit state.
				if(spawn.active){
					spawn.chunk.elements.removeValue(spawn.element, true);
				}
			}
			
			@Override
			public void entityAdded(Entity entity) {
				// noop
			}
		});
		
		spawnGrid = new VirtualGrid<SpawnAnimalChunk>() {

			@Override
			protected void dispose(SpawnAnimalChunk cell) {
				// remove remaining entities
				for(Entity entity : cell.entities){
					getEngine().removeEntity(entity);
				}
			}

			@Override
			protected SpawnAnimalChunk create(float worldX, float worldY) {
				SpawnAnimalChunk chunk = new SpawnAnimalChunk();
				chunk.zone.set(worldX, worldY, spawnGridScale, spawnGridScale);
				spawn(chunk, worldX, worldY);
				return chunk;
			}

			@Override
			protected void update(SpawnAnimalChunk cell, int distance) {
				// maybe change LOD
			}

			@Override
			protected void enter(SpawnAnimalChunk cell) 
			{
				// we materialize elements
				for(OpenWorldElement element : cell.elements){
					
					// instanciate animal
					Entity entity = getEngine().createEntity();
					
					G3DModel model = getEngine().createComponent(G3DModel.class);
					Model modelTempalte = assets.get(element.modelPath);
					model.modelInstance = new ModelInstance(modelTempalte);
					model.animationController = new AnimationController(model.modelInstance);
					
					entity.add(model);
					
					cell.entities.add(entity);
					
					// Keep track of it.
					SpawnAnimalComponent sp = getEngine().createComponent(SpawnAnimalComponent.class);
					sp.chunk = cell;
					sp.active = true;
					sp.state = null;
					sp.element = element;
					entity.add(sp);
					
					PathComponent path = getEngine().createComponent(PathComponent.class);
					Vector3[] controlPoints = new Vector3[4];
					// TODO limits are handled by update and initial points hould be same point
					pathBuilder.set(generatorSystem, 30, 3, .5f);
					pathBuilder.resetLimit().groundMin(0).groundMax(0).absoluteMin(envSystem.waterLevel - .5f);
					pathBuilder.createPath(controlPoints, element.position);
					
					path.path = new CatmullRomSpline<Vector3>(controlPoints , false);
					path.length = path.path.approxLength(100);
					entity.add(path);
					
					// TODO add kinematic body in order to allow player selection/interaction ...
					
					getEngine().addEntity(entity);
				}
			}

			@Override
			protected void exit(SpawnAnimalChunk cell) {
				// remove entity but keep element definition
				for(Entity entity : cell.entities){
					SpawnAnimalComponent spawn = SpawnAnimalComponent.components.get(entity);
					spawn.active = false;
					getEngine().removeEntity(entity);
				}
				cell.entities.clear();
			}
		};
		
		spawnGrid.resize(spawnGridSize, spawnGridSize, spawnGridMargin, spawnGridMargin, spawnGridScale);
	}
	
	@Override
	public void onPostInitialization() {
		spawnGenerator = new SpawnGenerator(SpawnGenerator.LAYER_ANIMAL, getEngine());
	}
	
	private void spawn(SpawnAnimalChunk chunk, float worldX, float worldY)
	{
		float centerX = worldX + spawnGridScale * (MathUtils.random() * .5f + .5f);
		float centerY = worldY + spawnGridScale * (MathUtils.random() * .5f + .5f);
		
		spawnGenerator.generate(chunk.elements, centerX, centerY);
		
		for(OpenWorldElement element : chunk.elements){
			
			// cluster position start from center
			float angle = MathUtils.random(MathUtils.PI2);
			float distance = MathUtils.random();
			distance *= distance;
			distance *= spawnGridScale/2;
			
			float x = centerX + MathUtils.cos(angle) * distance;
			float y = centerY + MathUtils.sin(angle) * distance;
			
			float altitude = generatorSystem.getAltitude(x, y);
			
			element.position.set(x, altitude, y);
		}
	}
	
	private Vector3 playerPosition = new Vector3();
	private Vector3 direction = new Vector3();
	private Vector3 target = new Vector3();
	
	@Override
	public void update(float deltaTime) 
	{
		Camera camera = cameraSystem.getCamera();
		if(camera == null) return;
		
		playerPosition.set(camera.position);
		
		// update grid
		spawnGrid.update(camera.position.x, camera.position.z);
		
		super.update(deltaTime);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		// TODO animal has a range area and never leave it ! so path could be computed once ?
		
		
		// TODO update animal logic and animations switch based on playerPosition
		SpawnAnimalComponent animal = SpawnAnimalComponent.components.get(entity);
		G3DModel model = G3DModel.components.get(entity);
		PathComponent path = PathComponent.components.get(entity);
		
		// XXX this is a kind of state machine. Should we use behavior trees ?
		
		// initial state, set it strolling
		if(animal.state == null){
			animal.state = State.STROLL;
			animal.pathTime = 0;
			animal.speed = 1;
			
			
			// TODO animation depends on state (walking, running, ...etc)
			model.animationController.setAnimation("Armature|walk", -1, 1.5f, null);
		}
		if(animal.state == State.STROLL)
		{
			float dst = playerPosition.dst(animal.element.position);
			if(dst < 10){
				model.animationController.setAnimation("Armature|run", -1, 1.5f, null);
				animal.state = State.FLEE;
				animal.speed = 8;
			}else{
				// update anim
				model.animationController.current.speed = animal.speed;
			}
		}
		if(animal.state == State.FLEE)
		{
			// TODO maybe not flee in any directions, flee from player ...
			float dst = playerPosition.dst(animal.element.position);
			if(dst > 30){
				model.animationController.setAnimation("Armature|walk", -1, 1.5f, null);
				animal.state = State.STROLL;
				animal.speed = .5f;
			}else{
				// update anim
				model.animationController.current.speed = animal.speed / 4;
			}
		}
		
		animal.pathTime += deltaTime * animal.speed / path.length;
		
		
		if(animal.pathTime > 1){
			CatmullRomSpline<Vector3> spline = (CatmullRomSpline<Vector3>)path.path;
			
			// algorithme :
			// compute ahead from current direction and head bias.
			// is ahead of animal appropriate zone ?
			//	- in its area ?
			//  - in appropriate environement (water/land/rocks...etc)
			// if so then reset bias.
			// else if bias not set then set a bias (random left or right).
			// finally generate a direction around bias.
			
			// Check 3 meters ahead of animal
			direction.set(spline.controlPoints[3]).sub(spline.controlPoints[2]).nor();
			target.set(animal.element.position).mulAdd(direction, 3);
			float altitudeAhead = generatorSystem.getAltitude(target.x, target.z);
			
			// in water
			if(altitudeAhead < envSystem.waterLevel || !animal.chunk.zone.contains(target.x, target.z)){
				if(animal.directionBias == 0){
					animal.directionBias = MathUtils.randomSign();
				}
			}
			// it's OK
			else{
				animal.directionBias = 0;
			}
			
			// generate a random direction around direction and bias
			direction.rotate(Vector3.Y, (animal.directionBias * 2 + MathUtils.random()) * 10);
			
			// compute final target : current position + 3 meters
			target.set(animal.element.position).mulAdd(direction, 3);
			target.y = generatorSystem.getAltitude(target.x, target.z);
			
			// append point
			pathBuilder.updateDynamicPath(spline.controlPoints, target);
			path.length = spline.approxLength(100);
			animal.pathTime -= 1;
			
			// XXX spline debug
			SplineDebugComponent sdc = SplineDebugComponent.components.get(entity);
			if(sdc == null) entity.add(sdc = getEngine().createComponent(SplineDebugComponent.class));
			sdc.dirty = true;
		}
		
		animal.pathTime = MathUtils.clamp(animal.pathTime, 0, 1); // clamp for security
		
		path.path.valueAt(animal.element.position, animal.pathTime);
		path.path.derivativeAt(direction , animal.pathTime);
		
		// update motion
		model.modelInstance.transform.setToLookAt(direction.nor().scl(-1), Vector3.Y).inv();
		model.modelInstance.transform.rotate(Vector3.Y, -90);			
		model.modelInstance.transform.setTranslation(animal.element.position);
	
		model.modelInstance.calculateTransforms();
	}

	public void clear() 
	{
		spawnGrid.clear();
	}
}
