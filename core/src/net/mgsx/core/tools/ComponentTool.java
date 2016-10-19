package net.mgsx.core.tools;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import net.mgsx.core.Editor;

abstract public class ComponentTool extends Tool
{
	public ComponentTool(String name, Editor editor, Class<? extends Component> assignableFor) {
		this(name, editor, Family.one(assignableFor).get());
	}
	
	public ComponentTool(String name, Editor editor, Family activator) {
		super(name, editor);
		this.activator = activator;
	}
	
	@Override
	protected void activate() 
	{
		Entity entity = editor.getSelected();
		Component component = createComponent(entity);
		if(component != null) entity.add(component);
		end();
	}

	abstract protected Component createComponent(Entity entity);

}
