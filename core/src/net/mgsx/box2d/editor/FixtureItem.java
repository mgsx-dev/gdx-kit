package net.mgsx.box2d.editor;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class FixtureItem {
	public String id;
	public FixtureDef def;
	public Fixture fixture;
	
	public FixtureItem(String id, FixtureDef def, Fixture fixture) {
		this.id = id;
		this.def = def;
		this.fixture = fixture;
	}
}
