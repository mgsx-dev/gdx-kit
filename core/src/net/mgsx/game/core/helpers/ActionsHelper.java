package net.mgsx.game.core.helpers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Action;

public class ActionsHelper {

	public static Action checkAssets(final AssetManager assets){
		return new Action() {
			@Override
			public boolean act(float delta) {
				return assets.update();
			}
		};
	}
	
	
}
