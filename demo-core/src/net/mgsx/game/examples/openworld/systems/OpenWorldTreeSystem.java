package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.PostInitializationListener;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.helpers.shaders.ShaderInfo;
import net.mgsx.game.core.helpers.shaders.ShaderProgramManaged;
import net.mgsx.game.core.helpers.shaders.Uniform;
import net.mgsx.game.examples.openworld.components.TreesComponent;
import net.mgsx.game.examples.openworld.model.OpenWorldPool;
import net.mgsx.game.examples.openworld.model.OpenWorldRuntimeSettings;
import net.mgsx.game.examples.openworld.utils.RandomLookup;
import net.mgsx.game.plugins.core.components.HeightFieldComponent;

@Storable("ow.trees")
@EditableSystem
public class OpenWorldTreeSystem extends IteratingSystem implements PostInitializationListener
{
	@Inject OpenWorldEnvSystem environment;

	private GameScreen screen;

	private RandomLookup rnd;
	
	Vector3 vp = new Vector3();
	
	@ShaderInfo(vs="shaders/tree.vert", fs="shaders/tree.frag", configs={"highQuality"})
	public static class TreeShader extends ShaderProgramManaged{
		
		@Uniform transient Matrix4 projTrans;
		@Uniform transient Vector3 sunDirection;
		@Uniform transient Color fogColor;
		
	}
	@Editable public TreeShader shader = new TreeShader();
	
	
	public OpenWorldTreeSystem(GameScreen screen) {
		super(Family.all(TreesComponent.class).get(), GamePipeline.RENDER);
		this.screen = screen;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(Family.all(TreesComponent.class, HeightFieldComponent.class).get(), new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				
			}
			
			@Override
			public void entityAdded(Entity entity) {
				buildTrees(entity);
			}
		});
		
		rnd = new RandomLookup(new RandomXS128(0xdeaddead), 16, 16);
	}
	
	@Override
	public void onPostInitialization() {
		if(OpenWorldRuntimeSettings.highQuality){
			shader.setConfig("highQuality");
		}else{
			shader.setConfig();
		}
	}
	
	private MeshBuilder builder = new MeshBuilder();
	
	private void buildTrees(Entity entity) {
		TreesComponent trees = TreesComponent.components.get(entity);
		HeightFieldComponent hfc = HeightFieldComponent.components.get(entity);
		
		
		builder.begin(OpenWorldPool.treesMeshAttributes, GL20.GL_TRIANGLES);
		
		// random grid (x/y)
		// read height map for y
		// build tree
		Vector3 p = new Vector3();
		for(int y=0 ; y<hfc.height ; y++){
			for(int x=0 ; x<hfc.width ; x++) {
				
				float dx = rnd.get(x, y) * 0.5f;
				float dy = rnd.get(x + 7, y + 3) * 0.5f;
				
				// lerp position
				float fx = x + dx;
				float fy = y + dy;
				
				int ix = MathUtils.floor(fx);
				int iy = MathUtils.floor(fy);
				float rx = fx - ix;
				float ry = fy - iy;
				
				float base00 = hfc.extraValues[(iy+1)*(hfc.width+2)+ix+1];
				float base10 = hfc.extraValues[(iy+1)*(hfc.width+2)+ix+2];
				float base01 = hfc.extraValues[(iy+2)*(hfc.width+2)+ix+1];
				float base11 = hfc.extraValues[(iy+2)*(hfc.width+2)+ix+2];
				
				float base = MathUtils.lerp(
						MathUtils.lerp(base00, base10, rx),
						MathUtils.lerp(base01, base11, rx), ry);
				
				if(base > 0 && base < .5f){
					buildTree(builder, p.set(hfc.position).add(fx, base - .1f, fy));
				}
				
			}
		}
		
		// Size of mesh is not predictable but in the worse case it could crash,
		// we have to ensure this never happens by canceling some meshes or create necessary meshes
		// during generation above.
		// Since HFC size is constant (for now) size is predictable, we compute in order to avoid pool garbaging.
		int maxTrees = hfc.height * hfc.width;
		int verticesPerTree = 16; // 2 boxes
		int maxVertices = maxTrees * verticesPerTree;
		
		int indicesPerTree = 72; // 2 box, 6 quads/box, 6 indices/quad = 72
		int maxIndices = maxTrees * indicesPerTree;
		
		Mesh mesh = OpenWorldPool.treesMeshPool.obtain(maxVertices, maxIndices);
		
		builder.end(mesh);
		
		trees.mesh = mesh;
	}
	
	// static pool
	private VertexInfo[] vertexInfo = new VertexInfo[]{
			new VertexInfo(), new VertexInfo(), new VertexInfo(), new VertexInfo(),
			new VertexInfo(), new VertexInfo(), new VertexInfo(), new VertexInfo()
	};
	
	@SuppressWarnings("deprecation")
	private void buildTree(MeshBuilder builder, Vector3 pos) {
		
		float s = .2f * (1 + .8f * rnd.getSigned((int)(pos.x * 500), (int)(pos.z * 500)));
		
		float r1 = 0.7f * s;
		float r2 = 0.4f * s;
		float h = 7f * s;
		float r3 = 1.5f * s;
		
		builder.box(
				vertexInfo[0].setPos(vp.set(pos).add(-r1, 0, -r1)).setUV(0, 0).setNor(-1, 0, -1),
				vertexInfo[1].setPos(vp.set(pos).add(-r2, h, -r2)).setUV(0, 1).setNor(-1, 0, -1),
				vertexInfo[2].setPos(vp.set(pos).add(r1, 0, -r1)).setUV(0, 0).setNor(1, 0, -1),
				vertexInfo[3].setPos(vp.set(pos).add(r2, h, -r2)).setUV(0, 1).setNor(1, 0, -1),
				vertexInfo[4].setPos(vp.set(pos).add(-r1, 0, r1)).setUV(0, 0).setNor(-1, 0, 1),
				vertexInfo[5].setPos(vp.set(pos).add(-r2, h, r2)).setUV(0, 1).setNor(-1, 0, 1),
				vertexInfo[6].setPos(vp.set(pos).add(r1, 0, r1)).setUV(0, 0).setNor(1, 0, 1),
				vertexInfo[7].setPos(vp.set(pos).add(r2, h, r2)).setUV(0, 1).setNor(1, 0, 1));
		
		builder.box(
				vertexInfo[0].setPos(vp.set(pos).add(-r3, h-r3, -r3)).setUV(1, 0).setNor(-1, -1, -1),
				vertexInfo[1].setPos(vp.set(pos).add(-r3, h+r3, -r3)).setUV(1, 1).setNor(-1, 1, -1),
				vertexInfo[2].setPos(vp.set(pos).add(r3, h-r3, -r3)).setUV(1, 0).setNor(1, -1, -1),
				vertexInfo[3].setPos(vp.set(pos).add(r3, h+r3, -r3)).setUV(1, 1).setNor(1, 1, -1),
				vertexInfo[4].setPos(vp.set(pos).add(-r3, h-r3, r3)).setUV(1, 0).setNor(-1, -1, 1),
				vertexInfo[5].setPos(vp.set(pos).add(-r3, h+r3, r3)).setUV(1, 1).setNor(-1, 1, 1),
				vertexInfo[6].setPos(vp.set(pos).add(r3, h-r3, r3)).setUV(1, 0).setNor(1, -1, 1),
				vertexInfo[7].setPos(vp.set(pos).add(r3, h+r3, r3)).setUV(1, 1).setNor(1, 1, 1));
		
	}

	@Override
	public void update(float deltaTime) {
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		
		shader.projTrans = screen.camera.combined;
		shader.sunDirection = environment.sunDirection;
		shader.fogColor = environment.fogColor;
		
		shader.begin();
		super.update(deltaTime);
		shader.end();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TreesComponent trees = TreesComponent.components.get(entity);
		trees.mesh.render(shader.program(), GL20.GL_TRIANGLES);
	}

}
