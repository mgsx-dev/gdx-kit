package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.helpers.FilesShader;
import net.mgsx.game.core.helpers.FilesShaderProgram;
import net.mgsx.game.core.helpers.systems.TransactionSystem;
import net.mgsx.game.plugins.g3d.systems.G3DRendererSystem;

@EditableSystem
public class EmissiveSystem extends TransactionSystem
{
	@Inject
	public G3DRendererSystem renderSystem; 
	
	@Editable
	public FilesShaderProgram shader = new FilesShaderProgram(
			Gdx.files.internal("shaders/emissive-vertex.glsl"),
			Gdx.files.internal("shaders/emissive-fragment.glsl"));
	
	@Editable
	public FilesShaderProgram compose = new FilesShaderProgram(
			Gdx.files.internal("shaders/compose-vertex.glsl"),
			Gdx.files.internal("shaders/compose-fragment.glsl"));
	
	private SpriteBatch batch;
	
	private FrameBuffer back;
	
	private FrameBuffer blurA, blurB;

	private FilesShaderProgram blurProgram;

	@Editable
	public float blurSize = 2;

	@Editable
	public float boost = 8;

	@Editable
	public float decimate = 256;
	
	@Editable
	public int resolution = 0;
	
	
	
	public EmissiveSystem() {
		super(GamePipeline.BEFORE_RENDER_OPAQUE, new AfterSystem(GamePipeline.AFTER_RENDER_OPAQUE){});
	}
	
	@Editable
	public void reload(){
		FilesShader provider = new FilesShader(Gdx.files.internal("shaders/td-vertex.glsl"),
				Gdx.files.internal("shaders/td-fragment.glsl"));
		getEngine().getSystem(G3DRendererSystem.class).setShaderProvider(provider);
	

	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		batch = new SpriteBatch();
		blurProgram = new FilesShaderProgram(
				Gdx.files.internal("shaders/blurx-vertex.glsl"),
				Gdx.files.internal("shaders/blurx-fragment.glsl"));

	}
	
	private FrameBuffer ensureBuffer(FrameBuffer fbo)
	{
		int w = 1 << resolution;
		int h = w;
		if(fbo == back || resolution < 1){
			w = Gdx.graphics.getWidth();
			h = Gdx.graphics.getHeight();
		}
		if(fbo == null || w != fbo.getWidth() || h != fbo.getHeight()){
			if(fbo != null) fbo.dispose();
			fbo = new FrameBuffer(Format.RGBA8888, w, h, true);
			fbo.getColorBufferTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
		return fbo;
	}

	@Override
	protected boolean updateBefore(float deltaTime) {
		
		back = ensureBuffer(back);
		blurA = ensureBuffer(blurA);
		blurB = ensureBuffer(blurB);
		
		// bind buffer
		back.begin();
		Gdx.gl.glClearColor(.1f, .1f, .1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		renderSystem.fboStack.add(back);
		
		// renderSystem.ambient.se
		
		return true;
	}

	@Override
	protected void updateAfter(float deltaTime) {
		// swap buffers and apply effects
		renderSystem.fboStack.pop();
		back.end();
		
		batch.disableBlending();
		
		OrthographicCamera cam = new OrthographicCamera(1, -1);
		cam.update(true);
		batch.setProjectionMatrix(cam.combined);
		
		blurB.begin();
		// Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		batch.setShader(shader.program());
		batch.begin();
		batch.draw(back.getColorBufferTexture(), -.5f, -.5f, 1, 1);
		batch.end();
		blurB.end();
		
		blurA.begin();
		// Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		batch.setShader(blurProgram.program());
		batch.begin();
		blurProgram.program().setUniformf("dir", new Vector2(blurSize / (float)blurB.getColorBufferTexture().getWidth(),0));
		batch.draw(blurB.getColorBufferTexture(), -.5f, -.5f, 1, 1);
		batch.end();
		blurA.end();
		
		blurB.begin();
		batch.setShader(blurProgram.program());
		batch.begin();
		blurProgram.program().setUniformf("dir", new Vector2(0, blurSize / (float)blurA.getColorBufferTexture().getHeight()));
		batch.draw(blurA.getColorBufferTexture(), -.5f, -.5f, 1, 1);
		batch.end();
		blurB.end();

		
		
		
	//	Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		
		
		batch.setShader(compose.program());
		batch.begin();
		compose.program().setUniformf("u_boost", boost);
		compose.program().setUniformf("u_decimate", decimate);
		compose.program().setUniformi("u_textureOver", 1);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		back.getColorBufferTexture().bind(GL20.GL_TEXTURE0);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1);
		blurB.getColorBufferTexture().bind(GL20.GL_TEXTURE1);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		batch.draw(back.getColorBufferTexture(), -.5f, -.5f, 1, 1);
		batch.end();
		
	}
}
