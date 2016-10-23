package net.mgsx.game.examples.platformer.core;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GameEngine;
import net.mgsx.game.core.GamePipeline;

public class PlatformerPostProcessing
{
	private FrameBuffer fbo;
	private Sprite screenSprite;
	private SpriteBatch batch;
	private ShaderProgram postProcessShader;
	private float time;
	private int timeLocation;
	private int worldLocation;
	private int frequencyLocation;
	private int rateLocation;
	private Vector2 world = new Vector2();
	
	public class Settings{
		public float speed = 1.0f;
		public float frequency = 10f;
		public float rate;
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
		
		// TODO resize event ?
		fbo = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		screenSprite = new Sprite(fbo.getColorBufferTexture(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		screenSprite.setFlip(false, true);
		batch = new SpriteBatch(1, postProcessShader);
		
		engine.entityEngine.addSystem(new EntitySystem(GamePipeline.BEFORE_RENDER) {
			@Override
			public void update(float deltaTime) 
			{
				fbo.begin();
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			}
		});
		engine.entityEngine.addSystem(new EntitySystem(GamePipeline.AFTER_RENDER) {
			@Override
			public void update(float deltaTime) 
			{
				fbo.end();
				
				time += deltaTime * settings.speed;
				
				float d = engine.camera.unproject(new Vector3()).z;
				Vector3 v = engine.camera.project(new Vector3(0,0,d));
				world.set(-v.x / Gdx.graphics.getWidth(), -v.y / Gdx.graphics.getHeight());
				
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				batch.begin();
				postProcessShader.setUniformf(timeLocation, time);
				postProcessShader.setUniformf(worldLocation, world);
				postProcessShader.setUniformf(frequencyLocation, settings.frequency);
				postProcessShader.setUniformf(rateLocation, settings.rate);
				screenSprite.draw(batch);
				batch.end();
			}
		});
	}
	
}
