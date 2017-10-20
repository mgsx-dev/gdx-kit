package net.mgsx.game.blueprint;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectSet;

import net.mgsx.game.blueprint.annotations.Inlet;
import net.mgsx.game.blueprint.annotations.Outlet;
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
	
	public CopyStrategy copyStrategy = CopyStrategy.NONE;
	
	public Array<NodeView> nodes = new Array<NodeView>();
	public Array<Link> links = new Array<Link>();
	private Skin skin;
	private Array<NodeView> result = new Array<NodeView>();
	private ObjectSet<NodeView> visited = new ObjectSet<NodeView>();

	
	public Graph(Skin skin, CopyStrategy copyStrategy) {
		this.skin = skin;
		this.copyStrategy = copyStrategy;
	}
	
	public NodeView addNode(final Object object, float x, float y) 
	{
		// TODO graph should handle logic nodes and graphview init from current graph and handle edition/sync in itself.
		
		// TODO GraphNode node = new GraphNode();
		NodeView node = new NodeView(this, object, skin);
		node.setX(x);
		node.setY(y);
		
		for(Accessor accessor : AccessorScanner.scan(object, Inlet.class)){
			node.addInlet(new Portlet(node, accessor, accessor.config(Inlet.class)));
		}
		for(Accessor accessor : AccessorScanner.scan(object, Outlet.class)){
			node.addOutlet(new Portlet(node, accessor, accessor.config(Outlet.class)));
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
			node.addInlet(new Portlet(node, a, inlet));
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
			node.addOutlet(new Portlet(node, a, outlet));
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

	public void addLink(Portlet src, Portlet dst) 
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
		
	}
	
	public void removeLinksFrom(Portlet portlet) {
		for(int i=0 ; i<links.size ; ){
			if(links.get(i).src == portlet){
				unlink(links.removeIndex(i));
			}else{
				i++;
			}
		}
	}
	public void removeLinksTo(Portlet portlet) {
		for(int i=0 ; i<links.size ; ){
			if(links.get(i).dst == portlet){
				unlink(links.removeIndex(i));
			}else{
				i++;
			}
		}
	}

	private void unlink(Link link){
		link.src.outLinks.removeValue(link, true);
		link.dst.inputLinks.removeValue(link, true);
	}
	
	public Array<NodeView> dependencyTree(NodeView node){
		result.clear();
		visited.clear();
		scanDependencies(node);
		return result;
	}
	
	public Array<NodeView> dependencyTree(){
		result.clear();
		visited.clear();
		return dependencyTree(nodes);
	}
	
	public Array<NodeView> dependencyTree(Array<NodeView> nodes){
		result.clear();
		visited.clear();
		for(NodeView node : nodes){
			scanDependencies(node);
		}
		return result;
	}
	
	private void scanDependencies(NodeView node)
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
