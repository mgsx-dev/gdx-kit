package net.mgsx.game.core.tools;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.plugins.Initializable;

abstract public class ComponentTool extends Tool
{
	public ComponentTool(String name, Editor editor, Class<? extends Component> assignableFor) {
		this(name, editor, Family.one(assignableFor).get());
	}
	
	public ComponentTool(String name, Editor editor, Family activator) {
		super(name, editor);
		this.activator = activator;
	}
	
	public ComponentTool(String name, Editor editor) {
		super(name, editor);
	}
	
	@Override
	protected void activate() 
	{
		Entity entity = editor.getSelected();
		Component component = createComponent(entity);
		if(component != null)
		{
			if(component instanceof Initializable) ((Initializable) component).initialize(editor.entityEngine, entity);
			entity.add(component);
		}
		end();
	}

	abstract protected Component createComponent(Entity entity);

}
