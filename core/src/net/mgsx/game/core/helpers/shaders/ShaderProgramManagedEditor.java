package net.mgsx.game.core.helpers.shaders;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.helpers.shaders.ShaderProgramManaged.ControlHandler;
import net.mgsx.game.core.ui.FieldEditor;
import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.core.ui.accessors.MethodAccessor;
import net.mgsx.game.core.ui.accessors.VoidAccessor;
import net.mgsx.game.core.ui.widgets.BooleanWidget;
import net.mgsx.game.core.ui.widgets.VoidWidget;

public class ShaderProgramManagedEditor implements FieldEditor
{
	@Override
	public Actor create(Accessor accessor, final Skin skin) {
		final ShaderProgramManaged spm = accessor.get(ShaderProgramManaged.class);
		
		
		Table table = new Table(skin);
		
		table.add(VoidWidget.instance.create(new VoidAccessor(spm, "reload"), skin));
		
		// TODO helper for find method ...
		if(spm.shaderInfo.inject()){
			table.add(BooleanWidget.labeled("Frozen").create(new MethodAccessor(spm, "frozen", "isFrozen", "freeze"), skin));
		}
		
		table.add(VoidWidget.instance.create(new VoidAccessor(spm, "dumpVS"), skin));
		table.add(VoidWidget.instance.create(new VoidAccessor(spm, "dumpFS"), skin));
		
		TextButton btLoadV = new TextButton("LoadVS", skin);
		TextButton btLoadF = new TextButton("LoadFS", skin);
		
		// TODO don't build bouton and listenr if false ...
		if(spm.shaderInfo.storable()){
			table.add(btLoadV);
			table.add(btLoadF);
		}
		
		table.row();
		
		final Cell configCell = table.add();
		
		
		
		spm.handler = new ControlHandler() {
			
			@Override
			public void loaded() 
			{
				if(configCell.getActor() == null){
					Table cTable = new Table(skin);
					configCell.setActor(cTable);
					for(final String config : spm.configs){
						final TextButton btConfig = new TextButton(config, skin, "toggle");
						cTable.add(btConfig);
						btConfig.addListener(new ChangeListener() {
							
							@Override
							public void changed(ChangeEvent event, Actor actor) {
								if(btConfig.isChecked())
									spm.currentConfig.add(config);
								else
									spm.currentConfig.remove(config);
								spm.invalidate();
							}
						});
					}
				}
				
			}
		};
		
		btLoadV.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				NativeService.instance.openLoadDialog(new DefaultCallback() {
					@Override
					public void selected(FileHandle file) {
						spm.changeVS(file);
					}
					@Override
					public boolean match(FileHandle file) {
						return file.name().endsWith(".vert") || file.name().endsWith(".glsl");
					}
					@Override
					public String description() {
						return "Vertex Shader (*.vert, *.glsl)";
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
						spm.changeFS(file);
					}
					
					@Override
					public boolean match(FileHandle file) {
						return file.name().endsWith(".frag") || file.name().endsWith(".glsl");
					}
					@Override
					public String description() {
						return "Fragment Shader (*.frag, *.glsl)";
					}
				});
			}
		});
		
		return table;
	}

}
