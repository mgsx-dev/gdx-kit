package net.mgsx.game.examples.rts.tools;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.tools.RectangleTool;
import net.mgsx.game.examples.rts.logic.RtsFactory;
import net.mgsx.game.examples.rts.logic.RtsFactory.GalaxySettings;

@Editable
public class GalaxyCreatorTool extends RectangleTool
{
	@Editable
	public GalaxySettings settings = new GalaxySettings();
	
	public GalaxyCreatorTool(EditorScreen editor) {
		super("Planet Galaxy", editor);
	}

	@Override
	protected void create(Vector2 startPoint, Vector2 endPoint) 
	{
		float x = Math.min(startPoint.x, endPoint.x);
		float y = Math.min(startPoint.y, endPoint.y);
		float w = Math.max(0, Math.abs(startPoint.x - endPoint.x));
		float h = Math.max(0, Math.abs(startPoint.y - endPoint.y));
		
		settings.random.setSeed(settings.seed);
		RtsFactory.createGalaxy(getEngine(), new Rectangle(x, y, w, h), settings);
	}
	


}
