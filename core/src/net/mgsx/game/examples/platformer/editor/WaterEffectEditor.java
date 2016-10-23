package net.mgsx.game.examples.platformer.editor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.plugins.GlobalEditorPlugin;
import net.mgsx.game.core.ui.EntityEditor;
import net.mgsx.game.examples.platformer.core.PlatformerPlugin;

public class WaterEffectEditor implements GlobalEditorPlugin
{
	@Override
	public Actor createEditor(Editor editor, Skin skin) 
	{
		EntityEditor e = new EntityEditor(skin);
		e.setEntity(editor.getPlugin(PlatformerPlugin.class).ppp.settings);
		return e;
	}

}
