package net.mgsx.game.plugins.g3d.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.plugins.SelectorPlugin;
import net.mgsx.game.plugins.g3d.components.G3DModel;

// TODO choice of conception :
// - each node or mesh part is an entity
// - export each models separately ?
// - create an entity for each model Instance children (hierarchy is treated as one entity !)
// - this could interfer with renderer ?
//
public class ModelSelector  extends SelectorPlugin
{
	private BoundingBox box;
	private Vector3 intersection = new Vector3();
	
	public ModelSelector(Editor editor) {
		super(editor);
	}

	private Node intersectRay(Node node, Ray ray)
	{
		node.calculateBoundingBox(box, true);
		if(Intersector.intersectRayBounds(ray, box, intersection))
		{
			if(node.parts != null)
			{
				for(NodePart nodePart : node.parts){
					
					Matrix4 mat = node.globalTransform.cpy().inv();
					Ray localRay = ray.cpy().mul(mat);
					
					
					MeshPart part = nodePart.meshPart;
					Mesh mesh = part.mesh;
					short[] indices = new short[mesh.getNumIndices()];
					mesh.getIndices(indices);
					int floatsPerVertex = mesh.getVertexSize() / Float.BYTES;
					float[] vertices = new float[mesh.getNumVertices() * floatsPerVertex];
					mesh.getVertices(vertices );
					if(Intersector.intersectRayTriangles(localRay, vertices, indices, floatsPerVertex, new Vector3())){
						return node;
					}
				}
			}
			
			return intersectRay(node.getChildren(), ray);
		}
		return null;
	}
	
	private Node intersectRay(Iterable<Node> nodes, Ray ray)
	{
		for(Node node : nodes)
		{
			Node foundNode = intersectRay(node, ray);
			if(foundNode != null){
				return foundNode;
			}
		}
		return null;
	}
	
	@Override
	public int getSelection(Array<Entity> entities, float screenX, float screenY) {
		Ray baseRay = editor.camera.getPickRay(screenX, screenY);
		Ray ray = new Ray();
		int count = 0;
		for(Entity entity : editor.entityEngine.getEntitiesFor(Family.one(G3DModel.class).get())){
			ray.set(baseRay);
			G3DModel model = entity.getComponent(G3DModel.class);
			box = new BoundingBox();
			model.modelInstance.calculateTransforms();
			model.modelInstance.calculateBoundingBox(box);
			Matrix4 mat = model.modelInstance.transform.cpy().inv();
			ray.mul(mat);
			if(Intersector.intersectRayBounds(ray, box, intersection))
			{
				Node node = intersectRay(model.modelInstance.nodes, ray);
				if(node != null){
					entities.add(entity);
					count++;
				}
			}
		}
		return count;
	}
}
