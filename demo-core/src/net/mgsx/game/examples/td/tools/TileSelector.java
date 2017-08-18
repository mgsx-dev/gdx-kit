package net.mgsx.game.examples.td.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.SelectorPlugin;
import net.mgsx.game.examples.td.systems.MapSystem;

public class TileSelector extends SelectorPlugin
{
	public TileSelector(EditorScreen editor) {
		super(editor);
	}

	@Override
	public int getSelection(Array<Entity> entities, float screenX, float screenY) {
		Vector2 worldPoint = editor.unproject(screenX, screenY);
		int x = MathUtils.floor(worldPoint.x);
		int y = MathUtils.floor(worldPoint.y);
		MapSystem map = getEngine().getSystem(MapSystem.class);
		Entity tile = map.getTile(x, y);
		if(tile != null){
			entities.add(tile);
			return 1;
		}
		return 0;
	}

}
