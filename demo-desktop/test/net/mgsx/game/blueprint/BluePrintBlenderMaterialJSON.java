package net.mgsx.game.blueprint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.blueprint.model.Graph;
import net.mgsx.game.blueprint.model.Graph.CopyStrategy;
import net.mgsx.game.blueprint.model.GraphNode;
import net.mgsx.game.blueprint.model.Portlet;
import net.mgsx.game.blueprint.ui.GraphView;
import net.mgsx.game.blueprint.ui.GraphView.GraphViewConfig;
import net.mgsx.game.core.GameApplication;
import net.mgsx.game.core.helpers.AssetHelper;
import net.mgsx.game.core.screen.StageScreen;
import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.core.ui.accessors.AccessorBase;

public class BluePrintBlenderMaterialJSON extends GameApplication {

	public static final int VEC1 = 1;
	public static final int VEC2 = 2;
	public static final int VEC3 = 3;
	public static final int VEC4 = 4;

	public static void main(String[] args) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new BluePrintBlenderMaterialJSON(), config);
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
		// config.file = Gdx.files.local("test-blueprint-blender-material-json.json");
		
		DataGraph dataGraph = new Json().fromJson(DataGraph.class, Gdx.files.absolute("/tmp/test.json"));
		
		for(DataNode dataNode : dataGraph.nodes){
			dataNode.graph = dataGraph;
			dataNode.index = graph.nodes.size;
			dataNode.data = graph.addNode(dataNode, dataNode.x, dataNode.y);
			for(final DataSocket dataInput : dataNode.inputs){
				AccessorBase fromAccessor = new AccessorBase() {
					@Override
					public void set(Object value) {
						// TODO Auto-generated method stub
					}
					
					@Override
					public Class getType() {
						switch(dataInput.type){
						case SHADER:
							return ShaderFlow.class;
						case RGBA:
						case VALUE:
						case VECTOR:
							return VectorFlow.class;
						default:
							return null;
						}
					}
					
					@Override
					public String getName() {
						return dataInput.name;
					}
					
					@Override
					public Object get() {
						return null; // XXX dataInput.value;
					}
				};
				dataNode.inputAccessors.put(dataInput.id, fromAccessor);
				dataNode.data.inlets.add(new Portlet(dataNode.data, fromAccessor, true));
			}
			for(final DataSocket dataOutput : dataNode.outputs){
				AccessorBase toAccessor = new AccessorBase() {
					@Override
					public void set(Object value) {
						// TODO Auto-generated method stub
					}
					
					@Override
					public Class getType() {
						switch(dataOutput.type){
						case SHADER:
							return ShaderFlow.class;
						case RGBA:
						case VALUE:
						case VECTOR:
							return VectorFlow.class;
						default:
							return null;
						}
					}
					
					@Override
					public String getName() {
						return dataOutput.name;
					}
					
					@Override
					public Object get() {
						// TODO Auto-generated method stub
						return null;
					}
				};
				dataNode.outputAccessors.put(dataOutput.id, toAccessor);
				dataNode.data.outlets.add(new Portlet(dataNode.data, toAccessor, false));
			}
		}
		for(DataLink dataLink : dataGraph.links){
			DataNode srcNode = dataGraph.nodes.get(dataLink.src_node);
			DataNode dstNode = dataGraph.nodes.get(dataLink.dst_node);
			Portlet src = srcNode.findOutlet(dataLink.src_port);
			Portlet dst = dstNode.findInlet(dataLink.dst_port);
			graph.addLink(src, dst);
		}
		
		DataNode root = null;
		for(DataNode n : dataGraph.nodes){
			if(n.type == NodeType.OUTPUT_MATERIAL){
				root = n;
			}
		}
		if(root != null){
			Array<String> shaders = dataGraph.generate(root);
			for(String line : shaders){
				System.out.println(line);
			}
		}
		
		GraphView view = new GraphView(graph, skin, config);
		
		view.addNodeType(); // TODO
		
		screen.getStage().addActor(view);
		view.setFillParent(true);
	}
	
	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
	}
	
	private static class ShaderFlow{}
	private static class VectorFlow{}
	
	private static class DataGraph{
		public final Array<DataNode> nodes = new Array<DataNode>();
		public final Array<DataLink> links = new Array<DataLink>();
		public Array<String> generate(DataNode root) {
			Array<String> code = new Array<String>();
			code.add("void main() {");
			root.genFragMain(code);
			code.add("}");
			return code;
		}
	}
	private static class DataNode{
		public DataGraph graph;
		private static final String indent = "    ";
		public transient int index;
		public float x,y,w,h;
		public String name;
		public NodeType type;
		public final Array<DataSocket> inputs = new Array<DataSocket>();
		public final Array<DataSocket> outputs = new Array<DataSocket>();
		public final ObjectMap<String, String> settings = new ObjectMap<String, String>();
		
		public transient GraphNode data;
		public transient final ObjectMap<String, Accessor> inputAccessors = new ObjectMap<String, Accessor>();
		public transient final ObjectMap<String, Accessor> outputAccessors = new ObjectMap<String, Accessor>();
		public Portlet findInlet(String src_port) {
			Accessor a = inputAccessors.get(src_port);
			for(Portlet e : data.inlets) if(e.accessor == a) return e;
			return null;
		}
		private DataSocket findInput(String name){
			for(DataSocket e : inputs) if(e.id.equals(name)) return e;
			return null;
		}
		private DataSocket findOutput(String name){
			for(DataSocket e : outputs) if(e.id.equals(name)) return e;
			return null;
		}
		
		public void genFragMain(Array<String> code) {
			/*
			 * should be : 
			 * 
			 * bsdf_baseColor = vec4(UVMap.x, 1.0)
			 * 
			 */
			
			switch (type) {
			case BSDF_PRINCIPLED:
				addCodeLine(code, "vec4 $_color = vec4(0.0)");
				addCodeLine(code, "vec4 $_base_color = " + genIn("Base Color", VEC4));
				addCodeLine(code, "$_color = $_baseColor");
				addCodeLine(code, "gl_FragColor = $_color");
				break;
			case OUTPUT_MATERIAL:
				DataNode in = findSource("Surface");
				if(in == null){
					code.add(indent + "discard;");
					// TODO ? code.add(indent + "gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);");
				}else{
					in.genFragMain(code);
					// code.add(indent + "gl_FragColor = " + genIn("Surface") + ";");
				}
				break;
			case SEPXYZ:
				break;
			case UVMAP:
				break;
			default:
				break;
			}
		}
		private void addCodeLine(Array<String> code, String content) {
			code.add(indent + content.replaceAll("\\$", varName()) + ";");
		}
		private DataNode findSource(String id) {
			for(DataLink l : graph.links){
				if(graph.nodes.get(l.dst_node) == this && l.dst_port.equals(id)){
					return graph.nodes.get(l.src_node);
				}
			}
			return null;
		}
		public Portlet findOutlet(String dst_port) {
			Accessor a = outputAccessors.get(dst_port);
			for(Portlet e : data.outlets) if(e.accessor == a) return e;
			return null;
		}
		
		public String genOut(String name, int outType){
			switch (type) {
			case BSDF_PRINCIPLED:
				break;
			case OUTPUT_MATERIAL:
				break;
			case SEPXYZ:
				if(name.equals("X")) return convert(genIn("Vector", VEC3) + ".x", VEC1, outType, false);
				if(name.equals("Y")) return convert(genIn("Vector", VEC3) + ".y", VEC1, outType, false);
				if(name.equals("Z")) return convert(genIn("Vector", VEC3) + ".z", VEC1, outType, false);
				break;
			case MATH:
				if(name.equals("Value")){
					String operation = settings.get("operation", "ADD");
					if(operation.equals("ADD")){
						DataSocket pa = inputs.get(0);
						DataSocket pb = inputs.get(1);
						if(pa.type == pb.type){
							int ptype = 0;
							switch(pa.type){
							case RGBA:
								ptype = VEC4;
								break;
							case SHADER:
								break;
							case VALUE:
								ptype = VEC1;
								break;
							case VECTOR:
								ptype = VEC3;
								break;
							default:
								break;
							
							}
							return convert("(" + genIn(pa, ptype) + " + " + genIn(pb, ptype) + ")", ptype, VEC4, true);
						}
						return "(" + genIn(pa, VEC4) + " + " + genIn(pb, VEC4) + ")";
					}
				}
				break;
			case UVMAP:
				if(name.equals("UV")){
					String mapName = settings.get("uv_map", "UV Map");
					
					return mapName.replaceAll(" +", "_");
				}
				break;
			}
			return notFound(name);
		}
		private String convert(String code, int from, int to, boolean group) {
			switch (to) {
			case VEC1: 
				switch (from) {
				case VEC1: return group ? "(" + code + ")" : code;
				case VEC2: return code + ".x";
				case VEC3: return code + ".x";
				case VEC4: return code + ".x";
				}
			case VEC2: 
				switch (from) {
				case VEC1: return "vec2(" + code + ")";
				case VEC2: return  group ? "(" + code + ")" : code;
				case VEC3: return code + ".xy";
				case VEC4: return code + ".xy";
				}
			case VEC3: 
				switch (from) {
				case VEC1: return "vec3(" + code + ")";
				case VEC2: return "vec3(" + code + ")";
				case VEC3: return  group ? "(" + code + ")" : code;
				case VEC4: return "vec3(" + code + ")";
				}
			case VEC4: 
				switch (from) {
				case VEC1: return "vec4(" + code + ")";
				case VEC2: return "vec4(" + code + ")";
				case VEC3: return "vec4(" + code + ")";
				case VEC4: return  group ? "(" + code + ")" : code;
				}
			}
			return  group ? "(" + code + ")" : code;
		}
		public String genIn(String name, int inType){
			DataNode src = findSource(name);
			if(src != null){
				return src.genOut(findSourcePort(name), inType);
			}
			/*
			switch (type) {
			case BSDF_PRINCIPLED:
				DataNode src = findSource(name);
				if(src != null){
					return src.genOut(findSourcePort(name), VEC4);
				}
				break;
			case OUTPUT_MATERIAL:
				if(name.equals("Surface")) return varName();
				break;
			case SEPXYZ:
				if(name.equals("Vector")) return varName();
			case UVMAP:
				break;
			}
			 */
			return notFound(name);
		}
		public String genIn(DataSocket port, int inType){
			for(DataLink l : graph.links){
				if(graph.nodes.get(l.dst_node) == this && l.dst_port.equals(port.id)){
					return graph.nodes.get(l.src_node).genOut(l.src_port, inType);
				}
			}
			switch (inType) {
			case VEC1: 
				return port.value[0] + "";
			case VEC2: 
				return "vec2(" + port.value[0] + ", " + port.value[1] + ")";
			case VEC3: return "vec3(" + port.value[0] + ", " + port.value[1] + ", " + port.value[2] + ")";
			case VEC4: 
				switch(port.value.length){
				case 1: return "vec4(" + port.value[0] + ")";
				case 2: return "vec4(" + port.value[0] + ")";
				case 3: return "vec4(" + port.value[0] + ")";
				default:
					return "vec4(" + port.value[0] + ", " + port.value[1] + ", " + port.value[2] + ", " + port.value[3] + ")";
				}
			}
			return null;
		}
		private String findSourcePort(String id) {
			for(DataLink l : graph.links){
				if(graph.nodes.get(l.dst_node) == this && l.dst_port.equals(id)){
					return l.src_port;
				}
			}
			return null;
		}
		private String notFound(String name) {
			throw new GdxRuntimeException("Property not found " + name);
		}
		private String varName() {
			return "var" + index;
		}
	}
	private static class DataLink{
		public int src_node, dst_node;
		public String src_port, dst_port;
	}
	private static enum NodeType{
		OUTPUT_MATERIAL, BSDF_PRINCIPLED, UVMAP, SEPXYZ, MATH, ADD_SHADER, EMISSION
	}
	
	private static enum DataType{
		SHADER, VECTOR, RGBA, VALUE
	}
	private static class DataSocket{
		public String id, name;
		public DataType type;
		public float [] value;
		
	}
	
	
	
	
	
	
}
