package net.mgsx.game.examples.platformer.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.Manifold.ManifoldPoint;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.platformer.components.OneWay;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DFixtureModel;
import net.mgsx.game.plugins.box2d.listeners.Box2DAdapter;
import net.mgsx.game.plugins.box2d.listeners.Box2DListener;

// TODO special systems for box2D listener ...
public class OneWaySystem extends IteratingSystem
{
	public OneWaySystem() {
		super(Family.all(OneWay.class, Box2DBodyModel.class).get(), GamePipeline.AFTER_PHYSICS);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(Family.all(OneWay.class, Box2DBodyModel.class).get(), new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
			}
			
			@Override
			public void entityAdded(final Entity entity) {
				final OneWay oneWay = OneWay.components.get(entity);
				final Vector2 normal = new Vector2().setAngle(oneWay.angle); 
				Box2DListener listener = new Box2DAdapter() {
					@Override
					public void preSolve(Contact contact, Fixture self, Fixture other, Manifold oldManifold) {
						for(ManifoldPoint p : oldManifold.getPoints()){
							//if(other.getBody().getLinearVelocityFromLocalPoint(p.localPoint).y<0) return;
							
							//if(other.getBody().getWorldVector(oldManifold.getLocalNormal()).dot(normal.set(Vector2.X).setAngle(oneWay.angle)) > 0.5f) return;
							if(other.getBody().getLinearVelocityFromLocalPoint(p.localPoint).dot(normal.set(Vector2.X).setAngle(oneWay.angle))<0) return;
						}
						contact.setEnabled(false);
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
