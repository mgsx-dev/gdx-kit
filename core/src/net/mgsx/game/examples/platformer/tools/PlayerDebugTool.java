package net.mgsx.game.examples.platformer.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.RectangleTool;
import net.mgsx.game.examples.platformer.components.KeyboardController;
import net.mgsx.game.examples.platformer.components.PlayerController;
import net.mgsx.game.examples.platformer.components.WalkingComponent;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DFixtureModel;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldSystem;

public class PlayerDebugTool extends RectangleTool
{
	public PlayerDebugTool(EditorScreen editor) {
		super("Create Player Debug", editor);
	}
	
	@Override
	protected void create(Vector2 startPoint, Vector2 endPoint) 
	{
		Entity entity = getEngine().createEntity();
		
		Box2DWorldSystem worldSystem = getEngine().getSystem(Box2DWorldSystem.class);
		
		Vector2 midPoint = startPoint.cpy().add(endPoint).scl(.5f);
		
		Box2DBodyModel physics = worldSystem.create(entity);
		physics.def.type = BodyType.DynamicBody;
		physics.def.fixedRotation = true;
		physics.def.position.set(midPoint);
		Body body = worldSystem.createBody(physics);
		body.setActive(true); // XXX
		
		CircleShape circle = new CircleShape();
		circle.setRadius(Math.min(Math.abs(midPoint.x - endPoint.x), Math.abs(midPoint.y - startPoint.y)));
		
		Box2DFixtureModel fix = worldSystem.createFixture(physics);
		fix.def.shape = circle;
		
		Fixture fixture = worldSystem.addFixture(physics, fix);
		fixture.setSensor(false); // XXX
		
		entity.add(physics);
		
		entity.add(getEngine().createComponent(PlayerController.class));
		entity.add(getEngine().createComponent(KeyboardController.class));
		entity.add(getEngine().createComponent(WalkingComponent.class));
		
		getEngine().addEntity(entity);
		
		editor.setSelection(entity);
	}

}
