package net.mgsx.game.examples.platformer.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.game.core.screen.StageScreen;
import net.mgsx.game.examples.platformer.PlatformerAssets;
import net.mgsx.game.examples.platformer.game.PlatformerGame;

public class GameMenuScreen extends StageScreen
{
	private final PlatformerGame game;
	
	public GameMenuScreen(PlatformerGame game) {
		super(null);
		this.game = game;
		game.getAssets().load(PlatformerAssets.skin);
	}
	
	@Override
	public void dispose() 
	{
		game.getAssets().unload(PlatformerAssets.skin.fileName);
		super.dispose();
	}
	
	@Override
	public void show() 
	{
		super.show();
		
		skin = game.getAssets().get(PlatformerAssets.skin);
		
		TextButton btStartGame = new TextButton("Start Game", skin);
		
		Table table = new Table();
		table.add(btStartGame);
		table.setFillParent(true);
		stage.addActor(table);
		
		btStartGame.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.state().startGame(game);
			}
		});
	}
	
	@Override
	public void hide() {
		stage.clear();
		super.hide();
	}
	
}
