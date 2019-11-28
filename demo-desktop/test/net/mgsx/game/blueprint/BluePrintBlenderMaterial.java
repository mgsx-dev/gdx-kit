package net.mgsx.game.blueprint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.blueprint.BluePrintGLSL.VertexAttributeNode;
import net.mgsx.game.blueprint.annotations.Inlet;
import net.mgsx.game.blueprint.annotations.Node;
import net.mgsx.game.blueprint.annotations.Outlet;
import net.mgsx.game.blueprint.model.Graph;
import net.mgsx.game.blueprint.model.Graph.CopyStrategy;
import net.mgsx.game.blueprint.model.GraphNode;
import net.mgsx.game.blueprint.model.Link;
import net.mgsx.game.blueprint.ui.GraphView;
import net.mgsx.game.blueprint.ui.GraphView.GraphViewConfig;
import net.mgsx.game.core.GameApplication;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.helpers.AssetHelper;
import net.mgsx.game.core.screen.StageScreen;

public class BluePrintBlenderMaterial extends GameApplication {

	public static void main(String[] args) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new BluePrintBlenderMaterial(), config);
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
		config.file = Gdx.files.local("test-blueprint-blender-material.json");
		
		GraphView view = new GraphView(graph, skin, config);
		
		view.addNodeType(VertexAttributeNode.class, SeparateXYZNode.class, MaterialNode.class, TextureSamplerNode.class,
				MaterialOutputNode.class, TexCoordNode.class, GreyScaleNode.class, ValueNode.class);
		
		view.load();
		
		screen.getStage().addActor(view);
		view.setFillParent(true);
	}
	
	public static interface ShaderGenerator{
		public String getVS(Graph graph, GraphNode node);
		public String getFS(Graph graph, GraphNode node);
		
		public String getFragDef();
		public String getVertDef();
	}
	
	public static String getToken(Graph graph, GraphNode node){
		return "node_" + graph.nodes.indexOf(node, true);
	}
	public static String getInputToken(Graph graph, GraphNode node, String name){
		Link link = node.getInput(name);
		if(link != null){
			return getToken(graph, link.src.node) + "_out_" + link.src.accessor.getName();
		}
		return null;
	}
	public static String getOutputToken(Graph graph, GraphNode node, String name){
		Link link = node.getOutput(name);
		if(link != null){
			return getToken(graph, link.dst.node) + "_in_" + link.dst.accessor.getName();
		}
		return null;
	}
	
	private void generate(){
		// find output surface
		Array<GraphNode> mos = graph.findNode(MaterialOutputNode.class);
		if(mos.size > 0){
			GraphNode mo = mos.first();
			Array<GraphNode> deps = graph.dependencyTree(mo);
			
			// Deps graph help structure
			String VSDef = "";
			String FSDef = "";
			String VSCode = "";
			String FSCode = "";
			
			for(GraphNode dep : deps){
				if(dep.object instanceof ShaderGenerator){
					String s;
					
					s= ((ShaderGenerator)dep.object).getVertDef();
					if(s != null) VSDef += s;
					
					s = ((ShaderGenerator)dep.object).getFragDef();
					if(s != null) FSDef += s;
					
					s = ((ShaderGenerator)dep.object).getVS(graph, dep);
					if(s != null) VSCode += s;
					
					s = ((ShaderGenerator)dep.object).getFS(graph, dep);
					if(s != null) FSCode += s;
				}
			}
			
			String VS = VSDef + "void main(){\n\n" + VSCode + "\n\n}\n";
			String FS = FSDef + "void main(){\n\n" + FSCode + "\n\n}\n";
			
			System.out.println("VERTEX SHADER:\n" + VS);
			
			System.out.println("FRAGMENT SHADER:\n" + FS);
			
			
			/*
			Portlet surface = mo.inlets.get(0);
			if(surface.inputLinks.size > 0){
				GraphNode shader = surface.inputLinks.first().src.node;
				if(shader.object instanceof ShaderGenerator){
					ShaderGenerator sg = (ShaderGenerator)shader.object;
					// TODO combine
					String vs = sg.getVS(graph, shader);
					String fs = sg.getFS(graph, shader);
					
					System.out.println(vs);
					System.out.println(fs);
				}
			}
			*/
		}
		
	}
	
	@Override
	public void render() {
		if(Gdx.input.isKeyJustPressed(Input.Keys.G)){
			generate();
		}
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
	}
	
	public static Texture defaultTexture; // TODO init
	
	@Node("Texture Coordinates")
	public static class TexCoordNode implements ShaderGenerator
	{
		@Outlet("Generated") public VertexAttribute generated = VertexAttribute.Position();
		@Outlet("Normal") public VertexAttribute normal = VertexAttribute.Normal();
		@Outlet("UV") public VertexAttribute uv = VertexAttribute.TexCoords(0);
		
		@Override
		public String getVS(Graph graph, GraphNode node) {
			return "v_normal = a_normal;\n";
		}
		@Override
		public String getFS(Graph graph, GraphNode node) {
			// TODO Auto-generated method stub
			return "";
		}
		
		// TODO should be outputed once ! : other method
		/* eg. :
		 * 
		public String getFragDefOnce() {
			return "varying vec2 v_texCoords0;\n" + 
					"varying vec3 v_position;\n" + 
					"varying vec3 v_normal;\n";
		}
		*/
		
		@Override
		public String getFragDef() {
			return "varying vec2 v_texCoords0;\n" + 
					"varying vec3 v_position;\n" + 
					"varying vec3 v_normal;\n";
		}
		@Override
		public String getVertDef() {
			return "varying vec2 v_texCoords0;\n" + 
					"varying vec3 v_position;\n" + 
					"varying vec3 v_normal;\n" + 
					"\n" + 
					"attribute vec3 a_normal;\n";
		}
	}
	
	@Node("Separate XYZ")
	public static class SeparateXYZNode implements ShaderGenerator
	{
		@Inlet("Vector") public VertexAttribute vector;
		@Outlet public FloatAttribute x;
		@Outlet public FloatAttribute y;
		@Outlet public FloatAttribute z;
		
		// TODO can't know if it's in FS or VS
		
		@Override
		public String getVS(Graph graph, GraphNode node) {
			return null;
		}
		@Override
		public String getFS(Graph graph, GraphNode node) {
			String s = "";
			if(node.getOutput("x") != null)
				s += getOutputToken(graph, node, "x") + " = " + getInputToken(graph, node, "vector") + ".x;\n";
			if(node.getOutput("y") != null)
				s += getOutputToken(graph, node, "y") + " = " + getInputToken(graph, node, "vector") + ".y;\n";
			if(node.getOutput("z") != null)
				s += getOutputToken(graph, node, "z") + " = " + getInputToken(graph, node, "vector") + ".z;\n";
			return s;
		}
		@Override
		public String getFragDef() {
			return null;
		}
		@Override
		public String getVertDef() {
			return null;
		}
	}
	
	@Node("Gray Scale")
	public static class GreyScaleNode implements ShaderGenerator
	{
		@Inlet public FloatAttribute value;
		@Outlet public ColorAttribute color;
		
		@Override
		public String getVS(Graph graph, GraphNode node) {
			return "";
		}
		@Override
		public String getFS(Graph graph, GraphNode node) {
			return getOutputToken(graph, node, "color") + " = vec3(" + getInputToken(graph, node, "value") + ");\n";
		}
		@Override
		public String getFragDef() {
			return ""; 
		}
		@Override
		public String getVertDef() {
			// TODO Auto-generated method stub
			return "";
		}
	}
	
	@Node("Value")
	public static class ValueNode
	{
		@Editable public float value;
		@Outlet("value") public FloatAttribute out;
	}
	
	@Node("Principled BSDF")
	public static class MaterialNode implements ShaderGenerator
	{
		@Inlet public ColorAttribute diffuseColor = ColorAttribute.createDiffuse(Color.WHITE);
		
		@Inlet public TextureAttribute diffuseMap = TextureAttribute.createDiffuse(defaultTexture);
		
		@Inlet public FloatAttribute shininess = FloatAttribute.createShininess(10f);
		
		@Outlet("BSDF") public Material bsdf = new Material();

		@Override
		public String getFS(Graph graph, GraphNode node) {
			
			
			// TODO this code is generic and should be externalized
			/*
			String s = "";
			Link dcLink = node.getInput("diffuseColor");
			if(dcLink != null){
				String dcToken = "node_" + graph.nodes.indexOf(dcLink.src.node, true) + "_out_" + dcLink.src.accessor.getName();
				s += "#define diffuseColor " + dcToken + "\n";
				
				// TODO gene code from dcLink.src.node
			}else{
				s += "uniform vec4 u_diffuseColor;\n";
				s += "#define diffuseColor u_diffuseColor\n";
			}
			s = ""; // XXX
			*/
			String s = "";
			// this is the real code
			
			s += "gl_FragColor = vec4(" + getInputToken(graph, node, "diffuseColor") + ".rgb, 1.0);\n";
			
			return s;
		}

		@Override
		public String getVS(Graph graph, GraphNode node) {
			return "";
		}

		@Override
		public String getFragDef() {
			
			return "float getShadow(float x){return x * 4.0;}\n";
		}

		@Override
		public String getVertDef() {
			
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	@Node("sampler")
	public static class TextureSamplerNode implements ShaderGenerator
	{
		@Inlet public VertexAttribute uv;
		
		@Outlet public TextureAttribute texture;

		@Override
		public String getVS(Graph graph, GraphNode node) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getFS(Graph graph, GraphNode node) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getFragDef() {
			return "uniform sampler2D node_?_out_texture;\n";
		}

		@Override
		public String getVertDef() {
			return null;
		}
		
	}
	
	public static class VectorFlow {}
	
	@Node("Material Output")
	public static class MaterialOutputNode
	{
		@Inlet public Material surface;
		@Inlet public Material volume;
		@Inlet public VectorFlow displacement;
		
	}
	
	
	
	
}
