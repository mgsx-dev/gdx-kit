package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.EntitySystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;

@EditableSystem
public class WaveSystem extends EntitySystem
{
	@Editable
	public int waveCount = 1;
	
	public float waveFactor = 1;
	
	public WaveSystem() 
	{
		super(GamePipeline.LOGIC);
	}
	
}
