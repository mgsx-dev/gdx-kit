package net.mgsx.box2d.editor.tools;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import net.mgsx.box2d.editor.model.BodyItem;
import net.mgsx.box2d.editor.model.WorldItem;
import net.mgsx.fwk.editor.tools.Tool;

public class MoveTool extends Tool
{
	private WorldItem worldItem;
	private BodyItem bodyItem;
	private Vector2 prev;
	
	public MoveTool(Camera camera, WorldItem worldItem) {
		super("Move", camera);
		this.worldItem = worldItem;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer,
			int button) {
		if(bodyItem != null){
			for(BodyItem bodyItem : worldItem.selection.bodies){
				bodyItem.body.setGravityScale(1);
				bodyItem.body.applyForceToCenter(0, 0, true);
			}
			bodyItem = null;
		}
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(bodyItem != null){
			Vector2 worldPos = unproject(screenX, screenY);
			for(BodyItem bodyItem : worldItem.selection.bodies){
				bodyItem.body.setAngularVelocity(0);
				bodyItem.body.setGravityScale(0);
				bodyItem.body.setLinearVelocity(0, 0);
				bodyItem.body.setTransform(
						new Vector2(bodyItem.body.getPosition()).add(worldPos).sub(prev), 
						bodyItem.body.getAngle());
				bodyItem.body.applyForceToCenter(0, 0, true); // wakeup to allow collisions !
			}
			prev = worldPos;
			return true; // catch event
		}
		return super.touchDragged(screenX, screenY, pointer);
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Body body = worldItem.queryFirstBody(unproject(screenX, screenY));
		if(body != null)
		{
			bodyItem = (BodyItem)body.getUserData();
			for(BodyItem bodyItem : worldItem.selection.bodies){
				bodyItem.body.setGravityScale(0);
			}
			prev = unproject(screenX, screenY);
			if(worldItem.selection.bodies.contains(bodyItem, true)){
				return true; // catch event if target in selection (prevent selection tool to operate)
			}
		}
		return false;
	}
}
