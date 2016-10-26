package net.mgsx.game.plugins.g3d;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.plugins.GlobalEditorPlugin;
import net.mgsx.game.core.ui.EntityEditor;

public class G3DEditor implements GlobalEditorPlugin
{

	@Override
	public Actor createEditor(Editor editor, Skin skin) 
	{
		return new EntityEditor(editor.getEditorPlugin(ModelPlugin.class).settings, skin); // XXX should be get plugin
	}

}
