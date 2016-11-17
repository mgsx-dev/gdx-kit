package net.mgsx.game.plugins.box2d.listeners;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Listener on entity component, does nothing when contact with other fixture
 * is not bound to an entity with the given component.
 * @author mgsx
 */
public class Box2DComponentListener<T extends Component> extends Box2DEntityListener
{
	private Class<T> type;
	
	public Box2DComponentListener(Class<T> type) {
		super();
		this.type = type;
	}

	@Override
	protected void beginContact(Contact contact, Fixture self, Fixture other, Entity otherEntity) {
		T component = otherEntity.getComponent(type);
		if(component != null){
			beginContact(contact, self, other, otherEntity, component);
		}
	}

	protected void beginContact(Contact contact, Fixture self, Fixture other, Entity otherEntity, T otherComponent){}

	@Override
	protected void endContact(Contact contact, Fixture self, Fixture other, Entity otherEntity) {
		T component = otherEntity.getComponent(type);
		if(component != null){
			endContact(contact, self, other, otherEntity, component);
		}
	}

	protected void endContact(Contact contact, Fixture self, Fixture other, Entity otherEntity, T otherComponent){}

	
	@Override
	protected void preSolve(Contact contact, Fixture self, Fixture other, Entity otherEntity, Manifold oldManifold) {
		T component = otherEntity.getComponent(type);
		if(component != null){
			preSolve(contact, self, other, otherEntity, component, oldManifold);
		}
	}

	protected void preSolve(Contact contact, Fixture self, Fixture other, Entity otherEntity, T otherComponent, Manifold oldManifold){}

	@Override
	protected void postSolve(Contact contact, Fixture self, Fixture other, Entity otherEntity, ContactImpulse impulse) {
		T component = otherEntity.getComponent(type);
		if(component != null){
			postSolve(contact, self, other, otherEntity, component, impulse);
		}
	}

	protected void postSolve(Contact contact, Fixture self, Fixture other, Entity otherEntity, T otherComponent, ContactImpulse impulse){}

}
