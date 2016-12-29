package net.mgsx.game.examples.platformer.tasks;

import net.mgsx.game.examples.platformer.logic.SecretComponent;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;

@TaskAlias("discoverSecret")
public class DiscoverSecretTask extends EntityLeafTask
{
	
	@Override
	public void start() {
		getEntity().add(getEngine().createComponent(SecretComponent.class));
	}
	
	@Override
	public com.badlogic.gdx.ai.btree.Task.Status execute() {
		return Status.RUNNING;
	}
	
	@Override
	public void end() {
		getEntity().remove(SecretComponent.class);
	}
	
}
