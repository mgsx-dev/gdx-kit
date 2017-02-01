package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

// TODO delete ? or this is just a hint to render tower base
@Storable("td.tower")
@EditableComponent(autoClone=true)
public class Tower implements Component 
{

}
