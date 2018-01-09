package net.mgsx.game.plugins.graphics.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;

public class FBOModel 
{
	final private transient Array<FrameBuffer> fboStack = new Array<FrameBuffer>();
	
	public void push(FrameBuffer fbo){
		fboStack.add(fbo);
		fbo.begin();
	}
	
	public void pop(){
		fboStack.pop().end();
		if(fboStack.size > 0){
			fboStack.peek().begin();
		}
	}

	/**
	 * restore last FBO context (fbo and viewport)
	 */
	public void bind() {
		if(fboStack.size > 0){
        	fboStack.peek().begin();
        }else{
        	FrameBuffer.unbind();
        	Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
	}
}
