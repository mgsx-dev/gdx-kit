package net.mgsx.game.examples.platformer.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import net.mgsx.game.core.helpers.FamilyHelper;
import net.mgsx.game.examples.platformer.components.LiquidComponent;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DFixtureModel;
import net.mgsx.game.plugins.box2d.listeners.Box2DAdapter;
import net.mgsx.game.plugins.box2d.listeners.Box2DListener;
import net.mgsx.game.plugins.box2d.systems.AbstractBox2DSystem;

public class LiquidSystem extends AbstractBox2DSystem
{
	public LiquidSystem() {
		super(FamilyHelper.all(LiquidComponent.class));
	}

	@Override
	protected void registerListener(Entity entity, Box2DBodyModel physics) {
		final LiquidComponent liquid = LiquidComponent.components.get(entity);
		Box2DListener listener = new Box2DAdapter() {
			@Override
			public void preSolve(Contact contact, Fixture self, Fixture other, Manifold oldManifold) {
				contact.setEnabled(false);
				
				// it depends on body density and liquid density
				// we limit a bit to avoid bouncing
				float densityFactor = Math.min(10, Math.max(0, (self.getDensity() + liquid.density) - other.getDensity()));
				
				// if other is far from surface, pressure raise and force raise as well
				
				// speed is a factor as well ..
				float speedFactor = -other.getBody().getLinearVelocity().y;
				
				float delta = -(other.getBody().getPosition().y - self.getBody().getPosition().y);
				float pressureFactor = Math.min(delta+0.8f, 1);
				float force = MathUtils.lerp(0, 11, pressureFactor);
				float limit = delta < 0  ? pressureFactor * pressureFactor: 1;
				other.getBody().applyForceToCenter(0, limit * densityFactor * other.getBody().getMass() * (force + speedFactor) * pressureFactor, true);
			}
		};
		// register listener on all no sensor fixtures
		for(Box2DFixtureModel fix : physics.fixtures)
			fix.fixture.setUserData(listener);
	}

}
