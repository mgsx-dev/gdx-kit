package net.mgsx.game.plugins.core.components;

import com.badlogic.ashley.core.Component;

import net.mgsx.game.core.annotations.EditableComponent;

/**
 * This component type tag an entity as physics controlled by logic.
 * 
 * By default, physics is the master piece, transform values are set by physics
 * and logic respond to it, note that logic can apply forces or set velocity.
 * 
 * Sometimes we want the physics to be driven by animation or logic. In this situation,
 * transform is set by animation and physics is synchronized on this value.
 * 
 * @author mgsx
 *
 */
@EditableComponent(autoClone=true)
public class SlavePhysics implements Component {
	
}
