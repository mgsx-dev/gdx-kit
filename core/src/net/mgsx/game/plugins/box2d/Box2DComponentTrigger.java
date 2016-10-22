package net.mgsx.game.plugins.box2d;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * Listener on entity component, does nothing when contact with other fixture
 * is not bound to an entity with the given component.
 * 
 * callback method is call on first begin contact and on last end contact.
 * 
 * @author mgsx
 */

public abstract class Box2DComponentTrigger<T extends Component> extends Box2DComponentListener<T> 
{
	private int contactCounter = 0; // TODO is it always true ?
	
	public Box2DComponentTrigger(Class<T> type) {
		super(type);
	}
	
	@Override
	protected void beginContact(Contact contact, Fixture self, Fixture other, Entity otherEntity, T otherComponent) {
		if(contactCounter == 0) enter(contact, self, other, otherEntity, otherComponent, true);
		contactCounter++;
	}

	protected abstract void enter(Contact contact, Fixture self, Fixture other, Entity otherEntity, T otherComponent, boolean b);

	@Override
	protected void endContact(Contact contact, Fixture self, Fixture other, Entity otherEntity, T otherComponent) {
		contactCounter--;
		if(contactCounter == 0) exit(contact, self, other, otherEntity, otherComponent, true);
	}

	protected abstract void exit(Contact contact, Fixture self, Fixture other, Entity otherEntity, T otherComponent, boolean b);


}
