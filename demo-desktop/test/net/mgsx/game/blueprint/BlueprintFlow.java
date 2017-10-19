package net.mgsx.game.blueprint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.ColorAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import net.mgsx.game.core.GameApplication;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.helpers.AssetHelper;
import net.mgsx.game.core.screen.StageScreen;
import net.mgsx.game.core.ui.EntityEditor;
import net.mgsx.game.core.ui.FieldEditor;
import net.mgsx.game.core.ui.accessors.Accessor;

public class BlueprintFlow extends GameApplication {

	public static void main(String[] args) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new BlueprintFlow(), config);
	}
	private Graph graph;

	@Override
	public void create() 
	{
		super.create();
		Link.inverted = true;
		
		EntityEditor.defaultTypeEditors.put(FlowEvent.class, new FieldEditor() {
			@Override
			public Actor create(Accessor accessor, Skin skin) {
				return new Label(accessor.get(FlowEvent.class) == null ? "=>" : "=>", skin);
			}
		});
		
		Skin skin = AssetHelper.loadAssetNow(assets, "uiskin.json", Skin.class);
		StageScreen screen;
		setScreen(screen = new StageScreen(skin));
		
		graph = new Graph(skin){
			@Override
			public void addLink(Portlet src, Portlet dst) {
				if(src.accessor.get() != null){
					removeLinksFrom(dst);
				}
				src.accessor.set(dst.accessor.get());
				super.addLink(src, dst);
			}

			
		};
		graph.addNode(new Initial(), 10, 50);
		graph.addNode(new Shoot(), 300, 200);
		// graph.addLink(graph.nodes.get(1), "size", graph.nodes.get(0), "amount");
		
		graph.addNodeType(Initial.class, Shoot.class, Flee.class, GameScreen.class, ColorNode.class);
		
		GraphView view = new GraphView(graph, skin);
		
		// view.setFillParent(true);
		ScrollPane scroll;
		screen.getStage().addActor(scroll = new ScrollPane(view));
		scroll.setFillParent(true);
	}
	
	@Override
	public void render() {
		graph.update();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
	}
	
	public static class FlowEvent {
		
		
		
	}
	
	@Node("INIT")
	public static class Initial {
		
		@Editable
		@Outlet
		public FlowEvent exit;
		
		@Editable
		@Outlet
		public void debug(){
			System.out.println(exit);
		}
	}
	
	@Node("ColorAction")
	public static class ColorNode extends FlowEvent {
		
		@Editable
		@Inlet
		public FlowEvent enter = this;
		
		@Editable
		@Outlet
		public FlowEvent exit;
		@Editable
		@Inlet
		public ColorAction action;
		
		public ColorNode() {
			action = new ColorAction();
			action.setColor(new Color()); // avoid NPE
		}
		
		@Editable
		@Outlet
		public void debug(){
			System.out.println(exit);
		}
	}
	
	@Node("shoot")
	public static class Shoot extends FlowEvent implements Updatable {
		
		@Editable
		@Inlet
		public FlowEvent enter = this;
		
		@Editable
		@Outlet
		public FlowEvent onDead, onComplete;
		
		@Editable
		@Outlet
		public void debug(){
			System.out.println(enter);
			System.out.println(onDead);
			System.out.println(onComplete);
		}
		
		
		@Override
		public void update()
		{
		}
	}

	@Node("flee")
	public static class Flee extends FlowEvent implements Updatable {
		
		@Editable
		@Inlet(multiple=true)
		public FlowEvent enter = this;
		
		@Editable
		@Inlet
		public float distance = 1;
		
		@Editable
		@Outlet
		public FlowEvent onComplete;
		
		@Editable
		@Outlet
		public void debug(){
			System.out.println(enter);
			System.out.println(onComplete);
		}
		
		
		@Override
		public void update()
		{
		}
	}
	
	@Node("GameScreen")
	public static class GameScreen extends FlowEvent {
		
		@Editable
		@Inlet
		public FlowEvent enter = this;
		
		@Editable
		@Outlet
		public FlowEvent onGameOver, onAbort, onSuccess;
		
		@Editable
		@Outlet
		public void debug(){
		}
		
		
	}

	
}
