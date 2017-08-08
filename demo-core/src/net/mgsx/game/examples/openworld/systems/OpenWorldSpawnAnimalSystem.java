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
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.examples.openworld.components.SpawnAnimalComponent;
import net.mgsx.game.examples.openworld.components.SpawnAnimalComponent.Environment;
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
@EditableSystem
public class OpenWorldSpawnAnimalSystem extends IteratingSystem implements PostInitializationListener
{
	public static class SpawnAnimalChunk {
		public Array<Entity> entities = new Array<Entity>();
		public Array<OpenWorldElement> elements = new Array<OpenWorldElement>();
		public Rectangle zone = new Rectangle();
	}
	
	@Editable public transient boolean splineDebug =false;

	
	@Inject OpenWorldCameraSystem cameraSystem;
	@Inject UserObjectSystem userObject;
	@Inject OpenWorldGeneratorSystem generatorSystem;
	@Inject OpenWorldEnvSystem envSystem;
	
	private VirtualGrid<SpawnAnimalChunk> spawnGrid;

	private int spawnGridSize = 10; //XXX 3; // 3 active chunks forming a minimal 3x3=9 chunks
	private int spawnGridMargin = 2; // XXX 2; // extra 2 chunks forming a maximal 5x5=25 chunks
	private float spawnGridScale = 30; //XXX 100; // 100m chunk size forming a 300m X 300m living grid, 500m X 500m memory
	
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
					for(int i=0 ; i<controlPoints.length ; i++) controlPoints[i] = new Vector3();
					path.path = new CatmullRomSpline<Vector3>(controlPoints , false);
					
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
	private Vector3 target = new Vector3();
	private Vector3 xAxis = new Vector3();
	private Vector3 yAxis = new Vector3();
	private Vector3 zAxis = new Vector3();
	private Vector3 up = new Vector3();
	private Vector3 tan = new Vector3();
	private Vector3 direction = new Vector3();
	
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
	
	private void enterEnvironment(SpawnAnimalComponent animal, G3DModel model, Environment env)
	{
		animal.environment = env;
		applyStateEnvironment(animal, model);
	}
	private void enterState(SpawnAnimalComponent animal, G3DModel model, State state)
	{
		animal.state = state;
		applyStateEnvironment(animal, model);
	}
	private void applyStateEnvironment(SpawnAnimalComponent animal, G3DModel model)
	{
		// state enter logic : depends on new state and current environment.
		// entering a state change : 
		// - animation and animation speed
		// - global speed
		
		if(animal.state == State.STROLL)
		{
			if(animal.environment == Environment.AIR)
			{
				animal.speed = 4;
				model.animationController.setAnimation("Armature|fly-full", -1, 1f, null);
			}
			else if(animal.environment == Environment.LAND)
			{
				animal.speed = .5f;
				model.animationController.setAnimation("Armature|walk", -1, 1f, null);
			}
			else if(animal.environment == Environment.WATER)
			{
				animal.speed = 2;
				model.animationController.setAnimation("Armature|swim-speed", -1, 1f, null);
			}
		}
		else if(animal.state == State.FLEE)
		{
			if(animal.environment == Environment.AIR)
			{
				animal.speed = 16;
				model.animationController.setAnimation("Armature|fly-full", -1, 2f, null);
			}
			else if(animal.environment == Environment.LAND)
			{
				animal.speed = 8;
				model.animationController.setAnimation("Armature|run", -1, 2f, null);
			}
			else if(animal.environment == Environment.WATER)
			{
				animal.speed = 8;
			}
		}
		else if(animal.state == State.IDLE)
		{
			
		}
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		float airMaxAltitude = 30;
		
		
		// update animal logic and animations switch based on playerPosition
		SpawnAnimalComponent animal = SpawnAnimalComponent.components.get(entity);
		G3DModel model = G3DModel.components.get(entity);
		PathComponent path = PathComponent.components.get(entity);
		
		// XXX this is a kind of state machine. Should we use behavior trees ?
		
		// initial state, set it strolling
		if(animal.state == null){
			animal.state = State.STROLL;
			animal.environment = Environment.AIR; // TODO remove this
			
			// compute initial altitude and path
			float altitude = generatorSystem.getAltitude(animal.element.position.x, animal.element.position.z);
			
			// check for abilities, which are limits
			float altitudeMin = altitude;
			float altitudeMax = airMaxAltitude; // TODO configurable high limite
			float groundMin = 0;
			float groundMax = airMaxAltitude;
			
			if(altitude < envSystem.waterLevel){
				if(!animal.element.waterAbility){
					altitudeMin = envSystem.waterLevel + 4; // XXX flying offset
				}
				if(!animal.element.airAbility){
					altitudeMax = envSystem.waterLevel;
				}
			}else{
				if(!animal.element.airAbility){
					groundMin = groundMax = 0;
				}else{
					groundMin = 4;
				}
			}
			
			float yMin = Math.max(altitudeMin, altitude+groundMin);
			float yMax = Math.min(altitudeMax, altitude+groundMax);
			
			animal.element.position.y = MathUtils.random(yMin, yMax);
			
			// compute initial environment depending on where is spawn entity and which ability
			// in some case animals could be just under/above water level whithout having ability.
			if(animal.element.position.y < envSystem.waterLevel)
			{
				if(animal.element.waterAbility) animal.environment = Environment.WATER;
				else if(animal.element.airAbility) animal.environment = Environment.AIR;
				else animal.environment = Environment.LAND;
			}
			else
			{
				if(animal.element.airAbility) animal.environment = Environment.AIR;
				else if(animal.element.landAbility) animal.environment = Environment.LAND;
				else animal.environment = Environment.WATER;
			}
			
			// TODO initial path should use same algo as for update path ...
			CatmullRomSpline<Vector3> spline = (CatmullRomSpline<Vector3>)path.path;
//			pathBuilder.set(generatorSystem, 40, 3.1f, .5f);
//			// TODO set altitude relative to ground !
//			pathBuilder.resetLimit().absoluteMin(altitudeMin).absoluteMax(altitudeMax)
//			.groundMin(groundMin).groundMax(groundMax);
//			pathBuilder.createPath(spline.controlPoints, animal.element.position);
			spline.controlPoints[0].set(animal.element.position);
			for(int i=1 ; i<spline.controlPoints.length ; i++){
				spline.controlPoints[i].set(spline.controlPoints[i-1]).add(.01f, 0, 0);
			}
			
			path.length = path.path.approxLength(100);
			
			applyStateEnvironment(animal, model);
			animal.pathTime = 0;
		}
		if(animal.state == State.STROLL)
		{
			float dst = playerPosition.dst(animal.element.position);
			if(dst < 10){
				enterState(animal, model, State.FLEE);
			}
		}
		if(animal.state == State.FLEE)
		{
			float dst = playerPosition.dst(animal.element.position);
			if(dst > 30){
				enterState(animal, model, State.STROLL);
			}
		}
		
		
		// update motion on path
		animal.pathTime += deltaTime * animal.speed / path.length;
		
		// update path
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
			
			float aheadDistance = 3;
			float segmentDistance = 3; // XXX depends on env ...
			
			// Check 3 meters ahead of animal
			direction.set(spline.controlPoints[3]).sub(spline.controlPoints[2]).nor();
			target.set(animal.element.position).mulAdd(direction, aheadDistance);
			float altitudeAhead = generatorSystem.getAltitude(target.x, target.z);
			
			// compute appropriate status based on ability.
			// and detect environment changes.
			boolean isAppropriate = animal.chunk.zone.contains(target.x, target.z);
			boolean isAquatic = altitudeAhead < envSystem.waterLevel;
			if(animal.environment == Environment.LAND){
				if(isAquatic){
					if(animal.element.waterAbility){
						enterEnvironment(animal, model, Environment.WATER);
					}else if(animal.element.airAbility){
						enterEnvironment(animal, model, Environment.AIR);
					}else{
						isAppropriate = false;
					}
				}
			}
			else if(animal.environment == Environment.WATER){
				if(!isAquatic){
					if(animal.element.landAbility){
						enterEnvironment(animal, model, Environment.LAND);
					}else if(animal.element.airAbility){
						enterEnvironment(animal, model, Environment.AIR);
					}else{
						isAppropriate = false;
					}
				}
			}
			else if(animal.environment == Environment.AIR){
				if(isAquatic){
					if(animal.element.waterAbility){
						enterEnvironment(animal, model, Environment.WATER);
					}else{
						isAppropriate = false;
					}
				}else{
					if(animal.element.landAbility){
						enterEnvironment(animal, model, Environment.LAND);
					}else{
						isAppropriate = false;
					}
				}
			}
			
			
			// if not appropritate then bias direction
			if(!isAppropriate){
				if(animal.directionBias == 0){
					animal.directionBias = MathUtils.randomSign();
				}
			}
			// it's OK
			else{
				animal.directionBias = 0;
			}
			
			// generate a random direction  :
			// - around direction and bias (XZ plan)
			// - around direction (Pitch)
			direction.rotate(Vector3.Y, (animal.directionBias * 2 + MathUtils.random()) * 10);
			direction.y += MathUtils.random(-.3f, .3f);
			direction.nor();
			
			// compute final target : current position + 3 meters
			target.set(animal.element.position).mulAdd(direction, segmentDistance);
			
			// clamp altitude based on animal environment mode
			// TODO add some offsets
			// TODO maybe have a pitchBias for non landbased and then appropriate status could be
			// also checked about collision with ground...
			float targetAltitude = generatorSystem.getAltitude(target.x, target.z);
			if(animal.environment == Environment.LAND){
				target.y = targetAltitude;
			}
			else if(animal.environment == Environment.WATER){
				if(target.y < targetAltitude) target.y = targetAltitude;
				else if(target.y > envSystem.waterLevel) target.y = envSystem.waterLevel;
			}
			else{
				if(target.y < targetAltitude) target.y = targetAltitude;
				else if(target.y > airMaxAltitude) target.y = airMaxAltitude; // TODO hard limit for flying animals
			}
			
			// append point
			pathBuilder.updateDynamicPath(spline.controlPoints, target);
			path.length = spline.approxLength(100);
			animal.pathTime -= 1;
			
			// XXX spline debug
			if(splineDebug){
				SplineDebugComponent sdc = SplineDebugComponent.components.get(entity);
				if(sdc == null) entity.add(sdc = getEngine().createComponent(SplineDebugComponent.class));
				sdc.dirty = true;
			}
		}
		
		// update transform from path
		animal.pathTime = MathUtils.clamp(animal.pathTime, 0, 1); // clamp for security
		
		path.path.valueAt(animal.element.position, animal.pathTime);
		path.path.derivativeAt(direction , animal.pathTime);
		
		// update model transform depending on current environment :
		// land and water animals keep head up.
		// air animals could roll on spline.
		if(animal.environment == Environment.AIR){
			model.modelInstance.transform.setToRotation(Vector3.Z, direction.nor().scl(-1));
			model.modelInstance.transform.setTranslation(animal.element.position);
		}else{
			// TODO find another way and sometimes matrix is not invertible and crash !
			
			direction.nor();
			tan.set(Vector3.Y).crs(direction).nor();
			up.set(tan).crs(direction).nor();
			
			xAxis.set(tan.x, direction.x, up.x);
			yAxis.set(tan.y, direction.y, up.y);
			zAxis.set(tan.z, direction.z, up.z);
			
			// TODO optimize : avoid post rotations by changing axis
			model.modelInstance.transform.set(xAxis, yAxis, zAxis, animal.element.position);
			model.modelInstance.transform.rotate(Vector3.Y, -90);
			model.modelInstance.transform.rotate(Vector3.Z, 90);
		}
	
		model.modelInstance.calculateTransforms();
	}
	
	public void clear() 
	{
		spawnGrid.clear();
	}
}
