package net.mgsx.game.core.tools;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

public class AbstractInputSystem extends EntitySystem implements InputProcessor
{
	public AbstractInputSystem() {
		super();
	}

	public AbstractInputSystem(int priority) {
		super(priority);
	}
	
	public void setProcessingInputs(boolean processing){
		if(processing){
			((InputMultiplexer)Gdx.input.getInputProcessor()).addProcessor(this);
		}else{
			((InputMultiplexer)Gdx.input.getInputProcessor()).removeProcessor(this);
		}
	}
	
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}
