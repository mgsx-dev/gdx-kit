package net.mgsx.game.examples.platformer.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.platformer.components.SpeedWalk;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DFixtureModel;
import net.mgsx.game.plugins.box2d.listeners.Box2DListener;

// TODO special systems for box2D listener ...
public class SpeedWalkSystem extends IteratingSystem
{
	public SpeedWalkSystem() {
		super(Family.all(SpeedWalk.class, Box2DBodyModel.class).get(), GamePipeline.AFTER_PHYSICS);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		// TODO Auto-generated method stub
		super.addedToEngine(engine);
		engine.addEntityListener(Family.all(SpeedWalk.class, Box2DBodyModel.class).get(), new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
			}
			
			@Override
			public void entityAdded(final Entity entity) {
				final SpeedWalk speedWalk = SpeedWalk.components.get(entity);
				Box2DListener listener = new Box2DListener() {
					@Override
					public void endContact(Contact contact, Fixture self, Fixture other) {
					}
					
					@Override
					public void beginContact(Contact contact, Fixture self, Fixture other) {
						contact.setTangentSpeed(speedWalk.speed);
						contact.setFriction(speedWalk.friction);
					}
				};
				Box2DBodyModel physics = Box2DBodyModel.components.get(entity);
				// register listener on all no sensor fixtures
				if(physics.body != null)
				for(Box2DFixtureModel fix : physics.fixtures)
					if(!fix.fixture.isSensor())
						fix.fixture.setUserData(listener);
			}
		});
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
	}
	
}
