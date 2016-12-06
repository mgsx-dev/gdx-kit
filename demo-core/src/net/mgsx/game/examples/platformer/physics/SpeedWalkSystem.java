package net.mgsx.game.examples.platformer.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.mgsx.game.core.helpers.FamilyHelper;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DFixtureModel;
import net.mgsx.game.plugins.box2d.listeners.Box2DAdapter;
import net.mgsx.game.plugins.box2d.listeners.Box2DListener;
import net.mgsx.game.plugins.box2d.systems.AbstractBox2DSystem;

public class SpeedWalkSystem extends AbstractBox2DSystem
{
	public SpeedWalkSystem() {
		super(FamilyHelper.all(SpeedWalk.class));
	}

	@Override
	protected void registerListener(Entity entity, Box2DBodyModel physics) {
		final SpeedWalk speedWalk = SpeedWalk.components.get(entity);
		Box2DListener listener = new Box2DAdapter() {
			@Override
			public void endContact(Contact contact, Fixture self, Fixture other) {
			}
			
			@Override
			public void beginContact(Contact contact, Fixture self, Fixture other) {
				contact.setTangentSpeed(speedWalk.speed);
				contact.setFriction(speedWalk.friction);
			}
		};
		// register listener on all no sensor fixtures
		if(physics.body != null)
		for(Box2DFixtureModel fix : physics.fixtures)
			if(!fix.fixture.isSensor())
				fix.fixture.setUserData(listener);
	}

	
}
