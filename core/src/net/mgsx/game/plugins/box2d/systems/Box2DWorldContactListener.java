package net.mgsx.game.plugins.box2d.systems;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.plugins.box2d.listeners.Box2DListener;

public class Box2DWorldContactListener implements ContactListener 
{
	private Array<Box2DListener> globalListeners;

	public Box2DWorldContactListener(Array<Box2DListener> globalListeners) {
		this.globalListeners = globalListeners;
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		Object dataA = fixtureA.getUserData();
		Object dataB = fixtureB.getUserData();
		
		if(dataA instanceof Box2DListener){
			((Box2DListener) dataA).preSolve(contact, fixtureA, fixtureB, oldManifold);
		}
		if(dataB instanceof Box2DListener){
			((Box2DListener) dataB).preSolve(contact, fixtureB, fixtureA, oldManifold);
		}
		for(Box2DListener listener : globalListeners){
			listener.preSolve(contact, fixtureA, fixtureB, oldManifold);
			listener.preSolve(contact, fixtureB, fixtureA, oldManifold);
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		Object dataA = fixtureA.getUserData();
		Object dataB = fixtureB.getUserData();
		
		if(dataA instanceof Box2DListener){
			((Box2DListener) dataA).postSolve(contact, fixtureA, fixtureB, impulse);
		}
		if(dataB instanceof Box2DListener){
			((Box2DListener) dataB).postSolve(contact, fixtureB, fixtureA, impulse);
		}
		for(Box2DListener listener : globalListeners){
			listener.postSolve(contact, fixtureA, fixtureB, impulse);
			listener.postSolve(contact, fixtureB, fixtureA, impulse);
		}
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		Object dataA = fixtureA.getUserData();
		Object dataB = fixtureB.getUserData();
		
		if(dataA instanceof Box2DListener){
			((Box2DListener) dataA).endContact(contact, fixtureA, fixtureB);
		}
		if(dataB instanceof Box2DListener){
			((Box2DListener) dataB).endContact(contact, fixtureB, fixtureA);
		}
		for(Box2DListener listener : globalListeners){
			listener.endContact(contact, fixtureA, fixtureB);
			listener.endContact(contact, fixtureB, fixtureA);
		}
	}

	@Override
	public void beginContact(Contact contact) 
	{
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		Object dataA = fixtureA.getUserData();
		Object dataB = fixtureB.getUserData();
		
		if(dataA instanceof Box2DListener){
			((Box2DListener) dataA).beginContact(contact, fixtureA, fixtureB);
		}
		if(dataB instanceof Box2DListener){
			((Box2DListener) dataB).beginContact(contact, fixtureB, fixtureA);
		}
		for(Box2DListener listener : globalListeners){
			listener.beginContact(contact, fixtureA, fixtureB);
			listener.beginContact(contact, fixtureB, fixtureA);
		}
	}
}