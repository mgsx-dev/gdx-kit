package net.mgsx.game.plugins.pd.tasks;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import net.mgsx.game.core.annotations.Asset;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.pd.patch.PdPatch;

@TaskAlias("pdRun")
public class PdRunTask extends EntityLeafTask
{
	@Asset(PdPatch.class)
	@TaskAttribute
	public String path;
	
	@Override
	public void start() {
		// TODO patch is already opened !
		getObject().assets.get(path, PdPatch.class);
		
	}
	
	@Override
	public com.badlogic.gdx.ai.btree.Task.Status execute() {
		return Status.RUNNING;
	}
	
	@Override
	public void end() {
		// TODO close patch
	}
}
