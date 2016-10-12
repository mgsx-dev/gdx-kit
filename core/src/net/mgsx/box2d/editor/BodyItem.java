package net.mgsx.box2d.editor;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;

import net.mgsx.box2d.editor.behavior.BodyBehavior;

public class BodyItem
{
	public String id;
	public BodyDef def;
	public Body body;
	public Array<FixtureItem> fixtures;
	public SpriteItem sprite;
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