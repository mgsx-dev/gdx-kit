package net.mgsx.game.core.tools;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.components.Initializable;

abstract public class ComponentTool extends Tool
{
	private Class<? extends Component> assignableFor; // TODO remove this in favor to allowed method
	
	public ComponentTool(String name, EditorScreen editor, Family activator) {
		super(name, editor);
		this.activator = activator; // TODO remove this in favor to allowed method
	}
	
	@Override
	protected void activate() 
	{
		Entity entity = currentEntity();
		Component component = createComponent(entity);
		if(component != null)
		{
			if(component instanceof Initializable) ((Initializable) component).initialize(getEngine(), entity);
			entity.add(component);
		}
		end();
	}

	abstract protected Component createComponent(Entity entity);

}
