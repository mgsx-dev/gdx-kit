package net.mgsx.plugins.sprite;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.core.Editor;
import net.mgsx.core.tools.SelectToolBase;

public class SelectSpriteTool extends SelectToolBase
{
	private Editor editor;
	public SelectSpriteTool(Editor editor) {
		super("", editor.orthographicCamera);
		this.editor = editor;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) 
	{
		Vector2 worldPoint = unproject(screenX, screenY);
		Entity found = null;
		for(Entity entity : editor.entityEngine.getEntitiesFor(Family.one(SpriteModel.class).get())){
			SpriteModel sprite = entity.getComponent(SpriteModel.class);
			if(sprite.sprite.getBoundingRectangle().contains(worldPoint)){
				found = entity;
			}
		}
		if(found != null)
		{
			handleSelection(found, editor.selection);
			return true;
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}
}
