package net.mgsx.game.plugins.g2d.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.SelectorPlugin;
import net.mgsx.game.plugins.g2d.components.SpriteModel;

public class SpriteSelector extends SelectorPlugin
{
	private EditorScreen editor;
	public SpriteSelector(EditorScreen editor) {
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
