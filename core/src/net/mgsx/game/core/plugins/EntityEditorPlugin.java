package net.mgsx.game.core.plugins;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Base for entity/component editor view
 */
public interface EntityEditorPlugin {

	public Actor createEditor(Entity entity, Skin skin);
}
