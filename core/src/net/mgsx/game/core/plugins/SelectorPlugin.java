package net.mgsx.game.core.plugins;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;

/**
 * Plugin that handle entity selection by a specific component
 * knowing an entity position/size/shape it can know which entities
 * are hit at a screen point (touch pointer) or a boundray (rectangle selection ...)
 */
abstract public class SelectorPlugin {

	protected EditorScreen editor;
	
	public SelectorPlugin(EditorScreen editor) {
		super();
		this.editor = editor;
	}
	
	protected Engine getEngine(){
		return editor.entityEngine;
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
