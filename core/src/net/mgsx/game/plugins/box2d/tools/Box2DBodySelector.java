package net.mgsx.game.plugins.box2d.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.plugins.SelectorPlugin;
import net.mgsx.game.plugins.box2d.components.WorldItem;

public class Box2DBodySelector extends SelectorPlugin
{
	private WorldItem worldItem;
	
	public Box2DBodySelector(Editor editor, WorldItem worldItem) {
		super(editor);
		this.worldItem = worldItem;
	}
	
	@Override
	public int getSelection(Array<Entity> entities, float screenX, float screenY) 
	{
		Vector2 worldPoint = editor.unproject(screenX, screenY);
		Body body = worldItem.queryFirstBody(worldPoint); // TODO many ?, do all box2d stuff here ?
		if(body != null)
		{
			Entity entity = (Entity)body.getUserData();
			entities.add(entity);
			return 1;
		}
		return 0;
	}
	
}
