package net.mgsx.plugins.box2d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import net.mgsx.core.Editor;
import net.mgsx.core.tools.SelectToolBase;
import net.mgsx.plugins.box2d.model.BodyItem;
import net.mgsx.plugins.box2d.model.WorldItem;
import net.mgsx.plugins.g3d.G3DModel;

public class SelectBodyTool extends SelectToolBase
{
	private WorldItem worldItem;
	private Editor editor;
	
	public SelectBodyTool(Editor editor, WorldItem worldItem) {
		super(editor);
		this.editor = editor;
		this.worldItem = worldItem;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) 
	{
		Vector2 worldPoint = unproject(screenX, screenY);
		Body body = worldItem.queryFirstBody(worldPoint);
		if(body != null)
		{
			Entity bodyItem = (Entity)body.getUserData();
			handleSelection(bodyItem, editor.selection);
			return true;
		}
		else
		{
			// TODO generalize maybe with a special selector plugin ... : give a screen position, get a list of items, then handle no/single and multi properly.
			// idem for select and move : if pick on already selected then dont change selection !
			for(Entity entity : editor.entityEngine.getEntitiesFor(Family.one(BodyItem.class).get())){
				editor.selection.removeValue(entity, true);
				editor.invalidateSelection();
			}
		}
		
		return super.touchDown(screenX, screenY, pointer, button);
	}
	
}
