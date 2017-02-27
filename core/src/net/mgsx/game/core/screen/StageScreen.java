package net.mgsx.game.core.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.mgsx.game.core.Kit;

/**
 * 
 * Conveniente screen implementation with Scene2D stage.
 * 
 * @author mgsx
 *
 */
public class StageScreen extends ScreenAdapter
{
	protected Stage stage;
	protected Skin skin;
	
	public StageScreen(Skin skin) 
	{
		this.skin = skin;
		stage = new Stage(new ScreenViewport()); // TODO may be chosen by screen implemntation !
	}
	
	@Override
	public void show() {
		super.show();
		Kit.inputs.addProcessor(stage);
	}
	
	@Override
	public void hide() {
		Kit.inputs.removeProcessor(stage);
		super.hide();
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}
	
	@Override
	public void render(float deltaTime) {
		stage.act(deltaTime);
		stage.draw();
	}
	
	public Skin getSkin() {
		return skin;
	}
	
	public Stage getStage() {
		return stage;
	}

	public Vector2 getTouch() {
		return getStage().screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
	}
	
}
