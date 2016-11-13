package net.mgsx.game.core.tools;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.Initializable;

abstract public class ComponentTool extends Tool
{
	private Class<? extends Component> assignableFor;
	
	public ComponentTool(String name, EditorScreen editor, Class<? extends Component> assignableFor) {
		this(name, editor, Family.one(assignableFor).get());
		this.assignableFor = assignableFor;
	}
	
	public ComponentTool(String name, EditorScreen editor, Family activator) {
		super(name, editor);
		this.activator = activator;
	}
	
	public Class<? extends Component> getAssignableFor() {
		return assignableFor;
	}
	
	public ComponentTool(String name, EditorScreen editor) {
		super(name, editor);
	}
	
	@Override
	protected void activate() 
	{
		Entity entity = editor.currentEntity();
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
