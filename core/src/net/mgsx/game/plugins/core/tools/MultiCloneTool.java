package net.mgsx.game.plugins.core.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.helpers.EntityHelper;
import net.mgsx.game.core.tools.RectangleTool;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@Editable
public class MultiCloneTool extends RectangleTool
{
	@Editable
	public int count = 10;
	
	public MultiCloneTool(EditorScreen editor) {
		super("Multi Clone", editor);
	}
	
	@Override
	protected void activate() {
		super.activate();
		if(editor.selection.size == 0) end();
	}

	@Override
	protected void create(Vector2 startPoint, Vector2 endPoint) 
	{
		for(int i=0 ; i<count ; i++)
		{
			Entity clone = EntityHelper.clone(getEngine(), editor.selection.peek());
			Transform2DComponent transform = Transform2DComponent.components.get(clone);
			if(transform != null){
				transform.position.set(MathUtils.random(startPoint.x, endPoint.x), MathUtils.random(startPoint.y, endPoint.y));
			}
		}
		
	}

}
