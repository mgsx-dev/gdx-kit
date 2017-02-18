package net.mgsx.game.core.storage;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Json;

// TODO not enough : linked entity may appear after current entity and not available
// at deserialization time. Requires another mecanism : "Resolver" which is run
// after deserialization and apply on already parsed component.
// this simplify things a bit : all fields are serialized normal way exept
// field marked as @Resolved, these fields are pushed to a context with id
// and resolved by resolver at resolve time. Maybe do it automatically for any
// Entity field !!
// More : ContextualDuplicable is also impacted and could be simplifyed ... as well
//
public abstract class ContextualSerializer<T> implements Json.Serializer<T> 
{
	protected EntityGroup context;
	private Class<T> type;

	
	public ContextualSerializer(Class<T> type) {
		super();
		this.type = type;
	}

	protected Entity findEntity(int reference){
		return context.find(reference);
	}

	public Class<T> getType() {
		return type;
	}
	
}
