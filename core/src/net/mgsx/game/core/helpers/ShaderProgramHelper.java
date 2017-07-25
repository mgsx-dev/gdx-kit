package net.mgsx.game.core.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class ShaderProgramHelper {

	public static boolean verbose = true;
	public static boolean enableCrossCompilation = true;
	
	public static String shaderVersion = "#version 100";
	
	private static String prependVert;
	private static String prependFrag;
	private static boolean inited = false;
	
	private static void init(){
		if(!inited){
			
			prependVert = shaderVersion + "\n" + Gdx.files.classpath("shaders/header.vert").readString() + "\n";
			prependFrag = shaderVersion + "\n" + Gdx.files.classpath("shaders/header.frag").readString() + "\n";
			
			inited = true;
		}
	}
	
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
		if(verbose){
			Gdx.app.log("ShaderProgram", "compiling " + vertFile.path() + " " + fragFile.path() + " ...");
		}

		String oldVert = ShaderProgram.prependVertexCode;
		String oldFrag = ShaderProgram.prependFragmentCode;
		try
		{
			init();
			
			if(enableCrossCompilation){
				// prepend code
				ShaderProgram.prependVertexCode = prependVert;
				if(oldVert != null) ShaderProgram.prependVertexCode += oldVert;
				ShaderProgram.prependFragmentCode = prependFrag;
				if(oldFrag != null) ShaderProgram.prependFragmentCode += oldFrag;
				
				ShaderProgram.prependVertexCode += "#line 0\n";
				ShaderProgram.prependFragmentCode += "#line 0\n";
			}
			
			ShaderProgram shader = new ShaderProgram(vertFile, fragFile);
			
			String fullLog = vertFile.path() + " " + fragFile.path() + "\n" + shader.getLog();
			
			if(!shader.isCompiled()){
				if(program == null){
					throw new GdxRuntimeException(fullLog);
				}else{
					Gdx.app.error("ShaderProgram", fullLog);
				}
			} else{
				if(program != null) program.dispose();
				if(verbose && shader.getLog().length() > 0){
					Gdx.app.log("ShaderProgram", fullLog);
				}
				if(verbose){
					Gdx.app.log("ShaderProgram", "successfully compiled " + vertFile.path() + " " + fragFile.path());
				}

				program = shader;
			}
			return program;
		}
		finally{
			ShaderProgram.prependVertexCode = oldVert;
			ShaderProgram.prependFragmentCode = oldFrag;
		}
	}
	
	public static ShaderProgram reload(FileHandle vertFile, FileHandle fragFile)
	{
		return reload(null, vertFile, fragFile);
	}

}
