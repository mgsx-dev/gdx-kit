package net.mgsx.game.examples.platformer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;

import net.mgsx.game.core.screen.ScreenClip;
import net.mgsx.game.core.screen.StageScreen;
import net.mgsx.game.examples.platformer.PlatformerWorkflow;

public class GameLoadingScreen extends StageScreen implements ScreenClip
{
	private AssetManager assets;
	
	private final static String splashFileName = "splash.png";
	
	private boolean finished = false;
	
	public GameLoadingScreen(PlatformerWorkflow game) {
		super(null);
		this.assets = game.assets();
		assets.load(splashFileName, Texture.class);
	}
	
	@Override
	public void show() {
		
		Table table = new Table();
		Image image = new Image(assets.get(splashFileName, Texture.class));
		image.setScaling(Scaling.fit);
		table.add(image).expand().center();
		table.setFillParent(true);
		stage.addActor(table);
		
//		Action endAction = Actions.run(new Runnable() {
//			@Override
//			public void run() {
//				finished = true;
//			}
//		});
//		
//		image.setColor(1, 1, 1, 0);
//		image.addAction(Actions.sequence(
//				Actions.alpha(1, 1), 
//				Actions.delay(2), 
//				ActionsHelper.checkAssets(assets), 
//				Actions.alpha(0, 1), 
//				endAction));
		
		super.show();
	}
	
	@Override
	public void render(float delta) 
	{
		finished = assets.update();
		
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render(delta);
	}
	
	@Override
	public void hide() {
		stage.clear();
		super.hide();
	}
	
	@Override
	public void dispose() {
		assets.unload(splashFileName); // TODO auto ? how ?
		super.dispose();
	}

	@Override
	public boolean isComplete() {
		return finished;
	}
}
