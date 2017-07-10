package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.examples.openworld.components.TreesComponent;
import net.mgsx.game.examples.openworld.utils.RandomLookup;
import net.mgsx.game.plugins.core.components.HeightFieldComponent;

public class OpenWorldTreeSystem extends IteratingSystem
{
	@Inject OpenWorldEnvSystem environment;

	private GameScreen screen;

	private RandomLookup rnd;
	
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
		
		shader = new ShaderProgram(
				Gdx.files.internal("shaders/tree.vert"), 
				Gdx.files.internal("shaders/tree.frag"));
		if(!shader.isCompiled()){
			throw new GdxRuntimeException(shader.getLog());
		}
		
		rnd = new RandomLookup(new RandomXS128(0xdeaddead), 16, 16);
	}
	
	private void buildTrees(Entity entity) {
		TreesComponent trees = TreesComponent.components.get(entity);
		HeightFieldComponent hfc = HeightFieldComponent.components.get(entity);
		
		MeshBuilder builder = new MeshBuilder();
		builder.begin(new VertexAttributes(
				VertexAttribute.Position(), 
				VertexAttribute.Normal(), 
				VertexAttribute.TexCoords(0)),
				GL20.GL_TRIANGLES);
		
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
				
				if(base > 0){
					buildTree(builder, p.set(hfc.position).add(fx, base - .1f, fy));
				}
				
			}
		}
		Mesh mesh = builder.end();
		
		trees.mesh = mesh;
		
		
	}
	
	Vector3 vp = new Vector3();
	private ShaderProgram shader;
	
	@SuppressWarnings("deprecation")
	private void buildTree(MeshBuilder builder, Vector3 pos) {
		
		float s = .1f * (1 + .8f * rnd.getSigned((int)(pos.x * 500), (int)(pos.z * 500)));
		
		float r1 = 0.7f * s;
		float r2 = 0.4f * s;
		float h = 7f * s;
		float r3 = 1.5f * s;
		
		builder.box(
				new VertexInfo().setPos(vp.set(pos).add(-r1, 0, -r1)).setUV(0, 0).setNor(-1, 0, -1),
				new VertexInfo().setPos(vp.set(pos).add(-r2, h, -r2)).setUV(0, 1).setNor(-1, 0, -1),
				new VertexInfo().setPos(vp.set(pos).add(r1, 0, -r1)).setUV(0, 0).setNor(1, 0, -1),
				new VertexInfo().setPos(vp.set(pos).add(r2, h, -r2)).setUV(0, 1).setNor(1, 0, -1),
				new VertexInfo().setPos(vp.set(pos).add(-r1, 0, r1)).setUV(0, 0).setNor(-1, 0, 1),
				new VertexInfo().setPos(vp.set(pos).add(-r2, h, r2)).setUV(0, 1).setNor(-1, 0, 1),
				new VertexInfo().setPos(vp.set(pos).add(r1, 0, r1)).setUV(0, 0).setNor(1, 0, 1),
				new VertexInfo().setPos(vp.set(pos).add(r2, h, r2)).setUV(0, 1).setNor(1, 0, 1));
		
		builder.box(
				new VertexInfo().setPos(vp.set(pos).add(-r3, h-r3, -r3)).setUV(1, 0).setNor(-1, -1, -1),
				new VertexInfo().setPos(vp.set(pos).add(-r3, h+r3, -r3)).setUV(1, 1).setNor(-1, 1, -1),
				new VertexInfo().setPos(vp.set(pos).add(r3, h-r3, -r3)).setUV(1, 0).setNor(1, -1, -1),
				new VertexInfo().setPos(vp.set(pos).add(r3, h+r3, -r3)).setUV(1, 1).setNor(1, 1, -1),
				new VertexInfo().setPos(vp.set(pos).add(-r3, h-r3, r3)).setUV(1, 0).setNor(-1, -1, 1),
				new VertexInfo().setPos(vp.set(pos).add(-r3, h+r3, r3)).setUV(1, 1).setNor(-1, 1, 1),
				new VertexInfo().setPos(vp.set(pos).add(r3, h-r3, r3)).setUV(1, 0).setNor(1, -1, 1),
				new VertexInfo().setPos(vp.set(pos).add(r3, h+r3, r3)).setUV(1, 1).setNor(1, 1, 1));
		
	}

	@Override
	public void update(float deltaTime) {
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		shader.begin();
		shader.setUniformMatrix("u_projTrans", screen.camera.combined);
		shader.setUniformf("u_sunDirection", environment.sunDirection);
		shader.setUniformf("u_fogColor", environment.fogColor);
		super.update(deltaTime);
		shader.end();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TreesComponent trees = TreesComponent.components.get(entity);
		trees.mesh.render(shader, GL20.GL_TRIANGLES);
	}
	
}
