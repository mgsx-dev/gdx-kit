package net.mgsx.game.examples.td.utils;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMesh;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMeshPart;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class MeshEmitter {

	private ModelMesh mesh;
	private int vcount, vsize, tcount, positionOffset = -1, normalOffset = -1;
	
	public MeshEmitter(ModelMesh mesh) {
		super();
		this.mesh = mesh;
		for(ModelMeshPart part : mesh.parts){
			tcount = part.indices.length / 3;
		}
		for(VertexAttribute attr : mesh.attributes){
			if(attr.usage == Usage.Position){
				positionOffset = attr.offset / 4;
			}else if(attr.usage == Usage.Normal){
				normalOffset = attr.offset / 4;
			}
		}
		vcount = mesh.vertices.length / vsize;
	}

	public void randomVertex(Vector3 position, Vector3 normal)
	{
		int v = MathUtils.random(vcount-1);
		v3(position, v, positionOffset);
		v3(normal, v, normalOffset);
	}
	public void randomTriangle(Vector3 position, Vector3 normal)
	{
		MathUtils.random(tcount-1);
		// TODO get triangle center ...
	}
	
	public Vector3 v3(Vector3 v, int index, int offset){
		int i = index * vsize + offset;
		v.set(mesh.vertices[i], mesh.vertices[i+1], mesh.vertices[i+2]);
		return v;
	}
}
