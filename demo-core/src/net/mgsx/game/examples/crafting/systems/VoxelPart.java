package net.mgsx.game.examples.crafting.systems;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.ShapeCache;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class VoxelPart implements RenderableProvider
{
	private static class VoxelData
	{
		float material;
		float light;
	}
	
	private ShapeCache cache;
	public int SIZE, SIZE2;
	private VoxelData [] data;
	private Vector3 offset = new Vector3();
	private float cubeSize;
	private VertexInfo corner00 = new VertexInfo();
	private VertexInfo corner10 = new VertexInfo();
	private VertexInfo corner11 = new VertexInfo();
	private VertexInfo corner01 = new VertexInfo();
	
	private static final float [] BLEND = {0, 1, (float)Math.sqrt(2), (float)Math.sqrt(3)};
	
	/** normals near to far bottom left */
	private static final Vector3[] normals = new Vector3[]{
			new Vector3(),new Vector3(),new Vector3(),new Vector3(),
			new Vector3(),new Vector3(),new Vector3(),new Vector3()
	};
	private static final Color[] colors = new Color[]{
			new Color(),new Color(),new Color(),new Color(),
			new Color(),new Color(),new Color(),new Color()
	};
	
	public VoxelPart(int sIZE, float cubeSize) {
		super();
		SIZE = sIZE;
		this.cubeSize = cubeSize;
	}
	
	final static int NMATERIAL = 4;

	public void setSize(int newSize, float cubeSize){
		if(newSize != SIZE){
			SIZE = newSize;
			SIZE2 = SIZE+2;
			this.cubeSize = cubeSize;
		}
	}
	
	
	public void generateData(VoxelWorldSystem config, Vector3 worldPos){
		SIZE2 = SIZE+2;
		final int cubes = SIZE2*SIZE2*SIZE2;
		
		// System.out.println(offset);
		if(data == null || data.length != cubes)
			data = new VoxelData[cubes];
		
		Vector3 p = new Vector3();
		Vector3 world = new Vector3();
		Vector2 p2 = new Vector2();
		
		// max 2^15-1 : 11^3 * 24
		// if(SIZE > 11) SIZE = 11;
		
		offset.set(worldPos);
		
		float time = 0;
		int cxf=0;
		
		boolean debug = true;
		
		for(int z=0 ; z<SIZE2 ; z++){
			for(int x=0 ; x<SIZE2 ; x++){
				
				for(int y=0 ; y<SIZE2 ; y++)
				{
					world.set(x-1,y-1,z-1).scl(cubeSize).add(worldPos);
					int index = (z * SIZE2 + y) * SIZE2 + x;
					data[index] = new VoxelData();
					data[index].material = 0;
					if(debug){
						p.set(world).scl(1.f / config.freq2D);
						float f = (float)config.simplex.eval(p.x, p.z, config.time);
						if(world.y - f * config.planarity < 1 * cubeSize){
							
							// check new noise 4D
							p.set(world).scl(1.f / config.freq3D);
							float f2 = (float)config.simplex.eval(p.x, p.y, p.z, config.time) * .5f + .5f;
							
							if(f2 > config.lacunarity){
								p.set(world).scl(1.f / config.freq3D2);
								float f3 = (float)config.simplex.eval(p.x, p.y, p.z, config.time) * .5f + .5f;
								float fm = (f2 - config.lacunarity) / (1 - config.lacunarity);
//								if(MathUtils.floor((world.y - f * config.planarity)/cubeSize) == 0){
//									p.set(world).scl(1.f / config.freq3D2);
//									if(config.simplex.eval(p.x, p.z, config.time) > .5f)
//										data[index].material = 1;
//								}
//								else
									data[index].material = 1 + f3 * NMATERIAL; //1 + (int)(f2  * (NMATERIAL));
							}
							else
								cxf++;
						}
						p.set(world).scl(1.f / config.freqLight);
						float fl = (float)config.simplex.eval(p.x, p.y, p.z, config.time) * .5f + .5f;
						data[index].light = fl;
					}
					
//					float n2 = (config.noise.apply(p2.set(world.x, world.z).scl(config.freq2D)) - .5f) * 2 * config.planarity;
//					
//					float n = config.noise.apply(p.set(world).scl(config.freq3D));
//					// float m = config.noise.apply(p2.set(world.x, world.z).scl(config.freq3D2));
//					float m = config.noise.apply(p.set(world).scl(config.freq3D2));
//					float height = world.y - n2;
////					if( n<config.lacunarity && height<0  || n<config.lacunarityTop &&world.y-.5f>n2){
//					if(MathUtils.floor(height / cubeSize) == 0){
//						if(config.noise.apply(p.set(world).scl(16))>.99f)
//						data[index].material = 2;
//					}else if(height < 0){
//						data[index].material = 1; //XXX + (int)(m * NMATERIAL);
//					}
//					data[index].light = config.noise.apply(p.set(world).scl(config.freqLight));
				}
			}
		}
	}
	
	private Vector3 setNormal(Vector3 normal, int x, int y, int z, boolean hx, boolean hy, boolean hz){
		int ix = hx ? x : 0;
		int iy = hy ? y : 0;
		int iz = hz ? z : 0;
		int sum = Math.abs(ix) + Math.abs(iy) + Math.abs(iz);
		float len = BLEND[sum];
		normal.x = ix * len;
		normal.y = iy * len;
		normal.z = iz * len;
		return normal;
	}
	
	private static Color[] TINTS = new Color[]{
			Color.WHITE, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW
	};
	
	private Color setColor(Color color, float material, float light) {
		Color c = TINTS[(int)(material-1)%TINTS.length];
		color.set(Color.WHITE).mul(0.2f + light * .8f); // XXX
		color.a = 1;
		return color;
	}
	
	private VoxelData data(int x, int y, int z){
		int index = ((z+1) * SIZE2 + y+1) * SIZE2 + x+1;
		return data[index];
	}
	
	public void generateMesh(float cubeSize, float cubeSpace, Texture texture)
	{
		final int cubes = SIZE*SIZE*SIZE;
		
		int minVertex = 6 * 4 * cubes;
		int minIndices = 6 * 6 * cubes;
		if(minVertex > 32768) minVertex = 32768;
		if(minIndices > 32768) minIndices = 32768;
		
		int maxCubes = 32768 / 36;
		
		final boolean smooth = true;
		
		final boolean color = true;
		
		float tsize = 1.f / 16.f;
		
		int dim = (int)MathUtils.log2(SIZE);
		
		if(cache == null)
			cache = new ShapeCache(minVertex, minIndices, new VertexAttributes(
				VertexAttribute.Position(), 
				VertexAttribute.Normal(), 
				VertexAttribute.TexCoords(0),
				VertexAttribute.ColorPacked()), GL20.GL_TRIANGLES);
		ShapeCache r = cache;
		MeshPartBuilder mpb = r.begin(GL20.GL_TRIANGLES);
		int cnt = 0;
		for(int z=0 ; z<SIZE ; z++)
			for(int y=0 ; y<SIZE ; y++)
				for(int x=0 ; x<SIZE ; x++){
					float value = data(x,y,z).material;
					if(value > 0 && cnt < maxCubes){
						
						boolean holeTop = data(x,y+1,z).material <= 0;
						boolean holeBottom = data(x,y-1,z).material <= 0;
						boolean holeLeft = data(x-1,y,z).material <= 0;
						boolean holeRight = data(x+1,y,z).material <= 0;
						boolean holeBack = data(x,y,z+1).material <= 0;
						boolean holeFront = data(x,y,z-1).material <= 0;
						
						if(holeTop || holeBottom || holeLeft || holeRight || holeBack || holeFront){
							
							if(color){
								
								setColor(colors[0], value, data(x,y,z).light);
								setColor(colors[1], value, data(x+1,y,z).light);
								setColor(colors[2], value, data(x,y+1,z).light);
								setColor(colors[3], value, data(x+1,y+1,z).light);
								setColor(colors[4], value, data(x,y,z+1).light);
								setColor(colors[5], value, data(x+1,y,z+1).light);
								setColor(colors[6], value, data(x,y+1,z+1).light);
								setColor(colors[7], value, data(x+1,y+1,z+1).light);
							}
							
							if(smooth){
								// compute normals smooth
								
								// near bottom left
								setNormal(normals[0], -1, -1, -1, holeLeft, holeBottom, holeFront);
								setNormal(normals[1],  1, -1, -1, holeRight, holeBottom, holeFront);
								setNormal(normals[2], -1,  1, -1, holeLeft, holeTop, holeFront);
								setNormal(normals[3],  1,  1, -1, holeRight, holeTop, holeFront);
								setNormal(normals[4], -1, -1,  1, holeLeft, holeBottom, holeBack);
								setNormal(normals[5],  1, -1,  1, holeRight, holeBottom, holeBack);
								setNormal(normals[6], -1,  1,  1, holeLeft, holeTop, holeBack);
								setNormal(normals[7],  1,  1,  1, holeRight, holeTop, holeBack);
							}
							
							int material =  (int)(value-1);
							
							float fx1 = x * cubeSpace + offset.x;
							float fy1 = y * cubeSpace + offset.y;
							float fz1 = z * cubeSpace + offset.z;
							
							float fx2 = fx1 + cubeSpace;
							float fy2 = fy1 + cubeSpace;
							float fz2 = fz1 + cubeSpace;
							
							float uoffset = material * tsize;
							float u1;
							float v1;
							float u2;
							float v2;
							
							if(holeTop){
								u1 = uoffset;
								v1 = 1.f / 16.f;
								u2 = u1+tsize;
								v2 = v1+tsize;
							}else{
								u1 = uoffset;
								v1 = 2 * tsize;
								u2 = u1+tsize;
								v2 = v1+tsize;
								
							}
							
							// top
							if(holeTop){
								
								float ut1 = uoffset;
								float vt1 = 0;
								float ut2 = ut1+tsize;
								float vt2 = vt1+tsize;
								
								corner00.setPos(fx1, fy2, fz1);
								corner01.setPos(fx2, fy2, fz1);
								corner10.setPos(fx1, fy2, fz2);
								corner11.setPos(fx2, fy2, fz2);
								if(color){
									corner00.setCol(colors[2]);
									corner01.setCol(colors[3]);
									corner10.setCol(colors[6]);
									corner11.setCol(colors[7]);
								}
								if(smooth){
									corner00.setNor(normals[2]);
									corner01.setNor(normals[3]);
									corner10.setNor(normals[6]);
									corner11.setNor(normals[7]);
								}else{
									corner00.setNor(0, 1, 0);
									corner01.setNor(0, 1, 0);
									corner10.setNor(0, 1, 0);
									corner11.setNor(0, 1, 0);
								}
								corner00.setUV(ut1, vt1);
								corner01.setUV(ut1, vt2);
								corner10.setUV(ut2, vt1);
								corner11.setUV(ut2, vt2);
								
								mpb.rect(corner00, corner10, corner11, corner01);
							}
							
							// bottom
							if(holeBottom){
								
								float ub1 = uoffset;
								float vb1 = 2*tsize;
								float ub2 = ub1+tsize;
								float vb2 = vb1+tsize;
								
								corner00.setPos(fx2, fy1, fz1);
								corner01.setPos(fx1, fy1, fz1);
								corner10.setPos(fx2, fy1, fz2);
								corner11.setPos(fx1, fy1, fz2);
								if(smooth){
									corner00.setNor(normals[1]);
									corner01.setNor(normals[0]);
									corner10.setNor(normals[5]);
									corner11.setNor(normals[4]);
								}else{
									corner00.setNor(0, -1, 0);
									corner01.setNor(0, -1, 0);
									corner10.setNor(0, -1, 0);
									corner11.setNor(0, -1, 0);
								}
								if(color){
									corner00.setCol(colors[1]);
									corner01.setCol(colors[0]);
									corner10.setCol(colors[5]);
									corner11.setCol(colors[4]);
								}
								corner00.setUV(ub1, vb1);
								corner01.setUV(ub1, vb2);
								corner10.setUV(ub2, vb1);
								corner11.setUV(ub2, vb2);
								
								mpb.rect(corner00, corner10, corner11, corner01);
							}
							
							// left
							if(holeLeft){
								
								corner00.setPos(fx1, fy1, fz2);
								corner01.setPos(fx1, fy1, fz1);
								corner10.setPos(fx1, fy2, fz2);
								corner11.setPos(fx1, fy2, fz1);
								if(smooth){
									corner00.setNor(normals[4]);
									corner01.setNor(normals[0]);
									corner10.setNor(normals[6]);
									corner11.setNor(normals[2]);
								}else{
									corner00.setNor(-1, 0, 0);
									corner01.setNor(-1, 0, 0);
									corner10.setNor(-1, 0, 0);
									corner11.setNor(-1, 0, 0);
								}
								if(color){
									corner00.setCol(colors[4]);
									corner01.setCol(colors[0]);
									corner10.setCol(colors[6]);
									corner11.setCol(colors[2]);
								}
								corner00.setUV(u1, v2);
								corner01.setUV(u2, v2);
								corner10.setUV(u1, v1);
								corner11.setUV(u2, v1);
								
								mpb.rect(corner00, corner10, corner11, corner01);
								
								
							}
							// right
							if(holeRight){
								
								corner00.setPos(fx2, fy1, fz1);
								corner01.setPos(fx2, fy1, fz2);
								corner10.setPos(fx2, fy2, fz1);
								corner11.setPos(fx2, fy2, fz2);
								if(smooth){
									corner00.setNor(normals[1]);
									corner01.setNor(normals[5]);
									corner10.setNor(normals[3]);
									corner11.setNor(normals[7]);
								}else{
									corner00.setNor(1, 0, 0);
									corner01.setNor(1, 0, 0);
									corner10.setNor(1, 0, 0);
									corner11.setNor(1, 0, 0);
								}
								if(color){
									corner00.setCol(colors[1]);
									corner01.setCol(colors[5]);
									corner10.setCol(colors[3]);
									corner11.setCol(colors[7]);
								}
								corner00.setUV(u1, v2);
								corner01.setUV(u2, v2);
								corner10.setUV(u1, v1);
								corner11.setUV(u2, v1);
								
								mpb.rect(corner00, corner10, corner11, corner01);
								
								
							}
							
							// front
							if(holeFront){
								
								corner00.setPos(fx1, fy1, fz1);
								corner01.setPos(fx2, fy1, fz1);
								corner10.setPos(fx1, fy2, fz1);
								corner11.setPos(fx2, fy2, fz1);
								if(smooth){
									corner00.setNor(normals[0]);
									corner01.setNor(normals[1]);
									corner10.setNor(normals[2]);
									corner11.setNor(normals[3]);
								}else{
									corner00.setNor(0, 0, -1);
									corner01.setNor(0, 0, -1);
									corner10.setNor(0, 0, -1);
									corner11.setNor(0, 0, -1);
								}
								if(color){
									corner00.setCol(colors[0]);
									corner01.setCol(colors[1]);
									corner10.setCol(colors[2]);
									corner11.setCol(colors[3]);
								}
								corner00.setUV(u1, v2);
								corner01.setUV(u2, v2);
								corner10.setUV(u1, v1);
								corner11.setUV(u2, v1);
								
								mpb.rect(corner00, corner10, corner11, corner01);
								
								
							}
							
							// back
							if(holeBack){
								
								corner00.setPos(fx2, fy1, fz2);
								corner01.setPos(fx1, fy1, fz2);
								corner10.setPos(fx2, fy2, fz2);
								corner11.setPos(fx1, fy2, fz2);
								if(smooth){
									corner00.setNor(normals[5]);
									corner01.setNor(normals[4]);
									corner10.setNor(normals[7]);
									corner11.setNor(normals[6]);
								}else{
									corner00.setNor(0, 0, 1);
									corner01.setNor(0, 0, 1);
									corner10.setNor(0, 0, 1);
									corner11.setNor(0, 0, 1);
								}
								if(color){
									corner00.setCol(colors[5]);
									corner01.setCol(colors[4]);
									corner10.setCol(colors[7]);
									corner11.setCol(colors[6]);
								}
								corner00.setUV(u1, v2);
								corner01.setUV(u2, v2);
								corner10.setUV(u1, v1);
								corner11.setUV(u2, v1);
								
								mpb.rect(corner00, corner10, corner11, corner01);
								
								
							}
							
							// BoxShapeBuilder.build(mpb, x * cubeSpace + offset.x, y* cubeSpace + offset.y, z* cubeSpace + offset.z, cubeSize, cubeSize, cubeSize);
							cnt++;
						}
					}
				}
		r.end();
		
		r.getMaterial().set(new TextureAttribute(TextureAttribute.Diffuse, texture));
		// System.out.println(cnt + "/" + cubes + "/" + maxCubes);
		
		cache = r;
	}

	

	@Override
	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		if(cache != null){
			cache.getRenderables(renderables, pool);
		}
	}

	public void reset() {
//		if(cache != null)
//			cache.dispose();
		cache = null;
	}
	
	
}
