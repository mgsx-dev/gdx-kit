package net.mgsx.game.examples.platformer.logic;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.Duplicable;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.g3d.components.G3DModel;

@Storable("example.platformer.bonus")
@EditableComponent(all={G3DModel.class, Box2DBodyModel.class})
public class BonusComponent implements Component, Duplicable
{
	public boolean catchable = true;

	@Override
	public Component duplicate(Engine engine) {
		BonusComponent clone = engine.createComponent(BonusComponent.class);
		clone.catchable = catchable;
		return clone;
	}


	
}
