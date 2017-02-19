package net.mgsx.game.plugins.ashley.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;

@EditableSystem
public class AshleyProfilerSystem extends EntitySystem
{
	@Editable(realtime=true, readonly=true)
	public int entities;
	
	@Editable(realtime=true, readonly=true)
	public int systems;
	
	@Editable
	public void clearPools(){
		if(getEngine() instanceof PooledEngine)
		{
			PooledEngine engine = (PooledEngine)getEngine();
			engine.clearPools();
		}
	}
	
	public AshleyProfilerSystem() {
		super();
		setProcessing(false); // TODO config
	}
	
	@Override
	public void update(float deltaTime) {
		entities = getEngine().getEntities().size();
		systems = getEngine().getSystems().size();
	}
}
