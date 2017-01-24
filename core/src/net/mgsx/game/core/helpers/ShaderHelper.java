package net.mgsx.game.core.helpers;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class ShaderHelper {

	public static void assertProgram(ShaderProgram program){
		program.begin();
		if(!program.isCompiled()){
			throw new GdxRuntimeException(program.getLog());
		}
		program.end();
	}
}
