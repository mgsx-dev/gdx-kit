package net.mgsx.game.core.screen;

import com.badlogic.gdx.Screen;

public class ScreenDelegate implements Screen
{
	protected Screen current;

	public ScreenDelegate(Screen current) {
		super();
		this.current = current;
	}

	@Override
	public void show() {
		if(current != null) current.show();
	}

	@Override
	public void render(float delta) {
		if(current != null) current.render(delta);
	}

	@Override
	public void resize(int width, int height) {
		if(current != null) current.resize(width, height);
	}

	@Override
	public void pause() {
		if(current != null) current.pause();
	}

	@Override
	public void resume() {
		if(current != null) current.resume();
	}

	@Override
	public void hide() {
		if(current != null) current.hide();
	}

	@Override
	public void dispose() {
		if(current != null) current.dispose();
	}
}
