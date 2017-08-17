package net.mgsx.game.core.helpers;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;

public class ApplicationWrapper implements ApplicationListener
{
	public ApplicationListener listener;
	
	public ApplicationWrapper(ApplicationListener listener) {
		super();
		this.listener = listener;
	}
	
	public void setListener(ApplicationListener listener) {
		this.listener.dispose();
		this.listener = listener;
		this.listener.create();
		this.listener.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void create() {
		listener.create();
	}

	@Override
	public void resize(int width, int height) {
		listener.resize(width, height);
	}

	@Override
	public void render() {
		listener.render();
	}

	@Override
	public void pause() {
		listener.pause();
	}

	@Override
	public void resume() {
		listener.resume();
	}

	@Override
	public void dispose() {
		listener.dispose();
	}

}
