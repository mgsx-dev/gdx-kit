package net.mgsx.game.plugins.g3d;

import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.plugins.GlobalEditorPlugin;
import net.mgsx.game.core.ui.EntityEditor;
import net.mgsx.game.plugins.g3d.ModelPlugin.FilesShader;

public class G3DEditor implements GlobalEditorPlugin
{

	@Override
	public Actor createEditor(final Editor editor, Skin skin) 
	{
		Table table = new Table(skin);
		TextButton btReload = new TextButton("reload", skin);
		table.add(btReload).row();
		
		table.add(new EntityEditor(editor.getEditorPlugin(ModelPlugin.class).settings, skin)); // XXX should be get plugin
	
		btReload.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				ModelPlugin mp = editor.getEditorPlugin(ModelPlugin.class);
				ShaderProvider sp = mp.shaderProviders[mp.settings.shader.ordinal()];
				if(sp instanceof FilesShader){
					mp.shaderProviders[mp.settings.shader.ordinal()] = ((FilesShader) sp).reload();
				}
			}
		});
		
		return table;
	}

}
