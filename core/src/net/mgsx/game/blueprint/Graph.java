package net.mgsx.game.blueprint;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.core.ui.accessors.AccessorScanner;

public class Graph {

	public Array<NodeView> nodes = new Array<NodeView>();
	public Array<Link> links = new Array<Link>();
	private Skin skin;
	public Array<Class> types = new Array<Class>();
	
	public Graph(Skin skin) {
		this.skin = skin;
	}
	
	public void update(){
		
		// TODO nope it's responsability of caller (how to process graph)
		for(Link link : links){
			
			link.update();
		}
		
		for(NodeView node : nodes){
			if(node.object instanceof Updatable){
				((Updatable) node.object).update();
			}
		}
		
	}
	
	public NodeView addNode(Object object, float x, float y) 
	{
		NodeView node = new NodeView(this, object, skin);
		node.setX(x);
		node.setY(y);
		
		for(Accessor accessor : AccessorScanner.scan(object, true, true)){
			Inlet inlet = accessor.config(Inlet.class);
			Outlet outlet = accessor.config(Outlet.class);
			if(inlet != null){
				node.addInlet(new Portlet(object, accessor, inlet));
			}
			if(outlet != null){
				node.addOutlet(new Portlet(object, accessor, outlet));
			}
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

	public void addLink(Portlet src, Portlet dst) {
		
		// TODO subclass may change this ....
		
		// TODO confusion between src/dst and inlet/outlet !!
		Link link = new Link(src, dst);
		links.add(link);
	}
	
	public void removeLinksFrom(Portlet portlet) {
		for(int i=0 ; i<links.size ; ){
			if(links.get(i).src == portlet){
				links.removeIndex(i);
			}else{
				i++;
			}
		}
	}
	public void removeLinksTo(Portlet portlet) {
		for(int i=0 ; i<links.size ; ){
			if(links.get(i).dst == portlet){
				links.removeIndex(i);
			}else{
				i++;
			}
		}
	}

	public void addNodeType(Class ...types) {
		this.types.addAll(types);
	}
	public void addNodeType(Array<Class<?>> types) {
		this.types.addAll(types);
	}
	
}
