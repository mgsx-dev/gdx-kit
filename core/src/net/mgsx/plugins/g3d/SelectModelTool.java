package net.mgsx.plugins.g3d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

import net.mgsx.core.Editor;
import net.mgsx.core.tools.SelectToolBase;

public class SelectModelTool extends SelectToolBase
{
	private Editor editor;
	public SelectModelTool(Editor editor) {
		super(editor);
		this.editor = editor;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) 
	{
		Vector3 intersection = new Vector3();
		Ray ray = editor.perspectiveCamera.getPickRay(screenX, screenY);
		Entity found = null;
		for(Entity entity : editor.entityEngine.getEntitiesFor(Family.one(G3DModel.class).get())){
			G3DModel model = entity.getComponent(G3DModel.class);
			BoundingBox box = new BoundingBox();
			model.modelInstance.calculateBoundingBox(box);
			if(Intersector.intersectRayBounds(ray, box, intersection)){
				found = entity;
			}
		}
		if(found != null)
		{
			handleSelection(found, editor.selection);
			return true;
		}
		else{
			for(Entity entity : editor.entityEngine.getEntitiesFor(Family.one(G3DModel.class).get())){
				editor.selection.removeValue(entity, true);
				editor.invalidateSelection();
			}
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}
}
