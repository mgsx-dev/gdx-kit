package net.mgsx.game.examples.platformer.core;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import net.mgsx.game.core.GamePipeline;

public class PlatformerPostProcessing
{
	private FrameBuffer fbo;
	private Sprite screenSprite;
	private SpriteBatch batch;
	private ShaderProgram postProcessShader;
	
	public PlatformerPostProcessing(Engine engine) 
	{
		// postProcessShader = SpriteBatch.createDefaultShader();
		postProcessShader = new ShaderProgram(
				Gdx.files.classpath("net/mgsx/game/examples/platformer/core/water-vertex.glsl"),
				Gdx.files.classpath("net/mgsx/game/examples/platformer/core/water-fragment.glsl"));
		
		// TODO resize event ?
		fbo = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		screenSprite = new Sprite(fbo.getColorBufferTexture(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		screenSprite.setFlip(false, true);
		batch = new SpriteBatch(1, postProcessShader);
		
		engine.addSystem(new EntitySystem(GamePipeline.BEFORE_RENDER) {
			@Override
			public void update(float deltaTime) 
			{
				fbo.begin();
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			}
		});
		engine.addSystem(new EntitySystem(GamePipeline.AFTER_RENDER) {
			@Override
			public void update(float deltaTime) 
			{
				fbo.end();
				
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				batch.begin();
				screenSprite.draw(batch);
				batch.end();
			}
		});
	}
	
}
