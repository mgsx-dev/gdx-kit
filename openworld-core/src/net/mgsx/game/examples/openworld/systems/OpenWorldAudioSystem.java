package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.PostInitializationListener;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.examples.openworld.components.ObjectMeshComponent;
import net.mgsx.game.examples.openworld.components.SpatialComponent;
import net.mgsx.game.examples.openworld.components.SpawnAnimalComponent;
import net.mgsx.game.examples.openworld.model.GameAction;
import net.mgsx.game.examples.openworld.model.OpenWorldGameEventListener;
import net.mgsx.game.plugins.bullet.components.BulletComponent;
import net.mgsx.game.plugins.bullet.system.BulletWorldSystem;
import net.mgsx.pd.Pd;

@EditableSystem
public class OpenWorldAudioSystem extends EntitySystem implements PostInitializationListener
{
	private static final int MAX_SPATIALS = 3;
	
	private static final int ENVIRONMENT_SAMPLES = 6;

	private static float ENVIRONMENT_DISTANCE = 10;

	private static final boolean sendCameraOcclusion = false;
	private static final boolean sendAnimals = false;
	private static final boolean sendMeteo = false;
	
	private static final boolean multiCastOcclusion = true;

	private static final float EPSILON = 0.7f;
	
	@Inject OpenWorldEnvSystem env;
	@Inject WeatherSystem weather;
	@Inject OpenWorldCameraSystem cameraSystem;
	@Inject OpenWorldGeneratorSystem generator;
	@Inject OpenWorldGameSystem gameSystem;
	@Inject BulletWorldSystem bulletSystem;
	
	@Editable(readonly=true, realtime=true) 
	public transient int animalsInRange;
	
	@Editable(readonly=true, realtime=true) 
	public transient float occF, occB, occL, occR, occT;
	
	
	@Editable public float animalsRange = 30f;
	
	@Editable public float maxOcclusionDistance = 10;
	
	private Ray rayFrom = new Ray(), rayResult = new Ray();
	private Vector3 rayDirection = new Vector3();
	
	private Array<SpatialComponent> spatials = new Array<SpatialComponent>();
	
	private ImmutableArray<Entity> animals, objects;
	
	private Vector3 occlusionTarget = new Vector3();
	private Vector3 occlusionDelta = new Vector3();
	private Vector3 povUp = new Vector3();
	private Vector3 povTan = new Vector3();
	private Vector3 cameraPrevPos = new Vector3();
	private boolean firstFrame = true;
	
	public OpenWorldAudioSystem() {
		super(GamePipeline.AFTER_LOGIC);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		animals = engine.getEntitiesFor(Family.all(SpawnAnimalComponent.class).get());
		objects = engine.getEntitiesFor(Family.all(SpatialComponent.class, BulletComponent.class).get());
		
		engine.addEntityListener(Family.all(ObjectMeshComponent.class).get(), new EntityListener() {
			@Override
			public void entityRemoved(Entity entity) {
			}
			
			@Override
			public void entityAdded(Entity entity) {
				ObjectMeshComponent omc = ObjectMeshComponent.components.get(entity);
				if("chest".equals(omc.userObject.element.type)){
					SpatialComponent spatial = getEngine().createComponent(SpatialComponent.class);
					spatial.seed = MathUtils.random();
					entity.add(spatial);
				}
			}
		});
	}
	
	@Override
	public void onPostInitialization() {
		gameSystem.addGameEventListener(new OpenWorldGameEventListener() {
			@Override
			public void onSecretUnlocked(String itemId) {
				Pd.audio.sendBang("onSecretUnlocked");
			}
			@Override
			public void onQuestUnlocked(String qid) {
				Pd.audio.sendBang("onQuestUnlocked");
			}
			@Override
			public void onQuestRevealed(String qid) {
				Pd.audio.sendBang("onQuestRevealed");
			}
			@Override
			public void onPlayerAction(GameAction action, String type) {
				switch(action){
				case CRAFT: Pd.audio.sendBang("CRAFT");
					break;
				case DESTROY: Pd.audio.sendBang("DESTROY");
					break;
				case DROP: Pd.audio.sendBang("DROP");
					break;
				case EAT: Pd.audio.sendBang("EAT");
					break;
				case GRAB: Pd.audio.sendBang("GRAB");
					break;
				case LOOK: Pd.audio.sendBang("LOOK");
					break;
				case SLEEP: Pd.audio.sendBang("SLEEP");
					break;
				case USE: Pd.audio.sendBang("USE");
					break;
				default:
					break;
				}
			}
		});
	}
	
	@Override
	public void update(float deltaTime) 
	{
		if(sendMeteo){
			Pd.audio.sendFloat("rain", weather.rainRate);
			Pd.audio.sendFloat("wind", weather.windSpeed);
			Pd.audio.sendFloat("time", env.timeOfDay);
		}
		
		Camera camera = cameraSystem.getCamera();
		if(camera == null) return;
		
		float altitude = generator.getAltitude(camera.position.x, camera.position.z);
		float altitudeDelta = altitude - env.waterLevel;
		Pd.audio.sendFloat("altitude", altitudeDelta);
		
		
		if(firstFrame){
			firstFrame = false;
		}else{
			String state = "idle";
			if(gameSystem.diving){
				state = "diving";
			}
			if(gameSystem.walking){
				state = "walking";
			}
			if(gameSystem.swimming){
				state = "swimming";
			}
			if(gameSystem.flying){
				state = "flying";
			}
			
			float cameraSpeed = cameraPrevPos.dst(camera.position) / deltaTime;
		
			Pd.audio.sendList("move", state, cameraSpeed);
		}
		cameraPrevPos.set(camera.position);
		
		if(sendAnimals){
			animalsInRange = 0;
			for(Entity entity : animals){
				SpawnAnimalComponent animal = SpawnAnimalComponent.components.get(entity);
				float dst = animal.element.position.dst(camera.position);
				if(dst < animalsRange){
					animalsInRange++;
				}
			}
			Pd.audio.sendFloat("animals", animalsInRange);
		}
		
		if(sendCameraOcclusion){
			// occlusion test
			Pd.audio.sendList("occlusion-fblrt", 
					occF = occlusionCast(camera, rayDirection.set(camera.direction)),
					occB = occlusionCast(camera, rayDirection.set(camera.direction).rotate(Vector3.Y, 180)),
					occL = occlusionCast(camera, rayDirection.set(camera.direction).rotate(Vector3.Y, 90)),
					occR = occlusionCast(camera, rayDirection.set(camera.direction).rotate(Vector3.Y, -90)),
					occT = occlusionCast(camera, camera.up)
					);
		}
		
		// sample around player to get environment.
		float floraFactor = 0;
		float waterFactor = 0;
		for(int i=0 ; i<ENVIRONMENT_SAMPLES ; i++){
			float angle = MathUtils.PI2 * (float)i / (float)ENVIRONMENT_SAMPLES;
			float px = camera.position.x + MathUtils.cos(angle) * ENVIRONMENT_DISTANCE;
			float py = camera.position.z + MathUtils.sin(angle) * ENVIRONMENT_DISTANCE;
			floraFactor += generator.getFlora(px, py) > 0 ? 1 : 0;
			waterFactor += (generator.getAltitude(px, py) - env.waterLevel) < 0 ? 1 : 0;
		}
		floraFactor += generator.getFlora(camera.position.x, camera.position.y) > 0 ? 1 : 0;
		waterFactor += (generator.getAltitude(camera.position.x, camera.position.y) - env.waterLevel) < 0 ? 1 : 0;
		
		waterFactor /= (1f + ENVIRONMENT_SAMPLES);
		floraFactor /= (1f + ENVIRONMENT_SAMPLES);
		
		Pd.audio.sendFloat("forest", floraFactor);
		Pd.audio.sendFloat("water", waterFactor);
		
		// sending camera information
		
		// compute camera TBN vectors and send to PD
		povTan.set(camera.direction).crs(camera.up).nor();
		povUp.set(povTan).crs(camera.direction).nor();
		
		Pd.audio.sendList("camera", 
				camera.position.x, camera.position.y, camera.position.z,
				camera.direction.x, camera.direction.y, camera.direction.z,
				povUp.x, povUp.y, povUp.z);
		
		// sending the nearest treasures (with distance, angle, and occlusion)
		
		// query distance
		spatials.clear();
		for(Entity entity : objects)
		{
			SpatialComponent spatial = SpatialComponent.components.get(entity);
			BulletComponent bullet = BulletComponent.components.get(entity);
			
			bullet.object.getWorldTransform().getTranslation(spatial.center);
			spatial.delta.set(spatial.center).sub(camera.position);
			spatial.distance = spatial.delta.len();
			
			spatials.add(spatial);
		}
		
		// filter nearest
		spatials.sort(SpatialComponent.distanceComparator);
		
		// send with more information
		for(int i=0 ; i<MAX_SPATIALS ; i++)
		{
			if(i < spatials.size)
			{
				SpatialComponent spatial = spatials.get(i);
				
				// add more info (optimization)
				
				// mode multi occlusion
				if(multiCastOcclusion)
				{
					float r = .5f;
					int count = 0;
					if(occlusionQuery(camera.position, occlusionTarget.set(spatial.center).add(r, r, r))) count++;
					if(occlusionQuery(camera.position, occlusionTarget.set(spatial.center).add(-r, r, r))) count++;
					if(occlusionQuery(camera.position, occlusionTarget.set(spatial.center).add(r, r, -r))) count++;
					if(occlusionQuery(camera.position, occlusionTarget.set(spatial.center).add(-r, r, -r))) count++;
					spatial.occlusion = count / 4f;
					
				}
				// mode simple occlusion
				else
				{
					spatial.occlusion = occlusionQuery(camera.position, spatial.delta, spatial.distance) < spatial.distance - EPSILON ? 1 : 0;
				}
				
				// angle
				spatial.angle = spatial.distance == 0 ? 0 : spatial.delta.dot(camera.direction) / spatial.distance;
				
				// send to PD : 
				// index, seed(positive), position(x,y,z), occlusion
				Pd.audio.sendList("chest", i, spatial.seed, 
						spatial.center.x, spatial.center.y, spatial.center.z,
						spatial.occlusion);
			}
			else
			{
				// send to PD : 
				// index, off(-1)
				Pd.audio.sendList("chest", i, -1);
			}
		}
		
	}
	
	private float occlusionCast(Camera camera, Vector3 direction){
		rayFrom.set(camera.position, direction);
		rayFrom.direction.scl(maxOcclusionDistance);
		if(bulletSystem.rayCastObject(rayFrom, rayResult) != null) {
			if(rayResult.direction.dot(direction) < -.7f)
				return rayResult.origin.dst(rayFrom.origin) / maxOcclusionDistance;
		}
		return 1;
	}
	
	/**
	 * return occlusion distance (from 0 to deltaLen)
	 * @param origin
	 * @param delta
	 * @param deltaLen
	 * @return
	 */
	private float occlusionQuery(Vector3 origin, Vector3 delta, float deltaLen){
		rayFrom.set(origin, delta);
		if(bulletSystem.rayCastObject(rayFrom, rayResult) != null) {
			return rayResult.origin.dst(rayFrom.origin);
		}
		return deltaLen;
	}
	
	private boolean occlusionQuery(Vector3 origin, Vector3 target){
		occlusionDelta.set(target).sub(origin);
		rayFrom.set(origin, occlusionDelta);
		if(bulletSystem.rayCastObject(rayFrom, rayResult) != null) {
			float len = occlusionDelta.len();
			if(rayResult.origin.dst(rayFrom.origin) < len - EPSILON){
				return true;
			}
		}
		return false;
	}
	
}
