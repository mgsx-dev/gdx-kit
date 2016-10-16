package net.mgsx.core.plugins;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import net.mgsx.core.Editor;

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
