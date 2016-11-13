package net.mgsx.game.examples.platformer.editor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.GlobalEditorPlugin;
import net.mgsx.game.core.ui.EntityEditor;
import net.mgsx.game.examples.platformer.PlatformerPlugin;

public class WaterEffectEditor implements GlobalEditorPlugin
{
	@Override
	public Actor createEditor(final EditorScreen editor, Skin skin) 
	{
		Table table = new Table(skin);
		EntityEditor e = new EntityEditor(skin);
		e.setEntity(editor.getPlugin(PlatformerPlugin.class).ppp.settings);
		
		TextButton btReload = new TextButton("reload shaders", skin);
		table.add(btReload);
		
		table.add(e).row();
		
		btReload.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				editor.getPlugin(PlatformerPlugin.class).ppp.loadShaders();
			}
		});
		
		return table;
	}

}
