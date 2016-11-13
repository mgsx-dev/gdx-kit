package net.mgsx.game.core.plugins;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import net.mgsx.game.core.EditorScreen;

/**
 * Base for a global (all entities) editor view
 */
public interface GlobalEditorPlugin {

	/**
	 * return a new editor. Note that this methd is called once, editor is reused during
	 * editor lifecycle.
	 * @param editor
	 * @param skin
	 * @return
	 */
	public Actor createEditor(EditorScreen editor, Skin skin);
}
