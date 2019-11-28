package net.mgsx.game.blueprint.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.mgsx.game.blueprint.annotations.Inlet;
import net.mgsx.game.blueprint.annotations.Node;
import net.mgsx.game.blueprint.annotations.Outlet;
import net.mgsx.game.blueprint.events.GraphEvent;
import net.mgsx.game.blueprint.model.Graph;
import net.mgsx.game.blueprint.model.GraphNode;
import net.mgsx.game.blueprint.model.Link;
import net.mgsx.game.blueprint.model.Portlet;
import net.mgsx.game.blueprint.ui.GraphView.GraphViewConfig;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.ui.EntityEditor;
import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.core.ui.accessors.AccessorScanner;

public class NodeView extends Table
{
	public Array<Portlet> inlets = new Array<Portlet>();
	public Array<Portlet> outlets = new Array<Portlet>();

	protected Table inletList;
	protected Table outletList;
	protected Table parameterList;
	protected HorizontalGroup header;
	public GraphNode node;
	private Graph graph;
	protected final GraphViewConfig config;

	
	public NodeView(GraphViewConfig config, Graph graph, GraphNode node, Skin skin) {
		super(skin);
		this.config = config;
		this.graph = graph;
		this.node = node;
		
		setBackground(skin.has("button-small", Drawable.class) ? "button-small" : "default-round");
		
		header = new HorizontalGroup();
		header.addActor(new Label(displayName(node.object), skin, skin.has("title", LabelStyle.class) ? "title" : "default"));
		inletList = new Table(skin);
		parameterList = new Table(skin);
		outletList = new Table(skin);
		
		
		layoutNode();
		
		setTouchable(Touchable.enabled);
		
		for(Portlet inlet : node.inlets){
			addInlet(inlet);
		}
		
		for(Portlet outlet : node.outlets){
			addOutlet(outlet);
		}
		
		for(Accessor param : AccessorScanner.scan(node.object, Editable.class)){
			if(param.config(Inlet.class) == null && param.config(Outlet.class) == null){
				parameterList.add(param.getName());
				Table tmpTable = new Table(getSkin());
				EntityEditor.createControl(tmpTable, node.object, param);
				parameterList.add(tmpTable.getCells().first().getActor());
				parameterList.row();
			}
		}
		
	}
	
	protected void layoutNode(){
		add(header).colspan(3).expandX().center();
		row();
		add(inletList).expand().fill();
		add(parameterList).expand().fill();
		add(outletList).expand().fill();
		row();
	}
	
	private String displayName(Object object) {
		if(object instanceof DynamicNode){
			return ((DynamicNode) object).displayName();
		}
		Node node = object.getClass().getAnnotation(Node.class);
		if(node != null && !node.value().isEmpty()){
			return node.value();
		}
		return object.getClass().getSimpleName();
	}

	protected Actor createPortlet(final Portlet portlet){
		TextButton bt = new TextButton("", getSkin());
		return bt;
	}
	
	public final void addInlet(final Portlet portlet){
		
		Actor actor = createPortlet(portlet);
		
		actor.setUserObject(portlet);
		portlet.actor = actor;
		
		colorize(portlet, actor);
		
		inlets.add(portlet);
		
		Table table = inletList;
		
		addPortletActor(table, portlet, actor);
		
		actor.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Array<Link> links = graph.removeLinksTo(portlet);
				for(Link link : links) fire(new GraphEvent.LinkRemovedEvent(link));
			}
		});
	}
	
	public final void addOutlet(final Portlet portlet){
		
		Actor actor = createPortlet(portlet);
		
		actor.setUserObject(portlet);
		portlet.actor = actor;
		
		colorize(portlet, actor);
		
		outlets.add(portlet);
		
		Table table = outletList;
		
		addPortletActor(table, portlet, actor);
		
		actor.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Array<Link> links = graph.removeLinksFrom(portlet);
				for(Link link : links) fire(new GraphEvent.LinkRemovedEvent(link));
			}
		});
		
	}

	protected void colorize(Portlet portlet, Actor actor) {
		Class portletType = portlet.accessor.getType();
		Color c = Color.WHITE;
		for(Entry<Class, Color> e : config.colorByType){
			if(e.key.isAssignableFrom(portletType)){
				c = e.value;
				break;
			}
		}
		actor.setColor(c);
		portlet.color.set(c);
	}

	protected void addPortletActor(Table table, Portlet portlet, Actor portletActor) {
		
		Actor editor = createEditor(portlet);
		
		if(portlet.outlet != null){
			
			table.add(portlet.getName()).expandX().fill();
			if(editor != null) table.add(editor);
			table.add(portletActor);
		}else{
			table.add(portletActor);
			if(editor != null) table.add(editor);
			table.add(portlet.getName()).expandX().fill();
		}
		
		
		table.row();
	}
	
	protected final Actor createEditor(Portlet portlet){
		if(portlet.accessor.config(Editable.class) != null)
		{
			Table tmpTable = new Table(getSkin());
			EntityEditor.createControl(tmpTable, portlet.node.object, portlet.accessor);
			return tmpTable.getCells().first().getActor();
		}
		return null;
	}

	
}
