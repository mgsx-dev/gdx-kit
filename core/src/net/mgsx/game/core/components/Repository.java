package net.mgsx.game.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.EditableComponent;

/**
 * Component for persistence tagging, contains all required information
 * to save entity. Automatically added to entities loaded from editor.
 * 
 * Tools may add this to created entities which should be persisted on saving.
 * 
 * In game context, it could be used to persist game state. Note that entity may have
 * or not depending on context (game or editor). For instance
 * - a generated sprite (canon bullet) won't never be persisted
 * - a background sprite will be persisted when editing but not when game is saved.
 * - the player entity could be persisted at some point during the game. 
 * 
 * @author mgsx
 *
 */
@EditableComponent(autoClone=true)
public class Repository implements Component
{
	
	public final static ComponentMapper<Repository> components = ComponentMapper.getFor(Repository.class);
}
