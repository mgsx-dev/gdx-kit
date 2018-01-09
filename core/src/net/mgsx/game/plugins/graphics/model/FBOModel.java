package net.mgsx.game.plugins.graphics.model;

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

	public void unbind() {
		if(fboStack.size > 0){
			fboStack.peek().end();
	    }
	}

	public void bind() {
		if(fboStack.size > 0){
        	fboStack.peek().begin();
        }
	}
}
