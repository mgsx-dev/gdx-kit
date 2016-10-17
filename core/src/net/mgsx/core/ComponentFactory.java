package net.mgsx.core;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public interface ComponentFactory 
{
	public Component create(Entity entity);
}
