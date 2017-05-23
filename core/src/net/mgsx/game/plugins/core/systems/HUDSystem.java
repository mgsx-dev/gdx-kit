package net.mgsx.game.plugins.core.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.Kit;

abstract public class HUDSystem extends EntitySystem
{
	private Stage stage;
	private boolean created;
	
	public HUDSystem() {
		super(GamePipeline.HUD);
		
		stage = new Stage(new ScreenViewport());
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		Kit.inputs.addProcessor(stage);
		stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
	}
	
	@Override
	public void removedFromEngine(Engine engine) 
	{
		Kit.inputs.removeProcessor(stage);
		super.removedFromEngine(engine);
	}
	
	@Override
	public void update(float deltaTime) 
	{
		if(!created){
			create();
			created = true;
		}
		// check screen size changes (same as screen.resize)
		if(stage.getViewport().getScreenWidth() != Gdx.graphics.getWidth() || stage.getViewport().getScreenHeight() != Gdx.graphics.getHeight()){
			stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		}
		stage.act(deltaTime);
		stage.draw();
	}
	
	protected void create(){}

	public Stage getStage() {
		return stage;
	}
}
