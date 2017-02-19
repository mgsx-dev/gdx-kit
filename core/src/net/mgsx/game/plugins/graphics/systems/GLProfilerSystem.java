package net.mgsx.game.plugins.graphics.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.profiling.GLProfiler;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;

@EditableSystem
public class GLProfilerSystem extends EntitySystem
{
	@Editable(realtime=true, readonly=true)
	public int calls;

	@Editable(realtime=true, readonly=true)
	public int textureBindings;

	@Editable(realtime=true, readonly=true)
	public int drawCalls;

	@Editable(realtime=true, readonly=true)
	public int shaderSwitches;

	@Editable(realtime=true, readonly=true)
	public int vertexCount;

	@Editable(realtime=true, readonly=true)
	public float fps;

	public GLProfilerSystem() {
		super();
		setProcessing(false); // XXX should be configured in plugin (default processing)
	}
	
	@Override
	public void setProcessing(boolean processing) {
		super.setProcessing(processing);
		if(processing) GLProfiler.enable(); else GLProfiler.disable();
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		calls = GLProfiler.calls;
		drawCalls = GLProfiler.drawCalls;
		shaderSwitches = GLProfiler.shaderSwitches;
		textureBindings = GLProfiler.textureBindings;
		vertexCount = GLProfiler.vertexCount.count;
		fps = Gdx.graphics.getFramesPerSecond();
		
		GLProfiler.reset();
	}
	
}
