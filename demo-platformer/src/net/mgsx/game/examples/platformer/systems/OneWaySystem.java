package net.mgsx.game.examples.platformer.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.Manifold.ManifoldPoint;

import net.mgsx.game.core.helpers.FamilyHelper;
import net.mgsx.game.examples.platformer.components.OneWay;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DFixtureModel;
import net.mgsx.game.plugins.box2d.listeners.Box2DAdapter;
import net.mgsx.game.plugins.box2d.listeners.Box2DListener;
import net.mgsx.game.plugins.box2d.systems.AbstractBox2DSystem;

public class OneWaySystem extends AbstractBox2DSystem
{
	public OneWaySystem() {
		super(FamilyHelper.all(OneWay.class));
	}
	
	@Override
	protected void registerListener(Entity entity, Box2DBodyModel physics) {
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
		// register listener on all no sensor fixtures
		if(physics.body != null)
		for(Box2DFixtureModel fix : physics.fixtures)
			if(!fix.fixture.isSensor())
				fix.fixture.setUserData(listener);
	}
	
}
