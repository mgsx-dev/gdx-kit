package net.mgsx.game.examples.gpu.utils;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;

public class MeshGenerator {

	public static Mesh grid(final int size)
	{
		final int floatsPerVertex = 6;
		final int vertexCount = size * size;
		float [] vertices = new float[vertexCount * floatsPerVertex];
		
		for(int i=0 ; i<size ; i++){
			for(int j=0 ; j<size ; j++){
				int index = (i * size + j) * floatsPerVertex;
				vertices[index + 0] = j;
				vertices[index + 1] = i;
				vertices[index + 2] = 0;
				
				vertices[index + 3] = 0;
				vertices[index + 4] = 0;
				vertices[index + 5] = 1;
			}
		}
		
		final int indicesCount = (size - 1) * (size - 1) * 6;
		short[] indices = new short[indicesCount];
		for(int i=0 ; i<size-1 ; i++){
			for(int j=0 ; j<size-1 ; j++){
				int index = (i * (size-1) + j) * 6;
				indices[index + 0] = (short)(i * size + j);
				indices[index + 1] = (short)(i * size + j + 1);
				indices[index + 2] = (short)((i+1) * size + j);
				
				indices[index + 3] = (short)((i+1) * size + j);
				indices[index + 4] = (short)(i * size + j + 1);
				indices[index + 5] = (short)((i+1) * size + j+1);
			}
		}
		
		Mesh mesh = new Mesh(true, vertices.length, vertices.length, VertexAttribute.Position(), VertexAttribute.Normal());
		mesh.setVertices(vertices);
		mesh.setIndices(indices);

		return mesh;
	}
}
