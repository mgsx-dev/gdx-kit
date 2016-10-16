package net.mgsx.plugins.sprite;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.mgsx.core.Editor;
import net.mgsx.core.plugins.SelectorPlugin;

public class SpriteSelector extends SelectorPlugin
{
	private Editor editor;
	public SpriteSelector(Editor editor) {
		super(editor);
		this.editor = editor;
	}

	@Override
	public int getSelection(Array<Entity> entities, float screenX, float screenY) {
		Vector2 worldPoint = editor.unproject(screenX, screenY);
		int count = 0;
		for(Entity entity : editor.entityEngine.getEntitiesFor(Family.one(SpriteModel.class).get())){
			SpriteModel sprite = entity.getComponent(SpriteModel.class);
			if(sprite.sprite.getBoundingRectangle().contains(worldPoint)){
				entities.add(entity);
				count++;
			}
		}
		return count;
	}
}
