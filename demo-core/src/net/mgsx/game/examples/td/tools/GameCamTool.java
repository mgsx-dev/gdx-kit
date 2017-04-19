package net.mgsx.game.examples.td.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.Kit;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.examples.td.components.Active;
import net.mgsx.game.examples.td.components.Enemy;
import net.mgsx.game.examples.td.components.Tower;
import net.mgsx.game.examples.td.systems.NavSystem;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.g3d.tools.ModelSelector;

public class GameCamTool extends Tool
{
	CameraInputController control;
	private boolean dragging;
	
	public GameCamTool(EditorScreen editor) {
		super("GameCamTool", editor);
	}
	
	@Override
	protected void activate() {
		control = new CameraInputController(editor.getGameCamera());
		// control.
		Kit.inputs.addProcessor(control);
		
		
		super.activate();
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		if(control != null){
			control.update();
		}
	}
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		control.touchDown(screenX, screenY, pointer, button);
		
		// TODO pick ray : if tower then select/unselect
		// if ground && tower selected => move
		// if enemi && tower selected => move to enemi
		// ....
		
		dragging = false;
		
		return true;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		dragging = true;
		control.touchDragged(screenX, screenY, pointer);
		return true;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) 
	{
		if(dragging) return false;
		
		ModelSelector selector = selection().findSelector(ModelSelector.class);
		Array<Entity> entities = new Array<Entity>();
		selector.getSelection(entities , screenX, screenY);
		
		Array<Entity> towers = new Array<Entity>();
		Array<Entity> enemies = new Array<Entity>();
		
		for(Entity e : entities){
			if(Tower.components.has(e)){
				towers.add(e);
			}else if(Enemy.components.has(e)){
				enemies.add(e);
			}
		}
		
		ImmutableArray<Entity> selectedTowers = getEngine().getEntitiesFor(Family.all(Active.class, Tower.class).get());
		ImmutableArray<Entity> selectedEnemies = getEngine().getEntitiesFor(Family.all(Active.class, Enemy.class).get());
		Entity currentTower = selectedTowers.size() > 0 ? selectedTowers.first() : null;
		
		if(towers.size > 0){
			for(Entity e : selectedTowers){
				e.remove(Active.class);
			}
			towers.peek().add(getEngine().createComponent(Active.class));
		}else{
			
			
			if(currentTower != null){
				Transform2DComponent transform = Transform2DComponent.components.get(currentTower);
				Ray ray = editor.getGameCamera().getPickRay(screenX, screenY);
				// ray.mul(editor.getGameCamera().combined.cpy().inv());
				ray.mul(new Matrix4().rotate(Vector3.X, 90));
				Vector3 nearest = new Vector3();
				// Navcast
				if(getEngine().getSystem(NavSystem.class).navMesh.rayCast(ray, nearest , new Vector3())){
					
					getEngine().getSystem(NavSystem.class).pathTo(currentTower, new Vector3(transform.position, transform.depth), nearest, new Vector3(0,0,-1));
				}

			}
		}
		
		return false;
	}
	
	@Override
	protected void desactivate() {
		Kit.inputs.removeProcessor(control);
		super.desactivate();
	}
	
	
}
