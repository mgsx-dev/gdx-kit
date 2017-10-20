package net.mgsx.game.blueprint.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectSet;

import net.mgsx.game.blueprint.annotations.Inlet;
import net.mgsx.game.blueprint.annotations.Outlet;
import net.mgsx.game.blueprint.ui.NodeView;
import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.core.ui.accessors.AccessorBase;
import net.mgsx.game.core.ui.accessors.AccessorScanner;

// TODO storage : use KIT storage infra : just implements JSON serializable
// need to store the nodes (if it's entity/components or system maybe just reference it, if not store !)
// don't store inlet/outlet fields but store the graph (from X.name to Y.name with X/Y = node id)
// TODO implements a special serializer ...

/**
 * Blueprint graph
 * 
 * There is a lot of graph types in nodal programming :
 * - it could be a dependency graph : nodes are linked together forming a process flow.
 * - it could be a flow graph : nodes are linked together forming a workflow.
 * - it could be a tree : nodes are linked together and relative position means children ordering
 * - it could be a event graph : messages are sent node to nodes (order of processing is important like triggers in PD)
 * 
 * Behaviors can be mixed : a flow graph with some dependencies. In this case, the copy strategy should be
 * defined in portlets.
 * 
 * So this graph abstraction can implements all of these. The commons are :
 * - there is nodes : a node is java object
 * - there is portlets (inlets and outlets) : a portlet is an accessor (a field, a method or the object itself)
 * - there is links : a link just reference an outlet as source and an inlet as destination.
 * 
 * There is so common operations :
 * - get a dependency graph : an ordereed list of dependend nodes, can be processed in this order in a
 *   dependency graph senario to resolve dependencies first.
 * 
 * @author mgsx
 *
 */
public class Graph 
{
	/**
	 * could be renamed as UpdateOutlet/UpdateInlet
	 */
	public static enum CopyStrategy {
		NONE, FROM_SRC, FROM_DST
	}
	
	public CopyStrategy copyStrategy;
	
	public Array<GraphNode> nodes = new Array<GraphNode>();
	public Array<Link> links = new Array<Link>();
	private transient Array<GraphNode> result = new Array<GraphNode>();
	private transient ObjectSet<GraphNode> visited = new ObjectSet<GraphNode>();

	public Graph() {
		this(CopyStrategy.NONE);
	}
	public Graph(CopyStrategy copyStrategy) {
		this.copyStrategy = copyStrategy;
	}
	
	public GraphNode addNode(final Object object, float x, float y) 
	{
		GraphNode node = new GraphNode(object);
		node.position.set(x, y);
		
		for(Accessor accessor : AccessorScanner.scan(object, Inlet.class)){
			node.inlets.add(new Portlet(node, accessor, accessor.config(Inlet.class)));
		}
		for(Accessor accessor : AccessorScanner.scan(object, Outlet.class)){
			node.outlets.add(new Portlet(node, accessor, accessor.config(Outlet.class)));
		}
		
		Inlet inlet = object.getClass().getAnnotation(Inlet.class);
		if(inlet != null){
			Accessor a = new AccessorBase() {
				
				@Override
				public void set(Object value) {
					throw new GdxRuntimeException("not supported");
				}
				
				@Override
				public Class getType() {
					return object.getClass();
				}
				
				@Override
				public String getName() {
					return "in";
				}
				
				@Override
				public Object get() {
					return object;
				}
				
			};
			node.inlets.add(new Portlet(node, a, inlet));
		}
		
		final Outlet outlet = object.getClass().getAnnotation(Outlet.class);
		if(outlet != null){
			Accessor a = new AccessorBase() {
				
				@Override
				public void set(Object value) {
					throw new GdxRuntimeException("not supported");
				}
				
				@Override
				public Class getType() {
					return object.getClass();
				}
				
				@Override
				public String getName() {
					return "out";
				}
				
				@Override
				public Object get() {
					return object;
				}
				
			};
			node.outlets.add(new Portlet(node, a, outlet));
		}
		
		
		nodes.add(node);
		return node;
	}

	private Portlet getInlet(NodeView node, String name) {
		for(Portlet let : node.inlets) if(let.getName().equals(name)) return let;
		return null;
	}

	private Portlet getOutlet(NodeView node, String name) {
		for(Portlet let : node.outlets) if(let.getName().equals(name)) return let;
		return null;
	}

	public void addLink(NodeView src, String srcOut, NodeView dst, String dstIn) {
		addLink(getInlet(dst, dstIn), getOutlet(src, srcOut));
	}

	public Link addLink(Portlet src, Portlet dst) 
	{
		switch(copyStrategy){
		case FROM_DST:
			removeLinksFrom(src);
			src.accessor.set(dst.accessor.get());
			break;
		case FROM_SRC:
			removeLinksTo(dst);
			dst.accessor.set(src.accessor.get());
			break;
		default:
			break;
		}
		
		Link link = new Link(src, dst);
		src.outLinks.add(link);
		dst.inputLinks.add(link);
		links.add(link);
		
		return link;
		
	}
	
	public Array<Link> removeLinksFrom(Portlet portlet) {
		Array<Link> removedLinks = new Array<Link>();
		for(Link link : links){
			if(link.src == portlet){
				removedLinks.add(link);
			}
		}
		for(Link link : removedLinks){
			unlink(link);
		}
		links.removeAll(removedLinks, true);
		return removedLinks;
	}
	public Array<Link> removeLinksTo(Portlet portlet) {
		Array<Link> removedLinks = new Array<Link>();
		for(Link link : links){
			if(link.dst == portlet){
				removedLinks.add(link);
			}
		}
		for(Link link : removedLinks){
			unlink(link);
		}
		links.removeAll(removedLinks, true);
		return removedLinks;
	}

	private void unlink(Link link){
		link.src.outLinks.removeValue(link, true);
		link.dst.inputLinks.removeValue(link, true);
	}
	
	public Array<GraphNode> dependencyTree(GraphNode node){
		result.clear();
		visited.clear();
		scanDependencies(node);
		return result;
	}
	
	public Array<GraphNode> dependencyTree(){
		result.clear();
		visited.clear();
		return dependencyTree(nodes);
	}
	
	public Array<GraphNode> dependencyTree(Array<GraphNode> nodes){
		result.clear();
		visited.clear();
		for(GraphNode node : nodes){
			scanDependencies(node);
		}
		return result;
	}
	
	private void scanDependencies(GraphNode node)
	{
		if(visited.add(node)){
			for(Portlet portlet : node.inlets){
				for(Link link : portlet.inputLinks){
					scanDependencies(link.src.node);
				}
			}
			result.add(node);
		}
	}
	
}
