package net.mgsx.game.core.plugins;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.Editor;

/**
 * Plugin that handle entity selection by a specific component
 * knowing an entity position/size/shape it can know which entities
 * are hit at a screen point (touch pointer) or a boundray (rectangle selection ...)
 */
abstract public class SelectorPlugin {

	protected Editor editor;
	
	public SelectorPlugin(Editor editor) {
		super();
		this.editor = editor;
	}

	/**
	 * add entities under the screen pointer
	 * @param entities
	 * @param screenX
	 * @param screenY
	 * @return entity count added
	 */
	abstract public int getSelection(Array<Entity> entities, float screenX, float screenY);
}
