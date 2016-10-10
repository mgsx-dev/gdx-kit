package net.mgsx.box2d.editor.tools;

import net.mgsx.box2d.editor.BodyItem;
import net.mgsx.box2d.editor.WorldItem;
import net.mgsx.fwk.editor.Tool;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class MoveTool extends Tool
{
	private WorldItem worldItem;
	private BodyItem bodyItem;
	
	public MoveTool(Camera camera, WorldItem worldItem) {
		super("Move", camera);
		this.worldItem = worldItem;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer,
			int button) {
		if(bodyItem != null){
			bodyItem.body.setGravityScale(1);
			bodyItem.body.applyForceToCenter(0, 0, true);
			bodyItem = null;
		}
		return super.touchUp(screenX, screenY, pointer, button);
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(bodyItem != null){
			Vector2 worldPos = unproject(screenX, screenY);
			bodyItem.body.getPosition().set(worldPos.x, worldPos.y);
			bodyItem.body.getLinearVelocity().set(0, 0);
			bodyItem.body.setAngularVelocity(0);
			bodyItem.body.setGravityScale(0);
			bodyItem.body.setLinearVelocity(0, 0);
			bodyItem.body.setTransform(new Vector2(worldPos.x, worldPos.y), bodyItem.body.getAngle());
			bodyItem.body.applyForceToCenter(0, 0, true); // wakeup to allow collisions !
			return true;
		}
		return super.touchDragged(screenX, screenY, pointer);
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Body body = worldItem.queryFirstBody(unproject(screenX, screenY), pixelSize().scl(2));
		if(body != null)
		{
			bodyItem = (BodyItem)body.getUserData();
			bodyItem.body.setGravityScale(0);
			return true;
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}
}
