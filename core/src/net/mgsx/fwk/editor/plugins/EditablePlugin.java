package net.mgsx.fwk.editor.plugins;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public interface EditablePlugin<T> {

	public Actor createEditor(T object, Skin skin);
}
