package net.mgsx.game.examples.platformer.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import net.mgsx.game.core.helpers.FamilyHelper;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DFixtureModel;
import net.mgsx.game.plugins.box2d.listeners.Box2DAdapter;
import net.mgsx.game.plugins.box2d.listeners.Box2DListener;
import net.mgsx.game.plugins.box2d.systems.AbstractBox2DSystem;

public class MagnetSystem extends AbstractBox2DSystem
{
	public MagnetSystem() {
		super(FamilyHelper.all(MagnetComponent.class));
	}

	@Override
	protected void registerListener(Entity entity, Box2DBodyModel physics) {
		final MagnetComponent magnet = MagnetComponent.components.get(entity);
		final Vector2 delta = new Vector2();
		Box2DListener listener = new Box2DAdapter() {
			@Override
			public void preSolve(Contact contact, Fixture self, Fixture other, Manifold oldManifold) {
				contact.setEnabled(false);
				
				delta.set(self.getBody().getPosition()).sub(other.getBody().getPosition());
				float dst2 = delta.len2();
				float forceFactor = dst2 <= 0 ? 1 : Math.min(1, 1.f / dst2);
				
				float correction = dst2 < 1 ? 0.5f : 1;
				delta.scl(forceFactor * magnet.force * correction);
				other.getBody().setLinearVelocity(delta); // Hard magnet method (work well)
				//other.getBody().applyForceToCenter(delta, true);
			}
		};
		// register listener on all no sensor fixtures
		for(Box2DFixtureModel fix : physics.fixtures)
			fix.fixture.setUserData(listener);
	}

}
