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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GameApplication;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.helpers.AssetHelper;
import net.mgsx.game.core.helpers.shaders.ShaderInfo;
import net.mgsx.game.core.helpers.shaders.ShaderProgramManaged;
import net.mgsx.game.core.helpers.shaders.Uniform;
import net.mgsx.game.core.meta.ClassRegistry;
import net.mgsx.game.core.screen.StageScreen;
import net.mgsx.game.core.ui.EntityEditor;
import net.mgsx.game.core.ui.FieldEditor;
import net.mgsx.game.core.ui.accessors.Accessor;
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

	@Override
	public void create() 
	{
		batch = new SpriteBatch();
		super.create();
		Link.inverted = true; // TODO graph behavior config or general purpose
		
		
		EntityEditor.defaultTypeEditors.put(TextureFlow.class, new FieldEditor() {
			@Override
			public Actor create(Accessor accessor, Skin skin) {
				return new Label(accessor.get(TextureFlow.class) == null ? "=>" : "=>", skin);
			}
		});
		
		Skin skin = AssetHelper.loadAssetNow(assets, "uiskin.json", Skin.class);
		final StageScreen screen;
		setScreen(screen = new StageScreen(skin));
		
		graph = new Graph(skin){
			private Array<NodeView> screens =new Array<NodeView>();
			@Override
			public void addLink(Portlet src, Portlet dst) {
				// TODO should be done in graph itself : in portlet can only have one source !
				removeLinksTo(dst);
//				if(inlet.accessor.get() != null){
//				}
				dst.accessor.set(src.accessor.get());
				super.addLink(src, dst);
			}
			@Override
			public void update() 
			{
				screens.clear();
				for(NodeView node : nodes){
					if(node.object instanceof TextureFlow){
						((TextureFlow)node.object).invalidate();
					}
					if(node.object instanceof ScreenNode){
						ScreenNode s = (ScreenNode)node.object;
						
						screens.add(node);
					}
				}
				batch.setProjectionMatrix(screen.getStage().getBatch().getProjectionMatrix());
				for(NodeView node : screens){
					if(node.object instanceof TextureFlow){
						((TextureFlow)node.object).validate();
					}
				}
				
			}
			
			
		};
		graph.addNode(new SceneRenderingNode(), 10, 50);
		graph.addNode(new BlurNode(), 300, 200);
		graph.addNode(new ScreenNode(), 100, 400);
		graph.addNode(new Compose(), 100, 100);
		// graph.addLink(graph.nodes.get(1), "size", graph.nodes.get(0), "amount");
		
		// TODO filter in class registry ... ?
		for(Class<?> type : ClassRegistry.instance.getTypesAnnotatedWith(Node.class))
			if(type.getName().startsWith(getClass().getName())) 
				graph.addNodeType(type);
		
		
		GraphView view = new GraphView(graph, skin);
		
		// view.setFillParent(true);
		ScrollPane scroll;
		screen.getStage().addActor(scroll = new ScrollPane(view));
		scroll.setFillParent(true);
	}
	
	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		graph.update();
		super.render();
	}
	
	// TODO should run without any dependency to the the graph editor

	
	// validation here is for texture cache only, not related to the graph
	public static class TextureFlow {
		
		public Texture texture;
		
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
		
		@Editable
		@Outlet
		public TextureFlow out = this;
		
		public SceneRenderingNode() {
			texture = new Texture(Gdx.files.internal("perlin.png"));
		}
		
		@Editable
		@Outlet
		public void debug(){
			System.out.println(out);
		}
		
		@Override
		protected void update() {
			// nothing to do
		}
	}
	
	@Node("Blur")
	public static class BlurNode extends TextureFlow {
		
		@Editable
		@Inlet
		public TextureFlow in;
		
		@Editable
		@Outlet
		public TextureFlow out = this;
		
		@Editable
		@Outlet
		public void debug(){
			System.out.println(in);
			System.out.println(out);
		}
		
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
		@Inlet
		public TheShader theShader = new TheShader();
		
		@Override
		public void update()
		{
			in.validate();
			
			
			if(fbo == null || fbo.getWidth() != in.texture.getWidth() || fbo.getHeight() != in.texture.getHeight()){
				if(fbo != null) fbo.dispose();
				fbo = new FrameBuffer(Format.RGBA8888, in.texture.getWidth(), in.texture.getHeight(), true);
				texture = fbo.getColorBufferTexture();
			}
			
			fbo.begin();
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
			theShader.texture = in.texture;
			theShader.projTrans = batch.getProjectionMatrix();
			// theShader.dir.set(v);
			
			theShader.begin();
			
			// TODO sprite draw
			ShaderProgram oldShader = batch.getShader();
			batch.setShader(theShader.program());
			batch.begin();
			batch.draw(in.texture, 0, 0);
			batch.end();
			batch.setShader(oldShader);
			
			theShader.end();
			fbo.end();
		}
	}

	@Node("Depth mixer")
	public static class Compose extends TextureFlow {
		
		@Editable
		@Inlet
		public TextureFlow inner;
		
		@Editable
		@Inlet
		public TextureFlow outer;
		
		@Editable
		@Inlet
		public float rate;
		
		@Editable
		@Outlet
		public TextureFlow out = this;
		
		@Editable
		@Outlet
		public void debug(){
			System.out.println(inner);
			System.out.println(outer);
		}
		
		
		@Override
		public void update()
		{
			if(inner == null) return;
			if(outer == null) return;
			inner.validate();
			outer.validate();
			
			
		}
	}

	@Node("Screen")
	public static class ScreenNode extends TextureFlow {
		
		@Editable
		@Inlet
		public TextureFlow in;
		
		
		@Editable
		@Outlet
		public void debug(){
			System.out.println(in);
		}
		
		
		@Override
		public void update()
		{
			// TODO should be handled by graph itself (dependency graph and cache interface), see
			// if all inlet are set and valide
			// update called only if ... annotation says so !
			if(in == null) return;
			in.validate();
			
			batch.begin();
			batch.draw(in.texture, 0, 0);
			batch.end();
		}
	}

	

	
}
