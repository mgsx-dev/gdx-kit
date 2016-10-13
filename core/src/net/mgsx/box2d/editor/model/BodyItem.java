package net.mgsx.box2d.editor.model;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;

import net.mgsx.box2d.editor.behavior.BodyBehavior;

public class BodyItem
{
	public String id; // TODO confusion with id/name in persistence model and ui model
	public BodyDef def;
	public Body body;
	public Array<FixtureItem> fixtures;
	public SpriteItem sprite; // TODO is sprite attached to body or introduce link body <=> sprite ?
	public BodyBehavior behavior;
	
	public BodyItem(String id, BodyDef def, Body body) {
		super();
		this.id = id;
		this.def = def;
		this.body = body;
		this.fixtures = new Array<FixtureItem>();
		if(body != null) body.setUserData(this);
	}

	@Override
	public String toString() {
		return id;
	}
}