package net.mgsx.game.examples.platformer.logic;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationDesc;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.Duplicable;
import net.mgsx.game.core.helpers.AnimationAdapter;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.g3d.components.G3DModel;

@Storable("example.platformer.bonus")
@EditableComponent(all={G3DModel.class, Box2DBodyModel.class})
public class BonusComponent implements Component, Duplicable
{
	private boolean catchable = true;

	public void setCatch(final Entity entity) 
	{
		catchable = false;
		
		entity.getComponent(Box2DBodyModel.class).context.schedule(new Runnable() {
			@Override
			public void run() {
				entity.getComponent(Box2DBodyModel.class).body.setActive(false);
			}
		});
		
		// destroy animation
		entity.getComponent(G3DModel.class).animationController.animate("apple.lp|apple.die", new AnimationAdapter(){
			@Override
			public void onEnd(AnimationDesc animation) {
				// TODO ok remove box2D object
				// ok remove 3DModel
				
				// manager.removeEntity(entity); // self destroy ! TODO no just remove the G3D part ? or set it disabled ?
			}
		}, 0.2f);
		
	}

	public boolean isCatchable() {
		return catchable;
	}

	@Override
	public Component duplicate(Engine engine) {
		BonusComponent clone = engine.createComponent(BonusComponent.class);
		clone.catchable = catchable;
		return clone;
	}

	public void enter(Entity entity) {
		catchable = true;
		entity.getComponent(Box2DBodyModel.class).body.setActive(true);
		entity.getComponent(G3DModel.class).animationController.setAnimation("apple.lp|apple.lpAction", -1);
	}

	public void exit(Entity entity) {
		entity.getComponent(Box2DBodyModel.class).body.setActive(false);
	}

	
}
