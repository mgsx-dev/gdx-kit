package net.mgsx.plugins.box2d.tools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import net.mgsx.core.Editor;
import net.mgsx.core.tools.SelectToolBase;
import net.mgsx.plugins.box2d.model.BodyItem;
import net.mgsx.plugins.box2d.model.SpriteItem;
import net.mgsx.plugins.box2d.model.WorldItem;

public class SelectTool extends SelectToolBase 
{
	private WorldItem worldItem;
	
	public SelectTool(Editor editor, WorldItem worldItem) {
		super(editor);
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
	
	

}
