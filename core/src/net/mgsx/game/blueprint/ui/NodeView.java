package net.mgsx.game.blueprint.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.blueprint.DynamicNode;
import net.mgsx.game.blueprint.Graph;
import net.mgsx.game.blueprint.GraphNode;
import net.mgsx.game.blueprint.Link;
import net.mgsx.game.blueprint.Portlet;
import net.mgsx.game.blueprint.annotations.Node;
import net.mgsx.game.blueprint.events.GraphEvent;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.ui.EntityEditor;

public class NodeView extends Table
{
	public Array<Portlet> inlets = new Array<Portlet>();
	public Array<Portlet> outlets = new Array<Portlet>();

	private Table inletList;
	private Table outletList;
	private HorizontalGroup header;
	public GraphNode node;
	private Graph graph;

	
	public NodeView(Graph graph, GraphNode node, Skin skin) {
		super(skin);
		this.graph = graph;
		this.node = node;
		
		setBackground("default-round");
		
		header = new HorizontalGroup();
		add(header).colspan(2).expandX().center();
		header.addActor(new Label(displayName(node.object), skin));
		row();
		inletList = new Table(skin);
		outletList = new Table(skin);
		add(inletList).expand().fill();
		add(outletList).expand().fill();
		row();
		
		setTouchable(Touchable.enabled);
		
		for(Portlet inlet : node.inlets){
			addInlet(inlet);
		}
		
		for(Portlet outlet : node.outlets){
			addOutlet(outlet);
		}
		
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

	public void addInlet(final Portlet portlet){
		
		TextButton bt = new TextButton("", getSkin());
		bt.setUserObject(portlet);
		portlet.actor = bt;
		inlets.add(portlet);
		
		Table table = inletList;
		table.add(bt);
		table.add(portlet.getName()).expandX().fill();
		
		if(portlet.accessor.config(Editable.class) != null)
		{
			Table tmpTable = new Table(getSkin());
			EntityEditor.createControl(tmpTable, portlet.node.object, portlet.accessor);
			table.add(tmpTable.getCells().first().getActor());
		}
		
		inletList.row();
		
		bt.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Array<Link> links = graph.removeLinksTo(portlet);
				for(Link link : links) fire(new GraphEvent.LinkRemovedEvent(link));
			}
		});

		
	}
	public void addOutlet(final Portlet portlet){
		
		TextButton bt = new TextButton("", getSkin());
		bt.setUserObject(portlet);
		portlet.actor = bt;
		outlets.add(portlet);
		
		Table table = outletList;
		table.add(portlet.getName()).expandX().fill();
		
		if(portlet.accessor.config(Editable.class) != null)
		{
			Table tmpTable = new Table(getSkin());
			EntityEditor.createControl(tmpTable, portlet.node.object, portlet.accessor);
			table.add(tmpTable.getCells().first().getActor());
		}
		table.add(bt);
		
		outletList.row();
		
		bt.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Array<Link> links = graph.removeLinksFrom(portlet);
				for(Link link : links) fire(new GraphEvent.LinkRemovedEvent(link));
			}
		});
		
	}

	
}
