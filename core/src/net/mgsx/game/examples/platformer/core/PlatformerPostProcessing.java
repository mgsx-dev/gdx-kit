package net.mgsx.game.examples.platformer.core;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GameEngine;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.g3d.G3DModel;

// TODO refactor things up here ... FBO ... shaders ...
public class PlatformerPostProcessing
{
	private FrameBuffer fbo, fbo2;
	private Sprite screenSprite;
	private SpriteBatch batch;
	private ShaderProgram postProcessShader, flatProgram;
	private float time;
	private int timeLocation;
	private int worldLocation;
	private int frequencyLocation;
	private int rateLocation;
	private int texture1Location;
	private Vector2 world = new Vector2();
	private ModelBatch modelBatch;
	Renderable renderable = new Renderable();
	Shader flatShader;
	
	private Array<ModelInstance> waterModels = new Array<ModelInstance>();
	
	public class Settings{
		public float speed = 1.5f;
		public float frequency = 100f;
		public float rate = 0.01f;
		public boolean enabled = false;
	}
	
	public Settings settings = new Settings();
	
	public PlatformerPostProcessing(final GameEngine engine) 
	{
		// postProcessShader = SpriteBatch.createDefaultShader();
		postProcessShader = new ShaderProgram(
				Gdx.files.classpath("net/mgsx/game/examples/platformer/core/water-vertex.glsl"),
				Gdx.files.classpath("net/mgsx/game/examples/platformer/core/water-fragment.glsl"));
		postProcessShader.begin();
		if(!postProcessShader.isCompiled()){
			System.err.println(postProcessShader.getLog());
		}
		postProcessShader.end();
		
		
		timeLocation = postProcessShader.getUniformLocation("u_time");
		worldLocation = postProcessShader.getUniformLocation("u_world");
		frequencyLocation = postProcessShader.getUniformLocation("u_frequency");
		rateLocation = postProcessShader.getUniformLocation("u_rate");
		texture1Location = postProcessShader.getUniformLocation("u_texture1");
		
		flatProgram = new ShaderProgram(
				Gdx.files.classpath("net/mgsx/game/examples/platformer/core/flat-vertex.glsl"),
				Gdx.files.classpath("net/mgsx/game/examples/platformer/core/flat-fragment.glsl"));
		flatProgram.begin();
		if(!flatProgram.isCompiled()){
			System.err.println(flatProgram.getLog());
		}
		flatProgram.end();
		
		
		flatShader = new BaseShader() {
			@Override
			public void init() {
				register(DefaultShader.Inputs.projTrans, DefaultShader.Setters.projViewWorldTrans);
				// TODO Auto-generated method stub
				Renderable renderable = new Renderable();
				MeshBuilder mb = new MeshBuilder();
				VertexAttributes attributes = new VertexAttributes(VertexAttribute.Position());
				mb.begin(attributes);
				
				
				renderable.meshPart.mesh = mb.end();
				init(flatProgram, renderable);
				program = flatProgram;
			}
			
			@Override
			public int compareTo(Shader other) {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public boolean canRender(Renderable instance) {
				// TODO Auto-generated method stub
				return true;
			}
		};
		flatShader.init();
		
		modelBatch = new ModelBatch(new ShaderProvider() {
			
			@Override
			public Shader getShader(Renderable renderable) {
				return flatShader;
			}
			
			@Override
			public void dispose() {
				
			}
		});
		
		
		batch = new SpriteBatch(1, postProcessShader);
		
		engine.entityEngine.addEntityListener(Family.all(WaterZone.class, G3DModel.class).get(), new EntityListener() {
			@Override
			public void entityRemoved(Entity entity) {
				waterModels.removeValue(entity.getComponent(G3DModel.class).modelInstance, true);
			}
			@Override
			public void entityAdded(Entity entity) {
				waterModels.add(entity.getComponent(G3DModel.class).modelInstance);
			}
		});
		
		engine.entityEngine.addSystem(new EntitySystem(GamePipeline.BEFORE_RENDER) {
			@Override
			public void update(float deltaTime) 
			{
				if(!settings.enabled) return;
				validate();
				
				fbo.begin();
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			}
		});
		engine.entityEngine.addSystem(new EntitySystem(GamePipeline.AFTER_RENDER) {
			@Override
			public void update(float deltaTime) 
			{
				if(!settings.enabled) return;
				
				fbo.end();
				
				fbo2.begin();
				Gdx.gl.glClearColor(0,0,0, 0);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
				modelBatch.begin(engine.camera);
				modelBatch.render(waterModels);
				modelBatch.end();
				fbo2.end();
				
				time += deltaTime * settings.speed;
				
				float d = engine.camera.unproject(new Vector3()).z;
				Vector3 v = engine.camera.project(new Vector3(0,0,d));
				world.set(-v.x / Gdx.graphics.getWidth(), -v.y / Gdx.graphics.getHeight());
				
				batch.disableBlending();
				batch.begin();
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
				
				Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
				fbo.getColorBufferTexture().bind(GL20.GL_TEXTURE0);
				Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1);
				fbo2.getColorBufferTexture().bind(GL20.GL_TEXTURE1);
				Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
				
				postProcessShader.setUniformi(texture1Location, 1);
				postProcessShader.setUniformf(timeLocation, time);
				postProcessShader.setUniformf(worldLocation, world);
				postProcessShader.setUniformf(frequencyLocation, settings.frequency);
				postProcessShader.setUniformf(rateLocation, settings.rate);
				screenSprite.draw(batch);
				batch.end();
			}
		});
	}
	
	private void validate()
	{
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();
		if(fbo == null || fbo.getWidth() != w || fbo.getHeight() != h)
		{
			if(fbo != null){
				fbo.dispose();
				fbo2.dispose();
			}
			
			fbo = new FrameBuffer(Format.RGBA8888, w, h, true);
			fbo2 = new FrameBuffer(Format.RGBA8888, w, h, true);
			screenSprite = new Sprite(fbo.getColorBufferTexture(), w, h);
			screenSprite.setFlip(false, true);
			
			batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			batch.setProjectionMatrix(batch.getProjectionMatrix()); // XXX necessary ?
		}
		
	}
	
}
