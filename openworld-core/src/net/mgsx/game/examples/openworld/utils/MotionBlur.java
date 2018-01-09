package net.mgsx.game.examples.openworld.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;

public class MotionBlur implements Disposable {

	public static final MotionBlur single = new MotionBlur();

	public float factor = 1;
	
	/** in pixels (typical 1, 2, 0, -1, -2) */
	public float spread = 0;
	
	private FrameBuffer fbo, accum, accum2;
	
	// TODO il ne faut pas utiliser le même batch car la camera et la transfo peuvent être différent !
	private Batch batch = new SpriteBatch();
	
	public FrameBuffer begin(float width, float height) {
		return begin(MathUtils.ceil(width), MathUtils.ceil(height));
	}
	public FrameBuffer begin(int width, int height) {
		if(fbo == null || fbo.getWidth() != width || fbo.getHeight() != height){
			if(fbo != null) fbo.dispose();
			fbo = new FrameBuffer(Format.RGBA8888, width, height, false);
			if(accum != null) accum.dispose();
			accum = new FrameBuffer(Format.RGBA8888, width, height, false);
			if(accum2 != null) accum2.dispose();
			accum2 = new FrameBuffer(Format.RGBA8888, width, height, false);
		}
		
		fbo.begin();
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);
		
		return fbo;
	}

	public void end(FrameBuffer next) {
		fbo.end();
		
		batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.begin();
		batch.enableBlending();
		
		// if(true) return;
		
		if(false){
			batch.setColor(1,1,1, 1f);
			batch.draw(fbo.getColorBufferTexture(), 
					0,0, fbo.getColorBufferTexture().getWidth(), fbo.getColorBufferTexture().getHeight(),
					0, 0, 1, 1);
			batch.end();
			return;
		}
		
		accum.begin();
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		batch.enableBlending();
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
//		
		// TODO could be done in one pass (pixel shader) without clearing anything !
		
		Gdx.gl.glColorMask(false, false, false, true);
		Gdx.gl.glClearColor(0, 0, 0, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glColorMask(true, true, true, true);
//		float factor = getColor().a; // > 0 ? 1f/(float)(this.factor * getColor().a) : 0;
		float extraU =	(float)spread / (float)accum2.getColorBufferTexture().getWidth(); //0.005f; // TODO param nbpixels (typical is 1 or 2, it's band size
		float extraV =	(float)spread / (float)accum2.getColorBufferTexture().getHeight(); //0.005f; // TODO param nbpixels (typical is 1 or 2, it's band size
		if(true){
			batch.setColor(factor, factor,factor, 1f);
			batch.draw(accum2.getColorBufferTexture(), 
					0, 0, accum2.getColorBufferTexture().getWidth(), accum2.getColorBufferTexture().getHeight(),
					extraU, extraV,1-extraU, 1-extraV);
		}
		
		batch.flush();
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		batch.setColor(1, 1,1, 1f);
		batch.draw(fbo.getColorBufferTexture(), 
				0, 0, fbo.getColorBufferTexture().getWidth(), fbo.getColorBufferTexture().getHeight(),
				0, 0,1, 1);
		
		
		
		batch.flush();
		
		Gdx.gl.glColorMask(true, true, true, true);
		
		
		accum.end();
		
		if(next != null) next.begin();
//		
//		batch.setColor(batch.getColor().set(1f,1f,1f,batch.getColor().a));
//		batch.draw(accum.getColorBufferTexture(), 
//				0, 0, accum.getColorBufferTexture().getWidth(), accum.getColorBufferTexture().getHeight(),
//				extra, extra,1-extra, 1-extra);
//		
//		batch.flush();
		
//		Gdx.gl.glClearColor(0, 0, .0f, 1);
//		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);
		
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		batch.enableBlending();
		batch.setColor(1,1,1, 1 - factor);
		batch.draw(accum.getColorBufferTexture(), 
				0,0, accum.getColorBufferTexture().getWidth(), accum.getColorBufferTexture().getHeight(),
				0, 0,1, 1);
		
		batch.end();
		
		FrameBuffer tmp = accum2;
		accum2 = accum;
		accum = tmp;
	}
	@Override
	public void dispose() {
		if(fbo != null) fbo.dispose();
		if(accum != null) accum.dispose();
		if(accum2 != null) accum2.dispose();
	}

}
