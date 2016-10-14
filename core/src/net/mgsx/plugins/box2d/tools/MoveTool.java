package net.mgsx.plugins.box2d.tools;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import net.mgsx.core.tools.Tool;
import net.mgsx.plugins.box2d.model.BodyItem;
import net.mgsx.plugins.box2d.model.SpriteItem;
import net.mgsx.plugins.box2d.model.WorldItem;

public class MoveTool extends Tool
{
	private WorldItem worldItem;
	private BodyItem bodyItem;
	private Vector2 prev;
	private boolean moving = false;
	
	public MoveTool(Camera camera, WorldItem worldItem) {
		super("Move", camera);
		this.worldItem = worldItem;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer,
			int button) {
		if(moving){
			moving = false;
			
			if(bodyItem != null){
				for(BodyItem bodyItem : worldItem.selection.bodies){
					bodyItem.body.setGravityScale(1);
					bodyItem.body.applyForceToCenter(0, 0, true);
				}
				bodyItem = null;
			}
			
		}
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(moving){
			Vector2 worldPos = unproject(screenX, screenY);
			Vector2 delta = new Vector2(worldPos).sub(prev);
			for(BodyItem bodyItem : worldItem.selection.bodies){
				bodyItem.body.setAngularVelocity(0);
				bodyItem.body.setGravityScale(0);
				bodyItem.body.setLinearVelocity(0, 0);
				bodyItem.body.setTransform(
						new Vector2(bodyItem.body.getPosition()).add(worldPos).sub(prev), 
						bodyItem.body.getAngle());
				bodyItem.body.applyForceToCenter(0, 0, true); // wakeup to allow collisions !
			}
			for(SpriteItem spriteItem : worldItem.selection.sprites){
				spriteItem.sprite.setPosition(spriteItem.sprite.getX() + delta.x, spriteItem.sprite.getY() + delta.y);
			}
			
			
			prev = worldPos;
			return true; // catch event
		}
		return super.touchDragged(screenX, screenY, pointer);
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Vector2 worldPoint = unproject(screenX, screenY);
		Body body = worldItem.queryFirstBody(worldPoint);
		prev = unproject(screenX, screenY);
		if(body != null)
		{
			moving = true;
			bodyItem = (BodyItem)body.getUserData();
			for(BodyItem bodyItem : worldItem.selection.bodies){
				bodyItem.body.setGravityScale(0);
			}
			if(worldItem.selection.bodies.contains(bodyItem, true)){
				return true; // catch event if target in selection (prevent selection tool to operate)
			}
		}
		SpriteItem currentSprite = null;
		for(SpriteItem spriteItem : worldItem.items.sprites){
			if(spriteItem.sprite.getBoundingRectangle().contains(worldPoint)){
				currentSprite = spriteItem;
			}
		}
		if(currentSprite != null)
		{
			moving = true;
			if(worldItem.selection.sprites.contains(currentSprite, true)){
				return true; // catch event if target in selection (prevent selection tool to operate)
			}
		}
		return false;
	}
}
