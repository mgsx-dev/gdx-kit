package net.mgsx.game.examples.openworld.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.examples.openworld.components.ObjectMeshComponent;
import net.mgsx.game.examples.openworld.model.Compound;
import net.mgsx.game.examples.openworld.model.ElementTemplate;
import net.mgsx.game.examples.openworld.model.OpenWorldElement;
import net.mgsx.game.examples.openworld.model.OpenWorldModel;
import net.mgsx.game.examples.openworld.systems.UserObjectSystem;
import net.mgsx.game.plugins.bullet.system.BulletWorldSystem;

public class CraftTransformTool extends Tool
{

	@Inject BulletWorldSystem bulletWorld;
	@Inject UserObjectSystem userObjectSystem;
	
	public CraftTransformTool(EditorScreen editor) {
		super("Craft Transform", editor);
	}
	
	private static class Pair{
		Entity e1, e2;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		
		if(button != Input.Buttons.LEFT) return false;
		
		Ray ray = camera().getPickRay(screenX, screenY);
		ray.direction.scl(camera().far);
		Ray rayResult = new Ray();
		Entity entity = bulletWorld.rayCast(ray, rayResult);
		
		if(entity != null && ObjectMeshComponent.components.has(entity)){
			
			// find linked entities

			final Array<Pair> pairs = new Array<Pair>();
			
			for(int i=0 ; i<bulletWorld.collisionWorld.getDispatcher().getNumManifolds() ; i++){
				btPersistentManifold m = bulletWorld.collisionWorld.getDispatcher().getManifoldByIndexInternal(i);
				Object o0 = m.getBody0().userData;
				Object o1 = m.getBody1().userData;
				if(o0 instanceof Entity && o1 instanceof Entity){
					Entity e0 = (Entity)o0;
					Entity e1 = (Entity)o1;
					ObjectMeshComponent omc0 = ObjectMeshComponent.components.get(e0);
					ObjectMeshComponent omc1 = ObjectMeshComponent.components.get(e1);
					if(omc0 != null && omc1 != null){
						Pair pair = new Pair();
						pair.e1 = e0;
						pair.e2 = e1;
						pairs.add(pair);
					}
				}
			}
			
			ObjectMap<Entity, ObjectSet<Entity>> map = new ObjectMap<Entity, ObjectSet<Entity>>();
			
			for(Pair p : pairs){
				ObjectSet<Entity> set = map.get(p.e1);
				if(set == null) map.put(p.e1, set = new ObjectSet<Entity>());
				set.add(p.e2);
				set = map.get(p.e2);
				if(set == null) map.put(p.e2, set = new ObjectSet<Entity>());
				set.add(p.e1);
			}
			
			ObjectSet<Entity> result = new ObjectSet<Entity>();
			findRecursive(result , entity, map);
			
			// TODO check which transform in the dictionary
			// if no transform match then create a random garbage ... :-)
			
				
			// aggregate entities by type
			Compound compound = new Compound();
			for(Entity e : result){
				ObjectMeshComponent omc = ObjectMeshComponent.components.get(e);
				compound.add(omc.userObject.element.type);
			}
			
			// TODO serialize recipe in a string (sorted keys + values separated by pipes ...)
			// TODO do the same for all recipes (cache in model)
			// and then map search ... (in model again)
			
			// check if can do it
			ElementTemplate foundRecipe = OpenWorldModel.findFusion(compound);
			
			// remove all anyway !
			for(Entity e : result){
				ObjectMeshComponent omc = ObjectMeshComponent.components.get(e);
				userObjectSystem.removeElement(omc.userObject);
			}

			if(foundRecipe != null){
				// TODO create the new object !
				RandomXS128 rnd = new RandomXS128();
				OpenWorldElement e = OpenWorldModel.generateNewElement(rnd.nextLong());
				e.dynamic = true;
				
				// TODO ? e.size *= size;
				
				e.position.set(rayResult.origin);
				e.rotation.idt();
				e.type = foundRecipe.id;
				
				userObjectSystem.appendObject(e);
			}
			else
			{
				// TODO create some basic objects (fail !)
				RandomXS128 rnd = new RandomXS128();
				OpenWorldElement e = OpenWorldModel.generateNewElement(rnd.nextLong());
				e.dynamic = true;
				
				// TODO ? e.size *= size;
				
				e.position.set(rayResult.origin);
				e.rotation.idt();
				
				// XXX userObjectSystem.appendObject(e);
				
			}
			
			
			return true;
		}
		return false;
	}
	
	private void findRecursive(ObjectSet<Entity> result, Entity current, ObjectMap<Entity, ObjectSet<Entity>> map){
		if(result.add(current)){
			ObjectSet<Entity> set = map.get(current);
			map.remove(current); // kill link to avoid infinite recursion
			if(set != null){
				for(Entity other : set){
					findRecursive(result, other, map);
				}
			}
		}
		
	}
	
}
