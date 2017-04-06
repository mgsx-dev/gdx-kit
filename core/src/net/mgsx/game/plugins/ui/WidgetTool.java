package net.mgsx.game.plugins.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.tools.RectangleTool;

abstract public class WidgetTool extends RectangleTool
{
	@Inject protected WidgetSystem widgets;
	
	final private WidgetFactory factory;
	
	public WidgetTool(String name, EditorScreen editor, WidgetFactory factory) {
		super(name, editor);
		this.factory = factory;
	}

	@Override
	protected void create(Vector2 startPoint, Vector2 endPoint) 
	{
		Entity entity = currentEntity();
	
		WidgetComponent widget = getEngine().createComponent(WidgetComponent.class);
		widget.bounds.setSize(0).setCenter(startPoint).merge(endPoint);
		
		Vector3 sa = editor.getGameCamera().project(new Vector3(startPoint, 0));
		Vector3 sb = editor.getGameCamera().project(new Vector3(endPoint, 0));
		sa.y = Gdx.graphics.getHeight() - sa.y;
		sb.y = Gdx.graphics.getHeight() - sb.y;
		Vector3 a = widgets.viewport.unproject(sa);
		Vector3 b = widgets.viewport.unproject(sb);
		
		widget.widget = factory.createActor(getEngine(), entity, widgets.skin);
		widget.factory = factory;
		
		widget.bounds.setSize(0).setCenter(new Vector2(a.x, a.y)).merge(new Vector2(b.x, b.y));
		entity.add(widget);
	}

}
