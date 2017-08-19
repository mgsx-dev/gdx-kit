package net.mgsx.game.examples.openworld.model;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

public class OpenWorldPool {

	public static final int TEXTURE_INFO_RESOLUTION = 16;
	
	public static final Pixmap pixmap = new Pixmap(TEXTURE_INFO_RESOLUTION, TEXTURE_INFO_RESOLUTION, Format.RGBA8888);
	
	public static final VertexAttributes landMeshAttributes = new VertexAttributes(
			VertexAttribute.Position(), 
			VertexAttribute.Normal(),
			VertexAttribute.TexCoords(0));
	
	public static final StaticMeshPool landMeshPool = new StaticMeshPool(landMeshAttributes);
	
	public static final VertexAttributes treesMeshAttributes = new VertexAttributes(
			VertexAttribute.Position(), 
			VertexAttribute.Normal(), 
			VertexAttribute.TexCoords(0));
	
	public static final StaticMeshPool treesMeshPool = new StaticMeshPool(treesMeshAttributes);
	
	
	public static class CellData {
		// {@link net.mgsx.game.plugins.core.components.HeightFieldComponent} cache
		int width, height;
		public float[] values;
		public float[] extraValues;
		public Vector3 [] normals;
		public Texture infoTexture;
		
		// TODO bullet height field cache !
	}
	
	private static final Pool<CellData> cellDataPool = new Pool<CellData>(){
		@Override
		protected CellData newObject() {
			return new CellData();
		}
		
	};
	
	public static CellData obtainCellData(int width, int height){
		CellData data = null;
		while(data == null){
			data = cellDataPool.obtain();
			if(data.values == null){
				data.width = width;
				data.height = height;
				data.values = new float[width * height];
				data.extraValues = new float[(width+2) * (height+2)];
				data.normals = new Vector3[width * height];
				for(int i=0 ; i<data.normals.length ; i++) data.normals[i] = new Vector3();
				data.infoTexture = new Texture(pixmap.getWidth(), pixmap.getHeight(), pixmap.getFormat());
			}
			else if(data.width != width || data.height != height){
				// freeup
				if(data.infoTexture != null){
					data.infoTexture.dispose();
					data.infoTexture = null;
				}
				data = null;
			}
		}
		return data;
	}
	
	public static void freeCellData(CellData data){
		cellDataPool.free(data);
	}
	
}
