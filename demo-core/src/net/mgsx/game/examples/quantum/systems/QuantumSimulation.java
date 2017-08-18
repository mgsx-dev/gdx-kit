package net.mgsx.game.examples.quantum.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.examples.quantum.components.Link;
import net.mgsx.game.examples.quantum.components.Planet;

@EditableSystem
public class QuantumSimulation extends EntitySystem
{
	@Editable public String author = "";
	@Editable public String name = "";
	@Editable public String description = "";
	@Editable public long seed;
	public int nextId;
	
	public QuantumSimulation() {
		super(GamePipeline.LOGIC);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(Family.all(Planet.class).get(), new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				for(Entity linkEntity : getEngine().getEntitiesFor(Family.all(Link.class).get())){
					Link link = Link.components.get(linkEntity);
					if(link.source == entity || link.target == entity){
						getEngine().removeEntity(linkEntity);
					}
				}
			}
			
			@Override
			public void entityAdded(Entity entity) {
				Planet planet = Planet.components.get(entity);
				planet.id = nextId++;
			}
		});
	}
}
