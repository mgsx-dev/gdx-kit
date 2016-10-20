package net.mgsx.game.examples.platformer.core;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationDesc;

import net.mgsx.game.core.components.Duplicable;
import net.mgsx.game.core.components.Transform2DComponent;
import net.mgsx.game.core.helpers.AnimationAdapter;
import net.mgsx.game.core.plugins.Initializable;
import net.mgsx.game.plugins.g3d.G3DModel;

public class BonusComponent implements Component, Initializable, Duplicable
{
	private Engine manager;
	private Entity entity;
	
	private boolean catchable = true;

	@Override
	public void initialize(Engine manager, Entity entity) {
		this.entity = entity;
		this.manager = manager;
		entity.getComponent(G3DModel.class).animationController.paused = false;
		entity.getComponent(G3DModel.class).animationController.setAnimation("apple.lp|apple.lpAction", -1);
		entity.add(new Transform2DComponent());
	}
	
	public void setCatch() 
	{
		catchable = false;
		
		// destroy animation
		entity.getComponent(G3DModel.class).animationController.animate("apple.lp|apple.die", new AnimationAdapter(){
			@Override
			public void onEnd(AnimationDesc animation) {
				manager.removeEntity(entity); // self destroy !
			}
		}, 0.2f);
		
	}

	public boolean isCatchable() {
		return catchable;
	}

	@Override
	public Component duplicate() {
		BonusComponent clone = new BonusComponent();
		clone.catchable = catchable;
		return clone;
	}

	
}
