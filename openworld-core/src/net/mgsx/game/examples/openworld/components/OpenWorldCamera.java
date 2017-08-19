package net.mgsx.game.examples.openworld.components;

import com.badlogic.ashley.core.Component;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("ow.camera")
@EditableComponent(autoClone=true)
public class OpenWorldCamera implements Component {

}
