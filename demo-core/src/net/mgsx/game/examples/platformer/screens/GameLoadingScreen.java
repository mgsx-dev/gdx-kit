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

public class GameLoadingScreen extends StageScreen implements ScreenClip
{
	private AssetManager assets;
	
	private final static String splashFileName = "splash.png";
	
	public GameLoadingScreen(AssetManager assets) {
		super(null);
		this.assets = assets;
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
		
		super.show();
	}
	
	@Override
	public void render(float delta) 
	{
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
		return assets.update();
	}

}
