package net.mgsx.game.core.helpers;

import java.nio.IntBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.BufferUtils;

public class GLHelper 
{
	private static IntBuffer intBuffer = BufferUtils.newIntBuffer(16);
	
	private static boolean complete = false;

	private static int textureMaxSize;
	private static int textureMinSize;
	
	private static void ensureComplete(){
		if(!complete){
			
			textureMinSize = 4; // TODO it appear to be the limit but don't know why.
			textureMaxSize = getInt(GL20.GL_MAX_TEXTURE_SIZE);
			
			complete = true;
		}
	}
	
	public static int getTextureMaxSize(){
		ensureComplete();
		return textureMaxSize;
	}

	public static int getTextureMinSize() {
		ensureComplete();
		return textureMinSize;
	}
	
	public static int getInt(int pname){
		intBuffer.clear();
		Gdx.gl.glGetIntegerv(pname, intBuffer);
		return intBuffer.get(0);
	}
}
