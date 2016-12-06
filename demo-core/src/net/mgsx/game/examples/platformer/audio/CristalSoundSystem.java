package net.mgsx.game.examples.platformer.audio;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.mgsx.game.core.helpers.FamilyBuilder;
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
	private float weight;
	private int count;
	
	public CristalSoundSystem() {
		super(new FamilyBuilder().all(CristalComponent.class));
	}
	
	@Override
	public void update(float deltaTime) 
	{
		if(count > 0){
			float ave = sum / count;
			Pd.audio.sendList(pdDrop, ave, weight/count);
		}
		count = 0;
		sum = 0;
		weight= 0;
	}
	

	@Override
	protected void registerListener(Entity entity, Box2DBodyModel physics) {
		Box2DListener listener = new Box2DAdapter() {
			@Override
			public void beginContact(Contact contact, Fixture self, Fixture other) {
				weight += other.getBody().getMass();
				sum += other.getBody().getLinearVelocity().len();
				count++;
			}
		};
		// register listener on all no sensor fixtures
		for(Box2DFixtureModel fix : physics.fixtures)
			fix.fixture.setUserData(listener);
	}

}
