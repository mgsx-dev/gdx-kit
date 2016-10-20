package net.mgsx.game.plugins.profiling;

import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.plugins.GlobalEditorPlugin;
import net.mgsx.game.core.ui.ToggleButton;

public class ProfilerPanel implements GlobalEditorPlugin
{
	@Override
	public Actor createEditor(Editor editor, Skin skin) 
	{
		final TextButton switchButton = new ToggleButton("enabled", "disabled", false, skin);
		switchButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(switchButton.isChecked()){
					enable();
				}else{
					disable();
				}
			}
		});
		final TextButton allButton = new ToggleButton("game+editor", "game", ProfilerPlugin.model.all, skin);
		allButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				ProfilerPlugin.model.all = allButton.isChecked();
			}
		});
		
		Table stats = new Table(skin);
		
		final Label fps = createRow(stats, "fps");
		final Label calls = createRow(stats, "calls");
		final Label drawCalls = createRow(stats, "drawCalls");
		final Label shaderSwitches = createRow(stats, "shaderSwitches");
		final Label textureBindings = createRow(stats, "textureBindings");
		final Label vertexCount = createRow(stats, "vertexCount");
		final Label triangleCount = createRow(stats, "triangleCount");
		
		Table main = new Table(skin){
			@Override
			public void act(float delta) {
				
				// display results
				fps.setText(String.valueOf(ProfilerPlugin.model.fps));
				calls.setText(String.valueOf(ProfilerPlugin.model.calls));
				drawCalls.setText(String.valueOf(ProfilerPlugin.model.drawCalls));
				shaderSwitches.setText(String.valueOf(ProfilerPlugin.model.shaderSwitches));
				textureBindings.setText(String.valueOf(ProfilerPlugin.model.textureBindings));
				vertexCount.setText(String.valueOf(ProfilerPlugin.model.vertexCount)); // TODO more
				triangleCount.setText(String.valueOf(ProfilerPlugin.model.vertexCount / 6)); // TODO not really true ...
				super.act(delta);
			}
		};
		
		// layout
		main.add(switchButton);
		main.add(allButton);
		main.row();
		main.add(stats).row();
		
		return main;
	}
	
	private Label createRow(Table parent, String name){
		Label label = new Label(name, parent.getSkin());
		Label value = new Label("", parent.getSkin());
		parent.add(label);
		parent.add(value);
		parent.row();
		return value;
	}
	
	private void enable(){
		GLProfiler.enable();
	}

	private void disable(){
		GLProfiler.disable();
	}

}
