package net.mgsx.game.blueprint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import net.mgsx.game.blueprint.Graph.CopyStrategy;
import net.mgsx.game.blueprint.annotations.Inlet;
import net.mgsx.game.blueprint.annotations.Node;
import net.mgsx.game.blueprint.annotations.Outlet;
import net.mgsx.game.core.GameApplication;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.helpers.AssetHelper;
import net.mgsx.game.core.screen.StageScreen;

public class BlueprintTest extends GameApplication {

	public static void main(String[] args) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new BlueprintTest(), config);
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
		graph.addNode(new FloatAtom(), 10, 50);
		graph.addNode(new Multiply(), 300, 200);
		
		
		GraphView view = new GraphView(graph, skin);
		view.addNodeType(FloatAtom.class, Multiply.class);
		
		ScrollPane scroll;
		screen.getStage().addActor(scroll = new ScrollPane(view));
		scroll.setFillParent(true);
	}
	
	private static interface Updatable{
		public void update();
	}
	
	@Override
	public void render() {
		for(GraphNode node : graph.dependencyTree()){
			if(node.object instanceof Updatable){
				((Updatable) node.object).update();
			}
		}
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
	}
	
	@Node("f")
	public static class FloatAtom {
		
		@Editable(realtime=true)
		@Outlet
		public float value;
		
	}
	
	@Node("*")
	public static class Multiply implements Updatable {
		
		@Editable(realtime=true)
		@Outlet
		public float value;
		
		@Editable(realtime=true)
		@Inlet
		public float a, b;
		
		@Editable(realtime=true)
		@Inlet 
		public boolean extra;
		
		@Editable(realtime=true)
		@Inlet 
		public int steps;
		
		@Editable
		@Inlet
		public void bang(){
			System.out.println("Bang");
		}
		
		
		@Override
		public void update()
		{
			value = a * b;
		}
	}

	
}
