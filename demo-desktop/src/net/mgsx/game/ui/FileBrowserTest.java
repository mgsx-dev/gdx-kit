package net.mgsx.game.ui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.mgsx.game.core.ui.actors.FileBrowserWidget;

public class FileBrowserTest extends ScreenAdapter {

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new Game(){
			@Override
			public void create() {
				setScreen(new FileBrowserTest(new Skin(Gdx.files.classpath("uiskin.json"))));
			}
		}, config);
	}
	private Stage stage;
	public FileBrowserTest(Skin skin) {
		stage = new Stage(new ScreenViewport());
		Table root = new Table();
		root.setFillParent(true);
		root.add(new FileBrowserWidget(skin)).expandY().top();
		stage.addActor(root);
	}
	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}
	@Override
	public void render(float delta) {
		stage.act();
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		stage.draw();
	}
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height);
	}

}
