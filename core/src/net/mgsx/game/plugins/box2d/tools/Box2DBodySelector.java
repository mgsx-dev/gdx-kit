package net.mgsx.game.plugins.box2d.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.SelectorPlugin;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldContext;

public class Box2DBodySelector extends SelectorPlugin
{
	private Box2DWorldContext worldItem;
	
	public Box2DBodySelector(EditorScreen editor, Box2DWorldContext worldItem) {
		super(editor);
		this.worldItem = worldItem;
	}
	
	@Override
	public int getSelection(Array<Entity> entities, float screenX, float screenY) 
	{
		Vector2 worldPoint = editor.unproject(screenX, screenY);
		Body body = worldItem.provider.queryFirstBody(worldPoint); // TODO many ?, do all box2d stuff here ?
		if(body != null)
		{
			Entity entity = (Entity)body.getUserData();
			entities.add(entity);
			return 1;
		}
		return 0;
	}
	
}
