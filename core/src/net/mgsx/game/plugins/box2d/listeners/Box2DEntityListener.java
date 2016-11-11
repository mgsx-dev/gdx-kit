package net.mgsx.game.plugins.box2d.listeners;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * Listener on entity, does nothing when contact with other fixture
 * is not bound to an entity.
 * @author mgsx
 */
public abstract class Box2DEntityListener implements Box2DListener
{
	@Override
	public void beginContact(Contact contact, Fixture self, Fixture other) {
		Object otherData = other.getBody().getUserData();
		if(otherData instanceof Entity)
		{
			beginContact(contact, self, other, (Entity)otherData);
		}
	}

	protected abstract void beginContact(Contact contact, Fixture self, Fixture other, Entity otherEntity);
	
	@Override
	public void endContact(Contact contact, Fixture self, Fixture other) {
		Object otherData = other.getBody().getUserData();
		if(otherData instanceof Entity)
		{
			endContact(contact, self, other, (Entity)otherData);
		}
	}

	protected abstract void endContact(Contact contact, Fixture self, Fixture other, Entity otherEntity);
}
