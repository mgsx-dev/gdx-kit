package net.mgsx.game.core.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class ShaderProgramHelper {

	/**
	 * Load or reload a shader program, useful for debugging.
	 * in case of loading, if shader is not compiling a runtime exception is thrown
	 * with compiler logs.
	 * in case of reloading, if shader is not compiling, compiler error logs are traced
	 * and the original shader is returned.
	 * @param program the original program (may be null for a first loading)
	 * @param vertFile the vertex shader file
	 * @param fragFile the fragment shader file
	 * @return the reloaded shader if success or the old one if failed.
	 */
	public static ShaderProgram reload(ShaderProgram program, FileHandle vertFile, FileHandle fragFile)
	{
		ShaderProgram shader = new ShaderProgram(vertFile, fragFile);
		if(!shader.isCompiled()){
			if(program == null){
				throw new GdxRuntimeException(shader.getLog());
			}else{
				Gdx.app.error("ShaderProgram", shader.getLog());
			}
		} else{
			if(program != null) program.dispose();
			program = shader;
		}
		return program;
	}
}
