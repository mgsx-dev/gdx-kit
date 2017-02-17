package net.mgsx.game.plugins.pd.tasks;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DFixtureModel;
import net.mgsx.game.plugins.box2d.listeners.Box2DListener;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.pd.Pd;

@TaskAlias("pdContact")
public class PdPhysicContacts extends EntityLeafTask implements Box2DListener
{
	@TaskAttribute
	public String symbol;
	
	private int contacts;
	
	@Override
	public void start() {
		Box2DBodyModel physics = Box2DBodyModel.components.get(getEntity());
		if(physics != null){
			for(Box2DFixtureModel fix : physics.fixtures){
				fix.fixture.setUserData(this);
			}
		}
	}
	
	@Override
	public void end() {
		Box2DBodyModel physics = Box2DBodyModel.components.get(getEntity());
		if(physics != null){
			for(Box2DFixtureModel fix : physics.fixtures){
				fix.fixture.setUserData(null);
			}
		}
	}
	
	@Override
	public Status execute() 
	{
		if(contacts > 0){
			Pd.audio.sendBang(symbol);
			contacts = 0;
		}
		return Status.SUCCEEDED;
	}

	@Override
	public void beginContact(Contact contact, Fixture self, Fixture other) {
		if(!other.isSensor()){
			contacts++;
		}
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