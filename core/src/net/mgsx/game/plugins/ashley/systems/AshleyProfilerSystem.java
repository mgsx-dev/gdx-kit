package net.mgsx.game.plugins.ashley.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.MathUtils;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.EnumType;

@EditableSystem
public class AshleyProfilerSystem extends EntitySystem
{
	@Editable(realtime=true, readonly=true)
	public int entities;
	
	@Editable(realtime=true, readonly=true)
	public int systems;
	
	@Editable(realtime=true, readonly=true, type=EnumType.BYTES)
	public float memory;
	
	@Editable(realtime=true, readonly=true, type=EnumType.BYTES)
	public float memoryRate;
	
	private transient long prevMemory;
	private transient long consumedMemory;
	private transient float time;
	
	
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
	
	@Editable
	public void forceGC(){
		Runtime.getRuntime().gc();
	}
	
	@Override
	public void update(float deltaTime) {
		entities = getEngine().getEntities().size();
		systems = getEngine().getSystems().size();
		
		time += deltaTime;
		
		long currentMemoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long deltaMemory = currentMemoryUsage - prevMemory;
		if(deltaMemory > 0){
			consumedMemory += currentMemoryUsage - prevMemory;
			if(time > 0){
				memoryRate = consumedMemory / time;
			}
		}else if(deltaMemory < 0){
			consumedMemory = 0;
			time = 0;
		}
		prevMemory = currentMemoryUsage;
		
		
		memory = MathUtils.floor(currentMemoryUsage);
	}
}
