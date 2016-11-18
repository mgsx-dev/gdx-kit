package net.mgsx.game.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;

import net.mgsx.game.core.annotations.EditableComponent;

/**
 * Implements component custom cloning : some field may or not be copied and
 * could be copied different way : by reference, by value...
 * 
 * If your component just require "by value" copy (or doesn't have fields to copy), then
 * you can add {@link EditableComponent} annotation to your component
 * and turn on {@link EditableComponent#autoTool()} flag.
 * 
 * @author mgsx
 */
public interface Duplicable {

	/**
	 * Create a clone.
	 * @param engine engine on which create component (usually {@link Engine#createComponent(Class)}
	 * @return clone component.
	 */
	public Component duplicate(Engine engine);
}
