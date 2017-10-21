package net.mgsx.game.examples.shmup.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent
public class Player implements Component
{
	public final static ComponentMapper<Player> components = ComponentMapper.getFor(Player.class);
}
