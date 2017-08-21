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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;

import net.mgsx.game.core.GamePipeline;
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
import net.mgsx.game.examples.openworld.utils.BulletBuilder;
import net.mgsx.game.plugins.bullet.system.BulletWorldSystem;
import net.mgsx.game.plugins.camera.model.POVModel;
import net.mgsx.game.plugins.core.components.HeightFieldComponent;
import net.mgsx.game.plugins.procedural.model.ClassicalPerlinNoise;

@Storable("ow.trees")
@EditableSystem
public class OpenWorldTreeSystem extends IteratingSystem implements PostInitializationListener
{
	@Inject OpenWorldEnvSystem environment;
	@Inject OpenWorldManagerSystem manager;
	@Inject OpenWorldGeneratorSystem generator;
	@Inject BulletWorldSystem bulletSystem;
	@Inject POVModel pov;

	/** how many tree per meter (max) */
	@Editable public float densityMax = 1f / 3f;
	
	@Editable public boolean collisions = false;
	
	private ClassicalPerlinNoise noise = new ClassicalPerlinNoise();
	
	Vector3 vp = new Vector3();
	
	@ShaderInfo(vs="shaders/tree.vert", fs="shaders/tree.frag", configs={"highQuality"})
	public static class TreeShader extends ShaderProgramManaged{
		
		@Uniform transient Matrix4 projTrans;
		@Uniform transient Vector3 sunDirection;
		@Uniform transient Color fogColor;
		
	}
	@Editable public TreeShader shader = new TreeShader();
	
	
	public OpenWorldTreeSystem() {
		super(Family.all(TreesComponent.class).get(), GamePipeline.RENDER);
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
	private BulletBuilder bulletBuilder = new BulletBuilder();
	
	private void buildTrees(Entity entity) {
		TreesComponent trees = TreesComponent.components.get(entity);
		HeightFieldComponent hfc = HeightFieldComponent.components.get(entity);
		
		// TODO instead of be based on land meshes, just generate forest based on
		// voronoi F1 algorithm
		
		if(collisions) bulletBuilder.beginStatic(hfc.position);
		
		builder.begin(OpenWorldPool.treesMeshAttributes, GL20.GL_TRIANGLES);
		
		noise.seed(generator.seedLayers[OpenWorldGeneratorSystem.SEED_LAYER_FLORA]);
		
		float zoneWidth = manager.worldCellScale;
		float zoneHeight = manager.worldCellScale;
		
		int width = MathUtils.floor(zoneWidth * densityMax);
		int height = MathUtils.floor(zoneHeight * densityMax);
		
		// random grid (x/y)
		// read height map for y
		// build tree
		Vector3 p = new Vector3();
		for(int y=0 ; y<height ; y++){
			for(int x=0 ; x<width ; x++) {
				
				float relX = (float)x / densityMax;
				float relY = (float)y / densityMax;
				
				// move tree a little
				float dx = noise.get(hfc.position.x + relX + 49, hfc.position.z + relY + 36) * 0.5f / densityMax;
				float dy = noise.get(hfc.position.x + relX + 7, hfc.position.z + relY + 3) * 0.5f / densityMax;
				
				// lerp position
				float fx = hfc.position.x + relX + dx;
				float fy = hfc.position.z + relY + dy;
				
				// check if flora is here
				float flora = generator.getFlora(fx, fy);
				if(flora < 0){
					// TODO another layer for seed
					float flora2 = generator.getFlora(fx * 30, fy * 30);
					if(-flora * flora2 < .2f) continue; // TODO config
				}
				
				
				// get altitude (don't generate trees in water)
				float base = generator.getAltitude(fx, fy);
				if(base < -.5f) continue; // TODO config trees in sand/water a little
				
				
				// XXX offset which doesn't work every time, needs to sample around to get min altitude
				buildTree(builder, p.set(fx, base - .1f, fy));
			}
		}
		
		// Size of mesh is not predictable but in the worse case it could crash,
		// we have to ensure this never happens by canceling some meshes or create necessary meshes
		// during generation above.
		// Since HFC size is constant (for now) size is predictable, we compute in order to avoid pool garbaging.
		int maxTrees = width * height;
		int verticesPerTree = 16; // 2 boxes
		int maxVertices = maxTrees * verticesPerTree;
		
		int indicesPerTree = 72; // 2 box, 6 quads/box, 6 indices/quad = 72
		int maxIndices = maxTrees * indicesPerTree;
		
		Mesh mesh = OpenWorldPool.treesMeshPool.obtain(maxVertices, maxIndices);
		
		builder.end(mesh);
		
		trees.mesh = mesh;
		
		if(collisions){
			btCollisionObject collisionObject = bulletBuilder.end();
			if(collisionObject != null){
				trees.collisionObject = collisionObject;
				trees.world = bulletSystem.collisionWorld;
				collisionObject.userData = trees;
				bulletSystem.collisionWorld.addCollisionObject(collisionObject);
			}
		}
	}
	
	// static pool
	private VertexInfo[] vertexInfo = new VertexInfo[]{
			new VertexInfo(), new VertexInfo(), new VertexInfo(), new VertexInfo(),
			new VertexInfo(), new VertexInfo(), new VertexInfo(), new VertexInfo()
	};
	
	@SuppressWarnings("deprecation")
	private void buildTree(MeshBuilder builder, Vector3 pos) {
		
		// TODO maybe another layer ? and correlated to flora layer ?
		float s = (noise.get(pos.x * 500, pos.z * 500)+1)*.5f;
		
		// base
		float r1 = MathUtils.lerp(.4f, .8f, s);
		// top
		float r2 = MathUtils.lerp(.2f, .6f, s);
		float h = MathUtils.lerp(3, 8, s);
		
		
		float random2 = (noise.get(pos.x * 10, pos.z * 10)+1)*.5f;

		// foliage half height
		float fh = MathUtils.lerp(1f, 4f, random2);
		// foliage radius
		float r3 = MathUtils.lerp(1f, 4f, 1-random2);
		
		// foliage center
		float h2 = h + fh;
		
		// TODO not box : cylinder without top/bottom polys
		builder.box(
				vertexInfo[0].setPos(vp.set(pos).add(-r1, 0, -r1)).setUV(0, 0).setNor(-1, 0, -1),
				vertexInfo[1].setPos(vp.set(pos).add(-r2, h, -r2)).setUV(0, 1).setNor(-1, 0, -1),
				vertexInfo[2].setPos(vp.set(pos).add(r1, 0, -r1)).setUV(0, 0).setNor(1, 0, -1),
				vertexInfo[3].setPos(vp.set(pos).add(r2, h, -r2)).setUV(0, 1).setNor(1, 0, -1),
				vertexInfo[4].setPos(vp.set(pos).add(-r1, 0, r1)).setUV(0, 0).setNor(-1, 0, 1),
				vertexInfo[5].setPos(vp.set(pos).add(-r2, h, r2)).setUV(0, 1).setNor(-1, 0, 1),
				vertexInfo[6].setPos(vp.set(pos).add(r1, 0, r1)).setUV(0, 0).setNor(1, 0, 1),
				vertexInfo[7].setPos(vp.set(pos).add(r2, h, r2)).setUV(0, 1).setNor(1, 0, 1));
		
		// TODO not box : deformed sphere without
		builder.box(
				vertexInfo[0].setPos(vp.set(pos).add(-r3, h2-fh, -r3)).setUV(1, 0).setNor(-1, -1, -1),
				vertexInfo[1].setPos(vp.set(pos).add(-r3, h2+fh, -r3)).setUV(1, 1).setNor(-1, 1, -1),
				vertexInfo[2].setPos(vp.set(pos).add(r3, h2-fh, -r3)).setUV(1, 0).setNor(1, -1, -1),
				vertexInfo[3].setPos(vp.set(pos).add(r3, h2+fh, -r3)).setUV(1, 1).setNor(1, 1, -1),
				vertexInfo[4].setPos(vp.set(pos).add(-r3, h2-fh, r3)).setUV(1, 0).setNor(-1, -1, 1),
				vertexInfo[5].setPos(vp.set(pos).add(-r3, h2+fh, r3)).setUV(1, 1).setNor(-1, 1, 1),
				vertexInfo[6].setPos(vp.set(pos).add(r3, h2-fh, r3)).setUV(1, 0).setNor(1, -1, 1),
				vertexInfo[7].setPos(vp.set(pos).add(r3, h2+fh, r3)).setUV(1, 1).setNor(1, 1, 1));
		
		// create the bullet shape
		if(collisions && h > 1){ // TODO config limit tree size
			float trunkRadius = (r1 + r2) / 2;
			bulletBuilder.beginShape()
			.position(pos.x, pos.y + h/2, pos.z)
			.cylinder(trunkRadius, h/2, trunkRadius)
			.endShape();
		}
	}

	@Override
	public void update(float deltaTime) {
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

		Gdx.gl.glCullFace(GL20.GL_FRONT); // XXX workaround
		
		
		shader.projTrans = pov.camera.combined;
		shader.sunDirection = environment.sunDirection;
		shader.fogColor = environment.fogColor;
		
		shader.begin();
		super.update(deltaTime);
		shader.end();
		
		Gdx.gl.glCullFace(GL20.GL_BACK); // XXX workaround
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TreesComponent trees = TreesComponent.components.get(entity);
		trees.mesh.render(shader.program(), GL20.GL_TRIANGLES);
	}

}
