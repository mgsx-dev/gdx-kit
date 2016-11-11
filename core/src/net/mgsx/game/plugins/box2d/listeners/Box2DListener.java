package net.mgsx.game.plugins.box2d.listeners;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

public interface Box2DListener {

	public void beginContact(Contact contact, Fixture self, Fixture other);	
	public void endContact(Contact contact, Fixture self, Fixture other);	
}
