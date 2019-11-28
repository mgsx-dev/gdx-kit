package net.mgsx.game.blueprint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import net.mgsx.game.blueprint.annotations.Inlet;
import net.mgsx.game.blueprint.annotations.Node;
import net.mgsx.game.blueprint.annotations.Outlet;
import net.mgsx.game.blueprint.model.Graph;
import net.mgsx.game.blueprint.model.Graph.CopyStrategy;
import net.mgsx.game.blueprint.ui.GraphView;
import net.mgsx.game.blueprint.ui.GraphView.GraphViewConfig;
import net.mgsx.game.core.GameApplication;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.helpers.AssetHelper;
import net.mgsx.game.core.screen.StageScreen;
import net.mgsx.game.core.storage.SaveConfiguration.Message;

public class BluePrintPuredata extends GameApplication {
	public static void main(String[] args) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new BluePrintPuredata(), config);
	}
	private Graph graph;

	@Override
	public void create() 
	{
		super.create();
		
		Skin skin = AssetHelper.loadAssetNow(assets, "sgxui/sgx-ui.json", Skin.class);
		StageScreen screen;
		setScreen(screen = new StageScreen(skin));
		
		graph = new Graph(CopyStrategy.FROM_DST);
		
		graph.addNode(new ParamNode(), 10, 50);
		
		
		GraphViewConfig config = new GraphViewConfig();
		config.setTypeColor(Color.ORANGE, AudioFlow.class);
		config.setTypeColor(Color.GOLD, AudioNode.class);
		config.setTypeColor(Color.BLUE, Message.class);
		config.setTypeColor(Color.CYAN, float.class);
		config.setTypeColor(Color.CYAN, MessageNode.class);
		
		GraphView view = new GraphView(graph, skin, config);
		view.addNodeType(AudioMathMulNode.class, ParamNode.class);
		
		ScrollPane scroll;
		screen.getStage().addActor(view);
		view.setFillParent(true);
	}
	
	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
	}
	
	public static class AudioFlow {
		
	}
	public static class MessageFlow {
		
	}
	
	public static class PuredataNode{
		
	}
	public static class AudioNode{
		
	}
	public static class MessageNode{
		
	}
	
	
	@Node("Param")
	public static class ParamNode extends MessageNode {
		
		@Editable
		@Outlet
		public float value;
		
		@Outlet
		public void debug(){
			System.out.println(value);
		}
	}
	
	
	@Node("*~")
	public static class AudioMathMulNode extends AudioNode {
		
		@Editable
		@Inlet
		public float value;
		
		@Inlet
		public AudioFlow in;
		@Outlet
		public AudioFlow out;
		
		@Outlet
		public void debug(){
			System.out.println(value);
		}
	}
}
