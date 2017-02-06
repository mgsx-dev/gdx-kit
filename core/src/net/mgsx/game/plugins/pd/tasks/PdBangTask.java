package net.mgsx.game.plugins.pd.tasks;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.pd.Pd;

@TaskAlias("pdBang")
public class PdBangTask extends EntityLeafTask
{
	@TaskAttribute
	public String symbol;
	
	@Override
	public Status execute() {
		Pd.audio.sendBang(symbol);
		return Status.SUCCEEDED;
	}
}
