package net.mgsx.game.examples.td.tasks;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import net.mgsx.game.examples.td.systems.WaveSystem;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;

@TaskAlias("startWave")
public class StartWaveTask extends EntityLeafTask
{
	@TaskAttribute
	public float factor = 1f;
	
	public com.badlogic.gdx.ai.btree.Task.Status execute() 
	{
		WaveSystem wave = getEngine().getSystem(WaveSystem.class);
		wave.waveFactor = factor;
		return Status.SUCCEEDED;
	};
}
