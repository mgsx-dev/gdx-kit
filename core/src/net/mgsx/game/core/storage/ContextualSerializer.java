package net.mgsx.game.core.storage;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Json;

public abstract class ContextualSerializer<T> implements Json.Serializer<T> 
{
	protected EntityGroupSerializer context;
	private Class<T> type;

	
	public ContextualSerializer(Class<T> type) {
		super();
		this.type = type;
	}

	protected Entity findEntity(int reference){
		return context.object.entities.get(reference);
	}

	public Class<T> getType() {
		return type;
	}
	
}
