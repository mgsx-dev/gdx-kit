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

import net.mgsx.game.blueprint.annotations.Inlet;
import net.mgsx.game.blueprint.annotations.Node;
import net.mgsx.game.blueprint.annotations.Outlet;
import net.mgsx.game.blueprint.events.GraphEvent;
import net.mgsx.game.blueprint.model.Graph;
import net.mgsx.game.blueprint.model.GraphNode;
import net.mgsx.game.blueprint.model.Link;
import net.mgsx.game.blueprint.model.Portlet;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.ui.EntityEditor;
import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.core.ui.accessors.AccessorScanner;

public class NodeView extends Table
{
	public Array<Portlet> inlets = new Array<Portlet>();
	public Array<Portlet> outlets = new Array<Portlet>();

	private Table inletList;
	private Table outletList;
	private Table parameterList;
	private HorizontalGroup header;
	public GraphNode node;
	private Graph graph;

	
	public NodeView(Graph graph, GraphNode node, Skin skin) {
		super(skin);
		this.graph = graph;
		this.node = node;
		
		setBackground("default-round");
		
		header = new HorizontalGroup();
		add(header).colspan(3).expandX().center();
		header.addActor(new Label(displayName(node.object), skin));
		row();
		inletList = new Table(skin);
		parameterList = new Table(skin);
		outletList = new Table(skin);
		add(inletList).expand().fill();
		add(parameterList).expand().fill();
		add(outletList).expand().fill();
		row();
		
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
