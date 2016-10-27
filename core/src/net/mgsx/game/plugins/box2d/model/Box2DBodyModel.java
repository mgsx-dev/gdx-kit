package net.mgsx.game.plugins.box2d.model;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.components.Duplicable;
import net.mgsx.game.core.components.Movable;
import net.mgsx.game.plugins.box2dold.behavior.BodyBehavior;
import net.mgsx.game.plugins.box2dold.model.WorldItem;

public class Box2DBodyModel implements Component, Duplicable
{
	public String id; // TODO confusion with id/name in persistence model and ui model
	public BodyDef def;
	public Body body;
	public Array<Box2DFixtureModel> fixtures;
	public BodyBehavior behavior;
	public Entity entity;
	public Movable slave;
	public boolean slaveEnabled;
	public WorldItem context;
	
	public Box2DBodyModel(){}
	public Box2DBodyModel(WorldItem context, Entity entity, String id, BodyDef def, Body body) {
		super();
		this.id = id;
		this.def = def;
		this.body = body;
		this.context = context;
		this.fixtures = new Array<Box2DFixtureModel>();
		if(body != null) body.setUserData(entity);
	}

	@Override
	public String toString() {
		return id;
	}
	
	@Override
	public Component duplicate() {
		Box2DBodyModel model = new Box2DBodyModel();
		model.id = id; // TODO ? + " (clone)";
		model.def = def;
		model.context = context;
		model.fixtures = new Array<Box2DFixtureModel>();
		model.body = body.getWorld().createBody(def);
		model.body.setTransform(body.getPosition(), body.getAngle());
		for(Box2DFixtureModel fixture : fixtures){
			Box2DFixtureModel newFixture = new Box2DFixtureModel();
			newFixture.def = fixture.def;
			newFixture.id = fixture.id;
			newFixture.fixture = model.body.createFixture(newFixture.def);
			model.fixtures.add(newFixture);
		}
		return model;
	}
	public void dispose() 
	{
		if(body != null)
		{
			// delete joints before.
			for(JointEdge jointEdge : body.getJointList()){
				//body.getWorld().isLocked()
				
				Object data = jointEdge.joint.getUserData();
				if(data instanceof Entity){
					Entity jointEntity = (Entity)data;
					jointEntity.remove(Box2DJointModel.class);
				}
				
			}
			
			
			body.getWorld().destroyBody(body);
			body = null;
			fixtures.clear();
		}
		
	}


}