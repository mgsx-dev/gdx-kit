package net.mgsx.game.examples.td.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.examples.td.components.Active;
import net.mgsx.game.examples.td.components.Enemy;
import net.mgsx.game.examples.td.components.Follow;
import net.mgsx.game.examples.td.components.PathFollower;
import net.mgsx.game.examples.td.components.Tower;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.spline.components.PathComponent;

/**
 * Ingame tool :
 * * touch down => 
 * ** free => nothing
 * ** same => unselect
 * ** another => select new entity
 * ** enemy => set prio target (end tool)
 * * drag => draw
 * * drop => 
 * ** empty => target destination
 * ** ally => link
 * ** enemy => follow target(and unlink) or link
 * 
 * @author mgsx
 *
 */
public class GameTool extends FollowPathTool
{
	public GameTool(EditorScreen editor) {
		super("Game Tool", editor);
		towers = editor.entityEngine.getEntitiesFor(towerFamily = Family.all(Transform2DComponent.class).one(Tower.class, Enemy.class).get());
		activeEnemies = editor.entityEngine.getEntitiesFor(towerFamily = Family.all(Enemy.class, Active.class).get());
	}

	private Entity current;
	private Family towerFamily;
	private ImmutableArray<Entity> towers;
	private ImmutableArray<Entity> activeEnemies;
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		
		Entity selected = hit(screenX, screenY);
		if(selected != null && Enemy.components.has(selected)){
			current = null;
			for(Entity e : activeEnemies){
				e.remove(Active.class);
			}
			selected.add(getEngine().createComponent(Active.class));
		}else{
			
			if(current == selected){
				
			}else{
				if(current != null) current.remove(Active.class);
				current = selected;
				if(current != null) current.add(getEngine().createComponent(Active.class));
			}
			if(current != null){
				super.touchDown(screenX, screenY, pointer, button);
			}
		}
		
		return true;
	}
	
	private Entity hit(int screenX, int screenY){
		Entity selected = null;
		Vector2 v = unproject(new Vector2(screenX, screenY));
		float dstMax = 1e30f;
		for(Entity entity : towers){
			Transform2DComponent transform = Transform2DComponent.components.get(entity);
			float dst = transform.position.dst(v);
			if(selected == null || dst < dstMax){
				dstMax = dst;
				selected = entity;
			}
		}
		return dstMax < 1 ? selected : null;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(current != null) super.touchDragged(screenX, screenY, pointer);
		return true;
	}
	
	@Override
	protected Entity currentEntity() {
		return current;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(current == null) return true;
		
		
		
		Entity targeted = hit(screenX, screenY);
		
		if(targeted == current){
			current.remove(Active.class);
			current = null;
		}else if(targeted != null){
			current.remove(PathComponent.class);
			current.remove(PathFollower.class);
			
			Follow follow = getEngine().createComponent(Follow.class);
			follow.minDistance = .5f;
			follow.maxDistance = 1f;
			follow.head = targeted;
			current.add(follow);
		}else{
			current.remove(Follow.class);
			super.touchUp(screenX, screenY, pointer, button);
		}
		
		
		return true;
	}
}
