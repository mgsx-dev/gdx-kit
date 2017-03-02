package net.mgsx.game.core.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/*
 * TODO refactor and separate :
 * 
 * ScreenTransition : abstract (do nothing with screens)
 * |- Sequence transition : delegate to a sequence (aka scene2d actions)
 * |- Viewport transition : just render screens with offset (slide ...)
 * |- RTT transition : just render both screens to texture (option freeze ...)
 *    |- Sprite transition : create 2 sprites for both screens
 *    |- Image transition : create 2 image (with special stage)
 *    |- PixelTransition : bind both texture to a shader
 *       |- Alpha transition : blend with time
 *       |- Page Roll Transition : apply page effect (draw new at back)
 */

/**
 * Transition effect using pixel shader : Each screens are rendered to FBO and these FBO
 * are rendered using custom ShaderProgram.
 * 
 * @author mgsx
 *
 */
public class ShaderTransition implements ScreenTransition
{
	protected int width, height;
	private FrameBuffer srcBuffer, dstBuffer;
	private SpriteBatch batch;
	private ShaderProgram shader;
	private int locTime;
	private Viewport viewport;
	
	public ShaderTransition(ShaderProgram shader) {
		this.shader = shader;
		batch = new SpriteBatch(1, shader);
		locTime = shader.getUniformLocation("u_time");
		viewport = new ScreenViewport();
	}
	
	@Override
	public void render(Screen src, Screen dst, float deltaTime, float t) 
	{
		if(t <= 0){
			src.render(deltaTime);
		}else if(t >= 1){
			dst.render(deltaTime);
		}else{
			srcBuffer.bind();
			src.render(deltaTime);
			
			dstBuffer.bind();
			dst.render(deltaTime);
			
			FrameBuffer.unbind();
			Gdx.gl.glClearColor(0, 0, 0, 0);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			viewport.update(width, height, true);
			batch.setProjectionMatrix(viewport.getCamera().combined);
			batch.disableBlending();
			batch.begin();
			shader.setUniformf(locTime, t);
			shader.setUniformi("u_texture1", 1);
			dstBuffer.getColorBufferTexture().bind(1);
			srcBuffer.getColorBufferTexture().bind(0);
			batch.draw(srcBuffer.getColorBufferTexture(), 0, 0, 
					0, 0, width, height, 1, 1, 0, 0, 0, width, height, false, true);

			batch.end();

			Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1);
			Gdx.gl.glBindTexture(dstBuffer.getColorBufferTexture().glTarget, 0);
			Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
			Gdx.gl.glBindTexture(srcBuffer.getColorBufferTexture().glTarget, 0);
		}
	}

	@Override
	public void resize(int width, int height) 
	{
		if(this.width == width && this.height == height) return;
		this.width = width;
		this.height = height;
		if(srcBuffer != null) srcBuffer.dispose();
		if(dstBuffer != null) dstBuffer.dispose();
		srcBuffer = createBuffer();
		dstBuffer = createBuffer();
	}
	
	private FrameBuffer createBuffer(){
		return new FrameBuffer(
				Format.RGBA8888,
				width,
				height, 
				true){
			@Override
			protected Texture createColorTexture() {
				Texture colorTexture = super.createColorTexture();
				colorTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
				colorTexture.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
				return colorTexture;
			}
		};
	}
}
