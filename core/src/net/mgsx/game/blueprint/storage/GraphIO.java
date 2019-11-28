package net.mgsx.game.blueprint.storage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.blueprint.annotations.Node;
import net.mgsx.game.blueprint.model.Graph;
import net.mgsx.game.blueprint.model.GraphNode;
import net.mgsx.game.blueprint.model.Link;
import net.mgsx.game.blueprint.model.Portlet;
import net.mgsx.game.blueprint.ui.GraphView.NodeFactory;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.core.ui.accessors.AccessorScanner;

public class GraphIO {

	private static final class IONode{
		public String type;
		public float x, y;
		public ObjectMap<String, Object> properties = new ObjectMap<String, Object>();
	}
	private static final class IOLink{
		public int src, dst;
		public String out, in;
	}
	private static final class IOGraph{
		public Array<IONode> nodes = new Array<IONode>();
		public Array<IOLink> links = new Array<IOLink>();
	}
	
	public void save(Graph graph, FileHandle file){
		IOGraph g = new IOGraph();
		
		for(GraphNode node : graph.nodes){
			Node an = node.object.getClass().getAnnotation(Node.class);
			IONode n = new IONode();
			n.x = node.position.x;
			n.y = node.position.y;
			n.type = an.value();
			
			for(Accessor param : AccessorScanner.scan(node.object, Editable.class)){
				n.properties.put(param.getName(), param.get());
			}
			
			g.nodes.add(n);
		}
		
		for(Link link : graph.links){
			IOLink l = new IOLink();
			
			l.src = graph.nodes.indexOf(link.src.node, true);
			l.dst = graph.nodes.indexOf(link.dst.node, true);
			
			l.out = link.src.accessor.getName();
			l.in = link.dst.accessor.getName();
			
			g.links.add(l);
		}
		
		Json json = new Json();
		json.setUsePrototypes(false);
		String content = json.prettyPrint(g);
		file.writeString(content, false);
	}
	
	public void load(Graph graph, FileHandle file, Array<NodeFactory> nodeFactories){
		Json json = new Json();
		IOGraph g = json.fromJson(IOGraph.class, file);
		
		ObjectMap<String, NodeFactory> factoryByName = new ObjectMap<String, NodeFactory>();
		for(NodeFactory type : nodeFactories){
			factoryByName.put(type.displayName(), type);
			
		}
		
		for(IONode n : g.nodes){
			NodeFactory factory = factoryByName.get(n.type);
			if(factory == null){
				Gdx.app.error("Blueprint", "no factory for " + n.type);
			}else{
				Object object = factory.create();
				graph.addNode(object, n.x, n.y);
				
				// TODO should be inverted : for each serialized props, set object
				for(Accessor param : AccessorScanner.scan(object, Editable.class)){
					object = n.properties.get(param.getName());
					if(object != null) param.set(object);
				}
			}
		}
		for(IOLink l : g.links){
			GraphNode src = graph.nodes.get(l.src);
			GraphNode dst = graph.nodes.get(l.dst);
			
			Portlet out = null;
			for(Portlet p : src.outlets){
				if(p.accessor.getName().equals(l.out)){
					out = p;
				}
			}
			Portlet in = null;
			for(Portlet p : dst.inlets){
				if(p.accessor.getName().equals(l.in)){
					in = p;
				}
			}
			if(out != null && in != null){
				graph.addLink(out, in);
			}else{
				if(out == null) Gdx.app.error("Blueprint", "no output [" + l.out + "], skip link creation.");
				if(in == null) Gdx.app.error("Blueprint", "no input [" + l.in + "], skip link creation.");
			}
		}
		
	}
}
