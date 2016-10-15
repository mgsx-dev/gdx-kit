package net.mgsx.core.tools;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import net.mgsx.core.Editor;

abstract public class ComponentTool extends Tool
{

	private Editor editor;
	private Class<? extends Component> assignableFor;

	public ComponentTool(Editor editor, Class<? extends Component> assignableFor) {
		super("", editor.orthographicCamera);
		this.editor = editor;
		this.assignableFor = assignableFor;
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
