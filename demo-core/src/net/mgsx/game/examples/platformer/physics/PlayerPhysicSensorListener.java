package net.mgsx.game.examples.platformer.physics;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.platformer.inputs.PlayerController;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.listeners.Box2DComponentTrigger;

public class PlayerPhysicSensorListener extends EntitySystem implements EntityListener
{
	public PlayerPhysicSensorListener() {
		super(GamePipeline.AFTER_PHYSICS);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(Family.all(Box2DBodyModel.class, PlayerPhysicSensor.class).get(), this);
	}
	
	@Override
	public void removedFromEngine(Engine engine) {
		engine.removeEntityListener(this);
		super.removedFromEngine(engine);
	}

	@Override
	public void entityAdded(final Entity entity) {
		Box2DBodyModel physics = Box2DBodyModel.components.get(entity);
		physics.setListener(new Box2DComponentTrigger<PlayerController>(PlayerController.class) {

			@Override
			protected void enter(Contact contact, Fixture self, Fixture other, Entity otherEntity,
					PlayerController otherComponent, boolean b) {
				PlayerPhysicSensor sensor = PlayerPhysicSensor.components.get(entity);
				if(sensor != null){
					sensor.inside = true;
				}
			}

			@Override
			protected void exit(Contact contact, Fixture self, Fixture other, Entity otherEntity,
					PlayerController otherComponent, boolean b) {
				PlayerPhysicSensor sensor = PlayerPhysicSensor.components.get(entity);
				if(sensor != null){
					sensor.inside = false;
				}
			}
		});
		
	}

	@Override
	public void entityRemoved(Entity entity) {
		Box2DBodyModel physics = Box2DBodyModel.components.get(entity);
		if(physics != null) physics.setListener(null);
	}
	
}
