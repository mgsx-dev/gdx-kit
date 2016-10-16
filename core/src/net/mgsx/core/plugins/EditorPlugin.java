package net.mgsx.core.plugins;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import net.mgsx.core.Editor;

public interface EditorPlugin {

	public Actor createEditor(Editor editor, Skin skin);
}
