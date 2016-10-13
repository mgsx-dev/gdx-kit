package net.mgsx.box2d.editor.tools;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

import net.mgsx.box2d.editor.model.BodyItem;
import net.mgsx.box2d.editor.model.SpriteItem;
import net.mgsx.box2d.editor.model.WorldItem;
import net.mgsx.fwk.editor.tools.Tool;

public class SelectTool extends Tool 
{
	private WorldItem worldItem;
	
	public SelectTool(Camera camera, WorldItem worldItem) {
		super("Select", camera);
		this.worldItem = worldItem;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Vector2 worldPoint = unproject(screenX, screenY);
		Body body = worldItem.queryFirstBody(worldPoint);
		if(body != null)
		{
			BodyItem bodyItem = (BodyItem)body.getUserData();
			handleSelection(bodyItem, worldItem.selection.bodies);
			return true;
		}
		SpriteItem currentSprite = null;
		for(SpriteItem spriteItem : worldItem.items.sprites){
			if(spriteItem.sprite.getBoundingRectangle().contains(worldPoint)){
				currentSprite = spriteItem;
			}
		}
		if(currentSprite != null)
		{
			handleSelection(currentSprite, worldItem.selection.sprites);
			return true;
		}
			
			
		worldItem.selection.clear();
		
		return super.touchDown(screenX, screenY, pointer, button);
	}
	
	private <T> void handleSelection(T itemSelected, Array<T> selection)
	{
		if(ctrl()){
			if(selection.contains(itemSelected, true)){
				selection.removeValue(itemSelected, true);
			}else{
				selection.add(itemSelected);
			}
		}else if(shift()){
			if(!selection.contains(itemSelected, true)){
				selection.add(itemSelected);
			}
		}else{
			selection.clear();
			selection.add(itemSelected);
		}
	}

}
