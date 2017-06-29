package net.mgsx.game.plugins.g3d.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.plugins.g3d.components.G3DModel;

/**
 * This system is not included by default and can be used in replacement of {@link G3DRendererSystem}
 * 
 * @author mgsx
 *
 */
@EditableSystem
public class G3DRendererDeferredSystem extends IteratingSystem
{
	private ModelBatch modelBatch, normalDepthModelBatch;
	private GameScreen screen;
	private FrameBuffer normalDepthBuffer, colorBuffer;
	private ShaderProgram pixelShader;
	private Batch batch;
	@Editable public float radius = 2;
	@Editable public float exponent = 2;
	
	public G3DRendererDeferredSystem(GameScreen screen) {
		super(Family.all(G3DModel.class).get(), GamePipeline.RENDER);
		this.screen = screen;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		normalDepthModelBatch = new ModelBatch(
				Gdx.files.internal("shaders/deferred.vs"), 
				Gdx.files.internal("shaders/deferred.fs"));
		
		modelBatch = new ModelBatch();
		
		batch = new SpriteBatch(4, pixelShader = new ShaderProgram(
				Gdx.files.internal("shaders/deferred-post.vs"), 
				Gdx.files.internal("shaders/deferred-post.fs")));
		
	}
	
	@Override
	public void update(float deltaTime) 
	{
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		
		if(normalDepthBuffer == null || normalDepthBuffer.getWidth() != width || normalDepthBuffer.getHeight() != height)
		{
			if(normalDepthBuffer != null){
				normalDepthBuffer.dispose();
				normalDepthBuffer = null;
				colorBuffer.dispose();
			}
			normalDepthBuffer = new FrameBuffer(Format.RGBA8888, width, height, true);
			colorBuffer = new FrameBuffer(Format.RGBA8888, width, height, true);
			batch.dispose();
			batch = new SpriteBatch(4, pixelShader);
		}
		
		colorBuffer.begin();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		modelBatch.begin(screen.camera);
		render(modelBatch);
		modelBatch.end();
		colorBuffer.end();

		
		normalDepthBuffer.begin();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		normalDepthModelBatch.begin(screen.camera);
		render(normalDepthModelBatch);
		normalDepthModelBatch.end();
		normalDepthBuffer.end();
		
		batch.begin();
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1);
		colorBuffer.getColorBufferTexture().bind(1);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		pixelShader.setUniformi("u_texture1", 1);
		pixelShader.setUniformf("u_expo", exponent);
		pixelShader.setUniformf("dir", new Vector2(radius / width, radius / height));
		batch.draw(normalDepthBuffer.getColorBufferTexture(), 0, 0, width, height, 0, 0, width, height, false, true);
		batch.end();
		
	}
	
	private void render(ModelBatch mb){
		for(Entity e : getEntities()){
			G3DModel model = G3DModel.components.get(e);
			if(model.inFrustum)
			{
				mb.render(model.modelInstance);
			}
		}
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
	}
}
