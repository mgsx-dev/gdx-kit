package net.mgsx.game.blueprint;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.ui.EntityEditor;

public class NodeView extends Table
{

	public Array<Portlet> inlets = new Array<Portlet>();
	public Array<Portlet> outlets = new Array<Portlet>();
	private Table inletList;
	private Table outletList;
	private HorizontalGroup header;
	public Object object;
	private Graph graph;

	
	public NodeView(Graph graph, Object object, Skin skin) {
		super(skin);
		this.graph = graph;
		this.object = object;
		
		Node meta = object.getClass().getAnnotation(Node.class);
		String name = meta != null ? meta.value() : "";
		name = name.isEmpty() ? object.getClass().getName() : name;
		
		setBackground("default-round");
		
//		debug();
		
		header = new HorizontalGroup();
		add(header).colspan(2).expandX().center();
		header.addActor(new Label(getTypeName(object.getClass()), skin));
		row();
		inletList = new Table(skin);
		outletList = new Table(skin);
		add(inletList).expand().fill();
		add(outletList).expand().fill();
		row();
		
		setTouchable(Touchable.enabled);
		
		
	}
	
	public void addInlet(final Portlet portlet){
		
		TextButton bt = new TextButton("", getSkin());
		bt.setUserObject(portlet);
		portlet.actor = bt;
		inlets.add(portlet);
		
		Table table = inletList;
		table.add(bt);
		table.add(portlet.getName()).expandX().fill();
		Table tmpTable = new Table(getSkin());
		EntityEditor.createControl(tmpTable, portlet.object, portlet.accessor);
		table.add(tmpTable.getCells().first().getActor());
		
		inletList.row();
		
		bt.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				graph.removeLinksTo(portlet);
			}
		});

//		if(portlet.field.getType() == float.class){
//			inletList.addActor(new FloatWidget(new FieldAccessor(portlet.object, portlet.field), true, false, getSkin()));
//		}
		
	}
	public void addOutlet(final Portlet portlet){
		
		TextButton bt = new TextButton("", getSkin());
		bt.setUserObject(portlet);
		portlet.actor = bt;
		outlets.add(portlet);
		
		Table table = outletList;
		table.add(portlet.getName()).expandX().fill();
		Table tmpTable = new Table(getSkin());
		EntityEditor.createControl(tmpTable, portlet.object, portlet.accessor);
		table.add(tmpTable.getCells().first().getActor());
		table.add(bt);
		
		outletList.row();
		
		bt.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				graph.removeLinksFrom(portlet);
			}
		});
		
//		if(portlet.field.getType() == float.class){
//			outletList.addActor(new FloatWidget(new FieldAccessor(portlet.object, portlet.field), true, false, getSkin()));
//		}
	}

	public static String getTypeName(Class<?> type) {
		Node meta = type.getAnnotation(Node.class);
		String name = meta != null ? meta.value() : "";
		name = name.isEmpty() ? type.getName() : name;
		return name;
	}
	
}
