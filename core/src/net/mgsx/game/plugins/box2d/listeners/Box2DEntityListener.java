package net.mgsx.game.plugins.box2d.listeners;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

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

	@Override
	public void preSolve(Contact contact, Fixture self, Fixture other, Manifold oldManifold) {
		Object otherData = other.getBody().getUserData();
		if(otherData instanceof Entity)
		{
			preSolve(contact, self, other, (Entity)otherData, oldManifold);
		}
	}
	
	protected abstract void preSolve(Contact contact, Fixture self, Fixture other, Entity otherEntity, Manifold oldManifold);

	
	@Override
	public void postSolve(Contact contact, Fixture self, Fixture other, ContactImpulse impulse) {
		Object otherData = other.getBody().getUserData();
		if(otherData instanceof Entity)
		{
			postSolve(contact, self, other, (Entity)otherData, impulse);
		}
	}

	protected abstract void postSolve(Contact contact, Fixture self, Fixture other, Entity otherEntity, ContactImpulse impulse);
	

	
}
