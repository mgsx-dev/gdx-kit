package net.mgsx.game.plugins.box2d.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.Duplicable;
import net.mgsx.game.plugins.box2d.listeners.Box2DListener;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldContext;

@Storable("box2d")
public class Box2DBodyModel implements Component, Duplicable, Poolable, Disposable
{
	
	public static ComponentMapper<Box2DBodyModel> components = ComponentMapper.getFor(Box2DBodyModel.class);
	
	public String id; // TODO confusion with id/name in persistence model and ui model
	public BodyDef def;
	public Body body;
	public Array<Box2DFixtureModel> fixtures = new Array<Box2DFixtureModel>();
	public Entity entity;
	public Box2DWorldContext context;

	public Rectangle bounds = new Rectangle();
	
	public Box2DBodyModel(){}
	public Box2DBodyModel(Box2DWorldContext context, Entity entity, String id, BodyDef def, Body body) {
		super();
		this.id = id;
		this.def = def;
		this.body = body;
		this.context = context;
		if(body != null) body.setUserData(entity);
	}

	@Override
	public String toString() {
		return id;
	}
	
	@Override
	public Component duplicate(Engine engine) {
		Box2DBodyModel model = engine.createComponent(Box2DBodyModel.class);
		model.id = id; // TODO ? + " (clone)";
		model.def = def;
		model.context = context;
		model.fixtures = new Array<Box2DFixtureModel>();
		for(Box2DFixtureModel fixture : fixtures){
			Box2DFixtureModel newFixture = new Box2DFixtureModel();
			newFixture.def = fixture.def;
			newFixture.id = fixture.id;
			model.fixtures.add(newFixture);
		}
		return model;
	}
	
	@Override
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
	
	@Override
	public void reset() 
	{
		// TODO other fields ?
		dispose();
		
	}
	public void setListener(Box2DListener listener) {
		for(Box2DFixtureModel fix : fixtures){
			fix.fixture.setUserData(listener);
		}
	}


}