package net.mgsx.game.core.helpers;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;

/**
 * Shader provider with backed files for vertex and fragments.
 * Can be reloaded.
 * 
 * @author mgsx
 *
 */
public class FilesShader extends DefaultShaderProvider
{
	FileHandle vertexShader, fragmentShader;
	
	public FilesShader(FileHandle vertexShader, FileHandle fragmentShader) 
	{
		super(vertexShader, fragmentShader);
		this.vertexShader = vertexShader;
		this.fragmentShader = fragmentShader;
	}
	public FilesShader reload()
	{
		dispose();
		return new FilesShader(vertexShader, fragmentShader);
	}
	
}