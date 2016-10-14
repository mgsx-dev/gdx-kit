package net.mgsx.core.plugins;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public interface EditablePlugin {

	public Actor createEditor(Entity entity, Skin skin);
}
