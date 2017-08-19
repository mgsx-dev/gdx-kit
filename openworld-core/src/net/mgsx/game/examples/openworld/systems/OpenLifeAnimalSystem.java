package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Asset;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.EnumType;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.examples.openworld.components.WildLifeComponent;
import net.mgsx.game.examples.openworld.model.OpenWorldPathBuilder;
import net.mgsx.game.plugins.g3d.components.G3DModel;
import net.mgsx.game.plugins.spline.components.PathComponent;
import net.mgsx.game.plugins.spline.components.SplineDebugComponent;

@Storable("ow.animals")
@EditableSystem
public class OpenLifeAnimalSystem extends IteratingSystem
{
	// FBX exported with 0.001 scale
	@Asset("openworld/ptero.g3dj")
	public Model birdModel;
	
	@Asset("openworld/fish.g3dj")
	public Model fishModel;
	
	@Asset("openworld/quadriped.g3dj")
	public Model quadriModel;
	
	@Inject OpenWorldCameraSystem cameraSystem;
	@Inject OpenWorldGeneratorSystem generator;
	@Inject OpenWorldEnvSystem env;
	
	@Editable public int maxAnimals = 10;
	private int numAnimals; // required because entity list doesn't refresh
	
	@Editable public float speed = 0.1f;
	@Editable public float distance = 5f;
	
	@Editable public float flyingGroundMin = 5f;
	@Editable public float flyingGroundRange = 10f;
	@Editable public float aquaticOffset = 1f;
	
	
	@Editable(type=EnumType.UNIT)
	public float randomness = .2f;
	
	@Editable public transient boolean debugSplines = false;
	
	private OpenWorldPathBuilder pathBuilder = new OpenWorldPathBuilder();

	public OpenLifeAnimalSystem() {
		super(Family.all(WildLifeComponent.class).get(), GamePipeline.RENDER);
	}
	
	public void update(float deltaTime) 
	{
		// if bird count not reached, lets create a bird
		// define its trajectory : around camera
		// 
		Camera camera = cameraSystem.getCamera();
		if(camera == null) return;
		
		// query world
		float cameraY = camera.position.y;
		float landY = generator.getAltitude(camera.position.x, camera.position.z);
		float waterY = env.waterLevel;
		boolean aquaticENV = waterY > landY;
		boolean aquaticPOV = cameraY < waterY;
		
		while(numAnimals < maxAnimals)
		{
			Model modelTempalte;
			String animation;
			
			// generate wild entity depending on environnement :
			// if there is water 
			//		if camera inside water : more fishes
			//		else more birds
			// else mix between birds and land ...
			boolean isFish = false;
			boolean isBird = false;
			// TODO random mix
			if(aquaticENV){
				// TODO mix when water transprency will be OK ... ?
				if(aquaticPOV){ 
					isFish = true; 
				}else{
					isBird = true;
				}
			}
			else{
				// land/bird mix
				isBird = MathUtils.randomBoolean();
			}
			
			if(isBird){
				modelTempalte = birdModel;
				animation = "Armature|fly-full";
			}
			else if(isFish){
				modelTempalte = fishModel;
				animation = "Armature|swim-speed";
			}else{
				modelTempalte = quadriModel;
				animation = "Armature|run"; // TODO walk or run depends on speed ...
			}
			
			Entity animal = getEngine().createEntity();
			G3DModel model = getEngine().createComponent(G3DModel.class);
			model.modelInstance = new ModelInstance(modelTempalte);
			model.animationController = new AnimationController(model.modelInstance);
			
			model.animationController.setAnimation(animation, -1, 1.5f, null);
			
			animal.add(model);
			
			PathComponent path = getEngine().createComponent(PathComponent.class);
			Vector3[] controlPoints = new Vector3[10]; // TODO should be configurable
			pathBuilder.set(generator, 30, distance, randomness);
			
			// algo for limits : 
			// - land animals : range for 0 to 0 relative to ground
			// - water animals : range from 0+epsilon (ground) to absolute water limit - epsilon
			// - air anmals : range from max(ground, water)+epsilon to arbitrary limit (no limit actually)
			if(isFish){
				pathBuilder.resetLimit().groundMin(aquaticOffset).absoluteMax(waterY - aquaticOffset);
			}else if(isBird){
				pathBuilder.resetLimit()
				//.absoluteMin(waterY + flyingGroundMin)
				.groundMin(flyingGroundMin)
				.absoluteMax(waterY + flyingGroundMin + flyingGroundRange);
			}else{
				pathBuilder.resetLimit().groundMin(0).groundMax(0).absoluteMin(waterY - .5f); // TODO swim
			}
			// TODO there might be some swiming animals (limited to sea level but can go there) ...
			
			
			// TODO algorithm to keep fish/land in suitable zone :
			// for fish/land : if path leads out then back in (180°)
			// for birds there is no such limitations
			// if land/fish is stucked, then just create an empty path or no path at all, it just wait... until he die
			
			// TODO maybe create at anoher specific distance from camera ...
			Vector3 init = pathBuilder.randomXZ(new Vector3(), camera.position);
			
			// TODO created path should returns how many dots as been computed due to limitations !
			pathBuilder.createPath(controlPoints, init);

			path.path = new CatmullRomSpline<Vector3>(controlPoints , false);
			path.length = path.path.approxLength(100);
			animal.add(path);
			
			WildLifeComponent life = getEngine().createComponent(WildLifeComponent.class);
			life.time = 0;
			life.speed = MathUtils.random(0.9f, 1.1f); // TODO conf and depends on animal type
			animal.add(life);
			
			if(debugSplines){
				animal.add(getEngine().createComponent(SplineDebugComponent.class));
			}
			
			getEngine().addEntity(animal);
			numAnimals++;
		}
		
		super.update(deltaTime);
	}

	private Vector3 position = new Vector3();
	private Vector3 direction = new Vector3();
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		WildLifeComponent life = WildLifeComponent.components.get(entity);
		G3DModel model = G3DModel.components.get(entity);
		PathComponent path = PathComponent.components.get(entity);
		
		life.time += deltaTime * speed * life.speed / path.length;
		
		
		if(life.time > 1){
			
			getEngine().removeEntity(entity);
			numAnimals--;
			return; // TODO free to pool
		}
		
		path.path.valueAt(position, life.time);
		path.path.derivativeAt(direction, life.time);
		
		// TODO config and refactor hard limit
		float dstLimit = 100;
		if(cameraSystem.getCamera().position.dst2(position) > dstLimit*dstLimit){
			getEngine().removeEntity(entity);
			numAnimals--;
			return; // TODO free to pool
		}
		
		// TODO fix models coordinate system instead of here (bird should be fixed)
		// TODO only birds can rool, other should be Y-up
		if(model.modelInstance.model == birdModel){
			model.modelInstance.transform.setToRotation(Vector3.Z, direction.nor().scl(-1));
			model.modelInstance.transform.setTranslation(position);
		}else{
			// TODO find an optimized way to avoid matrix inversion !
			// TODO cross product to roll a little ?
			model.modelInstance.transform.setToLookAt(direction.nor().scl(-1), Vector3.Y).inv();
			model.modelInstance.transform.rotate(Vector3.Y, -90);			
			model.modelInstance.transform.setTranslation(position);
		}
		
		model.modelInstance.calculateTransforms();
	}

	
}
