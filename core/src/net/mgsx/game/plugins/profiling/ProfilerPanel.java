package net.mgsx.game.plugins.profiling;

import java.lang.reflect.Field;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.core.plugins.GlobalEditorPlugin;
import net.mgsx.game.core.ui.ToggleButton;

public class ProfilerPanel implements GlobalEditorPlugin
{
	@Override
	public Actor createEditor(final Editor editor, Skin skin) 
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
	
		final Label entityCount = createRow(stats, "entityCount");
		
		// TODO ashley plugin !
		//
		ObjectMap<Integer, String> pipelineLegend = new ObjectMap<Integer, String>();
		for(Field field : GamePipeline.class.getFields()){
			Integer priority = (Integer)ReflectionHelper.get(null, field);
			if(priority != null) pipelineLegend.put(priority, field.getName());
		}
		for(EntitySystem system : editor.entityEngine.getSystems())
		{
			String systemName = system.getClass().getSimpleName();
			
			String priorityName = pipelineLegend.get(system.priority);
			if(priorityName == null) priorityName = "undefined (" + String.valueOf(system.priority) + ")";
			
			String description = "";
			if(system instanceof IteratingSystem){
				int size = ((IteratingSystem) system).getEntities().size();
				description += String.valueOf(size) + " entities";
			}
			
			createRow(stats, systemName).setText(priorityName + " " + description);
		}
		// end
		
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
				
				entityCount.setText(String.valueOf(editor.entityEngine.getEntities().size()));
				
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
