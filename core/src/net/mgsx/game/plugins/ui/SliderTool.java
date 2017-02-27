package net.mgsx.game.plugins.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.tools.RectangleTool;

public class SliderTool extends RectangleTool
{
	@Inject protected WidgetSystem widgets;
	
	public SliderTool(EditorScreen editor) {
		super("GUI Slider", editor);
	}

	@Override
	protected void create(Vector2 startPoint, Vector2 endPoint) 
	{
		Entity entity = currentEntity();
	
		SliderComponent slider = getEngine().createComponent(SliderComponent.class);
		slider.widget = new Slider(0, 1, .001f, false, editor.loadAssetNow("uiskin.json", Skin.class));
		slider.bounds.setSize(0).setCenter(startPoint).merge(endPoint);
		
		Vector3 sa = editor.getGameCamera().project(new Vector3(startPoint, 0));
		Vector3 sb = editor.getGameCamera().project(new Vector3(endPoint, 0));
		sa.y = Gdx.graphics.getHeight() - sa.y;
		sb.y = Gdx.graphics.getHeight() - sb.y;
		Vector3 a = widgets.viewport.unproject(sa);
		Vector3 b = widgets.viewport.unproject(sb);
		
		slider.bounds.setSize(0).setCenter(new Vector2(a.x, a.y)).merge(new Vector2(b.x, b.y));
		// slider.bounds.x += widgets.
		entity.add(slider);
	}

}
