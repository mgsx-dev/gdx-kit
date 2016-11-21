package net.mgsx.game.examples.platformer.screens;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.game.core.screen.StageScreen;
import net.mgsx.game.examples.platformer.PlatformerGame;

public class GameMenuScreen extends StageScreen
{
	private final static AssetDescriptor<Skin> skinAsset = new AssetDescriptor<Skin>("skins/game-skin.json", Skin.class);
	
	private final PlatformerGame game;
	
	public GameMenuScreen(PlatformerGame game) {
		super(null);
		this.game = game;
		game.getAssets().load(skinAsset);
	}
	
	@Override
	public void dispose() 
	{
		game.getAssets().unload(skinAsset.fileName);
		super.dispose();
	}
	
	@Override
	public void show() 
	{
		super.show();
		
		skin = game.getAssets().get(skinAsset);
		
		TextButton btStartGame = new TextButton("Start Game", skin);
		
		Table table = new Table();
		table.add(btStartGame);
		table.setFillParent(true);
		stage.addActor(table);
		
		btStartGame.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.startGame();
			}
		});
	}
	
	@Override
	public void hide() {
		stage.clear();
		super.hide();
	}
	
}
