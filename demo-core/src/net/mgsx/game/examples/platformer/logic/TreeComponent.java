package net.mgsx.game.examples.platformer.logic;

import com.badlogic.ashley.core.Component;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.g3d.components.G3DModel;

@Storable("example.platformer.tree")
@EditableComponent(name="Tree Logic", all={G3DModel.class, Box2DBodyModel.class})
public class TreeComponent implements Component
{
}
