package net.mgsx.core.plugins;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

abstract public class ComponentActivation {

	abstract public Component create(Entity entity);
	public void remove(Entity entity, Component component){}
}
