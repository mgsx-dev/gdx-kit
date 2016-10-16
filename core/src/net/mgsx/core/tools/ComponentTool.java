package net.mgsx.core.tools;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import net.mgsx.core.Editor;

abstract public class ComponentTool extends Tool
{

	private Editor editor;

	public ComponentTool(String name, Editor editor, Class<? extends Component> assignableFor) {
		super(name, editor);
		this.editor = editor;
		this.activator = Family.one(assignableFor).get();
	}
	
	@Override
	protected void activate() 
	{
		Entity entity = editor.getSelected();
		entity.add(createComponent(entity));
		end();
	}

	abstract protected Component createComponent(Entity entity);

}
