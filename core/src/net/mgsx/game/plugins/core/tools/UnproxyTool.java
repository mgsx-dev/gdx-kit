package net.mgsx.game.plugins.core.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.components.Repository;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.core.components.ProxyComponent;

public class UnproxyTool extends Tool
{

	public UnproxyTool(EditorScreen editor) {
		super("Make Proxy Local", editor);
		activator = Family.all(ProxyComponent.class).get();
	}
	
	@Override
	protected void activate() 
	{
		super.activate();
		
		Entity entity = editor.getSelected();
		
		ProxyComponent proxy = ProxyComponent.components.get(entity);
		
		for(Entity clone : proxy.clones.entities()){
			clone.add(getEngine().createComponent(Repository.class));
		}
		
		getEngine().removeEntity(entity);
	}

}
