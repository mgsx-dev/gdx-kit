package net.mgsx.game.core.storage.serializers;

import java.lang.reflect.Field;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;

import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.helpers.ReflectionHelper;

public class AnnotationBasedSerializer implements Serializer {

	private Class type;
	
	public AnnotationBasedSerializer(Class type) {
		super();
		this.type = type;
	}

	@Override
	public void write(Json json, Object object, Class knownType) 
	{
		json.writeObjectStart();
		for(Field field : type.getFields()){
			Storable storable = field.getAnnotation(Storable.class);
			if(storable != null){
				json.writeField(object, field.getName());
			}
		}
		json.writeObjectEnd();
	}

	@Override
	public Object read(Json json, JsonValue jsonData, Class type) 
	{
		Object object = ReflectionHelper.newInstance(type);
		for(Field field : type.getFields()){
			Storable storable = field.getAnnotation(Storable.class);
			if(storable != null){
				json.readField(object, field.getName(), jsonData);
			}
		}
		return object;
	}

}
