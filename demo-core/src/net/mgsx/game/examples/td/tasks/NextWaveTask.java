package net.mgsx.game.examples.td.tasks;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import net.mgsx.game.examples.td.systems.WaveSystem;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;

@TaskAlias("nextWave")
public class NextWaveTask extends EntityLeafTask
{
	@TaskAttribute
	public float rate = 1.2f; // 20% more
	
	public com.badlogic.gdx.ai.btree.Task.Status execute() 
	{
		WaveSystem wave = getEngine().getSystem(WaveSystem.class);
		wave.waveFactor *= rate;
		return Status.SUCCEEDED;
	};
}
