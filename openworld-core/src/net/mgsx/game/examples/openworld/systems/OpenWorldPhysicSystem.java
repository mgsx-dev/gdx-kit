package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.examples.openworld.components.ObjectMeshComponent;
import net.mgsx.game.examples.openworld.model.OpenWorldElement;
import net.mgsx.game.plugins.bullet.components.BulletComponent;
import net.mgsx.game.plugins.bullet.components.BulletHeightFieldComponent;
import net.mgsx.game.plugins.core.components.HeightFieldComponent;

/**
 * This system is responsible of all bullet physics manipulations :
 * - defines physical properties (friction, restitution, damping ...) for objects and ground
 * - apply forces and other constraints to dynamic objects
 * 
 * updates after logic stage because forces are based on logical states.
 * 
 * @author mgsx
 *
 */
@Storable("ow.physics")
@EditableSystem
public class OpenWorldPhysicSystem extends EntitySystem
{
	
	@Inject OpenWorldEnvSystem envSystem;
	@Inject WeatherSystem weatherSystem;
	@Inject OpenWorldGeneratorSystem generatorSystem;
	
	private Family terrainFamily = Family.all(BulletHeightFieldComponent.class, BulletComponent.class, HeightFieldComponent.class).get();
	private ImmutableArray<Entity> terrainEntities;
	
	private Family objectFamily = Family.all(ObjectMeshComponent.class, BulletComponent.class).get();
	private ImmutableArray<Entity> objectEntities;
	
	private transient Vector2 waterFlow = new Vector2();
	private transient Vector2 windFlow = new Vector2();
	
	public OpenWorldPhysicSystem() {
		super(GamePipeline.AFTER_LOGIC);
	}
	
	@Editable public float terrainFriction = .5f;
	@Editable public float terrainRestitution = .5f;
	
	@Editable public transient float AIR_DENSITY = 1.2f;
	@Editable public transient float WATER_DENSITY = 1000;
	
	@Editable
	public void applyToTerrain(){
		for(Entity entity : terrainEntities){
			applyTerrain(entity);
		}
	}
	
	private void applyTerrain(Entity entity){
		BulletComponent bullet = BulletComponent.components.get(entity);
		bullet.object.setFriction(terrainFriction);
		bullet.object.setRestitution(terrainRestitution);
	}
	
	@Editable
	public void applyToObject(){
		for(Entity entity : objectEntities){
			applyObject(entity);
		}
	}
	
	private void applyObject(Entity entity){
		BulletComponent bullet = BulletComponent.components.get(entity);
		ObjectMeshComponent object = ObjectMeshComponent.components.get(entity);
		// XXX object.userObject.element.
		// TODO based on object settings
		bullet.object.setFriction(.5f);
		bullet.object.setRestitution(.5f);
		if(bullet.object instanceof btRigidBody){
			btRigidBody body = (btRigidBody)bullet.object;
			// TODO damping should be based on surface / volume rate.
			// Workaround : density below 100 will fly
			float densityRate = 0.01f * object.userObject.element.density / AIR_DENSITY;
			float damping = 1 - MathUtils.clamp(densityRate * densityRate, 0, 1);
			object.userObject.element.damping = damping;
			body.setDamping(damping, 0);
		}
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		terrainEntities = engine.getEntitiesFor(terrainFamily);
		
		// set height fields properties
		engine.addEntityListener(terrainFamily, new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				// noop
			}
			
			@Override
			public void entityAdded(Entity entity) {
				applyTerrain(entity);
			}
		});
		
		// set objects properties
		
		objectEntities = engine.getEntitiesFor(objectFamily);
		
		engine.addEntityListener(objectFamily, new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				// noop
			}
			
			@Override
			public void entityAdded(Entity entity) {
				applyObject(entity);
			}
		});
	}
	
	@Override
	public void update(float deltaTime) 
	{
		// convert from Km/h to m/s
		float windSpeedMS = weatherSystem.windSpeed / 3.6f;
		windFlow.set(1, 0).setAngle(weatherSystem.windAngle).scl(windSpeedMS);

		for(Entity entity : objectEntities){
			updateObject(entity, deltaTime);
		}
	}

	private void updateObject(Entity entity, float deltaTime) 
	{
		BulletComponent bullet = BulletComponent.components.get(entity);
		ObjectMeshComponent object = ObjectMeshComponent.components.get(entity);
		OpenWorldElement element = object.userObject.element;
		
		// apply forces
		// TODO based on density and volume, apply forces
		if(bullet.object instanceof btRigidBody){
			
			btRigidBody body = (btRigidBody)bullet.object;
			
			Vector3 position = object.userObject.element.position;
			float waterDelta = envSystem.waterLevel - position.y;
			
			boolean changed = false;
			
			// under water
			if(waterDelta > 0){
				generatorSystem.getWaterCurrent(waterFlow, position.x, position.z);
				
				float densityRate = element.density / WATER_DENSITY;
				if(densityRate < 1){
					body.setDamping(0.8f, .8f);
					// TODO scaling factor and clamp
					float speed = MathUtils.clamp(
							.5f / densityRate,
							0, 2);
					
					body.setLinearVelocity(new Vector3(waterFlow.x, (float)Math.sqrt(waterDelta) * speed, waterFlow.y));
					// body.setDamping(-0.99f, .8f);
				}
				else{
					// simulate water viscosity
					// TODO may not be always 1
					body.setDamping(1f - densityRate * 1e-8f, .8f);
				}
				
				
				// TODO needs current object angle maybe based on local intertia.
				body.setAngularVelocity(new Vector3(1,1,1).scl(.1f));
				
				changed = true;
			}
			// above water
			else{
				// Case of flying object (density below air density)
				float densityRate = element.density / AIR_DENSITY;
				if(densityRate < 1){
					float speed = MathUtils.clamp(
							.5f / densityRate,
							0, 2);
					body.setLinearVelocity(new Vector3(0, speed, 0));
				}
				// case of normal object on ground just restore damping
				else{
					body.setDamping(object.userObject.element.damping, 0);
				}
			}
			
			if(weatherSystem.windSpeed > 0){
				
				// XXX take water flow but should take another noise layer.
				Vector2 windTurbulences = generatorSystem.getWaterCurrent(waterFlow, position.x, position.z);
				
				float extraWinfForce = windFlow.len() * 0.5f;
				
				body.applyCentralForce(new Vector3(windFlow.x + windTurbulences.x * extraWinfForce, extraWinfForce * 4, windFlow.y + windTurbulences.y * extraWinfForce));
				
				changed = true;
			}
			
			if(changed) body.activate();
		}
	}
	
	
}
