package net.mgsx.game.plugins.box2d.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;

import net.mgsx.game.plugins.box2d.tools.Box2DShapeFactory;

import com.badlogic.gdx.utils.JsonValue;

public class Box2DFixtureModel implements Serializable
{
	public String id;
	public FixtureDef def;
	public Fixture fixture;
	public Box2DFixtureModel(){}
	public Box2DFixtureModel(String id, FixtureDef def, Fixture fixture) {
		this.id = id;
		this.def = def;
		this.fixture = fixture;
		fixture.setUserData(this);
	}

	public void recreateAt(Vector2 offset) 
	{
		// TODO maybe another way ...
		Box2DShapeFactory.importShape(Box2DShapeFactory.exportShape(this), offset);
		
	}

	@Override
	public void write(Json json) {
		json.writeValue("def", def);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		def = json.readValue("def", FixtureDef.class, jsonData);
		// def.shape = json.readValue("shape", Shape.class, jsonData);
	}
}
