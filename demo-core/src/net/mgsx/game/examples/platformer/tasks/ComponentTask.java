package net.mgsx.game.examples.platformer.tasks;

import com.badlogic.ashley.core.Component;

import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;

public abstract class ComponentTask  extends EntityLeafTask
{
	private Class<? extends Component> type;
	
	public ComponentTask(Class<? extends Component> type) {
		super();
		this.type = type;
	}

	@Override
	public void start() {
		getEntity().add(getEngine().createComponent(type));
	}
	
	@Override
	public Status execute() 
	{
		return Status.RUNNING;
	}
	
	@Override
	public void end() {
		getEntity().remove(type);
	}
}