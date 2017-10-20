package net.mgsx.game.blueprint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.blueprint.annotations.Inlet;
import net.mgsx.game.blueprint.annotations.Node;
import net.mgsx.game.blueprint.annotations.Outlet;
import net.mgsx.game.blueprint.model.Graph;
import net.mgsx.game.blueprint.model.GraphNode;
import net.mgsx.game.blueprint.model.Link;
import net.mgsx.game.blueprint.model.Portlet;
import net.mgsx.game.blueprint.model.Graph.CopyStrategy;
import net.mgsx.game.blueprint.ui.GraphView;
import net.mgsx.game.core.GameApplication;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.helpers.AssetHelper;
import net.mgsx.game.core.helpers.shaders.ShaderInfo;
import net.mgsx.game.core.helpers.shaders.ShaderProgramManaged;
import net.mgsx.game.core.helpers.shaders.Uniform;
import net.mgsx.game.core.meta.ClassRegistry;
import net.mgsx.game.core.screen.StageScreen;
import net.mgsx.kit.config.ReflectionClassRegistry;


public class BlueprintPostProcessing extends GameApplication {

	public static void main(String[] args) 
	{
		ClassRegistry.instance = new ReflectionClassRegistry("net.mgsx.game.blueprint");
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new BlueprintPostProcessing(), config);
	}
	private Graph graph;
	
	private static Batch batch;

	private Array<GraphNode> screens =new Array<GraphNode>();
	
	private StageScreen screen;
	
	@Override
	public void create() 
	{
		batch = new SpriteBatch();
		super.create();
		
		Skin skin = AssetHelper.loadAssetNow(assets, "uiskin.json", Skin.class);
		setScreen(screen = new StageScreen(skin));
		
		graph = new Graph(CopyStrategy.FROM_SRC);
		
		graph.addNode(new SceneRenderingNode(), 10, 50);
		graph.addNode(new BlurNode(), 300, 200);
		graph.addNode(new ScreenNode(), 100, 400);
		graph.addNode(new Compose(), 100, 100);
		
		
		
		GraphView view = new GraphView(graph, skin);
		// TODO filter in class registry ... ?
		for(Class<?> type : ClassRegistry.instance.getTypesAnnotatedWith(Node.class))
			if(type.getName().startsWith(getClass().getName())) 
				view.addNodeType(type);
		
		ScrollPane scroll;
		screen.getStage().addActor(scroll = new ScrollPane(view));
		scroll.setFillParent(true);
	}
	
	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(screen.getStage().getBatch().getProjectionMatrix());
		
		// we update only active graph parts (nodes participating in screen node)
		screens.clear();
		for(GraphNode node : graph.nodes){
			if(node.object instanceof TextureFlow){
				((TextureFlow)node.object).invalidate();
			}
			if(node.object instanceof ScreenNode){
				screens.add(node);
			}
		}
		
		for(GraphNode node : graph.dependencyTree(screens)){
			// TODO method resolve inlets
			// OR ... change back to process node to avoid texture not existing
			// OR ... texture are created at the node creation (FBO, loading ...etc)
			// this will avoid extra access at each time when graph has not changed
			// For texture, since texture could change (resizing ...Etc)
			// it is safer to link with node directly !
			
			for(Portlet in : node.inlets){
				for(Link link : in.inputLinks){
					link.dst.accessor.set(link.src.accessor.get());
				}
			}
			if(node.object instanceof TextureFlow){
				((TextureFlow)node.object).validate();
			}
			
		}
		
		
		super.render();
	}
	
	// TODO should run without any dependency to the the graph editor

	
	// validation here is for texture cache only, not related to the graph
	public static class TextureFlow {
		
		private boolean valid = false;
		public void invalidate(){
			valid = false;
		}
		public void validate(){
			if(!valid){
				update();
				valid = true;
			}
		}
		protected void update() {
		}
		
	}
	
	@Node("Scene rendering")
	public static class SceneRenderingNode extends TextureFlow {
		
		@Outlet
		public Texture texture;
		
		public SceneRenderingNode() {
			texture = new Texture(Gdx.files.internal("perlin.png"));
		}
	}
	
	@Node("Blur")
	public static class BlurNode extends TextureFlow {
		
		@Inlet
		public Texture in;
		
		@Outlet
		public Texture texture;
		
		@ShaderInfo(vs="shaders/blurx-vertex.glsl", fs="shaders/blurx-fragment.glsl", inject=false)
		public static class TheShader extends ShaderProgramManaged{
			@Uniform 
			public Texture texture;
			@Uniform 
			public Matrix4 projTrans;
			@Uniform("dir")
			public Vector2 dir = new Vector2(1,1);
		}
		
		private FrameBuffer fbo;
		
		@Editable
		@Inlet // TODO shouldn't be an inlet just an editable ...
		public TheShader theShader = new TheShader();
		
		@Override
		public void update()
		{
			if(in == null) return;
			
			if(fbo == null || fbo.getWidth() != in.getWidth() || fbo.getHeight() != in.getHeight()){
				if(fbo != null) fbo.dispose();
				fbo = new FrameBuffer(Format.RGBA8888, in.getWidth(), in.getHeight(), true);
				texture = fbo.getColorBufferTexture();
			}
			
			fbo.begin();
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
			theShader.texture = in;
			theShader.projTrans = batch.getProjectionMatrix();
			// theShader.dir.set(v);
			
			theShader.begin();
			
			// sprite draw
			ShaderProgram oldShader = batch.getShader();
			batch.setShader(theShader.program());
			batch.begin();
			batch.draw(in, 0, 0);
			batch.end();
			batch.setShader(oldShader);
			
			theShader.end();
			fbo.end();
		}
	}

	@Node("Depth mixer")
	public static class Compose extends TextureFlow {
		
		@Outlet
		public Texture texture;
		
		@Inlet
		public Texture inner;
		
		@Inlet
		public Texture outer;
		
		@Editable
		@Inlet
		public float rate;
		
		@Override
		public void update()
		{
			if(inner == null || outer == null) return;
		}
	}

	@Node("Screen")
	public static class ScreenNode extends TextureFlow {
		
		@Inlet
		public Texture in;
		
		@Override
		public void update()
		{
			if(in == null) return;
			
			batch.begin();
			batch.draw(in, 0, 0);
			batch.end();
		}
	}

	

	
}
