package net.mgsx.game.plugins.core.editors;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.game.core.helpers.FilesShaderProgram;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.ui.FieldEditor;
import net.mgsx.game.core.ui.accessors.Accessor;

public class ShaderProgramEditor implements FieldEditor
{

	@Override
	public Actor create(Accessor accessor, Skin skin) 
	{
		final FilesShaderProgram shader = accessor.get(FilesShaderProgram.class);
		
		Table table = new Table(skin);
		
		TextButton btLoadV = new TextButton("Set vertex", skin);
		TextButton btLoadF = new TextButton("Set fragment", skin);
		TextButton btUpdate = new TextButton("Update", skin);
		
		table.add(btLoadV);
		table.add(btLoadF);
		table.add(btUpdate);
		
		btUpdate.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				shader.reload();
			}
		});
		
		btLoadV.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				NativeService.instance.openLoadDialog(new DefaultCallback() {
					@Override
					public void selected(FileHandle file) {
						shader.vertexShader = file;
					}
					@Override
					public boolean match(FileHandle file) {
						return file.name().endsWith("-vertex.glsl");
					}
					@Override
					public String description() {
						return "Vertex Shader (*-vertex.glsl)";
					}
				});
			}
		});
		
		btLoadF.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				NativeService.instance.openLoadDialog(new DefaultCallback() {
					@Override
					public void selected(FileHandle file) {
						shader.fragmentShader = file;
					}
					@Override
					public boolean match(FileHandle file) {
						return file.name().endsWith("-fragment.glsl");
					}
					@Override
					public String description() {
						return "Vertex Shader (*-fragment.glsl)";
					}
				});
			}
		});
		
		return table;
	}
	
}
