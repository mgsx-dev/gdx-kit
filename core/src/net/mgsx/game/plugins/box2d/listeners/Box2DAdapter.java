package net.mgsx.game.plugins.box2d.listeners;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class Box2DAdapter implements Box2DListener {

	@Override
	public void beginContact(Contact contact, Fixture self, Fixture other) {
	}

	@Override
	public void endContact(Contact contact, Fixture self, Fixture other) {
	}

	@Override
	public void preSolve(Contact contact, Fixture self, Fixture other, Manifold oldManifold) {
	}

	@Override
	public void postSolve(Contact contact, Fixture self, Fixture other, ContactImpulse impulse) {
	}

}
