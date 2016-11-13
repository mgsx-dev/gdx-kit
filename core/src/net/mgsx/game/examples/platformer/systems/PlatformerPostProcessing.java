package net.mgsx.game.examples.platformer.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
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

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.examples.platformer.components.WaterZone;
import net.mgsx.game.plugins.g3d.components.G3DModel;

// TODO refactor things up here ... FBO ... shaders ...
@EditableSystem("Post Processing Effects")
public class PlatformerPostProcessing extends EntitySystem
{
	private FrameBuffer fbo, fbo2, blurA, blurB;
	private Sprite screenSprite;
	private SpriteBatch batch;
	private ShaderProgram postProcessShader, flatProgram, blurXProgram;
	private float time;
	private int timeLocation;
	private int worldLocation;
	private int frequencyLocation;
	private int rateLocation;
	private int texture1Location, texture2Location;
	private Vector2 world = new Vector2();
	private ModelBatch modelBatch;
	Renderable renderable = new Renderable();
	Shader flatShader;
	private GameScreen engine;
	
	public PlatformerPostProcessing(GameScreen engine) {
		super(GamePipeline.AFTER_RENDER);
		loadShaders();
		this.engine = engine;
	}
	
	public FrameBuffer getMainTarget(){
		return fbo;
	}
	
	
	private Family waterEntity = Family.all(G3DModel.class, WaterZone.class).get();
	private Family nonwaterEntity = Family.all(G3DModel.class).exclude(WaterZone.class).get();
	
//	private Array<ModelInstance> waterModels = new Array<ModelInstance>();
//	private Array<ModelInstance> nonWaterModels = new Array<ModelInstance>();
	
	
	public class Settings
	{
		@Editable public float speed = 1.5f;
		@Editable public float frequency = 100f;
		@Editable public float rate = 0.01f;
		@Editable public boolean enabled = false;
		@Editable public boolean blur = true;
		@Editable public float nearStart = -3;
		@Editable public float nearEnd = -5;
		@Editable public float farStart = 13;
		@Editable public float farEnd = 17f;
		
		@Editable public boolean debugDepth;
		@Editable public boolean debugConfusion;
		@Editable public boolean debugLimits;
		
	}
	
	@Editable public Settings settings = new Settings();
	
	@Override
	public void update(float deltaTime) 
	{
		if(!settings.enabled) return;
		
		fbo.end();
		
		// TODO find a way to see FBO (FBO stack ? push/pop/bind ... etc ...
		if(!settings.debugDepth)
			fbo2.begin();
		
		Gdx.gl.glClearColor(0,0,0,0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		modelBatch.begin(engine.getRenderCamera());
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glDepthMask(true);
		
		flatProgram.begin();
		flatProgram.setUniformf("waterFactor", 1.f);
		
		
		for(Entity e : getEngine().getEntitiesFor(waterEntity)){
			modelBatch.render(e.getComponent(G3DModel.class).modelInstance, flatShader);
		}
		engine.getRenderCamera().near = 1f;
		engine.getRenderCamera().far = 200.f;
		modelBatch.flush();
		flatProgram.end();
		flatProgram.begin();
		
		//
		//modelBatch.render(waterModels);
		flatProgram.setUniformf("waterFactor", 0.0f);
		//modelBatch.render(nonWaterModels);
		for(Entity e : getEngine().getEntitiesFor(nonwaterEntity)){
			modelBatch.render(e.getComponent(G3DModel.class).modelInstance, flatShader);
		}
		flatProgram.end();
		modelBatch.end();
		
		if(!settings.debugDepth)
			fbo2.end();
		
		if(settings.debugDepth){
			return;
		}
		
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		if(settings.blur )
		{
			blurA.begin();
			batch.setShader(blurXProgram);
			batch.begin();
			blurXProgram.setUniformf("dir", new Vector2(1.5f / (float)Gdx.graphics.getWidth(),0));
			batch.draw(fbo.getColorBufferTexture(), 0, 0);
			batch.end();
			blurA.end();
			
			blurB.begin();
			batch.setShader(blurXProgram);
			batch.begin();
			blurXProgram.setUniformf("dir", new Vector2(0, 1.5f / (float)Gdx.graphics.getHeight()));
			batch.draw(blurA.getColorBufferTexture(), 0, 0);
			batch.end();
			blurB.end();
		}
		
		time += deltaTime * settings.speed;
		
		float d = engine.getRenderCamera().unproject(new Vector3()).z;
		d = 4;
		Vector3 v = engine.getRenderCamera().project(new Vector3(0,0,d));
		world.set(-v.x / Gdx.graphics.getWidth(), -v.y / Gdx.graphics.getHeight());
		
		batch.setShader(postProcessShader);
		batch.disableBlending();
		batch.begin();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		blurA.getColorBufferTexture().bind(GL20.GL_TEXTURE0);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1);
		fbo2.getColorBufferTexture().bind(GL20.GL_TEXTURE1);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE2);
		blurB.getColorBufferTexture().bind(GL20.GL_TEXTURE2);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		
		PerspectiveCamera perspective = (PerspectiveCamera)engine.getRenderCamera();
		
		Vector2 near = new Vector2(
				(settings.nearStart + perspective.position.z - perspective.near) / (perspective.far - perspective.near),
				(settings.nearEnd + perspective.position.z - perspective.near) / (perspective.far - perspective.near));
		Vector2 far = new Vector2(
				(settings.farStart + perspective.position.z - perspective.near) / (perspective.far - perspective.near),
			//	new Vector3(0, 0, settings.farStart).prj(engine.camera.projection).z,
				//new Vector3(0, 0, 0).prj(engine.camera.combined).z,
				(settings.farEnd + perspective.position.z - perspective.near) / (perspective.far - perspective.near));
		
		
		postProcessShader.setUniformf("focusNear", near);
		postProcessShader.setUniformf("focusFar", far);
		postProcessShader.setUniformi(texture1Location, 1);
		postProcessShader.setUniformi(texture2Location, 2);
		postProcessShader.setUniformf(timeLocation, time);
		postProcessShader.setUniformf(worldLocation, world);
		postProcessShader.setUniformf(frequencyLocation, settings.frequency);
		postProcessShader.setUniformf(rateLocation, settings.rate);
		screenSprite.draw(batch);
		batch.end();
		
	}
	
	void validate()
	{
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();
		if(fbo == null || fbo.getWidth() != w || fbo.getHeight() != h)
		{
			if(fbo != null){
				fbo.dispose();
				fbo2.dispose();
				blurA.dispose();
				blurB.dispose();
			}
			
			fbo = new FrameBuffer(Format.RGBA8888, w, h, true);
			fbo2 = new FrameBuffer(Format.RGBA8888, w, h, true);
			blurA = new FrameBuffer(Format.RGBA8888, w, h, false);
			blurB = new FrameBuffer(Format.RGBA8888, w, h, false);
			screenSprite = new Sprite(fbo.getColorBufferTexture(), w, h);
			screenSprite.setFlip(false, true);
			
			batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			batch.setProjectionMatrix(batch.getProjectionMatrix()); // XXX necessary ?
		}
		
	}
	
	private ShaderProgram loadShader(FileHandle vertex, FileHandle fragment)
	{
		ShaderProgram sp = new ShaderProgram(
				prefix(vertex),
				prefix(fragment));
		return sp;
	}

	private String prefix(FileHandle shaderFile) {
		String code = "";
		if(settings.debugDepth) code += "#define DEBUG_DEPTH\n";
		if(settings.debugConfusion) code += "#define DEBUG_CONFUSION\n";
		if(settings.debugLimits) code += "#define DEBUG_LIMITS\n";
		code += shaderFile.readString();
		return code;
	}

	@Editable
	public void loadShaders() 
	{
		// TODO unload before ...
		
		// TODO Auto-generated method stub
		// postProcessShader = SpriteBatch.createDefaultShader();
		postProcessShader = loadShader(
				Gdx.files.local("../core/src/net/mgsx/game/examples/platformer/core/water-vertex.glsl"),
				Gdx.files.local("../core/src/net/mgsx/game/examples/platformer/core/water-fragment.glsl"));
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
		texture2Location = postProcessShader.getUniformLocation("u_texture2");
		
		flatProgram = loadShader(
				Gdx.files.local("../core/src/net/mgsx/game/examples/platformer/core/flat-vertex.glsl"),
				Gdx.files.local("../core/src/net/mgsx/game/examples/platformer/core/flat-fragment.glsl"));
		flatProgram.begin();
		if(!flatProgram.isCompiled()){
			System.err.println(flatProgram.getLog());
		}
		flatProgram.end();
		
		
		blurXProgram = loadShader(
				Gdx.files.local("../core/src/net/mgsx/game/examples/platformer/core/blurx-vertex.glsl"),
				Gdx.files.local("../core/src/net/mgsx/game/examples/platformer/core/blurx-fragment.glsl"));
		blurXProgram.begin();
		if(!blurXProgram.isCompiled()){
			System.err.println(blurXProgram.getLog());
		}
		blurXProgram.end();

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
				
	}
	
}
