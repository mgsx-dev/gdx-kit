package net.mgsx.game.plugins.g3d;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.collision.BoundingBox;

public class NodeBoundary 
{
	public Node node;
	public BoundingBox local, global;
	private boolean inFrustum = true; // true at init time (all nodes enabled)
	public void update(ModelInstance model, Frustum frustum) 
	{
		global.set(local).mul(node.globalTransform).mul(model.transform);
		boolean inFrustumNow = frustum.boundsInFrustum(global);
		if(inFrustum != inFrustumNow){
			for(NodePart part : node.parts){
				part.enabled = inFrustumNow;
			}
			inFrustum = inFrustumNow;
		}
	}
	public void show() {
		for(NodePart part : node.parts)
		{
			part.enabled = true;
		}
		inFrustum = true;
	}
}
