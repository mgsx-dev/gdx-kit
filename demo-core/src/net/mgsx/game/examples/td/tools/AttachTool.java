package net.mgsx.game.examples.td.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.examples.td.components.Attachement;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class AttachTool extends Tool
{
	public AttachTool(EditorScreen editor) {
		super("Attach", editor);
	}
	
	@Override
	public boolean allowed(Array<Entity> selection) 
	{
		return selection.size > 1;
		// TODO more check : circular dependencies ... etc
	}
	
	@Override
	protected void activate() {
		editor.setInfo("Attach to last selected entities other selected entities.");
		
		// get mother ship (selection.last)
		Entity parentEntity = editor.selection.peek();
		Transform2DComponent parentTransform = Transform2DComponent.components.get(parentEntity);
		
		// attach/detach towers (selection - last)
		for(int i=0 ; i<editor.selection.size-1 ; i++)
		{
			Entity entity = editor.selection.get(i);
			entity.remove(Attachement.class);
			Transform2DComponent transform = Transform2DComponent.components.get(entity);
			if(transform != null)
			{
				Attachement attachement = getEngine().createComponent(Attachement.class);
				attachement.parent = parentEntity;
				
				attachement.offset.set(transform.position).sub(parentTransform.position).rotate(-parentTransform.angle);
				entity.add(attachement);
			}
		}
		
		end();
	}
}
