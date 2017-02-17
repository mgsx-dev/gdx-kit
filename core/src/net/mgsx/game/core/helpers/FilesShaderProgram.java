package net.mgsx.game.core.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

public class FilesShaderProgram implements Disposable
{
	public FileHandle vertexShader;
	public FileHandle fragmentShader;
	public ShaderProgram shader;
	
	public FilesShaderProgram(FileHandle vertexShader, FileHandle fragmentShader) {
		this.vertexShader = vertexShader;
		this.fragmentShader = fragmentShader;
		load();
	}
	
	private void load()
	{
		ShaderProgram sp = new ShaderProgram(vertexShader, fragmentShader);
		sp.begin();
		if(!sp.isCompiled()){
			Gdx.app.error("GLSL", sp.getLog());
			sp.end();
			sp.dispose();
		}
		else
		{
			sp.end();
			if(shader != null) shader.dispose();
			shader = sp;
		}
	}
	
	public void reload()
	{
		load();
	}

	@Override
	public void dispose() {
		shader.dispose();
	}

}
