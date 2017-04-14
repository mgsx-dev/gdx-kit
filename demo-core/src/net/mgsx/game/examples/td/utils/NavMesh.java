package net.mgsx.game.examples.td.utils;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

public class NavMesh {

	public ModelData model;
	
	public NavMesh(ModelData model) {
		super();
		this.model = model;
	}

	public float rayCast(Ray ray){
		
		float [] vertices = model.meshes.get(0).vertices; //parts[0].
		short [] indices = model.meshes.get(0).parts[0].indices;
		int vertexSize = 0;
		for(VertexAttribute attr : model.meshes.get(0).attributes){
			vertexSize += attr.numComponents;
		}
		
		Vector3 intersection = new Vector3();
		boolean intersect = Intersector.intersectRayTriangles(ray, vertices, indices, vertexSize, new Vector3());
		
		if(intersect){
			return ray.origin.dst(intersection);
		}
		return -1;
		
	}
	
	
	public boolean rayCast(Ray ray, Vector3 nearest, Vector3 normal){
		
		float [] vertices = model.meshes.get(0).vertices; //parts[0].
		short [] indices = model.meshes.get(0).parts[0].indices;
		int vertexSize = 0;
		for(VertexAttribute attr : model.meshes.get(0).attributes){
			vertexSize += attr.numComponents;
		}
		Vector3 t1 = new Vector3();
		Vector3 t2 = new Vector3();
		Vector3 t3 = new Vector3();
		Vector3 tmp = new Vector3();
		float distance = -1;
		for(int i=0 ; i<indices.length ; i+=3)
		{
			int i1 = indices[i+0] * vertexSize;
			int i2 = indices[i+1] * vertexSize;
			int i3 = indices[i+2] * vertexSize;
			t1.set(vertices[i1+0], vertices[i1+1], vertices[i1+2]);
			t2.set(vertices[i2+0], vertices[i2+1], vertices[i2+2]);
			t3.set(vertices[i3+0], vertices[i3+1], vertices[i3+2]);
			
			Vector3 intersection = new Vector3();
			if(Intersector.intersectRayTriangle(ray, t1, t2, t3, intersection)){
				float dst = intersection.dst2(ray.origin);
				if(distance < 0 || distance > intersection.dst2(ray.origin)){
					nearest.set(intersection);
					distance = dst;
					normal.set(t2).sub(t1);
					tmp.set(t3).sub(t1);
					normal.crs(tmp).nor();
				}
			}
		}
		
		return distance >= 0;
	}
	
}
