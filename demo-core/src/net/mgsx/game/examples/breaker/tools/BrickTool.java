package net.mgsx.game.examples.breaker.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.tools.RectangleTool;
import net.mgsx.game.examples.breaker.components.BrickComponent;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DFixtureModel;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldSystem;

public class BrickTool extends RectangleTool
{
	@Inject Box2DWorldSystem world;

	@Editable public boolean solid = false;
	
	@Editable public int life = 99;
	
	public BrickTool() {
		super();
	}

	@Override
	protected void create(Vector2 startPoint, Vector2 endPoint) 
	{
		float x = (startPoint.x + endPoint.x) / 2;
		float y = (startPoint.y + endPoint.y) / 2;
		float w = Math.abs(startPoint.x - endPoint.x);
		float h = Math.abs(startPoint.y - endPoint.y);

		selection().clear();
		
		// Entity entity = transcientEntity();
		
		world.getWorldContext().settings.bodyDef.type = BodyType.StaticBody;
		
		Box2DBodyModel bodyItem = world.getWorldContext().currentBody("Rectangle", x, y);
		
		Entity entity = bodyItem.entity;
		
		PolygonShape pshape = new PolygonShape();
		pshape.setAsBox(w/2, h/2, new Vector2(x, y).sub(bodyItem.body.getPosition()), 0); 
		
		FixtureDef fix = world.getWorldContext().settings.fixture();
		fix.shape = pshape;
		
		Fixture fixture = bodyItem.body.createFixture(fix);
		fixture.setDensity(1);
		fixture.setFriction(1);
		fixture.setRestitution(0.5f);
		Box2DFixtureModel fixtureItem = new Box2DFixtureModel("Polygon", fix, fixture);
		bodyItem.fixtures.add(fixtureItem);
		
		BrickComponent brick = getEngine().createComponent(BrickComponent.class);
		brick.life = life;
		brick.solid = solid;
		
		entity.add(brick);
	}
	

}
