package net.mgsx.game.plugins.box2d;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;

public class Box2DMultiplexer implements Box2DListener 
{
	private Array<Box2DListener> listeners = new Array<Box2DListener>();
	
	public Box2DMultiplexer(Box2DListener ... listeners) {
		this.listeners.addAll(listeners);
	}
	
	@Override
	public void beginContact(Contact contact, Fixture self, Fixture other) {
		for(Box2DListener listener : listeners){
			listener.beginContact(contact, self, other);
		}
	}

	@Override
	public void endContact(Contact contact, Fixture self, Fixture other) {
		for(Box2DListener listener : listeners){
			listener.endContact(contact, self, other);
		}
	}

}
