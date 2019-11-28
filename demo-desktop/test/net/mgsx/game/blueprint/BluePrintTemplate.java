package net.mgsx.game.blueprint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import net.mgsx.game.blueprint.model.Graph;
import net.mgsx.game.blueprint.model.Graph.CopyStrategy;
import net.mgsx.game.blueprint.ui.GraphView;
import net.mgsx.game.blueprint.ui.GraphView.GraphViewConfig;
import net.mgsx.game.core.GameApplication;
import net.mgsx.game.core.helpers.AssetHelper;
import net.mgsx.game.core.screen.StageScreen;

public class BluePrintTemplate extends GameApplication {

	public static void main(String[] args) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new BluePrintTemplate(), config);
	}
	private Graph graph;

	@Override
	public void create() 
	{
		super.create();
		
		Skin skin = AssetHelper.loadAssetNow(assets, "uiskin.json", Skin.class);
		StageScreen screen;
		setScreen(screen = new StageScreen(skin));
		
		graph = new Graph(CopyStrategy.FROM_SRC);
		
		GraphViewConfig config = new GraphViewConfig();
		GraphView view = new GraphView(graph, skin, config);
		
		screen.getStage().addActor(view);
		view.setFillParent(true);
	}
	
	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
	}
}
