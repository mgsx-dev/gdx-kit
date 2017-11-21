package net.mgsx.game.examples.openworld.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool;

// TODO pullup to KIT
public class StaticMeshPool
{
	private final VertexAttributes vertexAttributes;
	
	private Pool<Mesh> pool = new Pool<Mesh>(0){
		@Override
		protected Mesh newObject() {
			throw new GdxRuntimeException("not supported");
		}
	};
	
	public StaticMeshPool(VertexAttributes vertexAttributes) {
		this.vertexAttributes = vertexAttributes;
	}
	
	public Mesh obtain(int maxVertices, int maxIndices){
		Mesh mesh = null;
		if(pool.getFree() > 0){
			mesh = pool.obtain();
			if(mesh.getMaxVertices() < maxVertices || mesh.getMaxIndices() < maxIndices){
				Gdx.app.debug("Memory", "mesh too small, disposing");
				mesh.dispose();
				mesh = null;
			}
		}
		if(mesh == null){
			Gdx.app.debug("Memory", "allocate new mesh");
			mesh = new Mesh(true, maxVertices, maxIndices, vertexAttributes);
		}
		return mesh;
	}
	
	public void free(Mesh mesh){
		pool.free(mesh);
	}

}
