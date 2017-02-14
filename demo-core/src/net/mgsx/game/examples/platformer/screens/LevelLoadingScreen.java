package net.mgsx.game.examples.platformer.screens;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import net.mgsx.game.core.GameRegistry;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.helpers.ActionsHelper;
import net.mgsx.game.core.screen.ScreenClip;

public class LevelLoadingScreen extends GameScreen implements ScreenClip
{
	private Actor mainActor;
	public LevelLoadingScreen(AssetManager assets, GameRegistry registry) {
		super(assets, registry);
	}
	
	@Override
	public void show() {
		mainActor = new Actor();
		mainActor.addAction(Actions.sequence(
				Actions.delay(2), // wait 2 seconds
				ActionsHelper.checkAssets(assets))); // wait assets loaded
		super.show();
	}
	
	@Override
	public void dispose() {
		mainActor = null;
		super.dispose();
	}

	@Override
	public void render(float delta) {
		mainActor.act(delta);
		super.render(delta);
	}

	@Override
	public boolean isComplete() {
		return mainActor.getActions().size == 0;
	}

}
