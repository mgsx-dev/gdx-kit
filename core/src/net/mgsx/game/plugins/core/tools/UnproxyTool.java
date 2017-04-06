package net.mgsx.game.plugins.core.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.components.Repository;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.core.components.ProxyComponent;
import net.mgsx.game.plugins.core.systems.DependencySystem;
import net.mgsx.game.plugins.editor.systems.SelectionSystem;

public class UnproxyTool extends Tool
{
	@Inject SelectionSystem selection;
	
	public UnproxyTool(EditorScreen editor) {
		super("Make Proxy Local", editor);
		activator = Family.all(ProxyComponent.class).get();
	}
	
	@Override
	protected void activate() 
	{
		super.activate();
		
		Entity entity = selection.selected();
		
		for(Entity clone : getEngine().getSystem(DependencySystem.class).unlinkChildren(entity)){
			clone.add(getEngine().createComponent(Repository.class));
		}
		
		getEngine().removeEntity(entity);
	}

}
