package net.mgsx.game.core.plugins;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Base for a global (all entities) editor view
 */
public interface EngineEditor {

	/**
	 * return a new editor. Note that this methd is called once, editor is reused during
	 * editor lifecycle.
	 * @param editor
	 * @param skin
	 * @return
	 */
	public Actor createEditor(Engine engine, AssetManager assets, Skin skin);
}
