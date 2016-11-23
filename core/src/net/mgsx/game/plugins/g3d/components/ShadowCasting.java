package net.mgsx.game.plugins.g3d.components;

import com.badlogic.ashley.core.Component;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

/**
 * Entity which project shadows.
 * Rendering system will render {@link G3DModel} components in shadow pass.
 * 
 * @author mgsx
 *
 */
@Storable("g3d.shadow-casting")
@EditableComponent
public class ShadowCasting implements Component
{
	
}
