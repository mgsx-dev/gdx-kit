package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("td.entry")
@EditableComponent(autoClone=true)
public class Entry implements Component 
{
	
}
