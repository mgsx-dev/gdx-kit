package net.mgsx.game.examples.platformer.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.mgsx.game.core.helpers.FamilyBuilder;
import net.mgsx.game.examples.platformer.components.CristalComponent;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DFixtureModel;
import net.mgsx.game.plugins.box2d.listeners.Box2DAdapter;
import net.mgsx.game.plugins.box2d.listeners.Box2DListener;
import net.mgsx.game.plugins.box2d.systems.AbstractBox2DSystem;
import net.mgsx.pd.Pd;

public class CristalSoundSystem extends AbstractBox2DSystem
{
	private static final String pdDrop = "water-drop";

	private float sum;
	private int count;
	
	public CristalSoundSystem() {
		super(new FamilyBuilder().all(CristalComponent.class));
	}
	
	@Override
	public void update(float deltaTime) 
	{
		if(count > 0){
			float ave = sum / count;
			Pd.audio.sendFloat(pdDrop, MathUtils.clamp(1.0f - ave, 0, 1));
		}
		count = 0;
		sum = 0;
	}
	

	@Override
	protected void registerListener(Entity entity, Box2DBodyModel physics) {
		Box2DListener listener = new Box2DAdapter() {
			@Override
			public void beginContact(Contact contact, Fixture self, Fixture other) {
				sum += (other.getBody().getLinearVelocity().len() * 0 + 1) * other.getBody().getMass() / 1.f;
				count++;
			}
		};
		// register listener on all no sensor fixtures
		for(Box2DFixtureModel fix : physics.fixtures)
			fix.fixture.setUserData(listener);
	}

}
