package net.mgsx.game.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;

import net.mgsx.game.core.storage.EntityGroup;

public interface ContextualDuplicable {

	public Component duplicate(Engine engine, EntityGroup sourceGroup, EntityGroup cloneGroup);
}
