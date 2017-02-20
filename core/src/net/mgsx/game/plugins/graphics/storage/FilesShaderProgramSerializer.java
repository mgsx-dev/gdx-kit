package net.mgsx.game.plugins.graphics.storage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;

import net.mgsx.game.core.helpers.FilesShaderProgram;

// TODO file resolving may be broken : only works with internal files ...
public class FilesShaderProgramSerializer implements Serializer<FilesShaderProgram>{

	@Override
	public void write(Json json, FilesShaderProgram object, Class knownType) {
		json.writeObjectStart();
		json.writeValue("vertex", stripPath(object.vertexShader));
		json.writeValue("fragment", stripPath(object.fragmentShader));
		json.writeObjectEnd();
		
	}
	
	// TODO refactor code with other strip path code ...
	private String stripPath(FileHandle file)
	{
		String root = Gdx.files.internal("").file().getAbsolutePath();
		String current = file.file().getAbsolutePath();
		if(current.startsWith(root)){
			return current.substring(root.length()+1);
		}
		return file.path();
	}

	@Override
	public FilesShaderProgram read(Json json, JsonValue jsonData, Class type) 
	{
		String vertexPath = json.readValue("vertex", String.class, jsonData);
		String fragmentPath = json.readValue("fragment", String.class, jsonData);
		return new FilesShaderProgram(Gdx.files.internal(vertexPath), Gdx.files.internal(fragmentPath));
	}

}
