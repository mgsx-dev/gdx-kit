package net.mgsx.plugins.box2dold.tools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.mgsx.core.Editor;
import net.mgsx.core.tools.Tool;
import net.mgsx.plugins.box2d.model.Box2DBodyModel;
import net.mgsx.plugins.box2d.model.Box2DFixtureModel;
import net.mgsx.plugins.box2dold.model.WorldItem;

public class MoveShapeTool extends Tool
{
	private WorldItem worldItem;
	private Box2DFixtureModel fixtureItem;
	private Body body;
	private Vector2 prev;
	private Box2DBodyModel originalBody;
	
	public MoveShapeTool(Editor editor, WorldItem worldItem) {
		super("Move", editor);
		this.worldItem = worldItem;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer,
			int button) {
		if(body != null){
			worldItem.world.destroyBody(body);
			fixtureItem.recreateAt(body.getPosition().sub(originalBody.body.getPosition()));
			fixtureItem.fixture = originalBody.body.createFixture(fixtureItem.def);
			fixtureItem.fixture.setUserData(fixtureItem);
			
			originalBody = null;
			body = null;
			fixtureItem = null;
		}
		return super.touchUp(screenX, screenY, pointer, button);
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(body != null){
			Vector2 worldPos = unproject(screenX, screenY);
			body.setTransform(
					new Vector2(body.getPosition()).add(worldPos).sub(prev), 
					body.getAngle());
			prev = worldPos;
			return true;
		}
		return super.touchDragged(screenX, screenY, pointer);
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) 
	{
		// temporariliy separte fixture
		Fixture fixture = worldItem.queryFirstFixture(unproject(screenX, screenY));
		if(fixture != null)
		{
			fixtureItem = (Box2DFixtureModel)fixture.getUserData();
			originalBody = fixtureItem.getBodyItem();
			
			// TODO option copy / move
			originalBody.body.destroyFixture(fixture);
			
			body = worldItem.world.createBody(originalBody.def);
			body.setActive(false);
			body.setTransform(originalBody.body.getPosition(), originalBody.body.getAngle());
			
			fixtureItem.recreateAt(body.getPosition().sub(originalBody.body.getPosition()));
			
			body.createFixture(fixtureItem.def);
			
			prev = unproject(screenX, screenY);
			return true;
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}
}
