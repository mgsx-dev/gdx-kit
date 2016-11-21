package net.mgsx.game.core.helpers;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class ActionsHelper {

	public static Action checkAssets(final AssetManager assets){
		return new Action() {
			@Override
			public boolean act(float delta) {
				return assets.update();
			}
		};
	}

	public static Action systemProcessing(final EntitySystem system, final boolean processing) 
	{
		return Actions.run(new Runnable() {
			
			@Override
			public void run() {
				system.setProcessing(processing);
			}
		});
	}

	public static Action animationComplete(final AnimationController animationController) {
		return new Action() {
			@Override
			public boolean act(float delta) {
				return animationController.current == null;
			}
		};
	}
	
	
}
