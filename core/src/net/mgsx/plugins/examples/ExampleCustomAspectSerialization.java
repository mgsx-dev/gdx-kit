package net.mgsx.plugins.examples;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;

public class ExampleCustomAspectSerialization
{
	public static class MyAspect
	{
		public float x;
	}
	
	public static void exampleWithDirectMapping(Json json) {
		
		json.setSerializer(MyAspect.class, new Serializer<MyAspect>(){

			@Override
			public void write(Json json, MyAspect object, Class knownType) 
			{
				json.writeObjectStart();
				json.writeField(object, "x");
				json.writeObjectEnd();
			}

			@Override
			public MyAspect read(Json json, JsonValue jsonData, Class type) {
				MyAspect object = new MyAspect();
				json.readField(object, "x", jsonData);
				return object;
			}});
	}
	
	
	public static class MyDto
	{
		public float x;
	}
	
	public static void exampleWithDtoMapping(Json json) {
		
		json.setSerializer(MyAspect.class, new Serializer<MyAspect>(){

			@Override
			public void write(Json json, MyAspect object, Class knownType) 
			{
				MyDto dto = new MyDto();
				dto.x = object.x;
				json.writeValue(dto);
			}

			@Override
			public MyAspect read(Json json, JsonValue jsonData, Class type) {
				MyDto dto = json.readValue(MyDto.class, jsonData);
				MyAspect object = new MyAspect();
				object.x = dto.x;
				return object;
			}});
	}
	

}
