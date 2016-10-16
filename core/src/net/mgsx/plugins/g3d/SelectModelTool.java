package net.mgsx.plugins.g3d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

import net.mgsx.core.Editor;
import net.mgsx.core.tools.SelectToolBase;

public class SelectModelTool extends SelectToolBase
{
	private Editor editor;
	private BoundingBox box;
	private Vector3 intersection = new Vector3();
	
	public SelectModelTool(Editor editor) {
		super(editor);
		this.editor = editor;
	}

	private boolean intersectRay(Node node, Ray ray)
	{
		node.calculateBoundingBox(box, true);
		box.mul(node.globalTransform);
		if(Intersector.intersectRayBounds(ray, box, intersection))
		{
			if(node.parts != null && node.parts.size > 0 && !node.hasChildren()) return true;
			for(Node child : node.getChildren()){
				if(intersectRay(child, ray)){
					return true;
				}
			}
//			for(NodePart nodePart : node.parts){
//				// TODO if precise collision with triangles nodePart.meshPart.mesh.
//			}
		}
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) 
	{
		
		Ray ray = editor.perspectiveCamera.getPickRay(screenX, screenY);
		Entity found = null;
		for(Entity entity : editor.entityEngine.getEntitiesFor(Family.one(G3DModel.class).get())){
			G3DModel model = entity.getComponent(G3DModel.class);
			box = new BoundingBox();
			model.modelInstance.calculateBoundingBox(box);
			box.mul(model.modelInstance.transform);
			if(Intersector.intersectRayBounds(ray, box, intersection)){
				for(Node node : model.modelInstance.nodes){
					if(intersectRay(node, ray)){
						found = entity;
					}
				}
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
