package net.mgsx.game.plugins.particle2d.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.boundary.components.BoundaryComponent;
import net.mgsx.game.plugins.particle2d.components.Particle2DComponent;

public class P2DBoundaryTool extends Tool
{
	public P2DBoundaryTool(EditorScreen editor) {
		super("Particle Boundary", editor);
		activator = Family.all(Particle2DComponent.class).get();
	}
	
	@Override
	protected void activate() {
		
		
		Entity entity = editor.currentEntity();
		Particle2DComponent p = Particle2DComponent.components.get(entity);
		
		BoundaryComponent boundary = BoundaryComponent.components.get(entity);
		if(boundary == null) entity.add(getEngine().createComponent(BoundaryComponent.class));
		
		p.localBox.set(p.effect.getBoundingBox());
		p.localBox.min.sub(p.position.x, p.position.y, 0);	
		p.localBox.max.sub(p.position.x, p.position.y, 0);	
		p.localBox.set(p.localBox.min, p.localBox.max);
		end();
	}

}
