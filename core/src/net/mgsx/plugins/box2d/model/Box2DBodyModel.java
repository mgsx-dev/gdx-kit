package net.mgsx.plugins.box2d.model;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;

import net.mgsx.core.storage.Storable;
import net.mgsx.plugins.box2dold.behavior.BodyBehavior;
import net.mgsx.plugins.box2dold.model.SpriteItem;

public class Box2DBodyModel implements Component, Storable
{
	public String id; // TODO confusion with id/name in persistence model and ui model
	public BodyDef def;
	public Body body;
	public Array<Box2DFixtureModel> fixtures;
	public SpriteItem sprite; // TODO is sprite attached to body or introduce link body <=> sprite ?
	public BodyBehavior behavior;
	public Entity entity;
	public Box2DBodyModel(){}
	public Box2DBodyModel(Entity entity, String id, BodyDef def, Body body) {
		super();
		this.id = id;
		this.def = def;
		this.body = body;
		this.fixtures = new Array<Box2DFixtureModel>();
		if(body != null) body.setUserData(entity);
	}

	@Override
	public String toString() {
		return id;
	}


}