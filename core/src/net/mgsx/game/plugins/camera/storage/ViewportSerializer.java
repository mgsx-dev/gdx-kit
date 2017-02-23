package net.mgsx.game.plugins.camera.storage;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ViewportSerializer implements Serializer<Viewport>
{

	@Override
	public void write(Json json, Viewport object, Class knownType) {
		json.writeObjectStart();
		
		if(object instanceof ScreenViewport){
			json.writeValue("type", "screen");
			json.writeField(object, "unitsPerPixel");
		}else if(object instanceof FitViewport){
			json.writeValue("type", "fit");
			json.writeField(object, "worldWidth");
			json.writeField(object, "worldHeight");
		}else if(object instanceof FillViewport){
			json.writeValue("type", "fill");
			json.writeField(object, "worldWidth");
			json.writeField(object, "worldHeight");
		}else if(object instanceof StretchViewport){
			json.writeValue("type", "stretch");
			json.writeField(object, "worldWidth");
			json.writeField(object, "worldHeight");
		}else if(object instanceof ExtendViewport){
			json.writeValue("type", "extend");
			json.writeField(object, "minWorldWidth");
			json.writeField(object, "minWorldHeight");
			json.writeField(object, "maxWorldWidth");
			json.writeField(object, "maxWorldHeight");
		}
		
		json.writeObjectEnd();
	}

	@Override
	public Viewport read(Json json, JsonValue jsonData, Class type) 
	{
		Viewport object = null;
		String vpType = jsonData.getString("type");
		if("screen".equals(vpType)){
			object = new ScreenViewport(null);
			json.readField(object, "unitsPerPixel", jsonData);
		}else if("extend".equals(vpType)){
			object = new ExtendViewport(
					jsonData.getFloat("minWorldWidth"),
					jsonData.getFloat("minWorldHeight"),
					jsonData.getFloat("maxWorldWidth"),
					jsonData.getFloat("maxWorldHeight"), null);
		}else if("fit".equals(vpType)){
			object = new FitViewport(
					jsonData.getFloat("worldWidth"),
					jsonData.getFloat("worldHeight"), null);
		}else if("fill".equals(vpType)){
			object = new FillViewport(
					jsonData.getFloat("worldWidth"),
					jsonData.getFloat("worldHeight"), null);
		}else if("stretch".equals(vpType)){
			object = new StretchViewport(
					jsonData.getFloat("worldWidth"),
					jsonData.getFloat("worldHeight"), null);
		}
		return object;
	}

}
