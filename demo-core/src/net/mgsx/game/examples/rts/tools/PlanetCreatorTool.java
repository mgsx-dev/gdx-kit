package net.mgsx.game.examples.rts.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.RectangleTool;
import net.mgsx.game.examples.rts.components.PlanetComponent;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class PlanetCreatorTool extends RectangleTool
{

	
	public PlanetCreatorTool(EditorScreen editor) {
		super("Planet Create", editor);
	}

	@Override
	protected void create(Vector2 startPoint, Vector2 endPoint) 
	{
		// scattering ...
		
		float x = Math.min(startPoint.x, endPoint.x);
		float y = Math.min(startPoint.y, endPoint.y);
		float w = Math.max(0, Math.abs(startPoint.x - endPoint.x));
		float h = Math.max(0, Math.abs(startPoint.y - endPoint.y));
		
		generate(x,y,w,h,2);
		
	}
	
	private void generate(float x, float y, float w, float h, int depth){
		float dispersion = 0.2f;
		if(depth <= 0){
			
			Entity entity = getEngine().createEntity();
			PlanetComponent planet = getEngine().createComponent(PlanetComponent.class);
			Transform2DComponent transform = getEngine().createComponent(Transform2DComponent.class);
			transform.position.set(x + w/2, y + h/2);
			float maxSize = Math.min(w, h)/2;
			planet.population = MathUtils.random(50, 100);
			planet.color = Color.BROWN.cpy();
			planet.size = MathUtils.random(.5f, 1) * maxSize;
			entity.add(transform);
			entity.add(planet);
			getEngine().addEntity(entity);
		}else{
			float cx = MathUtils.random(.5f - dispersion, .5f + dispersion) * w;
			float cy = MathUtils.random(.5f - dispersion, .5f + dispersion) * h;
			int nextDepth = depth-1;
			generate(x, y, cx, cy, nextDepth);
			generate(x+cx, y, w-cx, cy, nextDepth);
			generate(x, y+cy, cx, h-cy, nextDepth);
			generate(x+cx, y+cy, w-cx, h-cy, nextDepth);
		}
	}

}
