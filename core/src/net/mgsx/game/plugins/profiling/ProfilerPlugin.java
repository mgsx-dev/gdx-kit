package net.mgsx.game.plugins.profiling;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.profiling.GLProfiler;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.plugins.EditorPlugin;

public class ProfilerPlugin extends EditorPlugin
{
	static ProfilerModel model = new ProfilerModel();
	
	@Override
	public void initialize(Editor editor) 
	{
		editor.addGlobalEditor("Profiler", new ProfilerPanel());
		
		// reset counter before processing to prevent editor drawing statistics.
		editor.entityEngine.addSystem(new EntitySystem(GamePipeline.FIRST) {
			@Override
			public void update(float deltaTime) {
				if(!model.all){
					GLProfiler.reset();
				}
			}
		});
		editor.entityEngine.addSystem(new EntitySystem(GamePipeline.LAST) {
			@Override
			public void update(float deltaTime) 
			{
				model.calls = GLProfiler.calls;
				model.drawCalls = GLProfiler.drawCalls;
				model.shaderSwitches = GLProfiler.shaderSwitches;
				model.textureBindings = GLProfiler.textureBindings;
				model.vertexCount = GLProfiler.vertexCount.count;
				model.fps = Gdx.graphics.getFramesPerSecond();
				
				GLProfiler.reset();
			}
		});
		
	}
}
