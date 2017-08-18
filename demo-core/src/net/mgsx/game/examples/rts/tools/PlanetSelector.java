package net.mgsx.game.examples.rts.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.SelectorPlugin;
import net.mgsx.game.examples.rts.components.PlanetComponent;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class PlanetSelector extends SelectorPlugin
{

	public PlanetSelector(EditorScreen editor) {
		super(editor);
	}

	@Override
	public int getSelection(Array<Entity> entities, float screenX, float screenY) {
		Vector2 worldPoint = editor.unproject(screenX, screenY);
		int count = 0;
		for(Entity entity : editor.entityEngine.getEntitiesFor(Family.all(PlanetComponent.class, Transform2DComponent.class).get())){
			PlanetComponent planet = entity.getComponent(PlanetComponent.class);
			Transform2DComponent transform = Transform2DComponent.components.get(entity);
			if(worldPoint.dst2(transform.position) <= planet.size * planet.size){
				entities.add(entity);
				count++;
			}
		}
		return count;
	}

}
