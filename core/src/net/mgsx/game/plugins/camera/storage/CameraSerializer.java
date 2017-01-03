package net.mgsx.game.plugins.camera.storage;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;

public class CameraSerializer implements Serializer<Camera>
{

	@Override
	public void write(Json json, Camera object, Class knownType) {
		json.writeObjectStart();
		if(object instanceof OrthographicCamera){
			json.writeValue("ortho", true);
			json.writeField(object, "zoom");
		}else if(object instanceof PerspectiveCamera){
			json.writeField(object, "fieldOfView");
		}
		json.writeField(object, "position");
		json.writeField(object, "direction");
		json.writeField(object, "up");
		json.writeField(object, "near");
		json.writeField(object, "far");
		json.writeField(object, "viewportWidth");
		json.writeField(object, "viewportHeight");
		json.writeObjectEnd();
	}

	@Override
	public Camera read(Json json, JsonValue jsonData, Class type) 
	{
		Camera object;
		boolean ortho = jsonData.getBoolean("ortho", false);
		if(ortho){
			object = new OrthographicCamera();
			json.readField(object, "zoom", jsonData);
		}else{
			object = new PerspectiveCamera();
			json.readField(object, "fieldOfView", jsonData);
		}
		json.readField(object, "position", jsonData);
		json.readField(object, "direction", jsonData);
		json.readField(object, "up", jsonData);
		json.readField(object, "near", jsonData);
		json.readField(object, "far", jsonData);
		json.readField(object, "viewportWidth", jsonData);
		json.readField(object, "viewportHeight", jsonData);
		
		object.update(true);
		return object;
	}

}
