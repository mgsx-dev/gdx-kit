package net.mgsx.game.plugins.core.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.mgsx.game.core.GamePipeline;

abstract public class HUDSystem extends EntitySystem
{
	private Stage stage;
	
	public HUDSystem() {
		super(GamePipeline.HUD);
		
		stage = new Stage(new ScreenViewport());
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		Gdx.input.setInputProcessor(stage);
		stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
	}
	
	@Override
	public void removedFromEngine(Engine engine) 
	{
		Gdx.input.setInputProcessor(null);
		super.removedFromEngine(engine);
	}
	
	@Override
	public void update(float deltaTime) 
	{
		// check screen size changes (same as screen.resize)
		if(stage.getViewport().getScreenWidth() != Gdx.graphics.getWidth() || stage.getViewport().getScreenHeight() != Gdx.graphics.getHeight()){
			stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		}
		stage.act(deltaTime);
		stage.draw();
	}
	
	public Stage getStage() {
		return stage;
	}
}
