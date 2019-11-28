package net.mgsx.game.blueprint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
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

public class BluePrintGLSL extends GameApplication {

	public static void main(String[] args) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new BluePrintGLSL(), config);
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
		
		view.addNodeType(VertexAttributeNode.class, EnvNode.class, MaterialNode.class, TextureSamplerNode.class,
				MeshNode.class);
		
		screen.getStage().addActor(view);
		view.setFillParent(true);
	}
	
	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
	}
	
	public static Texture defaultTexture; // TODO init
	
	@Node("vertex")
	public static class VertexAttributeNode
	{
		@Outlet public VertexAttribute position = VertexAttribute.Position();
		@Outlet public VertexAttribute normal = VertexAttribute.Normal();
		@Outlet public VertexAttribute texCoords0 = VertexAttribute.TexCoords(0);
		@Outlet public VertexAttribute texCoords1 = VertexAttribute.TexCoords(1);
	}
	
	@Node("environment")
	public static class EnvNode
	{
		@Outlet public ColorAttribute ambientLight = new ColorAttribute(ColorAttribute.AmbientLight, Color.BLACK);
//		@Outlet TextureAttribute normal = TextureAttribute.cre.Normal();
//		@Outlet VertexAttribute texCoords0 = VertexAttribute.TexCoords(0);
//		@Outlet VertexAttribute texCoords1 = VertexAttribute.TexCoords(1);
		
	}
	
	@Node("material")
	public static class MaterialNode
	{
		@Inlet public ColorAttribute diffuseColor = ColorAttribute.createDiffuse(Color.WHITE);
		
		@Inlet public TextureAttribute diffuseMap = TextureAttribute.createDiffuse(defaultTexture);
		
		@Editable
		@Inlet public FloatAttribute shininess = FloatAttribute.createShininess(10f);
		
		@Outlet public Material material = new Material();
		
	}
	
	@Node("sampler")
	public static class TextureSamplerNode
	{
		@Inlet public VertexAttribute uv;
		
		@Editable
		@Outlet public TextureAttribute texture;
		
	}
	
	public static class MeshFlow {}
	
	@Outlet
	@Node("mesh")
	public static class MeshNode extends MeshFlow
	{
		@Inlet public Material material;
	}
	
	@Outlet
	@Node("render")
	public static class RenderNode extends MeshFlow
	{
		@Inlet public MeshFlow mesh;
	}
	
	
	
	
}
