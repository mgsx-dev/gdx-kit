package net.mgsx.box2d.editor;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import net.mgsx.box2d.editor.persistence.Repository;

public class FixtureItem {
	public String id;
	public FixtureDef def;
	public Fixture fixture;
	
	public FixtureItem(String id, FixtureDef def, Fixture fixture) {
		this.id = id;
		this.def = def;
		this.fixture = fixture;
		fixture.setUserData(this);
	}

	public void recreateAt(Vector2 offset) 
	{
		Repository.importShape(Repository.exportShape(this), offset);
		
	}

	public BodyItem getBodyItem() {
		return (BodyItem)fixture.getBody().getUserData();
	}
}
