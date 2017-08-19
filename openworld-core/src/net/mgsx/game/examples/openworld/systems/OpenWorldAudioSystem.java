package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.PostInitializationListener;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.examples.openworld.components.SpawnAnimalComponent;
import net.mgsx.game.examples.openworld.model.GameAction;
import net.mgsx.game.examples.openworld.model.OpenWorldGameEventListener;
import net.mgsx.game.plugins.bullet.system.BulletWorldSystem;
import net.mgsx.pd.Pd;

@EditableSystem
public class OpenWorldAudioSystem extends EntitySystem implements PostInitializationListener
{
	@Inject OpenWorldEnvSystem env;
	@Inject WeatherSystem weather;
	@Inject OpenWorldCameraSystem cameraSystem;
	@Inject OpenWorldGeneratorSystem generator;
	@Inject OpenWorldGameSystem gameSystem;
	@Inject BulletWorldSystem bulletSystem;
	
	@Editable(readonly=true, realtime=true) 
	public transient int animalsInRange;
	
	@Editable(readonly=true, realtime=true) 
	public transient float occF, occB, occL, occR, occT, flora, altitudeDelta;
	
	
	@Editable public float animalsRange = 30f;
	
	@Editable public float maxOcclusionDistance = 10;
	
	private Ray rayFrom = new Ray(), rayResult = new Ray();
	private Vector3 rayDirection = new Vector3();
	
	private ImmutableArray<Entity> animals;
	
	public OpenWorldAudioSystem() {
		super(GamePipeline.AFTER_LOGIC);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		animals = engine.getEntitiesFor(Family.all(SpawnAnimalComponent.class).get());
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
	public void update(float deltaTime) {
		Pd.audio.sendFloat("rain", weather.rainRate);
		Pd.audio.sendFloat("wind", weather.windSpeed);
		Pd.audio.sendFloat("time", env.timeOfDay);
		
		Camera camera = cameraSystem.getCamera();
		if(camera == null) return;
		
		flora = generator.getFlora(camera.position.x, camera.position.z);
		Pd.audio.sendFloat("flora", flora);
		
		float altitude = generator.getAltitude(camera.position.x, camera.position.z);
		altitudeDelta = altitude - env.waterLevel;
		Pd.audio.sendFloat("altitude", altitudeDelta);
		
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
		Pd.audio.sendList("move", state, cameraSystem.currentMove / deltaTime);
		
		animalsInRange = 0;
		for(Entity entity : animals){
			SpawnAnimalComponent animal = SpawnAnimalComponent.components.get(entity);
			float dst = animal.element.position.dst(camera.position);
			if(dst < animalsRange){
				animalsInRange++;
			}
		}
		Pd.audio.sendFloat("animals", animalsInRange);
		
		// occlusion test
		Pd.audio.sendList("occlusion-fblrt", 
				occF = occlusionCast(camera, rayDirection.set(camera.direction)),
				occB = occlusionCast(camera, rayDirection.set(camera.direction).rotate(Vector3.Y, 180)),
				occL = occlusionCast(camera, rayDirection.set(camera.direction).rotate(Vector3.Y, 90)),
				occR = occlusionCast(camera, rayDirection.set(camera.direction).rotate(Vector3.Y, -90)),
				occT = occlusionCast(camera, camera.up)
			);
		
		// TODO sample around player to get environment.
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

	
}
