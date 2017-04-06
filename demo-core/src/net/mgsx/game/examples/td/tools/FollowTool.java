package net.mgsx.game.examples.td.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.examples.td.components.Follow;

public class FollowTool extends Tool
{
	public FollowTool(EditorScreen editor) {
		super("Follow Tool", editor);
	}
	
	@Override
	public boolean allowed(Array<Entity> selection) {
		return selection.size == 2;
	}
	
	@Override
	protected void activate() 
	{
		super.activate();
		
		Entity source = selection().selection.first();
		Entity target = selection().selection.peek();
		Follow follow = Follow.components.get(source);
		
		source.remove(Follow.class);
		if(follow == null || follow.head != target){
			follow = getEngine().createComponent(Follow.class);
			follow.head = target;
			source.add(follow);
		}
		desactivate();
		
	}
}
